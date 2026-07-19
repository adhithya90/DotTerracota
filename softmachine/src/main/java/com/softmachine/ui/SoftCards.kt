package com.softmachine.ui

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.designlens.core.ShaderPanel
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.coroutines.launch

// ------------------------------------------------------------------- hero

/** Grey clover/flower mark from the masthead. */
@Composable
fun SoftFlower(modifier: Modifier, color: Color = Color(0xFFB9B3A8)) {
    Canvas(modifier) {
        val c = center
        val r = size.minDimension * 0.20f
        for (i in 0 until 6) {
            val a = i * PI / 3
            drawCircle(
                color, r,
                Offset(c.x + (r * 1.25f * cos(a)).toFloat(), c.y + (r * 1.25f * sin(a)).toFloat())
            )
        }
        drawCircle(color, r * 0.9f, c)
    }
}

@Composable
fun CloudCard(modifier: Modifier) {
    val pulse = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val view = LocalView.current
    SoftPanel(modifier, corner = 26.dp) {
        Box(
            Modifier.fillMaxSize().pointerInput(Unit) {
                detectTapGestures {
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                    scope.launch {
                        pulse.snapTo(1f)
                        pulse.animateTo(0f, spring(dampingRatio = 0.16f, stiffness = 160f))
                    }
                }
            }
        ) {
            ShaderPanel(SoftShaders.PUFFY_CLOUD, Modifier.fillMaxSize(), animated = true) {
                setFloatUniform("uPulse", pulse.value)
            }
            Column(
                Modifier.align(Alignment.BottomCenter).padding(bottom = 13.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                EngravedLabel("INFLATE / SOFTEN / CONNECT", 6.5.sp)
                Spacer(Modifier.height(8.dp))
                DrilledDots(Modifier.size(76.dp, 5.dp))
            }
        }
    }
}

/** Text stamped into the surface: a light bottom-offset copy plays the lit lower edge. */
@Composable
fun EngravedLabel(text: String, size: androidx.compose.ui.unit.TextUnit) {
    Box {
        SoftText(
            text, size, Color(0xB8F6F1ED),
            weight = FontWeight.Medium, spacingEm = 0.22f,
            modifier = Modifier.padding(top = 1.2.dp)
        )
        SoftText(text, size, Color(0xFF83807A), weight = FontWeight.Medium, spacingEm = 0.22f)
    }
}

/** A row of drilled pager holes; the first position carries a small metal ball. */
@Composable
fun DrilledDots(modifier: Modifier, count: Int = 12, active: Int = 0) {
    Canvas(modifier) {
        val step = size.width / count
        val r = 1.5.dp.toPx()
        for (i in 0 until count) {
            val c = Offset(step * i + step / 2f, size.height / 2f)
            if (i == active) {
                // metal ball: drop shadow, sphere gradient, offset highlight
                drawCircle(Color(0x33555049), r * 1.5f, Offset(c.x + r * 0.3f, c.y + r * 0.55f))
                drawCircle(
                    Brush.radialGradient(
                        listOf(Color(0xFFF7F4F0), Color(0xFFB1AAA3), Color(0xFF89837C)),
                        center = Offset(c.x - r * 0.5f, c.y - r * 0.6f), radius = r * 2.6f
                    ),
                    r * 1.45f, c
                )
                drawCircle(Color(0xE6FFFFFF), r * 0.4f, Offset(c.x - r * 0.45f, c.y - r * 0.5f))
            } else {
                // drilled pit: dark opening, lit lower rim
                drawCircle(Color(0xCCFFFFFF), r, Offset(c.x, c.y + 0.8.dp.toPx()))
                drawCircle(
                    Brush.radialGradient(
                        listOf(Color(0xFF96908A), Color(0xFFC6BFB8)),
                        center = Offset(c.x - r * 0.3f, c.y - r * 0.4f), radius = r * 1.4f
                    ),
                    r, c
                )
            }
        }
    }
}

// ------------------------------------------------------------------ brand

@Composable
fun SoftFeaturesCard(modifier: Modifier) {
    SoftPanel(modifier) {
        Column(
            Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            SoftFeatureRow("GENTLE HAPTICS") { c ->
                val s = size.minDimension
                for (i in 0 until 4) {
                    val a = i * PI / 2 + PI / 4
                    drawCircle(c, s * 0.16f,
                        Offset(center.x + (s * 0.20f * cos(a)).toFloat(), center.y + (s * 0.20f * sin(a)).toFloat()),
                        style = Stroke(1.dp.toPx()))
                }
            }
            SoftFeatureRow("FLUID TRANSITIONS") { c ->
                for (row in 0..1) {
                    val p = Path()
                    val y0 = size.height * (0.35f + row * 0.3f)
                    p.moveTo(0f, y0)
                    var x = 0f
                    while (x < size.width) {
                        p.quadraticTo(x + size.width * 0.125f, y0 + (if ((x / (size.width / 2)).toInt() % 2 == 0) -1 else 1) * size.height * 0.16f,
                            x + size.width * 0.25f, y0)
                        x += size.width * 0.25f
                    }
                    drawPath(p, c, style = Stroke(1.dp.toPx(), cap = StrokeCap.Round))
                }
            }
            SoftFeatureRow("NATURAL RHYTHM") { c ->
                drawCircle(c, size.minDimension * 0.36f, center, style = Stroke(1.dp.toPx()))
            }
            SoftFeatureRow("HUMAN CENTERED") { c ->
                val s = size.minDimension
                val p = Path().apply {
                    moveTo(s * 0.5f, s * 0.80f)
                    cubicTo(s * 0.05f, s * 0.48f, s * 0.22f, s * 0.12f, s * 0.5f, s * 0.34f)
                    cubicTo(s * 0.78f, s * 0.12f, s * 0.95f, s * 0.48f, s * 0.5f, s * 0.80f)
                    close()
                }
                drawPath(p, c, style = Stroke(1.dp.toPx(), cap = StrokeCap.Round))
            }
        }
    }
}

@Composable
private fun SoftFeatureRow(text: String, icon: DrawScope.(Color) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Canvas(Modifier.size(13.dp)) { icon(SoftColors.ink.copy(alpha = 0.65f)) }
        Spacer(Modifier.width(12.dp))
        SoftLabel(text, 7.sp, SoftColors.ink.copy(alpha = 0.75f))
    }
}

// ----------------------------------------------------------------- system

@Composable
fun SystemStatusCard(modifier: Modifier) {
    SoftPanel(modifier) {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            SoftLabel("SYSTEM STATUS", 6.5.sp)
            Spacer(Modifier.height(12.dp))
            StatusRow("BATTERY", "82%", 0.82f)
            Spacer(Modifier.height(12.dp))
            StatusRow("STORAGE", "128 GB", 0.56f)
            Spacer(Modifier.height(12.dp))
            StatusRow("MEMORY", "6.2 GB", 0.38f)
        }
    }
}

@Composable
private fun StatusRow(label: String, value: String, fill: Float) {
    Column {
        Row(Modifier.fillMaxWidth()) {
            SoftLabel(label, 6.sp)
            Spacer(Modifier.weight(1f))
            SoftText(value, 8.5.sp, SoftColors.ink, weight = FontWeight.Normal)
        }
        Spacer(Modifier.height(5.dp))
        SoftBar(fill, Modifier.fillMaxWidth().height(6.dp))
    }
}

/** Rounded inset track with a lavender fill. */
@Composable
fun SoftBar(fill: Float, modifier: Modifier) {
    Canvas(modifier) {
        val r = size.height / 2f
        drawRoundRect(Color(0xFFDCD4CE), cornerRadius = CornerRadius(r, r))
        drawRoundRect(
            Color(0x338A8274), size = Size(size.width, size.height * 0.4f),
            cornerRadius = CornerRadius(r, r)
        )
        if (fill > 0.02f) {
            drawRoundRect(
                Brush.horizontalGradient(listOf(SoftColors.lavenderPale, SoftColors.lavender)),
                size = Size(size.width * fill, size.height),
                cornerRadius = CornerRadius(r, r)
            )
        }
    }
}

@Composable
fun SoftButtonsRow(modifier: Modifier) {
    Row(modifier, horizontalArrangement = Arrangement.SpaceBetween) {
        SoftRoundButton { c ->
            drawLine(c, Offset(center.x, size.height * 0.2f), Offset(center.x, size.height * 0.8f), 1.2.dp.toPx(), StrokeCap.Round)
            drawLine(c, Offset(size.width * 0.2f, center.y), Offset(size.width * 0.8f, center.y), 1.2.dp.toPx(), StrokeCap.Round)
        }
        SoftRoundButton { c ->
            drawCircle(c, size.minDimension * 0.18f, center, style = Stroke(1.2.dp.toPx()))
            for (i in 0 until 8) {
                val a = i * PI / 4
                drawLine(c,
                    Offset(center.x + (size.minDimension * 0.30f * cos(a)).toFloat(), center.y + (size.minDimension * 0.30f * sin(a)).toFloat()),
                    Offset(center.x + (size.minDimension * 0.42f * cos(a)).toFloat(), center.y + (size.minDimension * 0.42f * sin(a)).toFloat()),
                    1.1.dp.toPx(), StrokeCap.Round)
            }
        }
        SoftRoundButton { c ->
            drawCircle(c, size.minDimension * 0.34f, center)
            drawCircle(SoftColors.panel, size.minDimension * 0.32f,
                Offset(center.x + size.minDimension * 0.22f, center.y - size.minDimension * 0.12f))
        }
        SoftRoundButton { c ->
            for (i in -1..1) drawCircle(c, 1.1.dp.toPx(), Offset(center.x + i * 4.dp.toPx(), center.y))
        }
    }
}

@Composable
private fun SoftRoundButton(icon: DrawScope.(Color) -> Unit) {
    val view = LocalView.current
    Box(
        Modifier
            .pressSquash(depth = 0.86f)
            .softShadow(corner = 50.dp, offset = 3.dp, blur = 8.dp)
            .size(27.dp)
            .clip(CircleShape)
            .background(Color(0xFFF0EBE6)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.size(13.dp)) { icon(SoftColors.ink.copy(alpha = 0.7f)) }
    }
}

@Composable
fun SoftBrightness(modifier: Modifier) {
    var level by remember { mutableFloatStateOf(0.55f) }
    var auto by remember { mutableStateOf(true) }
    val view = LocalView.current
    SoftPanel(modifier, corner = 50.dp, color = Color(0xFFEDE7E2)) {
        Row(
            Modifier.fillMaxSize().padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Canvas(Modifier.size(10.dp)) {
                val c = SoftColors.ink.copy(alpha = 0.35f + 0.45f * level)
                drawCircle(c, size.minDimension * 0.18f, center, style = Stroke(1.2.dp.toPx()))
                for (i in 0 until 8) {
                    val a = i * PI / 4
                    drawLine(c,
                        Offset(center.x + (size.minDimension * 0.30f * cos(a)).toFloat(), center.y + (size.minDimension * 0.30f * sin(a)).toFloat()),
                        Offset(center.x + (size.minDimension * 0.44f * cos(a)).toFloat(), center.y + (size.minDimension * 0.44f * sin(a)).toFloat()),
                        1.dp.toPx(), StrokeCap.Round)
                }
            }
            Spacer(Modifier.width(10.dp))
            Canvas(
                Modifier.weight(1f).height(20.dp)
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures { change, _ ->
                            auto = false
                            level = (change.position.x / size.width).coerceIn(0f, 1f)
                            change.consume()
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures {
                            auto = false
                            level = (it.x / size.width).coerceIn(0f, 1f)
                            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                        }
                    }
            ) {
                val n = 13
                val step = size.width / n
                for (i in 0 until n) {
                    val on = i < n * level
                    drawCircle(
                        if (on) SoftColors.dim.copy(alpha = 0.8f) else SoftColors.faint.copy(alpha = 0.45f),
                        1.5.dp.toPx(), Offset(step * i + step / 2f, size.height / 2f)
                    )
                }
            }
            Spacer(Modifier.width(10.dp))
            SoftLabel(
                "AUTO", 5.5.sp,
                if (auto) SoftColors.lavenderDeep else SoftColors.faint,
                modifier = Modifier.pointerInput(Unit) {
                    detectTapGestures {
                        auto = !auto
                        if (auto) level = 0.55f
                        view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                    }
                }
            )
        }
    }
}

// ------------------------------------------------------------------ sound

@Composable
fun SoftEqualizerCard(modifier: Modifier) {
    val levels = remember { mutableListOf(0.7f, 0.5f, 0.8f, 0.45f, 0.6f, 0.35f).map { mutableFloatStateOf(it) } }
    SoftPanel(modifier) {
        Column(Modifier.fillMaxSize().padding(horizontal = 11.dp, vertical = 14.dp)) {
            SoftLabel("EQUALIZER", 6.5.sp, modifier = Modifier.padding(start = 4.dp))
            Spacer(Modifier.height(12.dp))
            Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                levels.forEach { state ->
                    Canvas(
                        Modifier.width(16.dp).fillMaxHeight()
                            .pointerInput(Unit) {
                                detectVerticalDragGestures { change, _ ->
                                    state.floatValue = (1f - change.position.y / size.height).coerceIn(0.05f, 1f)
                                    change.consume()
                                }
                            }
                            .pointerInput(Unit) {
                                detectTapGestures {
                                    state.floatValue = (1f - it.y / size.height).coerceIn(0.05f, 1f)
                                }
                            }
                    ) {
                        val cx = size.width / 2f
                        val trackW = 3.dp.toPx()
                        drawRoundRect(
                            Color(0xFFDCD4CE), Offset(cx - trackW / 2, 0f),
                            Size(trackW, size.height), CornerRadius(trackW / 2)
                        )
                        val v = state.floatValue
                        val top = size.height * (1f - v)
                        drawRoundRect(
                            SoftColors.lavender, Offset(cx - 2.5.dp.toPx(), top),
                            Size(5.dp.toPx(), size.height - top), CornerRadius(2.5.dp.toPx())
                        )
                        drawCircle(Color.White, 3.4.dp.toPx(), Offset(cx, top + 3.dp.toPx()))
                        drawCircle(SoftColors.lavenderDeep, 2.dp.toPx(), Offset(cx, top + 3.dp.toPx()))
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                SoftChip("SOFT", true)
                SoftChip("BALANCE", false)
                SoftChip("FOCUS", false)
            }
        }
    }
}

@Composable
private fun SoftChip(text: String, filled: Boolean) {
    Box(
        if (filled) Modifier.clip(RoundedCornerShape(8.dp)).background(SoftColors.lavender)
        else Modifier.clip(RoundedCornerShape(8.dp)).background(Color(0xFFE2DBD4)),
        contentAlignment = Alignment.Center
    ) {
        SoftText(
            text, 4.8.sp,
            if (filled) Color(0xFF4E4658) else SoftColors.dim,
            weight = FontWeight.Medium, spacingEm = 0.03f,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.5.dp)
        )
    }
}

/** The big squishy lavender pebble. */
@Composable
fun PebbleButton(modifier: Modifier) {
    Box(
        modifier
            .pressSquash(depth = 0.82f)
            .softShadow(corner = 50.dp, offset = 5.dp, blur = 12.dp)
            .clip(RoundedCornerShape(50))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFFD4C8DA), Color(0xFFBDAEC8), Color(0xFFA190AC))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.fillMaxSize()) {
            // inner AO along the bottom edge
            drawOval(
                Brush.radialGradient(
                    listOf(Color.Transparent, Color(0x2E4E4658)),
                    center = Offset(center.x, size.height * 0.62f),
                    radius = size.width * 0.62f
                ),
                Offset(-size.width * 0.1f, size.height * 0.25f),
                Size(size.width * 1.2f, size.height * 0.85f)
            )
            // top sheen
            drawArc(
                Color(0x73FFFFFF), -160f, 140f, false,
                Offset(size.width * 0.10f, size.height * 0.08f),
                Size(size.width * 0.8f, size.height * 0.8f),
                style = Stroke(2.dp.toPx(), cap = StrokeCap.Round)
            )
            drawLine(
                Color(0xF7FFFFFF),
                Offset(center.x - size.width * 0.14f, center.y),
                Offset(center.x + size.width * 0.14f, center.y),
                2.6.dp.toPx(), StrokeCap.Round
            )
        }
    }
}

// ------------------------------------------------------------ color / type

@Composable
fun SoftPaletteCard(modifier: Modifier) {
    SoftPanel(modifier) {
        Column(Modifier.fillMaxSize().padding(14.dp)) {
            SoftLabel("COLOR PALETTE", 6.5.sp)
            Spacer(Modifier.height(10.dp))
            val entries = listOf(
                Color(0xFFD2BDDB) to "#DCCCF1",
                Color(0xFFF6D2BE) to "#FFDCC6",
                Color(0xFFE7E1DC) to "#E9E7E2",
                Color(0xFFF3EFEA) to "#F6F5F2",
                Color(0xFF3B3835) to "#3A3A3A",
            )
            entries.forEach { (c, hex) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .softShadow(corner = 9.dp, offset = 2.dp, blur = 5.dp)
                            .size(38.dp, 13.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(c)
                    )
                    Spacer(Modifier.width(8.dp))
                    SoftText(hex, 5.sp, SoftColors.dim, spacingEm = 0f)
                }
                Spacer(Modifier.height(9.dp))
            }
        }
    }
}

@Composable
fun SoftTypographyCard(modifier: Modifier) {
    SoftPanel(modifier) {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            SoftLabel("TYPOGRAPHY", 6.5.sp)
            Spacer(Modifier.height(6.dp))
            SoftText("Aa", 26.sp, SoftColors.lavenderDeep, weight = FontWeight.Light)
            Spacer(Modifier.height(6.dp))
            SoftText(
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
                7.sp, SoftColors.ink.copy(alpha = 0.8f), spacingEm = 0.06f, lineHeight = 10.5.sp
            )
            Spacer(Modifier.height(3.dp))
            SoftText(
                "abcdefghijklmnopqrstuvwxyz",
                7.sp, SoftColors.ink.copy(alpha = 0.65f), spacingEm = 0.06f, lineHeight = 10.5.sp
            )
            Spacer(Modifier.height(3.dp))
            SoftText("0123456789", 7.sp, SoftColors.ink.copy(alpha = 0.65f), spacingEm = 0.06f)
            Spacer(Modifier.weight(1f))
            SoftLabel("TYPE: SOFT NEUE REGULAR", 6.sp, SoftColors.faint)
        }
    }
}

// ----------------------------------------------------------------- player

@Composable
fun SoftPlayerCard(modifier: Modifier) {
    SoftPanel(modifier, corner = 18.dp) {
        Column(Modifier.fillMaxSize()) {
            Box(Modifier.fillMaxWidth().weight(1f)) {
                ShaderPanel(SoftShaders.CLOUDSCAPE, Modifier.fillMaxSize(), animated = true)
                Column(Modifier.align(Alignment.BottomStart).padding(12.dp)) {
                    SoftText("AMBIENT CLOUD", 8.5.sp, Color.White, weight = FontWeight.Medium, spacingEm = 0.12f)
                    SoftLabel("FLOATING POINTS", 5.5.sp, Color(0xCCFFFFFF))
                }
            }
            Column(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 9.dp)) {
                Canvas(Modifier.fillMaxWidth().height(5.dp)) {
                    val y = size.height / 2f
                    drawLine(Color(0xFFDCD4CE), Offset(0f, y), Offset(size.width, y), 2.dp.toPx(), StrokeCap.Round)
                    drawLine(SoftColors.lavender, Offset(0f, y), Offset(size.width * 0.27f, y), 2.dp.toPx(), StrokeCap.Round)
                    drawCircle(Color.White, 2.6.dp.toPx(), Offset(size.width * 0.27f, y))
                    drawCircle(SoftColors.lavenderDeep, 1.5.dp.toPx(), Offset(size.width * 0.27f, y))
                }
                Row(Modifier.fillMaxWidth()) {
                    SoftText("0:58", 5.5.sp, SoftColors.dim)
                    Spacer(Modifier.weight(1f))
                    SoftText("3:45", 5.5.sp, SoftColors.dim)
                }
                Spacer(Modifier.height(6.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Canvas(Modifier.size(9.dp)) { drawSkipSoft(SoftColors.ink.copy(alpha = 0.75f), false) }
                    Spacer(Modifier.width(7.dp))
                    Box(
                        Modifier
                            .pressSquash(depth = 0.85f)
                            .softShadow(corner = 50.dp, offset = 3.dp, blur = 8.dp)
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(SoftColors.lavender),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(Modifier.size(8.dp)) {
                            val c = Color(0xFF4E4658)
                            val w = 2.dp.toPx()
                            drawRoundRect(c, Offset(size.width * 0.20f, 0f), Size(w, size.height), CornerRadius(w / 2))
                            drawRoundRect(c, Offset(size.width * 0.62f, 0f), Size(w, size.height), CornerRadius(w / 2))
                        }
                    }
                    Spacer(Modifier.width(7.dp))
                    Canvas(Modifier.size(9.dp)) { drawSkipSoft(SoftColors.ink.copy(alpha = 0.75f), true) }
                }
                Spacer(Modifier.height(3.dp))
            }
        }
    }
}

@Composable
fun SoftTiles(modifier: Modifier, time: Float) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SoftTile {
            Canvas(Modifier.size(16.dp)) {
                val c = SoftColors.ink.copy(alpha = 0.6f)
                for (row in 0..2) {
                    val p = Path()
                    val y0 = size.height * (0.25f + row * 0.25f)
                    p.moveTo(0f, y0)
                    p.quadraticTo(size.width * 0.25f, y0 - size.height * 0.12f, size.width * 0.5f, y0)
                    p.quadraticTo(size.width * 0.75f, y0 + size.height * 0.12f, size.width, y0)
                    drawPath(p, c, style = Stroke(1.dp.toPx(), cap = StrokeCap.Round))
                }
            }
        }
        SoftTile { SoftText("23°", 10.sp, SoftColors.ink.copy(alpha = 0.75f), weight = FontWeight.Light) }
        SoftTile {
            Canvas(Modifier.size(16.dp)) {
                val c = SoftColors.ink.copy(alpha = 0.6f)
                drawCircle(c, size.minDimension * 0.28f, center, style = Stroke(1.dp.toPx()))
                drawCircle(c, 1.dp.toPx(), Offset(center.x, center.y - size.minDimension * 0.42f))
                drawLine(c, Offset(center.x, center.y + size.minDimension * 0.28f),
                    Offset(center.x, center.y + size.minDimension * 0.46f), 1.dp.toPx(), StrokeCap.Round)
            }
        }
        SoftTile {
            Canvas(Modifier.size(16.dp)) {
                val n = 12
                for (i in 0 until n) {
                    val a = i / n.toFloat() * 2 * PI
                    val head = ((time * 0.9f) % 1f) * n
                    val d = ((i - head + n) % n) / n.toFloat()
                    drawCircle(
                        SoftColors.dim.copy(alpha = 0.15f + 0.7f * (1f - d)), 1.2.dp.toPx(),
                        Offset(
                            center.x + (size.minDimension * 0.40f * cos(a)).toFloat(),
                            center.y + (size.minDimension * 0.40f * sin(a)).toFloat()
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun SoftTile(content: @Composable () -> Unit) {
    SoftPanel(Modifier.fillMaxWidth().height(32.dp), corner = 12.dp) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { content() }
    }
}

// ----------------------------------------------------------------- motion

@Composable
fun SoftMicroCard(modifier: Modifier, time: Float) {
    SoftPanel(modifier) {
        Column(Modifier.fillMaxSize().padding(14.dp)) {
            SoftLabel("MICRO INTERACTIONS", 6.5.sp)
            Spacer(Modifier.height(6.dp))
            Row(Modifier.weight(1f).fillMaxWidth()) {
                MicroCell("PULSE", Modifier.weight(1f)) { PulseBall(time) }
                MicroCell("BOUNCE", Modifier.weight(1f)) { BounceBall() }
                MicroCell("SETTLE", Modifier.weight(1f)) { SettleCheck() }
            }
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier.fillMaxWidth()
                    .softShadow(corner = 50.dp, offset = 2.dp, blur = 6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFE8E2DC))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SoftLabel("LOADING", 6.sp)
                Spacer(Modifier.width(12.dp))
                Canvas(Modifier.weight(1f).height(5.dp)) {
                    val r = size.height / 2f
                    drawRoundRect(Color(0xFFDCD4CE), cornerRadius = CornerRadius(r, r))
                    val w = size.width * 0.35f
                    val x = ((time * 0.45f) % 1.3f - 0.15f) * size.width
                    drawRoundRect(
                        Brush.horizontalGradient(
                            listOf(Color(0x00C9BCE8), SoftColors.lavender, Color(0x00C9BCE8)),
                            startX = x, endX = x + w
                        ),
                        topLeft = Offset(0f, 0f), size = size, cornerRadius = CornerRadius(r, r)
                    )
                }
            }
        }
    }
}

@Composable
private fun MicroCell(label: String, modifier: Modifier, content: @Composable () -> Unit) {
    Column(modifier.fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally) {
        SoftLabel(label, 5.5.sp, SoftColors.dim)
        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) { content() }
    }
}

@Composable
private fun PulseBall(time: Float) {
    Canvas(Modifier.size(44.dp)) {
        val phase = (time % 1.8f) / 1.8f
        drawCircle(
            SoftColors.lavender.copy(alpha = (1f - phase) * 0.4f),
            size.minDimension * (0.18f + phase * 0.30f), center,
            style = Stroke(1.2.dp.toPx())
        )
        val phase2 = ((time + 0.9f) % 1.8f) / 1.8f
        drawCircle(
            SoftColors.lavender.copy(alpha = (1f - phase2) * 0.4f),
            size.minDimension * (0.18f + phase2 * 0.30f), center,
            style = Stroke(1.2.dp.toPx())
        )
        softBall(center, size.minDimension * 0.17f)
    }
}

@Composable
private fun BounceBall() {
    val y = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val view = LocalView.current
    Canvas(
        Modifier.size(44.dp).pointerInput(Unit) {
            detectTapGestures {
                view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                scope.launch {
                    y.snapTo(-1f)
                    y.animateTo(0f, spring(dampingRatio = 0.28f, stiffness = 220f))
                }
            }
        }
    ) {
        val travel = size.height * 0.28f
        val by = center.y + y.value * travel
        val squash = (1f + (y.value * 0.4f).coerceIn(-0.25f, 0.25f))
        // ground shadow
        drawOval(
            Color(0x2E887A72),
            Offset(center.x - size.width * 0.18f / squash, size.height * 0.82f),
            Size(size.width * 0.36f / squash, size.height * 0.08f)
        )
        softBall(Offset(center.x, by), size.minDimension * 0.17f, squashY = squash)
    }
}

@Composable
private fun SettleCheck() {
    Canvas(Modifier.size(44.dp)) {
        drawCircle(Color(0xFFE8E2DC), size.minDimension * 0.34f, center)
        drawCircle(Color(0x2E887A72), size.minDimension * 0.34f, center, style = Stroke(1.dp.toPx()))
        val c = SoftColors.lavenderDeep
        val p = Path().apply {
            moveTo(center.x - size.width * 0.12f, center.y + size.height * 0.01f)
            lineTo(center.x - size.width * 0.02f, center.y + size.height * 0.10f)
            lineTo(center.x + size.width * 0.14f, center.y - size.height * 0.09f)
        }
        drawPath(p, c, style = Stroke(2.dp.toPx(), cap = StrokeCap.Round))
    }
}

private fun DrawScope.softBall(c: Offset, r: Float, squashY: Float = 1f) {
    drawOval(
        Brush.radialGradient(
            listOf(Color(0xFFDCD0E4), Color(0xFFB7A8C6), Color(0xFF94859F)),
            center = Offset(c.x - r * 0.3f, c.y - r * 0.4f), radius = r * 2.2f
        ),
        Offset(c.x - r, c.y - r * squashY),
        Size(r * 2, r * 2 * squashY)
    )
}
