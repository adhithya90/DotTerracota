package com.pixelcraft.ui

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixelcraft.ui.Scenes.nightThumb

/** Bordered panel with the board's hard offset shadow. */
@Composable
fun PxPanel(
    modifier: Modifier = Modifier,
    color: Color = Px.white,
    border: Color = Px.ink,
    corner: Dp = 4.dp,
    shadow: Dp = 2.dp,
    content: @Composable () -> Unit,
) {
    Box(
        modifier
            .drawBehind {
                drawRoundRect(
                    Px.ink.copy(alpha = 0.16f),
                    Offset(shadow.toPx(), shadow.toPx()),
                    Size(size.width, size.height),
                    androidx.compose.ui.geometry.CornerRadius(corner.toPx())
                )
            }
            .clip(RoundedCornerShape(corner))
            .background(color)
            .border(1.dp, border, RoundedCornerShape(corner))
    ) { content() }
}

/** Pixel press: the button drops onto its own shadow instantly. No easing. */
@Composable
fun PxButton(
    label: String,
    bg: Color,
    fg: Color,
    modifier: Modifier = Modifier,
    trailing: List<String>? = null,
    trailingPal: Map<Char, Color>? = null,
) {
    var pressed by remember { mutableStateOf(false) }
    val view = LocalView.current
    Box(
        modifier
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown()
                    pressed = true
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    waitForUpOrCancellation()
                    pressed = false
                }
            }
    ) {
        Box(
            Modifier
                .matchParentSize()
                .offset(if (pressed) 2.dp else 0.dp, if (pressed) 2.dp else 0.dp)
                .drawBehind {
                    val s = (if (pressed) 1.dp else 3.dp).toPx()
                    drawRoundRect(
                        Px.ink.copy(alpha = 0.22f), Offset(s, s), Size(size.width, size.height),
                        androidx.compose.ui.geometry.CornerRadius(3.dp.toPx())
                    )
                }
                .clip(RoundedCornerShape(3.dp))
                .background(bg)
                .border(1.dp, Px.ink, RoundedCornerShape(3.dp))
        ) {
            Row(
                Modifier.fillMaxSize().padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PxText(label, 1.1.dp, fg)
                Spacer(Modifier.weight(1f))
                if (trailing != null) Sprite(trailing, trailingPal ?: mapOf('1' to fg), 1.2.dp)
            }
        }
    }
}

/** Interactive toggle tile: instant state swap, crisp tick. */
@Composable
fun ToggleTile(spec: Sprites.Toggle, modifier: Modifier) {
    var on by remember { mutableStateOf(spec.on) }
    val view = LocalView.current
    val bg = if (on) spec.bg else Color(0xFF242C38)
    val glyph = when {
        !on -> Px.gray
        spec.bg == Px.yellow || spec.bg == Px.green -> Px.ink
        else -> Px.white
    }
    Box(
        modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bg)
            .then(if (on) Modifier else Modifier.border(0.5.dp, Color(0xFF2E3846), RoundedCornerShape(4.dp)))
            .pointerInput(Unit) {
                detectTapGestures {
                    on = !on
                    view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Sprite(spec.rows, mapOf('1' to glyph), 1.3.dp)
    }
}

@Composable
fun NotificationCard(modifier: Modifier) {
    PxPanel(modifier, color = Color(0xFF2F6BE0), border = Px.ink, corner = 5.dp) {
        Row(Modifier.fillMaxSize().padding(horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Sprite(Sprites.trophy, mapOf('1' to Px.yellow), 2.4.dp)
            Spacer(Modifier.width(9.dp))
            Column {
                PxText("ACHIEVEMENT", 1.1.dp, Px.white)
                Spacer(Modifier.height(2.dp))
                PxText("UNLOCKED!", 1.1.dp, Px.white)
                Spacer(Modifier.height(3.dp))
                PxText("PIXEL PERFECTION", 0.9.dp, Color(0xFFCFE0FF))
                Spacer(Modifier.height(2.dp))
                PxText("+150 XP", 0.9.dp, Px.yellow)
            }
            Spacer(Modifier.weight(1f))
            Sprite(Sprites.chevBig, mapOf('1' to Color(0xFFCFE0FF)), 1.3.dp)
        }
    }
}

@Composable
fun StreakCard(modifier: Modifier) {
    PxPanel(modifier) {
        Row(Modifier.fillMaxSize().padding(horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Sprite(Sprites.flame, mapOf('1' to Color(0xFFF08A3C)), 1.6.dp)
            Spacer(Modifier.width(7.dp))
            Column {
                PxText("STREAK", 1.dp, Px.label)
                Spacer(Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    PxText("12", 1.8.dp, Px.ink)
                    Spacer(Modifier.width(4.dp))
                    PxText("DAYS", 1.dp, Px.label)
                }
            }
            Spacer(Modifier.weight(1f))
            Canvas(Modifier.size(44.dp, 22.dp)) {
                val heights = listOf(3, 5, 2, 6, 4, 8, 5, 9, 7, 11)
                val cw = size.width / (heights.size * 3 - 1) * 3
                heights.forEachIndexed { i, bh ->
                    val c = if (i % 2 == 1) Color(0xFFF08A3C) else Color(0xFFF5A25D)
                    val h = bh / 12f * size.height
                    drawRect(c, Offset(i * cw, size.height - h), Size(cw * 0.66f, h))
                }
            }
        }
    }
}

@Composable
fun LevelCard(modifier: Modifier) {
    PxPanel(modifier) {
        Row(Modifier.fillMaxSize().padding(horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Sprite(Sprites.avatarLevel, Sprites.avatarLevelPal, 2.dp)
            Spacer(Modifier.width(9.dp))
            Column {
                PxText("LEVEL 7", 1.3.dp, Px.ink)
                Spacer(Modifier.height(5.dp))
                Box(Modifier.width(96.dp).height(6.dp).background(Color(0xFFE4E1DA)).border(0.5.dp, Px.ink)) {
                    Box(Modifier.fillMaxSize(0.68f).background(Px.green))
                }
                Spacer(Modifier.height(4.dp))
                PxText("680 / 1000 XP", 0.85.dp, Px.dim)
            }
        }
    }
}

@Composable
fun AppCard(modifier: Modifier) {
    var liked by remember { mutableStateOf(false) }
    val view = LocalView.current
    PxPanel(modifier) {
        Row(Modifier.fillMaxSize().padding(9.dp)) {
            Canvas(Modifier.size(34.dp, 58.dp)) {
                nightThumb(size.width / Scenes.THUMB_W)
            }
            Spacer(Modifier.width(9.dp))
            Column(Modifier.weight(1f)) {
                Row {
                    Column {
                        PxText("NIGHT DRIVE", 0.8.dp, Px.ink)
                        Spacer(Modifier.height(3.dp))
                        PxText("CHIPWAVE", 0.7.dp, Px.dim)
                    }
                    Spacer(Modifier.weight(1f))
                    Box(Modifier.pointerInput(Unit) {
                        detectTapGestures {
                            liked = !liked
                            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                        }
                    }) {
                        Sprite(
                            if (liked) Sprites.heart else Sprites.heartOutline,
                            mapOf('1' to if (liked) Px.pink else Px.ink), 1.2.dp
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
                Box(Modifier.fillMaxWidth().height(4.dp).background(Color(0xFFE4E1DA)).border(0.5.dp, Color(0xFFC9C4BB))) {
                    Box(Modifier.fillMaxSize(0.36f).background(Px.blue))
                }
                Spacer(Modifier.height(3.dp))
                Row(Modifier.fillMaxWidth()) {
                    PxText("1:24", 0.85.dp, Px.dim)
                    Spacer(Modifier.weight(1f))
                    PxText("3:56", 0.85.dp, Px.dim)
                }
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Sprite(Sprites.prev, Sprites.inkPal, 1.2.dp)
                    Spacer(Modifier.width(12.dp))
                    PlayCircle()
                    Spacer(Modifier.width(12.dp))
                    Sprite(Sprites.next, Sprites.inkPal, 1.2.dp)
                }
            }
        }
    }
}

@Composable
private fun PlayCircle() {
    var pressed by remember { mutableStateOf(false) }
    val view = LocalView.current
    Box(
        Modifier
            .size(17.dp)
            .offset(if (pressed) 1.dp else 0.dp, if (pressed) 1.dp else 0.dp)
            .clip(RoundedCornerShape(50))
            .background(Px.ink)
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(); pressed = true
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    waitForUpOrCancellation(); pressed = false
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Sprite(Sprites.play, Sprites.whitePal, 1.2.dp)
    }
}

/** Stepped slider: the knob snaps whole pixels, never glides. */
@Composable
fun PxSlider(modifier: Modifier) {
    var value by remember { mutableFloatStateOf(0.72f) }
    val view = LocalView.current
    var lastStep by remember { mutableIntStateOf(17) }
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        androidx.compose.foundation.layout.BoxWithConstraints(
            Modifier
                .weight(1f)
                .height(20.dp)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { change, _ ->
                        val raw = (change.position.x / size.width).coerceIn(0f, 1f)
                        val step = (raw * 24).toInt()
                        if (step != lastStep) {
                            lastStep = step
                            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                        }
                        value = step / 24f
                        change.consume()
                    }
                },
            contentAlignment = Alignment.CenterStart
        ) {
            val trackW = maxWidth
            Box(Modifier.fillMaxWidth().height(4.dp).background(Color(0xFFD8D4CB)))
            Box(Modifier.fillMaxWidth(value).height(4.dp).background(Px.blue))
            Box(
                Modifier
                    .offset(x = (trackW - 10.dp) * value)
                    .size(10.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Px.blue)
                    .border(1.5.dp, Px.white, RoundedCornerShape(50))
            )
        }
        Spacer(Modifier.width(8.dp))
        PxText("${(value * 100).toInt()}%", 1.dp, Px.label)
    }
}

@Composable
fun ProgressBars(modifier: Modifier, time: Float) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.weight(1f).height(9.dp).background(Px.white).border(1.dp, Px.ink)) {
                Box(Modifier.fillMaxSize(0.78f).background(Px.blue))
            }
            Spacer(Modifier.width(8.dp))
            PxText("78%", 1.dp, Px.label)
        }
        Canvas(Modifier.fillMaxWidth().height(11.dp)) {
            drawRect(Px.ink, Offset.Zero, Size(size.width, size.height))
            drawRect(Px.white, Offset(1.dp.toPx(), 1.dp.toPx()), Size(size.width - 2.dp.toPx(), size.height - 2.dp.toPx()))
            val segs = 9
            val filled = 5 + frameOf(time, 1.2f, 4)
            val sw = (size.width - 6.dp.toPx()) / segs
            for (i in 0 until segs) {
                val c = if (i < filled) Px.green else Color(0xFF20262F)
                drawRect(c, Offset(3.dp.toPx() + i * sw, 2.dp.toPx()), Size(sw - 2.dp.toPx() / 2, size.height - 4.dp.toPx()))
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Canvas(Modifier.weight(0.7f).height(11.dp)) {
                drawRect(Px.ink, Offset.Zero, Size(size.width, size.height))
                drawRect(Px.white, Offset(1.dp.toPx(), 1.dp.toPx()), Size(size.width - 2.dp.toPx(), size.height - 2.dp.toPx()))
                val shift = frameOf(time, 6f, 4) * 2.dp.toPx()
                var x = -size.height + shift
                while (x < size.width * 0.62f) {
                    for (yy in 1 until (size.height / 1.dp.toPx()).toInt() - 1) {
                        val sx = x + yy * 1.dp.toPx()
                        if (sx > 0 && sx < size.width * 0.62f) {
                            drawRect(Px.pink, Offset(sx, yy * 1.dp.toPx()), Size(2.dp.toPx(), 1.dp.toPx()))
                        }
                    }
                    x += 4.dp.toPx()
                }
            }
            Spacer(Modifier.width(8.dp))
            PxText("LOADING...", 1.dp, Px.label)
        }
    }
}

/** Micro interactions: everything stepped, the like-heart bursts on tap. */
@Composable
fun MicroRows(modifier: Modifier, time: Float) {
    val view = LocalView.current
    Column(modifier, verticalArrangement = Arrangement.spacedBy(13.dp)) {
        MicroRow("BUTTON PRESS") {
            val pressedDemo = frameOf(time, 1.6f, 2) == 1
            MiniChip(Px.blue, pressed = false)
            ArrowGap()
            MiniChip(Color(0xFF2A62C4), pressed = pressedDemo)
        }
        MicroRow("TOGGLE ON") {
            MiniSwitch(false)
            ArrowGap()
            MiniSwitch(true)
        }
        MicroRow("LIKE ANIMATION") {
            var burst by remember { mutableStateOf(false) }
            Box(Modifier.pointerInput(Unit) {
                detectTapGestures {
                    burst = !burst
                    view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                }
            }) {
                Sprite(Sprites.heartOutline, mapOf('1' to Px.dim), 1.2.dp)
            }
            ArrowGap()
            Sprite(
                if (burst || frameOf(time, 0.8f, 2) == 1) Sprites.heartBurst else Sprites.heartOutline,
                if (burst || frameOf(time, 0.8f, 2) == 1) Sprites.heartBurstPal else mapOf('1' to Px.dim),
                1.2.dp
            )
        }
        MicroRow("LOADING") {
            val f = frameOf(time, 6f, Sprites.loadFrames.size)
            Sprites.loadFrames.indices.forEach { i ->
                Sprite(
                    Sprites.loadFrames[(i + f) % Sprites.loadFrames.size],
                    mapOf('1' to Color(0xFF3A4048)), 1.4.dp
                )
                Spacer(Modifier.width(5.dp))
            }
        }
    }
}

@Composable
private fun MicroRow(label: String, content: @Composable () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.width(50.dp), contentAlignment = Alignment.CenterStart) {
            PxText(label, 0.58.dp, Px.label)
        }
        Spacer(Modifier.weight(1f))
        content()
    }
}

@Composable
private fun MiniChip(bg: Color, pressed: Boolean) {
    Box(
        Modifier
            .offset(if (pressed) 1.dp else 0.dp, if (pressed) 1.dp else 0.dp)
            .size(14.dp, 12.dp)
            .drawBehind {
                val s = (if (pressed) 1.dp else 2.dp).toPx()
                drawRoundRect(Px.ink.copy(alpha = 0.25f), Offset(s, s), Size(size.width, size.height),
                    androidx.compose.ui.geometry.CornerRadius(2.dp.toPx()))
            }
            .clip(RoundedCornerShape(2.dp))
            .background(bg)
            .border(1.dp, Px.ink, RoundedCornerShape(2.dp)),
        contentAlignment = Alignment.Center
    ) {
        Sprite(Sprites.chev, Sprites.whitePal, 1.0.dp)
    }
}

@Composable
private fun MiniSwitch(on: Boolean) {
    Box(
        Modifier.size(15.dp, 11.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(if (on) Px.green else Color(0xFFB9BEC6))
            .border(1.dp, Px.ink, RoundedCornerShape(2.dp))
    ) {
        Box(
            Modifier.align(if (on) Alignment.CenterEnd else Alignment.CenterStart)
                .padding(2.dp).size(6.dp)
                .background(Px.white).border(1.dp, Px.ink)
        )
    }
}

@Composable
private fun ArrowGap() {
    Spacer(Modifier.width(4.dp))
    Sprite(listOf("..1.", "..11", "1111", "..11", "..1."), mapOf('1' to Px.gray), 1.0.dp)
    Spacer(Modifier.width(4.dp))
}
