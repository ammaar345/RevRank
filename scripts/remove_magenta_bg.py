"""
Remove #FF00FF (magenta) background from all generated asset PNGs.
Run from the repo root or pass a directory path.

Usage:
    python scripts/remove_magenta_bg.py
    python scripts/remove_magenta_bg.py design/assets
"""

import sys, os, glob
from PIL import Image

MAGENTA = (255, 0, 255)
TOLERANCE = 30

def remove_magenta_bg(img: Image.Image, tol: int = TOLERANCE) -> Image.Image:
    img = img.convert("RGBA")
    pixels = img.load()
    w, h = img.size
    for y in range(h):
        for x in range(w):
            r, g, b, a = pixels[x, y]
            if abs(r - MAGENTA[0]) <= tol and abs(g - MAGENTA[1]) <= tol and abs(b - MAGENTA[2]) <= tol:
                pixels[x, y] = (r, g, b, 0)
    return img

def main():
    path = sys.argv[1] if len(sys.argv) > 1 else "."
    pattern = os.path.join(path, "*.png")
    files = glob.glob(pattern)
    if not files:
        print(f"No PNGs found in '{path}'")
        return
    for fp in files:
        img = Image.open(fp)
        cleaned = remove_magenta_bg(img)
        cleaned.save(fp)
        print(f"  cleaned: {os.path.basename(fp)}")
    print(f"\nDone. {len(files)} files processed in-place.")

if __name__ == "__main__":
    main()
