#!/usr/bin/env python3
"""
Icon generator for Statement Manager.

Outputs:
  packaging/app.icns           macOS — squircle with app background
  src/.../img/app-icon.png     in-app PNG fallback (512px)
  packaging/app.png            Linux  — transparent-background squircle (512px)
  packaging/app.ico            Windows — transparent-background squircle (multi-size)

Run from the project root:
  python3.12 packaging/make_icons.py

Requires: Pillow  (python3.12 -m pip install Pillow)
          iconutil (macOS, built-in)
"""

import os
import shutil
import subprocess
from PIL import Image, ImageDraw, ImageFont

FONT_PATH = "src/main/resources/fonts/PlayfairDisplay-Bold.ttf"
OUT_PNG   = "src/main/resources/img/app-icon.png"

PADDING_RATIO = 25 / 256   # matches original — 25px padding at 256px canvas
RADIUS_RATIO  = 0.22        # squircle corner radius as fraction of inner size

MAC_SIZES = [16, 32, 32, 64, 128, 256, 256, 512, 512, 1024]
MAC_NAMES = [
    "icon_16x16.png",
    "icon_16x16@2x.png",
    "icon_32x32.png",
    "icon_32x32@2x.png",
    "icon_128x128.png",
    "icon_128x128@2x.png",
    "icon_256x256.png",
    "icon_256x256@2x.png",
    "icon_512x512.png",
    "icon_512x512@2x.png",
]
WIN_SIZES = [16, 32, 48, 64, 128, 256]

NAVY     = (27,  58,  107, 255)
GOLD     = (201, 168, 76,  255)
GOLD_DIM = (201, 168, 76,  140)   # inner ring ~55% opacity
APP_BG   = (247, 245, 242, 255)   # -fx-base: #f7f5f2


def render_icon(size: int) -> Image.Image:
    s = size / 48.0

    outer = [(x * s, y * s) for x, y in [(24,2),(42,12),(42,36),(24,46),(6,36),(6,12)]]
    inner = [(x * s, y * s) for x, y in [(24,6),(39,14),(39,34),(24,42),(9,34),(9,14)]]

    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)

    d.polygon(outer, fill=NAVY)
    d.polygon(outer, outline=GOLD, width=max(1, round(1.8 * s)))

    ring = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    ImageDraw.Draw(ring).polygon(inner, outline=GOLD_DIM, width=max(1, round(0.6 * s)))
    img = Image.alpha_composite(img, ring)

    font = ImageFont.truetype(FONT_PATH, round(17 * s))
    d2 = ImageDraw.Draw(img)
    d2.text((16.5 * s, 30.0 * s), "S", font=font, fill=GOLD, anchor="ms")
    d2.text((29.5 * s, 30.0 * s), "M", font=font, fill=GOLD, anchor="ms")

    return img


def make_squircle(size: int, bg_color: tuple) -> Image.Image:
    padding = round(size * PADDING_RATIO)
    inner   = size - 2 * padding
    radius  = round(inner * RADIUS_RATIO)

    icon = render_icon(inner)

    # Composite icon onto background, then punch transparent corners via squircle mask
    squircle = Image.new("RGBA", (inner, inner), bg_color)
    squircle.paste(icon, (0, 0), icon)

    mask = Image.new("L", (inner, inner), 0)
    ImageDraw.Draw(mask).rounded_rectangle([0, 0, inner - 1, inner - 1], radius=radius, fill=255)
    squircle.putalpha(mask)

    canvas = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    canvas.paste(squircle, (padding, padding), squircle)
    return canvas


def build_icns() -> None:
    iconset = "packaging/app.iconset"
    os.makedirs(iconset, exist_ok=True)
    for name, size in zip(MAC_NAMES, MAC_SIZES):
        make_squircle(size, APP_BG).save(os.path.join(iconset, name))
    subprocess.run(
        ["iconutil", "-c", "icns", iconset, "-o", "packaging/app.icns"],
        check=True,
    )
    shutil.rmtree(iconset)
    print("Written: packaging/app.icns")

    render_icon(512).save(OUT_PNG)
    print(f"Written: {OUT_PNG}")


def build_linux_png() -> None:
    make_squircle(512, (0, 0, 0, 0)).save("packaging/app.png")
    print("Written: packaging/app.png")


def build_windows_ico() -> None:
    img = make_squircle(256, (0, 0, 0, 0))
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
