package com.dotterracota.ui

import com.designlens.core.MonoText
import com.designlens.core.ShaderPanel
import com.designlens.core.rememberTimeSeconds

import android.view.HapticFeedbackConstants
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlinx.coroutines.launch

private val cream = Palette.cream
private val ink = Palette.ink

// ---------------------------------------------------------------- hero pieces

@Composable
fun OrbitPlanet(modifier: Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val c = center
            val dash = PathEffect.dashPathEffect(floatArrayOf(1.5f, 5f))
            drawCircle(Color(0x55EAD9C6), size.minDimension * 0.38f, c,
                style = Stroke(0.8.dp.toPx(), pathEffect = dash))
            drawCircle(Color(0x33EAD9C6), size.minDimension * 0.455f, c, style = Stroke(0.6.dp.toPx()))
            drawArc(
                Color(0x26EAD9C6), -40f, 200f, false,
                Offset(c.x - size.minDimension * 0.53f, c.y - size.minDimension * 0.53f),
                Size(size.minDimension * 1.06f, size.minDimension * 1.06f),
                style = Stroke(0.6.dp.toPx())
            )
            // satellite dots
            val r1 = size.minDimension * 0.455f
            val a1 = -0.35
            drawCircle(Color(0xFFF2E7DC), 1.8.dp.toPx(),
                Offset(c.x + (r1 * cos(a1)).toFloat(), c.y + (r1 * sin(a1)).toFloat()))
            val r2 = size.minDimension * 0.38f
            val a2 = 2.6
            drawCircle(Color(0xAAE09B7A), 1.2.dp.toPx(),
                Offset(c.x + (r2 * cos(a2)).toFloat(), c.y + (r2 * sin(a2)).toFloat()))
        }
        ShaderPanel(Shaders.PLANET, Modifier.size(94.dp))
    }
}

// ------------------------------------------------------------- label + cards

@Composable
fun FutureIsWarmCard(modifier: Modifier) {
    Box(
        modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Palette.terra)
            .padding(start = 16.dp, top = 14.dp)
    ) {
        Column {
            FidgetDotMatrixText("FUTURE\nIS\nWARM", pitch = 2.5.dp, color = Color(0xFFF6EBDD), lineGapRows = 2)
            Spacer(Modifier.height(7.dp))
            DotGlyph(listOf("110110", "011011"), pitch = 1.6.dp, color = Color(0x88F6EBDD))
        }
    }
}

@Composable
fun FeaturesCard(modifier: Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Palette.card)
            .padding(horizontal = 13.dp, vertical = 9.dp),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        FeatureRow("INTUITIVE LAYERS") { c ->
            for (i in 0..2) {
                val y = size.height * (0.30f + i * 0.20f)
                val p = Path().apply {
                    moveTo(size.width * 0.2f, y)
                    lineTo(size.width * 0.5f, y - size.height * 0.14f)
                    lineTo(size.width * 0.8f, y)
                }
                drawPath(p, c, style = Stroke(0.9.dp.toPx(), cap = StrokeCap.Round))
            }
        }
        FeatureRow("CONTEXT AWARE") { c ->
            drawCircle(c, size.minDimension * 0.34f, center, style = Stroke(0.9.dp.toPx()))
            drawCircle(c, size.minDimension * 0.10f, center)
        }
        FeatureRow("MINIMAL BY DESIGN") { c ->
            drawCircle(c, size.minDimension * 0.34f, center, style = Stroke(0.9.dp.toPx()))
            drawLine(c, Offset(center.x - size.width * 0.16f, center.y),
                Offset(center.x + size.width * 0.16f, center.y), 0.9.dp.toPx(), StrokeCap.Round)
        }
        FeatureRow("MADE TO FEEL") { c ->
            drawCircle(c, size.minDimension * 0.34f, center, style = Stroke(0.9.dp.toPx()))
            val p = Path().apply {
                moveTo(center.x - size.width * 0.16f, center.y)
                quadraticTo(center.x - size.width * 0.08f, center.y - size.height * 0.14f, center.x, center.y)
                quadraticTo(center.x + size.width * 0.08f, center.y + size.height * 0.14f,
                    center.x + size.width * 0.16f, center.y)
            }
            drawPath(p, c, style = Stroke(0.9.dp.toPx(), cap = StrokeCap.Round))
        }
    }
}

@Composable
private fun FeatureRow(text: String, icon: DrawScope.(Color) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Canvas(Modifier.size(11.dp)) { icon(Color(0xFFE8DFD2)) }
        Spacer(Modifier.width(9.dp))
        MonoText(text, 6.sp, Color(0xFFE8DFD2))
    }
}

// ------------------------------------------------------------------- row two

@Composable
fun BatteryCard(modifier: Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF121212))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row {
            Column {
                MonoText("BATTERY", 5.sp, Color(0xFF9A9186))
                Spacer(Modifier.height(1.dp))
                MonoText("76%", 10.5.sp, Color(0xFFF0E8DB), weight = FontWeight.SemiBold)
            }
            Spacer(Modifier.weight(1f))
            Canvas(Modifier.size(20.dp, 22.dp)) {
                val lit = listOf(
                    "0110", "1111", "1101", "1111", "1011", "1110", "0111"
                )
                val p = size.width / 4f
                lit.forEachIndexed { row, bits ->
                    bits.forEachIndexed { col, b ->
                        drawCircle(
                            if (b == '1') Color(0xFFE7C25B) else Color(0xFF3A3629),
                            1.2.dp.toPx(), Offset(col * p + p / 2f, row * (size.height / 7f) + 1.5.dp.toPx())
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        DotBar(0.76f, Modifier.fillMaxWidth().height(4.dp), count = 30,
            litColor = Color(0xFFEDE4D6), dimColor = Color(0xFF35322D))
        Spacer(Modifier.height(7.dp))
        MonoText("STORAGE", 5.sp, Color(0xFF9A9186))
        Spacer(Modifier.height(1.dp))
        MonoText("128 GB", 8.5.sp, Color(0xFFF0E8DB), weight = FontWeight.SemiBold)
        Spacer(Modifier.height(3.dp))
        DotBar(0.42f, Modifier.fillMaxWidth().height(4.dp), count = 30,
            litColor = Color(0xFFCBC2B4), dimColor = Color(0xFF35322D))
        Spacer(Modifier.height(7.dp))
        MonoText("RAM", 5.sp, Color(0xFF9A9186))
        Spacer(Modifier.height(1.dp))
        MonoText("8 GB", 8.5.sp, Color(0xFFF0E8DB), weight = FontWeight.SemiBold)
        Spacer(Modifier.height(3.dp))
        DotBar(0.30f, Modifier.fillMaxWidth().height(4.dp), count = 30,
            litColor = Color(0xFFCBC2B4), dimColor = Color(0xFF35322D))
    }
}

@Composable
fun EqualizerCard(modifier: Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Palette.cardLight)
            .border(0.6.dp, Color(0x14000000), RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        MonoText("SOUND\nEQUALIZER", 6.sp, Color(0xFF2A2620), lineHeight = 9.5.sp)
        Spacer(Modifier.weight(1f))
        Canvas(Modifier.fillMaxWidth().height(52.dp)) {
            val heights = listOf(5, 8, 4, 9, 6, 3, 7, 10, 5, 7, 4, 6)
            val cols = heights.size
            val stepX = size.width / cols
            val stepY = size.height / 11f
            heights.forEachIndexed { i, h ->
                val x = stepX * i + stepX / 2f
                for (j in 0 until h) {
                    val y = size.height - j * stepY - stepY / 2f
                    drawCircle(Color(0xFF2B2721), 1.3.dp.toPx(), Offset(x, y))
                }
                if (h >= 8) drawCircle(Palette.terra, 1.3.dp.toPx(),
                    Offset(x, size.height - h * stepY - stepY / 2f))
                // faint scatter above
                if (i % 3 == 0) drawCircle(Color(0x332B2721), 1.1.dp.toPx(),
                    Offset(x, size.height - (h + 2) * stepY))
            }
        }
        Spacer(Modifier.height(9.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            Chip("BASS", filled = true)
            Chip("MID", filled = false)
            Chip("TREBLE", filled = false)
        }
    }
}

@Composable
private fun Chip(text: String, filled: Boolean) {
    val shape = RoundedCornerShape(5.dp)
    Box(
        if (filled) Modifier.clip(shape).background(Palette.terra)
        else Modifier.clip(shape).border(0.7.dp, Color(0x552A2620), shape),
        contentAlignment = Alignment.Center
    ) {
        MonoText(
            text, 4.3.sp,
            if (filled) cream else Color(0xFF4A443C),
            spacingEm = 0.06f,
            modifier = Modifier.padding(horizontal = 4.5.dp, vertical = 3.dp)
        )
    }
}

/**
 * The clay volume knob. When [interactive], drag anywhere on it to rotate: the clay face
 * and indicator spin (clamped to ±135°) while the lighting stays fixed, with haptic
 * detents every 10 volume steps. Reports 0..100 through [onVolume].
 */
@Composable
fun KnobDial(
    modifier: Modifier,
    interactive: Boolean = false,
    onVolume: ((Int) -> Unit)? = null,
) {
    var angle by remember { mutableFloatStateOf(0f) }
    var lastDetent by remember { mutableIntStateOf(0) }
    val view = LocalView.current
    Box(modifier) {
        // drop shadow
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                Brush.radialGradient(
                    listOf(Color(0x40000000), Color.Transparent),
                    center = Offset(center.x, center.y + size.height * 0.10f),
                    radius = size.minDimension * 0.58f
                ),
                radius = size.minDimension * 0.58f,
                center = Offset(center.x, center.y + size.height * 0.10f)
            )
        }
        Box(
            Modifier
                .fillMaxSize()
                .padding(3.dp)
                .clip(CircleShape)
                .then(
                    if (interactive) Modifier.pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            val c = Offset(size.width / 2f, size.height / 2f)
                            val p0 = change.previousPosition - c
                            val p1 = change.position - c
                            var d = Math.toDegrees(
                                (atan2(p1.y, p1.x) - atan2(p0.y, p0.x)).toDouble()
                            ).toFloat()
                            if (d > 180f) d -= 360f
                            if (d < -180f) d += 360f
                            angle = (angle + d).coerceIn(-135f, 135f)
                            onVolume?.invoke(((angle + 135f) / 270f * 100f).roundToInt())
                            val detent = (angle / 27f).roundToInt()
                            if (detent != lastDetent) {
                                lastDetent = detent
                                view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                            }
                            change.consume()
                        }
                    } else Modifier
                )
        ) {
            // rotating clay face; oversized so corners never show while spinning
            Box(
                Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationZ = angle; scaleX = 1.45f; scaleY = 1.45f }
            ) {
                ShaderPanel(Shaders.CLAY, Modifier.fillMaxSize())
                Box(Modifier.fillMaxSize().background(Palette.terra.copy(alpha = 0.62f)))
            }
            // rotating indicator
            Canvas(Modifier.fillMaxSize().graphicsLayer { rotationZ = angle }) {
                drawLine(
                    Color(0xFFF3EBE0),
                    Offset(center.x - size.width * 0.14f, center.y),
                    Offset(center.x + size.width * 0.14f, center.y),
                    2.2.dp.toPx(), StrokeCap.Round
                )
            }
            // fixed lighting on top
            Canvas(Modifier.fillMaxSize()) {
                drawCircle(
                    Brush.radialGradient(
                        listOf(Color(0x00000000), Color(0x30000000), Color(0x66000000)),
                        center = Offset(size.width * 0.42f, size.height * 0.38f),
                        radius = size.minDimension * 0.72f
                    ),
                    radius = size.minDimension, center = center
                )
                drawArc(
                    Color(0x66FFE8D5), -160f, 140f, false,
                    Offset(1.dp.toPx(), 1.dp.toPx()),
                    Size(size.width - 2.dp.toPx(), size.height - 2.dp.toPx()),
                    style = Stroke(1.2.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }
    }
}

@Composable
fun TogglesRow(modifier: Modifier) {
    Row(modifier, horizontalArrangement = Arrangement.SpaceBetween) {
        ToggleCircle(
            initial = true, activeBg = Color(0xFFE6DACA), activeIcon = Color(0xFF2A2620)
        ) { ic, _ -> drawPlane(ic) }
        ToggleCircle(
            initial = true, activeBg = Palette.terra, activeIcon = cream
        ) { ic, _ -> drawSun(ic, 1f) }
        ToggleCircle(
            initial = false, activeBg = Color(0xFFF3EBE2), activeIcon = Color(0xFF2A2620)
        ) { ic, bg ->
            drawCircle(ic, size.minDimension * 0.40f, center)
            drawCircle(
                bg, size.minDimension * 0.38f,
                Offset(center.x + size.minDimension * 0.26f, center.y - size.minDimension * 0.14f)
            )
        }
        ToggleCircle(
            initial = false, activeBg = Palette.terra, activeIcon = cream
        ) { ic, _ ->
            val p = size.minDimension / 3.4f
            for (r in 0..2) for (c in 0..2) {
                drawCircle(ic, 0.9.dp.toPx(),
                    Offset(center.x + (c - 1) * p, center.y + (r - 1) * p))
            }
        }
    }
}

@Composable
private fun ToggleCircle(
    initial: Boolean,
    activeBg: Color,
    activeIcon: Color,
    inactiveBg: Color = Color(0xFF161616),
    inactiveIcon: Color = cream,
    icon: DrawScope.(iconColor: Color, bgColor: Color) -> Unit,
) {
    var on by remember { mutableStateOf(initial) }
    val bg by animateColorAsState(if (on) activeBg else inactiveBg, tween(260), label = "toggleBg")
    val ic by animateColorAsState(if (on) activeIcon else inactiveIcon, tween(260), label = "toggleIc")
    val press = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()
    val view = LocalView.current
    Box(
        Modifier
            .size(27.dp)
            .graphicsLayer { scaleX = press.value; scaleY = press.value }
            .clip(CircleShape)
            .background(bg)
            .clickable(interactionSource = null, indication = null) {
                on = !on
                view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                scope.launch {
                    press.snapTo(0.82f)
                    press.animateTo(1f, spring(dampingRatio = 0.35f, stiffness = 600f))
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.size(12.dp)) { icon(ic, bg) }
    }
}

@Composable
fun BrightnessBar(modifier: Modifier) {
    var level by remember { mutableFloatStateOf(0.62f) }
    var auto by remember { mutableStateOf(true) }
    val view = LocalView.current
    Row(
        modifier
            .clip(RoundedCornerShape(50))
            .background(Palette.card)
            .padding(horizontal = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Canvas(Modifier.size(9.dp)) {
            drawSun(Color(0xFFE8DFD2).copy(alpha = 0.45f + 0.55f * level), 1f)
        }
        Spacer(Modifier.width(10.dp))
        DotBar(
            level,
            Modifier
                .weight(1f)
                .height(20.dp)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { change, _ ->
                        auto = false
                        level = (change.position.x / size.width).coerceIn(0f, 1f)
                        change.consume()
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures { o ->
                        auto = false
                        level = (o.x / size.width).coerceIn(0f, 1f)
                        view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                    }
                },
            count = 20,
            litColor = Color(0xFFEDE4D6), dimColor = Color(0xFF3B3733), fade = true
        )
        Spacer(Modifier.width(10.dp))
        MonoText(
            "AUTO", 5.8.sp,
            if (auto) Palette.terra else Color(0xFF6A645C),
            weight = FontWeight.SemiBold,
            modifier = Modifier.clickable(interactionSource = null, indication = null) {
                auto = !auto
                if (auto) level = 0.62f
                view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
            }
        )
    }
}

// ----------------------------------------------------------------- row three

@Composable
fun PaletteCard(modifier: Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFF8F3EA))
            .border(0.6.dp, Color(0x12000000), RoundedCornerShape(10.dp))
            .padding(9.dp)
    ) {
        MonoText("COLOR PALETTE", 5.6.sp, Color(0xFF2A2620))
        Spacer(Modifier.height(8.dp))
        val entries = listOf(
            Color(0xFFC75A39) to "#C75A39",
            Color(0xFFE09B7A) to "#E09B7A",
            Color(0xFFEAD9C6) to "#EAD9C6",
            Color(0xFFF7F4F1) to "#F7F4F1",
            Color(0xFF111112) to "#111112",
        )
        entries.forEach { (c, hex) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(48.dp, 15.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(c)
                        .border(0.5.dp, Color(0x14000000), RoundedCornerShape(7.dp))
                )
                Spacer(Modifier.width(7.dp))
                MonoText(hex, 4.8.sp, Color(0xFF6A6157))
            }
            Spacer(Modifier.height(7.dp))
        }
    }
}

@Composable
fun MaterialTextureCard(modifier: Modifier) {
    Column(modifier) {
        MonoText("MATERIAL & TEXTURE", 5.6.sp, Color(0xFF2A2620))
        Spacer(Modifier.height(8.dp))
        val labels = listOf("CLAY\nTEXTURE", "TRANSPARENT\nTECH", "MICRO\nPERFORATION", "ANODIZED\nMETAL")
        Row {
            Column(Modifier.clip(RoundedCornerShape(8.dp))) {
                Box(Modifier.size(76.dp, 30.dp)) { ShaderPanel(Shaders.CLAY, Modifier.fillMaxSize()) }
                Box(Modifier.size(76.dp, 30.dp)) { ShaderPanel(Shaders.TECH, Modifier.fillMaxSize()) }
                Box(Modifier.size(76.dp, 30.dp)) { PerforationTile(Modifier.fillMaxSize()) }
                Box(Modifier.size(76.dp, 30.dp)) { ShaderPanel(Shaders.ANODIZED, Modifier.fillMaxSize()) }
            }
            Spacer(Modifier.width(8.dp))
            Column {
                labels.forEach { label ->
                    Box(Modifier.height(30.dp), contentAlignment = Alignment.CenterStart) {
                        MonoText(label, 4.8.sp, Color(0xFF4A443C), lineHeight = 7.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun PerforationTile(modifier: Modifier) {
    Canvas(modifier.background(Color(0xFFD9D3CA))) {
        drawRect(
            Brush.verticalGradient(listOf(Color(0xFFDFD9D0), Color(0xFFCEC8BF)))
        )
        val p = 4.dp.toPx()
        var y = p / 2f
        while (y < size.height) {
            var x = p / 2f
            while (x < size.width) {
                drawCircle(Color(0xFF9B958C), 1.dp.toPx(), Offset(x, y))
                x += p
            }
            y += p
        }
    }
}

@Composable
fun TypographyCard(modifier: Modifier) {
    Box(
        modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF121212))
            .padding(12.dp)
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val c = Color(0x66E8DFD2)
            val l = 6.dp.toPx()
            val w = 0.8.dp.toPx()
            // corner brackets
            listOf(
                Offset(0f, 0f) to Pair(Offset(l, 0f), Offset(0f, l)),
                Offset(size.width, 0f) to Pair(Offset(size.width - l, 0f), Offset(size.width, l)),
                Offset(0f, size.height) to Pair(Offset(l, size.height), Offset(0f, size.height - l)),
                Offset(size.width, size.height) to
                    Pair(Offset(size.width - l, size.height), Offset(size.width, size.height - l)),
            ).forEach { (corner, arms) ->
                drawLine(c, corner, arms.first, w)
                drawLine(c, corner, arms.second, w)
            }
        }
        Column(Modifier.padding(6.dp)) {
            MonoText("TYPOGRAPHY", 5.6.sp, Color(0xFFB8AFA2))
            Spacer(Modifier.height(9.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                DotMatrixText("A", pitch = 4.6.dp, color = Color(0xFFEDE4D6))
                Spacer(Modifier.width(12.dp))
                DotMatrixText("a", pitch = 4.6.dp, color = Color(0xFFEDE4D6))
            }
            Spacer(Modifier.height(11.dp))
            DotMatrixText("ABCDEFGHIJKLMN", pitch = 1.55.dp, color = Color(0xFFDDD3C4))
            Spacer(Modifier.height(3.dp))
            DotMatrixText("OPQRSTUV WXYZ", pitch = 1.55.dp, color = Color(0xFFDDD3C4))
            Spacer(Modifier.height(3.dp))
            DotMatrixText("0123456789", pitch = 1.55.dp, color = Color(0xFFDDD3C4))
            Spacer(Modifier.weight(1f))
            MonoText("DOT MATRIX REGULAR", 5.2.sp, Color(0xFF8F867A))
        }
    }
}

// ------------------------------------------------------------------ row four

@Composable
fun PlayerCard(modifier: Modifier) {
    Column(modifier.clip(RoundedCornerShape(12.dp))) {
        ShaderPanel(Shaders.MARS, Modifier.fillMaxWidth().height(62.dp))
        Column(
            Modifier
                .fillMaxSize()
                .background(Color(0xFF87381F))
                .padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            MonoText("AMBIENT CLAY", 6.8.sp, Color(0xFFF3EAD9), weight = FontWeight.SemiBold)
            Spacer(Modifier.height(1.dp))
            MonoText("SOUND SCAPE", 4.4.sp, Color(0xAAF3EAD9))
            Spacer(Modifier.height(7.dp))
            DotBar(
                0.42f, Modifier.fillMaxWidth().height(4.dp), count = 26,
                litColor = Color(0xFFF3EAD9), dimColor = Color(0x55F3EAD9), dotRadius = 1.dp
            )
            Spacer(Modifier.height(2.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                MonoText("02:15", 4.2.sp, Color(0x99F3EAD9))
                MonoText("05:10", 4.2.sp, Color(0x99F3EAD9))
            }
            Spacer(Modifier.weight(1f))
            Row(
                Modifier.fillMaxWidth().padding(bottom = 2.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Canvas(Modifier.size(9.dp)) { drawSkip(Color(0xFFF3EAD9), forward = false) }
                Spacer(Modifier.width(9.dp))
                Box(
                    Modifier.size(23.dp).clip(CircleShape).background(Color(0xFFF3EAD9)),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(Modifier.size(9.dp)) { drawPlayTriangle(Color(0xFF3A1A0E)) }
                }
                Spacer(Modifier.width(9.dp))
                Canvas(Modifier.size(9.dp)) { drawSkip(Color(0xFFF3EAD9), forward = true) }
            }
        }
    }
}

@Composable
fun SmallTiles(modifier: Modifier, time: Float) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(5.dp)) {
        SmallTile {
            Canvas(Modifier.size(18.dp)) {
                rotate(time * 90f) {
                    for (i in 0 until 10) {
                        val a = i / 10f * 2 * Math.PI
                        val alpha = 0.15f + 0.85f * (i / 10f)
                        drawCircle(
                            Color(0xFF2B2721).copy(alpha = alpha), (0.8f + i * 0.06f).dp.toPx(),
                            Offset(
                                center.x + (size.minDimension * 0.42f * cos(a)).toFloat(),
                                center.y + (size.minDimension * 0.42f * sin(a)).toFloat()
                            )
                        )
                    }
                }
            }
        }
        SmallTile {
            DotGlyph(
                listOf("0011100", "0111110", "1111111", "0000000", "0101010", "1010101"),
                pitch = 2.2.dp, color = Color(0xFF2B2721)
            )
        }
        SmallTile {
            DotMatrixText("21°", pitch = 1.9.dp, color = Palette.terra)
        }
        SmallTile {
            DotGlyph(
                listOf(
                    "0001000", "0001000", "0001000", "1110111", "0001000", "0001000", "0001000"
                ),
                pitch = 2.2.dp, color = Color(0xFF2B2721)
            )
        }
    }
}

@Composable
private fun SmallTile(content: @Composable () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(31.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF2ECE1))
            .border(0.5.dp, Color(0x10000000), RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) { content() }
}

@Composable
fun MicroInteractionsCard(modifier: Modifier, time: Float) {
    Column(
        modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Palette.terra)
            .padding(12.dp)
    ) {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            MonoText("MICRO INTERACTIONS", 6.sp, Color(0xFFF6EBDD))
        }
        Spacer(Modifier.height(4.dp))
        Row(Modifier.weight(1f)) {
            MicroCell("TAP", Modifier.weight(1f)) { tapAnim(time) }
            MicroCell("FOCUS", Modifier.weight(1f)) { focusAnim(time) }
        }
        Row(Modifier.weight(1f)) {
            MicroCell("LOADING", Modifier.weight(1f)) { loadingAnim(time) }
            MicroCell("COMPLETE", Modifier.weight(1f)) { completeAnim() }
        }
    }
}

@Composable
private fun MicroCell(label: String, modifier: Modifier, anim: DrawScope.() -> Unit) {
    Column(modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            Canvas(Modifier.size(42.dp)) { anim() }
        }
        MonoText(label, 4.8.sp, Color(0xFFF6EBDD))
        Spacer(Modifier.height(4.dp))
    }
}

private val microCream = Color(0xFFF6EBDD)

private fun DrawScope.tapAnim(time: Float) {
    val phase = (time % 1.6f) / 1.6f
    for (ring in 0..2) {
        val r = size.minDimension * (0.14f + ring * 0.14f)
        drawDottedCircle(microCream.copy(alpha = 0.55f - ring * 0.14f), r, 14 + ring * 6)
    }
    // expanding pulse
    val pr = size.minDimension * (0.14f + phase * 0.36f)
    drawCircle(microCream.copy(alpha = (1f - phase) * 0.5f), pr, center, style = Stroke(1.dp.toPx()))
    drawCircle(microCream, size.minDimension * 0.10f, center)
}

private fun DrawScope.focusAnim(time: Float) {
    drawCircle(microCream.copy(alpha = 0.8f), size.minDimension * 0.40f, center,
        style = Stroke(0.8.dp.toPx()))
    drawCircle(microCream, size.minDimension * 0.06f, center)
    val a = time * 1.6
    drawCircle(
        microCream, 2.2.dp.toPx(),
        Offset(
            center.x + (size.minDimension * 0.40f * cos(a)).toFloat(),
            center.y + (size.minDimension * 0.40f * sin(a)).toFloat()
        )
    )
}

private fun DrawScope.loadingAnim(time: Float) {
    val n = 16
    for (i in 0 until n) {
        val a = i / n.toFloat() * 2 * Math.PI
        val head = ((time * 1.2f) % 1f) * n
        val d = ((i - head + n) % n) / n.toFloat()
        drawCircle(
            microCream.copy(alpha = 0.15f + 0.85f * (1f - d)), 1.6.dp.toPx(),
            Offset(
                center.x + (size.minDimension * 0.40f * cos(a)).toFloat(),
                center.y + (size.minDimension * 0.40f * sin(a)).toFloat()
            )
        )
    }
}

private fun DrawScope.completeAnim() {
    drawCircle(microCream.copy(alpha = 0.9f), size.minDimension * 0.40f, center,
        style = Stroke(1.dp.toPx()))
    val p = Path().apply {
        moveTo(center.x - size.width * 0.13f, center.y + size.height * 0.01f)
        lineTo(center.x - size.width * 0.03f, center.y + size.height * 0.11f)
        lineTo(center.x + size.width * 0.15f, center.y - size.height * 0.10f)
    }
    drawPath(p, microCream, style = Stroke(1.4.dp.toPx(), cap = StrokeCap.Round))
    drawCircle(microCream, 1.8.dp.toPx(),
        Offset(center.x + size.width * 0.36f, center.y - size.height * 0.36f))
}

private fun DrawScope.drawDottedCircle(color: Color, radius: Float, count: Int) {
    for (i in 0 until count) {
        val a = i / count.toFloat() * 2 * Math.PI
        drawCircle(
            color, 1.dp.toPx(),
            Offset(center.x + (radius * cos(a)).toFloat(), center.y + (radius * sin(a)).toFloat())
        )
    }
}

