# Design Lens

I saw a pretty terracotta moodboard and got completely carried away. Then it happened again with a lavender one. So now this is Design Lens: a small gallery app where each visual style is rebuilt entirely as running code. Jetpack Compose for the UI, AGSL shaders for the materials. There isn't a single PNG in the app.

You land on a dark page, pick a lens, and swipe through twelve screens of it. Each card on the landing page is a live render, not a thumbnail.

<p align="center">
  <img src="art/landing.png" width="240" alt="The landing page" />
  <img src="art/s00_hero.png" width="240" alt="Dot Terracota hero" />
  <img src="art/sm_hero.png" width="240" alt="Soft Machine hero" />
</p>

## Dot Terracota

Warm industrial: dot matrix type, brushed metal, glowing tubes, clay.

| | |
|---|---|
| <img src="art/subwoofer.gif" width="300" /> | **The subwoofer.** Tap it and it goes *thump*. The membrane pumps, the port breathes, the lights kick. Tap the orange circle and the lights turn off. Weirdly satisfying. |
| <img src="art/knob.gif" width="300" /> | **The knob.** You can spin it. It clicks as it turns. The clay rotates but the lighting stays still, which is the thing that makes your brain accept it as an object. It controls a number that does nothing. |
| <img src="art/fidget.gif" width="300" /> | **The type.** Poke the dots and they run away from your finger, then wobble back. Drag and they follow you around like confused ducklings. I have spent an embarrassing amount of time doing this. |
| <img src="art/system.gif" width="300" /> | **The controls.** The toggles toggle. The slider slides. The little sun dims when you drag brightness down. |

<p align="center">
  <img src="art/s01_home.png" width="160" />
  <img src="art/s04_system.png" width="160" />
  <img src="art/s08_typography.png" width="160" />
  <img src="art/s09_speaker.png" width="160" />
</p>

## Soft Machine

Lavender neumorphism: warm greige, mauve, Montserrat, everything molded and squeezable. The colors are sampled from the reference board, not eyeballed. I learned that lesson the hard way.

| | |
|---|---|
| <img src="art/soft_cloud.gif" width="300" /> | **The cloud.** A raymarched blob of smooth-min spheres with real ambient occlusion and soft shadows, rendered live in a fragment shader. Tap it and it squishes like a marshmallow. |
| <img src="art/soft_cushion.gif" width="300" /> | **The air core.** A thermoformed blister pack: felt knob, double-walled plastic moulding, a racetrack pressure channel. The puck actually slides along the channel, and the bead chain parts around it. |
| <img src="art/soft_silicone.gif" width="300" /> | **The silicone.** Pristine until you touch it. A quick tap makes a soft shallow dent. Hold, and your finger keeps sinking. Let go and it springs back with a wobble. |
| <img src="art/soft_textures.gif" width="300" /> | **The textures.** Each material answers touch in its own physics. Silicone sinks slowly. Glass doesn't dent at all: your finger wipes the frost clear, and the fog re-condenses after you leave. The micro bumps flatten fast, snap back crisply, and tick under a dragging finger. |

<p align="center">
  <img src="art/sm_home.png" width="160" />
  <img src="art/sm_cushion.png" width="160" />
  <img src="art/sm_player.png" width="160" />
  <img src="art/sm_color.png" width="160" />
</p>

## How it's drawn

Two tools:

**Shaders** for anything that should feel like a material: brushed metal, screws, glow tubes, clay, the planet, the raymarched cloud, the blister pack, silicone, milky glass. They're resolution independent, so the same shader draws a tiny landing-card preview or a full screen and doesn't care. Interactive ones take extra uniforms (`uPulse`, `uKnob`, `uTouch`) that Compose feeds from spring animations.

**Canvas** for anything that should feel like UI: the dot matrix font (a 5×7 grid I typed in by hand, letter by letter, like it's 1982), progress dots, equalizers, icons, the spinny bits.

One layout trick: every screen is designed in made-up units, and I rescale `LocalDensity` so those units fill whatever screen you have. No responsive logic anywhere.

## Running it

Open it in Android Studio and press the green triangle. Or:

```
./gradlew assembleDebug
```

minSdk is 33, because `RuntimeShader`. Sorry.
