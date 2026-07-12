# Flow Asset Runner

Fans the prompts in `design/gforce-asset-prompts.md` across several Google Flow
logins and generates every asset unattended. More accounts = more throughput
(each account has its own Flow quota).

## One-time setup

```bash
cd automation
pip install -r requirements.txt
playwright install chromium
```

Then edit `flow_runner.py`:

1. **PROFILES** — one entry per Google account. Each needs a UNIQUE
   `user_data_dir` (any empty writable folder). Never share a dir between two
   accounts.
2. **SELECTORS** — see below. This is the only part that breaks over time.

## Get the selectors (10 min, once)

The script needs to know which page elements to type into and click. Grab them:

1. Open <https://labs.google/fx/tools/flow> in Chrome, logged in.
2. Press **F12** → **Elements** tab.
3. Use the element picker (top-left arrow icon) and click the target on the page:
   - the **prompt text box** → right-click the highlighted node → **Copy → Copy selector** → paste into `SEL["prompt_input"]`
   - the **generate/create button** → same → `SEL["generate_btn"]`
   - after a generation finishes, the **download button** → same → `SEL["download_btn"]`
4. `SEL["result_ready"]` — a selector that only exists once a result is done.
   The default (`video, img[src^='blob:']`) works for most cases; tighten it if
   the script downloads too early.
5. Aspect ratio: easiest is to set 9:16 (or 1:1) once in the Flow UI — it
   persists in the profile — and leave the `aspect_*` selectors as `None`.
   Only wire them up if you mix ratios in one run.

Copied selectors can be long and brittle (e.g. `#root > div > div:nth-child(2)...`).
Prefer stable ones by hand when you can: an `aria-label`, `data-*` attribute, or
visible button text (`button:has-text('Create')`).

## Run

```bash
python flow_runner.py
```

- First run: a Chrome window opens per profile. Log into Google **manually** in
  each (do NOT script the login — it gets accounts flagged). The script waits,
  then continues on its own. Sessions are saved; later runs skip the login.
- Output lands in `automation/out/`, named after each `**File:**` in the md.
- Re-running is safe: anything already in `out/` is skipped. A prompt that fails
  (quota, timeout) is requeued to another live account automatically.

Override the prompt file:

```bash
python flow_runner.py --md ../design/some-other-prompts.md
```

## Gotchas

- **Headed only.** Google blocks headless Chromium login. Keep windows visible.
- **RAM.** Each headed window is heavy. 3–5 parallel accounts is realistic on one
  machine. Beyond that, run on more machines or in batches.
- **UI drift.** When Google reshapes Flow, the selectors rot — update the `SEL`
  block, nothing else.
- **ToS / quota.** Automating Flow is against Google ToS at scale, and burns
  per-account quota fast. Personal-scale, polite pacing (`COOLDOWN_S`) only.
  Your accounts, your risk.
