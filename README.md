# Dot Terracota

An Android app that takes one moodboard — warm terracotta minimalism meets industrial hardware — and rebuilds the whole thing as running code. Jetpack Compose for the UI, AGSL shaders for the materials.

There are no image assets in this repo. Every plate, planet, glow tube and speck of grain is computed at runtime. If the mockup had brushed metal, I had to write brushed metal. If it had a clay planet, I had to light a sphere.

<p align="center">
  <img src="art/tour.gif" width="240" alt="Swiping through all twelve screens" />
  <img src="art/s00_hero.png" width="240" alt="Hero screen" />
  <img src="art/s02_hardware.png" width="240" alt="Dot matrix interface" />
</p>

## Play with it

Static screens are only half the point. Most things in here respond to touch, and I tuned them until they were fun to fidget with.

| | |
|---|---|
| <img src="art/subwoofer.gif" width="300" /> | **The subwoofer.** Tap the plate and the membrane pumps — the perforation grid dilates, the bass port breathes, and the glow tubes kick brighter, all driven by a bouncy spring feeding a shader uniform. Tap the terracotta disc and the lights switch off, fading to the milky gray of an unpowered LED tube. |
| <img src="art/knob.gif" width="300" /> | **The knob.** Drag anywhere on it to rotate. The clay face and indicator spin while the lighting stays put, which is what sells it as a physical object. Travel is clamped like a real volume pot, with haptic detents every ten steps. |
| <img src="art/fidget.gif" width="300" /> | **The type.** Tap FUTURE IS WARM and the dots pop away from your finger, over-swell, and jiggle back. Drag and they trail around your fingertip like magnetized beads. Zero practical purpose. |
| <img src="art/system.gif" width="300" /> | **The controls.** The quick toggles flip states with animated color and a press bounce. The brightness bar is a working slider — drag it and the sun icon dims with the level, which kicks AUTO off. |

## The screens

Twelve of them, one swipe apart. A hero, a lock screen, and one screen per artifact of the design language.

<p align="center">
  <img src="art/s01_home.png" width="160" />
  <img src="art/s03_brand.png" width="160" />
  <img src="art/s04_system.png" width="160" />
  <img src="art/s05_sound.png" width="160" />
</p>
<p align="center">
  <img src="art/s08_typography.png" width="160" />
  <img src="art/s09_speaker.png" width="160" />
  <img src="art/s10_player.png" width="160" />
  <img src="art/s11_motion.png" width="160" />
</p>

## How it's drawn

Two kinds of rendering, split by what they're good at:

**AGSL shaders** (`ui/Shaders.kt`) handle anything that should look like a material: the brushed metal plates with their seams, screws and emissive tubes, the clay planet, the clay texture on the knob, the anodized metal swatch, the Mars landscape in the player, and the gradient-and-grain backgrounds. They're all resolution independent, so the same shader renders a small card and a full screen without a pixel of difference. Interactive ones take extra uniforms — `uPulse` for the subwoofer excursion, `uLight` for the tubes — driven from Compose by spring animations.

**Canvas drawing** handles everything that should look like UI: a 5×7 dot-matrix font I built for all the pixelated type (rendered dot by dot, with a radial-gradient glow where it needs one), dot progress bars, the equalizer, orbit rings, every icon, and the micro-interaction animations.

One layout trick worth mentioning: each screen is designed in fixed "design units" and `LocalDensity` is rescaled so those units fill the screen width. Components carry no responsive logic at all, and the same composables render at any size — the poster this started as used the exact same code, just smaller.

## Running it

Open in Android Studio and run, or:

```
./gradlew assembleDebug
```

Needs minSdk 33 — `RuntimeShader` is an API 33 thing. No dependencies beyond Compose itself.
