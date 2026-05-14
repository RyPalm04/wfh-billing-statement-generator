#!/usr/bin/env python3
"""
Icon generator for WFH Billing Statement Generator.

Outputs:
  packaging/app.icns           macOS — white-background squircle
  src/.../img/app-icon.png     macOS dev-run fallback (256px PNG, same as icns)
  packaging/app.png            Linux  — transparent-background squircle (256px)
  packaging/app.ico            Windows — transparent-background squircle (multi-size)

Run from the project root:
  python3 packaging/make_icons.py

Requires: Pillow  (pip install Pillow)
"""

import os
import shutil
import subprocess
from PIL import Image, ImageDraw

SOURCE  = "src/main/resources/com/palmer/billingstatementgenerator/img/wfh splash logo.jpg"
OUT_PNG = "src/main/resources/com/palmer/billingstatementgenerator/img/app-icon.png"

# Crop isolates the tree circle only, excluding the "WRIGHT FUNERAL HOME" text band.
CROP = (222, 88, 844, 710)

PADDING_RATIO = 25 / 256   # 25 px of transparent padding at a 256 px canvas
RADIUS_RATIO  = 0.22        # squircle corner radius as a fraction of inner size

MAC_SIZES = [16, 32, 32, 64, 128, 256]
MAC_NAMES = [
    "icon_16x16.png",
    "icon_16x16@2x.png",
    "icon_32x32.png",
    "icon_32x32@2x.png",
    "icon_128x128.png",
    "icon_128x128@2x.png",
]
WIN_SIZES = [16, 32, 48, 64, 128, 256]


def make_squircle(size: int, white_bg: bool) -> Image.Image:
    """Render the tree into a squircle canvas of *size* pixels.

    white_bg=True  → white fill inside squircle, transparent outside (macOS style)
    white_bg=False → white fill inside squircle, fully transparent outside (Linux/Windows)

    Both variants have transparent corners; the difference is purely conceptual —
    same RGBA output — but the flag documents intent and keeps the two paths explicit.
    """
    src  = Image.open(SOURCE).convert("RGB")
    tree = src.crop(CROP)

    padding = round(size * PADDING_RATIO)
    inner   = size - 2 * padding
    radius  = round(inner * RADIUS_RATIO)

    tree_resized = tree.resize((inner, inner), Image.LANCZOS)

    mask = Image.new("L", (inner, inner), 0)
    draw = ImageDraw.Draw(mask)
    draw.rounded_rectangle([0, 0, inner - 1, inner - 1], radius=radius, fill=255)

    # Composite tree onto white, then punch transparent corners via the squircle mask.
    squircle = Image.new("RGBA", (inner, inner), (255, 255, 255, 255))
    squircle.paste(tree_resized, (0, 0))
    squircle.putalpha(mask)

    canvas = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    canvas.paste(squircle, (padding, padding), squircle)
    return canvas


def build_icns() -> None:
    iconset = "packaging/app.iconset"
    os.makedirs(iconset, exist_ok=True)
    for name, size in zip(MAC_NAMES, MAC_SIZES):
        make_squircle(size, white_bg=True).save(os.path.join(iconset, name))
    subprocess.run(
        ["iconutil", "-c", "icns", iconset, "-o", "packaging/app.icns"],
        check=True,
    )
    shutil.rmtree(iconset)
    print("Written: packaging/app.icns")

    make_squircle(256, white_bg=True).save(OUT_PNG)
    print(f"Written: {OUT_PNG}")


def build_linux_png() -> None:
    make_squircle(256, white_bg=False).save("packaging/app.png")
    print("Written: packaging/app.png")


def build_windows_ico() -> None:
    img = make_squircle(256, white_bg=False)
    img.save(
        "packaging/app.ico",
        format="ICO",
        sizes=[(s, s) for s in WIN_SIZES],
    )
    print("Written: packaging/app.ico")


if __name__ == "__main__":
    build_icns()
    build_linux_png()
    build_windows_ico()