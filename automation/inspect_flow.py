"""
inspect_flow.py — one-off DOM grabber so we can set the real Flow selectors.

Run it, log in, get the Flow prompt screen visible, press Enter. It writes every
visible interactive element (inputs, editable divs, buttons, aria-labels) to
flow_dom.txt. Paste that file back / it can be read from disk to pick exact
selectors for flow_runner.py.
"""

import asyncio
from pathlib import Path
from playwright.async_api import async_playwright

PROFILE = r"D:\flow-profiles\acct1"          # same profile you logged into
FLOW_URL = "https://labs.google/fx/tools/flow"
OUT = Path(__file__).resolve().parent / "flow_dom.txt"

JS = r"""
() => {
  const out = [];
  const sel = 'textarea, input, [contenteditable="true"], [role="textbox"], button, [role="button"], [aria-label]';
  document.querySelectorAll(sel).forEach(el => {
    const r = el.getBoundingClientRect();
    if (r.width === 0 && r.height === 0) return;          // skip hidden
    out.push({
      tag: el.tagName.toLowerCase(),
      type: el.getAttribute('type'),
      name: el.getAttribute('name'),
      id: el.id || null,
      cls: (el.className && el.className.toString().slice(0, 70)) || null,
      role: el.getAttribute('role'),
      aria: el.getAttribute('aria-label'),
      ph: el.getAttribute('placeholder'),
      dtid: el.getAttribute('data-testid'),
      text: (el.innerText || '').trim().slice(0, 40) || null,
      editable: el.isContentEditable,
    });
  });
  return out;
}
"""


def fmt(d):
    parts = [d["tag"]]
    if d["id"]:   parts.append(f'#{d["id"]}')
    if d["type"]: parts.append(f'type={d["type"]}')
    if d["name"]: parts.append(f'name={d["name"]}')
    if d["role"]: parts.append(f'role={d["role"]}')
    if d["dtid"]: parts.append(f'data-testid={d["dtid"]}')
    if d["aria"]: parts.append(f'aria="{d["aria"]}"')
    if d["ph"]:   parts.append(f'placeholder="{d["ph"]}"')
    if d["editable"]: parts.append("contenteditable")
    if d["text"]: parts.append(f'text="{d["text"]}"')
    if d["cls"]:  parts.append(f'.{d["cls"]}')
    return "  ".join(parts)


async def main():
    async with async_playwright() as pw:
        ctx = await pw.chromium.launch_persistent_context(
            PROFILE, headless=False, channel="chrome",
            ignore_default_args=["--enable-automation"],
            args=["--disable-blink-features=AutomationControlled"],
        )
        page = ctx.pages[0] if ctx.pages else await ctx.new_page()
        await page.goto(FLOW_URL, wait_until="domcontentloaded")

        input("\n>>> In the browser: log in if needed, open the Flow screen with the "
              "PROMPT BOX visible.\n>>> Then come back here and press Enter...\n")

        data = await page.evaluate(JS)
        lines = [fmt(d) for d in data]
        OUT.write_text("\n".join(lines), encoding="utf-8")
        print(f"\nWrote {len(lines)} visible elements -> {OUT}")
        print("Send that file back (or it can be read from disk).")

        input("\nPress Enter to close the browser...\n")
        await ctx.close()


if __name__ == "__main__":
    asyncio.run(main())
