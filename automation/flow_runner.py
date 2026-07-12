"""
flow_runner.py — fan RevRank asset prompts across multiple Google Flow logins.

What it does:
  1. Parses design/gforce-asset-prompts.md -> list of (output_name, prompt).
  2. Distributes prompts across N browser profiles (one Google login each) via a
     shared queue. Workers run in parallel, one prompt in flight per profile.
  3. Each worker drives Flow's web UI: paste prompt, generate, wait, download.
  4. Skips prompts whose output already exists (safe to re-run / resume).
  5. A prompt that fails (quota, timeout) is requeued to a live worker.

Flow has no public API, so this drives the real web UI. Google may change that
UI at any time — when it breaks, update the SELECTORS block below. Nothing else
should need touching.

Setup:
  pip install playwright
  playwright install chromium
  First run per profile: a window opens, log into Google manually, then leave it.
  The session is saved in that profile's user_data_dir and reused after.
"""

import asyncio
import re
import sys
from pathlib import Path
from playwright.async_api import async_playwright, TimeoutError as PWTimeout

# ---------------------------------------------------------------------------
# CONFIG — edit these
# ---------------------------------------------------------------------------

# Repo-relative default; override with --md
MD_PATH = Path(__file__).resolve().parent.parent / "design" / "gforce-asset-prompts.md"

# Where finished assets land
OUT_DIR = Path(__file__).resolve().parent / "out"

# One entry per Google account. user_data_dir MUST be unique per account —
# never share a dir between two accounts. Create the dirs anywhere writable.
PROFILES = [
    {"name": "acct1", "user_data_dir": r"D:\flow-profiles\acct1"},
    {"name": "acct2", "user_data_dir": r"D:\flow-profiles\acct2"},
    # add more accounts -> more throughput (each has its own Flow quota)
]

FLOW_URL = "https://labs.google/fx/tools/flow"

# Headed is REQUIRED. Google flags headless Chromium and blocks login.
HEADLESS = False

# Per-generation ceiling. Flow image gen is fast; video is minutes. Be generous.
GEN_TIMEOUT_MS = 10 * 60 * 1000

# Polite pause between prompts on the same account (anti-rate-flag).
COOLDOWN_S = 3

# Image model to select in Flow. Must match the label Flow shows in its model
# picker exactly (e.g. "Nano Banana 2" / "Gemini 3 Pro Image"). Set the picker
# selectors below or this is a no-op and Flow uses whatever model is active.
MODEL_NAME = "Nano Banana 2"

# ---------------------------------------------------------------------------
# SELECTORS — the only part that rots when Google changes the UI.
# Grab real values: open Flow, F12 -> Elements -> right-click node ->
# Copy -> Copy selector. Replace the placeholders below. See README.md.
# ---------------------------------------------------------------------------
SEL = {
    # the prompt text box
    "prompt_input": "textarea",
    # button that starts generation (Enter fallback used if this is missing)
    "generate_btn": "button:has-text('Create')",
    # appears/enabled only when a generation has finished (used to detect "done")
    "result_ready": "video, img[src^='blob:'], [data-generation-status='done']",
    # per-result download control (opens a browser download event)
    "download_btn": "button[aria-label*='Download' i]",
    # OPTIONAL: aspect-ratio control. Leave as None to skip (set ratio manually
    # in the UI once — it usually persists in the profile).
    "aspect_open": None,        # e.g. "button[aria-label*='Aspect' i]"
    "aspect_9x16": None,        # e.g. "role=menuitem[name='9:16']"
    "aspect_1x1": None,         # e.g. "role=menuitem[name='1:1']"
    # OPTIONAL: model picker, to force MODEL_NAME (Nano Banana 2). Leave None to
    # use whatever model Flow currently has selected.
    "model_open": None,         # e.g. "button[aria-label*='Model' i]"
    "model_option": None,       # e.g. f"role=menuitem[name='{MODEL_NAME}']"
}

# ---------------------------------------------------------------------------
# PARSER — tuned to gforce-asset-prompts.md structure:
#   **File:** `name.jpeg` — desc
#   ```
#   <prompt...>
#   ```
# The leading "Master Style Block" fence has no **File:** before it, so it is
# skipped automatically.
# ---------------------------------------------------------------------------
FILE_RE = re.compile(r"\*\*File:\*\*\s*`([^`]+)`")


def parse_prompts(md_path: Path):
    lines = md_path.read_text(encoding="utf-8").splitlines()
    out, last_file, in_fence, buf = [], None, False, []
    for line in lines:
        if not in_fence:
            m = FILE_RE.search(line)
            if m:
                last_file = m.group(1)
            elif line.strip().startswith("```"):
                in_fence, buf = True, []
        else:
            if line.strip().startswith("```"):
                in_fence = False
                prompt = "\n".join(buf).strip()
                if last_file and prompt:
                    out.append((last_file, prompt))
                last_file = None
            else:
                buf.append(line)
    return out


def out_path_for(name: str) -> Path:
    # name like "bg-sportbike-front.jpeg" -> out/bg-sportbike-front.<ext>
    # Flow gives video (.mp4) or image; we save whatever the download provides
    # and just key existence off the stem.
    return OUT_DIR / name


def already_done(name: str) -> bool:
    stem = Path(name).stem
    return any(p.stem == stem for p in OUT_DIR.glob(f"{stem}.*"))


def wants_9x16(name: str) -> bool:
    return Path(name).name.startswith("bg-")


# ---------------------------------------------------------------------------
# WORKER
# ---------------------------------------------------------------------------
async def set_model(page):
    """Force MODEL_NAME (Nano Banana 2) if the picker selectors are set."""
    if not SEL["model_open"]:
        return
    try:
        await page.click(SEL["model_open"], timeout=5000)
        opt = SEL["model_option"] or f"text={MODEL_NAME}"
        await page.click(opt, timeout=5000)
    except PWTimeout:
        pass  # non-fatal; model may already be selected in the profile


async def set_aspect(page, name):
    if not SEL["aspect_open"]:
        return
    try:
        await page.click(SEL["aspect_open"], timeout=5000)
        target = SEL["aspect_9x16"] if wants_9x16(name) else SEL["aspect_1x1"]
        if target:
            await page.click(target, timeout=5000)
    except PWTimeout:
        pass  # non-fatal; ratio may already be set in the profile


async def generate_one(page, name, prompt):
    await set_model(page)
    await page.fill(SEL["prompt_input"], prompt)
    await set_aspect(page, name)
    # submit
    try:
        await page.click(SEL["generate_btn"], timeout=5000)
    except PWTimeout:
        await page.press(SEL["prompt_input"], "Enter")

    # wait for a finished result
    await page.wait_for_selector(SEL["result_ready"], timeout=GEN_TIMEOUT_MS)

    # download it via the browser download event
    dest = out_path_for(name)
    async with page.expect_download(timeout=60_000) as dl_info:
        await page.click(SEL["download_btn"])
    download = await dl_info.value
    # keep Flow's real extension, but our chosen stem
    suffix = Path(download.suggested_filename).suffix or ".mp4"
    dest = dest.with_suffix(suffix)
    await download.save_as(str(dest))
    return dest


async def worker(pw, profile, queue: asyncio.Queue):
    ctx = await pw.chromium.launch_persistent_context(
        profile["user_data_dir"], headless=HEADLESS,
        accept_downloads=True,
        channel="chrome",                       # real Chrome, not bundled Chromium
        ignore_default_args=["--enable-automation"],
        args=["--disable-blink-features=AutomationControlled"],
    )
    page = ctx.pages[0] if ctx.pages else await ctx.new_page()
    await page.goto(FLOW_URL, wait_until="domcontentloaded")

    # give a human a moment on first run to complete Google login
    if "accounts.google" in page.url or await page.query_selector(SEL["prompt_input"]) is None:
        print(f'[{profile["name"]}] log into Google in this window, then it continues...')
        try:
            await page.wait_for_selector(SEL["prompt_input"], timeout=5 * 60 * 1000)
        except PWTimeout:
            print(f'[{profile["name"]}] no login detected, giving up this profile')
            await ctx.close()
            return

    done = 0
    while True:
        try:
            name, prompt = queue.get_nowait()
        except asyncio.QueueEmpty:
            break
        if already_done(name):
            print(f'[{profile["name"]}] skip (exists): {name}')
            continue
        try:
            dest = await generate_one(page, name, prompt)
            done += 1
            print(f'[{profile["name"]}] OK -> {dest.name}')
        except Exception as e:
            msg = str(e).lower()
            print(f'[{profile["name"]}] FAIL {name}: {e!r}')
            await queue.put((name, prompt))          # let another worker retry
            if "quota" in msg or "limit" in msg:
                print(f'[{profile["name"]}] quota hit, retiring account')
                break
        await asyncio.sleep(COOLDOWN_S)

    print(f'[{profile["name"]}] finished, {done} generated')
    await ctx.close()


# ---------------------------------------------------------------------------
# MAIN
# ---------------------------------------------------------------------------
async def main(md_path: Path):
    OUT_DIR.mkdir(parents=True, exist_ok=True)
    prompts = parse_prompts(md_path)
    if not prompts:
        print(f"No prompts parsed from {md_path}")
        return
    todo = [(n, p) for (n, p) in prompts if not already_done(n)]
    print(f"{len(prompts)} prompts in md, {len(todo)} still to generate, "
          f"{len(PROFILES)} accounts")

    q: asyncio.Queue = asyncio.Queue()
    for item in todo:
        q.put_nowait(item)

    async with async_playwright() as pw:
        await asyncio.gather(*[worker(pw, prof, q) for prof in PROFILES])

    remaining = q.qsize()
    print(f"Done. {remaining} prompt(s) left unfinished "
          f"(re-run to retry — finished ones are skipped).")


if __name__ == "__main__":
    md = Path(sys.argv[sys.argv.index("--md") + 1]) if "--md" in sys.argv else MD_PATH
    asyncio.run(main(md))
