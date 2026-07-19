package com.dotterracota.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

private val creamUI = Color(0xFFF3EBE2)
private val glassDark = Color(0xCC2A150D)

@Composable
fun LockScreen(modifier: Modifier = Modifier, topPad: androidx.compose.ui.unit.Dp = 0.dp) {
    Box(modifier) {
        ShaderPanel(Shaders.WALLPAPER, Modifier.fillMaxSize())
        Column(Modifier.fillMaxSize().padding(horizontal = 10.dp)) {
            Spacer(Modifier.height(topPad))
            Spacer(Modifier.height(8.dp))
            StatusRow()
            Spacer(Modifier.height(22.dp))
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                DotMatrixText(
                    "09\n30",
                    pitch = 3.6.dp,
                    color = creamUI,
                    lineGapRows = 2,
                    glow = true,
                )
            }
            Spacer(Modifier.weight(1f))
            Row(Modifier.fillMaxWidth().height(56.dp), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                WeatherWidget(Modifier.weight(1f).fillMaxSize())
                MoodWidget(Modifier.weight(1f).fillMaxSize())
            }
            Spacer(Modifier.height(7.dp))
            MediaCard()
            Spacer(Modifier.height(8.dp))
            QuickActions()
            Spacer(Modifier.height(9.dp))
            Box(
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(38.dp, 3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(creamUI.copy(alpha = 0.9f))
            )
            Spacer(Modifier.height(5.dp))
        }
    }
}

@Composable
private fun StatusRow() {
    Row(Modifier.fillMaxWidth().padding(top = 4.dp)) {
        Column {
            MonoText("TUE, 21 MAY", 5.sp, creamUI, weight = FontWeight.SemiBold)
            Spacer(Modifier.height(3.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Canvas(Modifier.size(6.dp)) { drawSun(creamUI, 0.9f) }
                Spacer(Modifier.width(3.dp))
                MonoText("24°", 5.sp, creamUI)
            }
        }
        Spacer(Modifier.weight(1f))
        Canvas(Modifier.size(24.dp, 6.dp).padding(top = 1.dp)) {
            val c = creamUI
            // signal triangle
            val p = Path().apply {
                moveTo(0f, size.height)
                lineTo(size.height * 1.1f, size.height)
                lineTo(size.height * 1.1f, 0f)
                close()
            }
            drawPath(p, c)
            // wifi
            val wx = size.height * 1.9f
            drawArc(c, -135f, 90f, false, Offset(wx, 1f), Size(size.height, size.height),
                style = Stroke(1.1f, cap = StrokeCap.Round))
            drawCircle(c, 1f, Offset(wx + size.height / 2f, size.height * 0.85f))
            // battery
            drawRoundRect(c, Offset(size.width - size.height * 1.9f, 0.5f),
                Size(size.height * 1.7f, size.height - 1f), CornerRadius(1.5f, 1.5f))
        }
    }
}

@Composable
private fun WeatherWidget(modifier: Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xE0964225))
            .padding(8.dp)
    ) {
        Canvas(Modifier.size(11.dp)) { drawSun(creamUI, 1f) }
        Spacer(Modifier.weight(1f))
        MonoText("24°", 9.sp, creamUI, weight = FontWeight.SemiBold)
        MonoText("SUNNY", 4.sp, creamUI.copy(alpha = 0.75f))
    }
}

@Composable
private fun MoodWidget(modifier: Modifier) {
    Box(
        modifier
            .clip(RoundedCornerShape(12.dp))
            .background(glassDark),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val c = Offset(size.width / 2f, size.height / 2f)
            // radial dot spiral
            for (ring in 1..4) {
                val n = 6 + ring * 5
                val rad = ring * size.minDimension * 0.105f
                for (i in 0 until n) {
                    val a = i * (2 * Math.PI / n) + ring * 0.5
                    val alpha = 0.25f + 0.55f * ((i % n).toFloat() / n)
                    drawCircle(
                        creamUI.copy(alpha = alpha), 0.9.dp.toPx(),
                        Offset(c.x + (rad * cos(a)).toFloat(), c.y + (rad * sin(a)).toFloat())
                    )
                }
            }
            drawCircle(creamUI, 1.6.dp.toPx(), c)
        }
    }
}

@Composable
private fun MediaCard() {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(glassDark)
            .padding(horizontal = 9.dp, vertical = 7.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Palette.terra.copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(Modifier.size(8.dp)) {
                    val w = size.width
                    for (i in 0..3) {
                        val h = size.height * listOf(0.5f, 0.9f, 0.65f, 0.4f)[i]
                        drawLine(
                            creamUI, Offset(w * (0.125f + i * 0.25f), size.height),
                            Offset(w * (0.125f + i * 0.25f), size.height - h), 1.2f, StrokeCap.Round
                        )
                    }
                }
            }
            Spacer(Modifier.width(6.dp))
            Column {
                MonoText("CLAY", 5.5.sp, creamUI, weight = FontWeight.SemiBold)
                MonoText("CURRENT MOOD", 3.8.sp, creamUI.copy(alpha = 0.6f))
            }
            Spacer(Modifier.weight(1f))
            Canvas(Modifier.size(8.dp)) {
                drawArc(creamUI.copy(alpha = 0.8f), -60f, 120f, false,
                    Offset(-size.width * 0.6f, 0f), Size(size.width * 1.4f, size.height),
                    style = Stroke(1f, cap = StrokeCap.Round))
                drawCircle(creamUI.copy(alpha = 0.8f), 1.2f, Offset(size.width * 0.2f, size.height / 2f))
            }
        }
        Spacer(Modifier.height(7.dp))
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Canvas(Modifier.size(7.dp)) { drawSkip(creamUI, forward = false) }
            Canvas(Modifier.size(8.dp)) { drawPlayTriangle(creamUI) }
            Canvas(Modifier.size(7.dp)) { drawSkip(creamUI, forward = true) }
        }
    }
}

@Composable
private fun QuickActions() {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        QuickAction { drawPhoneIcon(creamUI) }
        QuickAction { drawChatIcon(creamUI) }
        QuickAction {
            drawCircle(creamUI, size.minDimension * 0.42f, center, style = Stroke(1.2.dp.toPx()))
            drawCircle(creamUI, size.minDimension * 0.14f, center)
        }
        QuickAction {
            for (i in -1..1) drawCircle(creamUI, 1.dp.toPx(),
                Offset(center.x + i * 3.5.dp.toPx(), center.y))
        }
    }
}

@Composable
private fun QuickAction(icon: DrawScope.() -> Unit) {
    Box(
        Modifier
            .size(25.dp)
            .clip(CircleShape)
            .background(glassDark),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.size(11.dp)) { icon() }
    }
}

// ---- tiny icon draw helpers (shared) ----

fun DrawScope.drawSun(color: Color, scale: Float) {
    val c = center
    val r = size.minDimension * 0.22f * scale
    drawCircle(color, r, c)
    for (i in 0 until 8) {
        val a = i * Math.PI / 4
        val r1 = size.minDimension * 0.34f * scale
        val r2 = size.minDimension * 0.48f * scale
        drawLine(
            color,
            Offset(c.x + (r1 * cos(a)).toFloat(), c.y + (r1 * sin(a)).toFloat()),
            Offset(c.x + (r2 * cos(a)).toFloat(), c.y + (r2 * sin(a)).toFloat()),
            size.minDimension * 0.07f, StrokeCap.Round
        )
    }
}

fun DrawScope.drawPlayTriangle(color: Color) {
    val p = Path().apply {
        moveTo(size.width * 0.22f, size.height * 0.1f)
        lineTo(size.width * 0.92f, size.height * 0.5f)
        lineTo(size.width * 0.22f, size.height * 0.9f)
        close()
    }
    drawPath(p, color)
}

fun DrawScope.drawSkip(color: Color, forward: Boolean) {
    val w = size.width
    val h = size.height
    val barW = w * 0.16f
    if (forward) {
        val tri = Path().apply {
            moveTo(0f, 0f); lineTo(w * 0.68f, h * 0.5f); lineTo(0f, h); close()
        }
        drawPath(tri, color)
        drawRoundRect(color, Offset(w - barW, 0f), Size(barW, h), CornerRadius(barW * 0.5f, barW * 0.5f))
    } else {
        val tri = Path().apply {
            moveTo(w, 0f); lineTo(w * 0.32f, h * 0.5f); lineTo(w, h); close()
        }
        drawPath(tri, color)
        drawRoundRect(color, Offset(0f, 0f), Size(barW, h), CornerRadius(barW * 0.5f, barW * 0.5f))
    }
}

fun DrawScope.drawPhoneIcon(color: Color) {
    val s = size.minDimension
    drawArc(
        color, 40f, 200f, false,
        Offset(s * 0.12f, s * 0.12f), Size(s * 0.76f, s * 0.76f),
        style = Stroke(s * 0.16f, cap = StrokeCap.Round)
    )
    drawCircle(color, s * 0.10f, Offset(s * 0.26f, s * 0.30f))
    drawCircle(color, s * 0.10f, Offset(s * 0.74f, s * 0.70f))
}

fun DrawScope.drawChatIcon(color: Color) {
    val s = size.minDimension
    drawRoundRect(
        color, Offset(s * 0.06f, s * 0.10f), Size(s * 0.88f, s * 0.62f),
        CornerRadius(s * 0.18f, s * 0.18f)
    )
    val tail = Path().apply {
        moveTo(s * 0.25f, s * 0.68f)
        lineTo(s * 0.25f, s * 0.95f)
        lineTo(s * 0.50f, s * 0.70f)
        close()
    }
    drawPath(tail, color)
}

fun DrawScope.drawMoon(color: Color, stroke: Float) {
    val s = size.minDimension
    val p = Path().apply {
        addArc(
            androidx.compose.ui.geometry.Rect(Offset(s * 0.1f, s * 0.1f), Size(s * 0.8f, s * 0.8f)),
            60f, 250f
        )
    }
    drawPath(p, color, style = Stroke(stroke, cap = StrokeCap.Round))
}

fun DrawScope.drawPlane(color: Color) {
    val s = size.minDimension
    val p = Path().apply {
        moveTo(s * 0.5f, s * 0.04f)
        lineTo(s * 0.58f, s * 0.42f)
        lineTo(s * 0.95f, s * 0.62f)
        lineTo(s * 0.93f, s * 0.72f)
        lineTo(s * 0.56f, s * 0.60f)
        lineTo(s * 0.54f, s * 0.80f)
        lineTo(s * 0.66f, s * 0.90f)
        lineTo(s * 0.64f, s * 0.97f)
        lineTo(s * 0.5f, s * 0.92f)
        lineTo(s * 0.36f, s * 0.97f)
        lineTo(s * 0.34f, s * 0.90f)
        lineTo(s * 0.46f, s * 0.80f)
        lineTo(s * 0.44f, s * 0.60f)
        lineTo(s * 0.07f, s * 0.72f)
        lineTo(s * 0.05f, s * 0.62f)
        lineTo(s * 0.42f, s * 0.42f)
        close()
    }
    drawPath(p, color)
}
