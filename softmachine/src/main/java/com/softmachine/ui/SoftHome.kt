package com.softmachine.ui

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.designlens.core.ShaderPanel
import com.designlens.core.rememberTimeSeconds
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val glass = Color(0xD9F7F2EE)
private val glassDim = Color(0xBFF7F2EE)

/** The Soft Machine phone home screen, full bleed. */
@Composable
fun SoftHome(modifier: Modifier = Modifier, topPad: Dp = 0.dp, bottomPad: Dp = 0.dp) {
    val time by rememberTimeSeconds()
    Box(modifier) {
        ShaderPanel(SoftShaders.BLOB_WALLPAPER, Modifier.fillMaxSize(), animated = true)
        Column(Modifier.fillMaxSize().padding(horizontal = 12.dp)) {
            Spacer(Modifier.height(topPad))
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column {
                    SoftText("Tue, 21 May", 6.5.sp, SoftColors.ink.copy(alpha = 0.85f))
                    Spacer(Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Canvas(Modifier.size(7.dp)) { drawCloudIcon(SoftColors.ink.copy(alpha = 0.7f)) }
                        Spacer(Modifier.width(3.dp))
                        SoftText("22°", 6.sp, SoftColors.ink.copy(alpha = 0.7f))
                    }
                }
                Spacer(Modifier.weight(1f))
                Canvas(Modifier.size(20.dp, 5.dp)) {
                    val c = SoftColors.ink.copy(alpha = 0.75f)
                    val p = Path().apply {
                        moveTo(0f, size.height); lineTo(size.height, size.height)
                        lineTo(size.height, 0f); close()
                    }
                    drawPath(p, c)
                    drawRoundRect(
                        c, Offset(size.width - size.height * 1.8f, 0.5f),
                        Size(size.height * 1.6f, size.height - 1f), CornerRadius(1.5f, 1.5f)
                    )
                }
            }
            Spacer(Modifier.height(14.dp))
            SoftText("Good morning", 13.sp, SoftColors.ink, weight = FontWeight.Light)
            Row(verticalAlignment = Alignment.Bottom) {
                SoftText("Alex", 19.sp, SoftColors.ink, weight = FontWeight.Normal)
                Spacer(Modifier.width(4.dp))
                Box(
                    Modifier.padding(bottom = 5.dp).size(4.dp).clip(CircleShape)
                        .background(SoftColors.lavenderDeep)
                )
            }
            Spacer(Modifier.height(6.dp))
            SoftText(
                "Take it slow, everything\nis in motion.",
                6.5.sp, SoftColors.ink.copy(alpha = 0.55f), lineHeight = 10.sp
            )
            Spacer(Modifier.height(6.dp))
            Box(Modifier.fillMaxWidth()) {
                Box(
                    Modifier
                        .align(Alignment.CenterEnd)
                        .softShadow(corner = 50.dp, offset = 3.dp, blur = 7.dp)
                        .size(17.dp)
                        .clip(CircleShape)
                        .background(Color(0xF2F9F5F1)),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(Modifier.size(7.dp)) {
                        val c = SoftColors.ink.copy(alpha = 0.8f)
                        drawLine(c, Offset(center.x, 0f), Offset(center.x, size.height), 1.dp.toPx(), StrokeCap.Round)
                        drawLine(c, Offset(0f, center.y), Offset(size.width, center.y), 1.dp.toPx(), StrokeCap.Round)
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            Row(Modifier.fillMaxWidth().height(46.dp), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                // weather widget
                Column(
                    Modifier.weight(1f).fillMaxSize()
                        .clip(RoundedCornerShape(13.dp)).background(glass)
                        .padding(horizontal = 9.dp, vertical = 7.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Canvas(Modifier.size(9.dp)) { drawCloudIcon(SoftColors.ink.copy(alpha = 0.65f)) }
                        Spacer(Modifier.width(5.dp))
                        SoftText("22°", 9.5.sp, SoftColors.ink, weight = FontWeight.Normal)
                    }
                    Spacer(Modifier.weight(1f))
                    SoftText("Cloudy", 5.5.sp, SoftColors.dim)
                }
                // breath widget
                Row(
                    Modifier.weight(1f).fillMaxSize()
                        .clip(RoundedCornerShape(13.dp)).background(glass)
                        .padding(horizontal = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Canvas(Modifier.size(16.dp)) {
                        val breath = (sin(time * 0.8f) + 1f) / 2f
                        val n = 20
                        for (i in 0 until n) {
                            val a = i / n.toFloat() * 2 * PI
                            val on = i < n * (0.3f + 0.7f * breath)
                            drawCircle(
                                if (on) SoftColors.lavenderDeep.copy(alpha = 0.8f)
                                else SoftColors.faint.copy(alpha = 0.5f),
                                0.7.dp.toPx(),
                                Offset(
                                    center.x + (size.minDimension * 0.42f * cos(a)).toFloat(),
                                    center.y + (size.minDimension * 0.42f * sin(a)).toFloat()
                                )
                            )
                        }
                    }
                    Spacer(Modifier.width(5.dp))
                    Column {
                        SoftText("Breath", 5.5.sp, SoftColors.ink)
                        SoftText("4 min", 7.5.sp, SoftColors.ink, weight = FontWeight.Normal)
                    }
                }
            }
            Spacer(Modifier.height(7.dp))
            SoftPlayerWidget()
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                HomeAction { drawPhoneIconSoft(it) }
                HomeAction { drawChatIconSoft(it) }
                HomeAction { drawGearIcon(it) }
                HomeAction { drawCameraIcon(it) }
            }
            Spacer(Modifier.height(9.dp))
            Box(
                Modifier.align(Alignment.CenterHorizontally)
                    .size(36.dp, 3.dp).clip(RoundedCornerShape(2.dp))
                    .background(SoftColors.ink.copy(alpha = 0.75f))
            )
            Spacer(Modifier.height(5.dp))
            Spacer(Modifier.height(bottomPad))
        }
    }
}

@Composable
private fun SoftPlayerWidget() {
    Column(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(14.dp)).background(glass)
            .padding(horizontal = 9.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(15.dp).clip(RoundedCornerShape(5.dp))) {
                ShaderPanel(SoftShaders.CLOUDSCAPE, Modifier.fillMaxSize())
            }
            Spacer(Modifier.width(6.dp))
            Column {
                SoftText("Soft Focus", 7.sp, SoftColors.ink, weight = FontWeight.Medium)
                SoftText("Ambiental", 5.5.sp, SoftColors.dim)
            }
            Spacer(Modifier.weight(1f))
            Canvas(Modifier.size(9.dp)) {
                val c = SoftColors.ink.copy(alpha = 0.6f)
                for (i in 0..3) {
                    val h = size.height * listOf(0.4f, 0.85f, 0.6f, 0.35f)[i]
                    drawLine(
                        c, Offset(size.width * (0.15f + i * 0.24f), (size.height + h) / 2f),
                        Offset(size.width * (0.15f + i * 0.24f), (size.height - h) / 2f),
                        1.dp.toPx(), StrokeCap.Round
                    )
                }
            }
        }
        Spacer(Modifier.height(7.dp))
        Canvas(Modifier.fillMaxWidth().height(6.dp)) {
            val y = size.height / 2f
            drawLine(SoftColors.faint.copy(alpha = 0.6f), Offset(0f, y), Offset(size.width, y), 2.dp.toPx(), StrokeCap.Round)
            drawLine(SoftColors.lavenderDeep, Offset(0f, y), Offset(size.width * 0.35f, y), 2.dp.toPx(), StrokeCap.Round)
            drawCircle(Color(0xFFF7F4F0), 2.6.dp.toPx(), Offset(size.width * 0.35f, y))
            drawCircle(SoftColors.lavenderDeep, 1.4.dp.toPx(), Offset(size.width * 0.35f, y))
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            SoftText("1:42", 5.sp, SoftColors.dim)
            SoftText("4:28", 5.sp, SoftColors.dim)
        }
        Spacer(Modifier.height(4.dp))
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Canvas(Modifier.size(8.dp)) { drawSkipSoft(SoftColors.ink.copy(alpha = 0.8f), false) }
            Canvas(Modifier.size(9.dp)) {
                val c = SoftColors.ink.copy(alpha = 0.85f)
                val w = 2.2.dp.toPx()
                drawRoundRect(c, Offset(size.width * 0.22f, 0f), Size(w, size.height), CornerRadius(w / 2))
                drawRoundRect(c, Offset(size.width * 0.62f, 0f), Size(w, size.height), CornerRadius(w / 2))
            }
            Canvas(Modifier.size(8.dp)) { drawSkipSoft(SoftColors.ink.copy(alpha = 0.8f), true) }
        }
    }
}

@Composable
private fun HomeAction(icon: DrawScope.(Color) -> Unit) {
    Box(
        Modifier
            .softShadow(corner = 50.dp, offset = 2.5.dp, blur = 6.dp)
            .size(24.dp)
            .clip(CircleShape)
            .background(glassDim),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.size(10.dp)) { icon(SoftColors.ink.copy(alpha = 0.8f)) }
    }
}

// ------------------------------------------------------------ shared icons

fun DrawScope.drawCloudIcon(color: Color) {
    val s = size.minDimension
    drawCircle(color, s * 0.22f, Offset(s * 0.35f, s * 0.55f))
    drawCircle(color, s * 0.28f, Offset(s * 0.58f, s * 0.45f))
    drawCircle(color, s * 0.20f, Offset(s * 0.78f, s * 0.58f))
    drawRoundRect(
        color, Offset(s * 0.18f, s * 0.50f), Size(s * 0.72f, s * 0.28f),
        CornerRadius(s * 0.14f, s * 0.14f)
    )
}

fun DrawScope.drawSkipSoft(color: Color, forward: Boolean) {
    val w = size.width
    val h = size.height
    val barW = w * 0.15f
    if (forward) {
        val tri = Path().apply { moveTo(0f, 0f); lineTo(w * 0.68f, h * 0.5f); lineTo(0f, h); close() }
        drawPath(tri, color)
        drawRoundRect(color, Offset(w - barW, 0f), Size(barW, h), CornerRadius(barW / 2, barW / 2))
    } else {
        val tri = Path().apply { moveTo(w, 0f); lineTo(w * 0.32f, h * 0.5f); lineTo(w, h); close() }
        drawPath(tri, color)
        drawRoundRect(color, Offset(0f, 0f), Size(barW, h), CornerRadius(barW / 2, barW / 2))
    }
}

fun DrawScope.drawPhoneIconSoft(color: Color) {
    val s = size.minDimension
    drawArc(
        color, 40f, 200f, false,
        Offset(s * 0.12f, s * 0.12f), Size(s * 0.76f, s * 0.76f),
        style = Stroke(s * 0.14f, cap = StrokeCap.Round)
    )
    drawCircle(color, s * 0.09f, Offset(s * 0.27f, s * 0.30f))
    drawCircle(color, s * 0.09f, Offset(s * 0.73f, s * 0.70f))
}

fun DrawScope.drawChatIconSoft(color: Color) {
    val s = size.minDimension
    drawRoundRect(
        color, Offset(s * 0.08f, s * 0.12f), Size(s * 0.84f, s * 0.58f),
        CornerRadius(s * 0.20f, s * 0.20f), style = Stroke(s * 0.09f)
    )
    val tail = Path().apply {
        moveTo(s * 0.28f, s * 0.66f); lineTo(s * 0.28f, s * 0.92f); lineTo(s * 0.52f, s * 0.68f); close()
    }
    drawPath(tail, color)
}

fun DrawScope.drawGearIcon(color: Color) {
    val s = size.minDimension
    val c = center
    drawCircle(color, s * 0.20f, c, style = Stroke(s * 0.09f))
    for (i in 0 until 8) {
        val a = i * PI / 4
        drawLine(
            color,
            Offset(c.x + (s * 0.30f * cos(a)).toFloat(), c.y + (s * 0.30f * sin(a)).toFloat()),
            Offset(c.x + (s * 0.42f * cos(a)).toFloat(), c.y + (s * 0.42f * sin(a)).toFloat()),
            s * 0.10f, StrokeCap.Round
        )
    }
}

fun DrawScope.drawCameraIcon(color: Color) {
    val s = size.minDimension
    drawRoundRect(
        color, Offset(s * 0.08f, s * 0.22f), Size(s * 0.84f, s * 0.60f),
        CornerRadius(s * 0.14f, s * 0.14f), style = Stroke(s * 0.09f)
    )
    drawCircle(color, s * 0.15f, Offset(s * 0.5f, s * 0.52f), style = Stroke(s * 0.09f))
    drawLine(color, Offset(s * 0.34f, s * 0.22f), Offset(s * 0.42f, s * 0.10f), s * 0.09f, StrokeCap.Round)
    drawLine(color, Offset(s * 0.42f, s * 0.10f), Offset(s * 0.58f, s * 0.10f), s * 0.09f, StrokeCap.Round)
    drawLine(color, Offset(s * 0.58f, s * 0.10f), Offset(s * 0.66f, s * 0.22f), s * 0.09f, StrokeCap.Round)
}
