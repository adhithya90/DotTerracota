package com.dotterracota.ui

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.exp
import kotlin.math.max

/** Classic 5x7 LED dot-matrix glyphs. Each glyph is 7 rows of 5 bits. */
private val DOT_FONT: Map<Char, List<String>> = mapOf(
    'A' to listOf("01110", "10001", "10001", "11111", "10001", "10001", "10001"),
    'B' to listOf("11110", "10001", "10001", "11110", "10001", "10001", "11110"),
    'C' to listOf("01110", "10001", "10000", "10000", "10000", "10001", "01110"),
    'D' to listOf("11110", "10001", "10001", "10001", "10001", "10001", "11110"),
    'E' to listOf("11111", "10000", "10000", "11110", "10000", "10000", "11111"),
    'F' to listOf("11111", "10000", "10000", "11110", "10000", "10000", "10000"),
    'G' to listOf("01110", "10001", "10000", "10111", "10001", "10001", "01111"),
    'H' to listOf("10001", "10001", "10001", "11111", "10001", "10001", "10001"),
    'I' to listOf("01110", "00100", "00100", "00100", "00100", "00100", "01110"),
    'J' to listOf("00111", "00010", "00010", "00010", "00010", "10010", "01100"),
    'K' to listOf("10001", "10010", "10100", "11000", "10100", "10010", "10001"),
    'L' to listOf("10000", "10000", "10000", "10000", "10000", "10000", "11111"),
    'M' to listOf("10001", "11011", "10101", "10101", "10001", "10001", "10001"),
    'N' to listOf("10001", "11001", "10101", "10011", "10001", "10001", "10001"),
    'O' to listOf("01110", "10001", "10001", "10001", "10001", "10001", "01110"),
    'P' to listOf("11110", "10001", "10001", "11110", "10000", "10000", "10000"),
    'Q' to listOf("01110", "10001", "10001", "10001", "10101", "10010", "01101"),
    'R' to listOf("11110", "10001", "10001", "11110", "10100", "10010", "10001"),
    'S' to listOf("01111", "10000", "10000", "01110", "00001", "00001", "11110"),
    'T' to listOf("11111", "00100", "00100", "00100", "00100", "00100", "00100"),
    'U' to listOf("10001", "10001", "10001", "10001", "10001", "10001", "01110"),
    'V' to listOf("10001", "10001", "10001", "10001", "10001", "01010", "00100"),
    'W' to listOf("10001", "10001", "10001", "10101", "10101", "10101", "01010"),
    'X' to listOf("10001", "10001", "01010", "00100", "01010", "10001", "10001"),
    'Y' to listOf("10001", "10001", "01010", "00100", "00100", "00100", "00100"),
    'Z' to listOf("11111", "00001", "00010", "00100", "01000", "10000", "11111"),
    '0' to listOf("01110", "10001", "10011", "10101", "11001", "10001", "01110"),
    '1' to listOf("00100", "01100", "00100", "00100", "00100", "00100", "01110"),
    '2' to listOf("01110", "10001", "00001", "00010", "00100", "01000", "11111"),
    '3' to listOf("11111", "00010", "00100", "00010", "00001", "10001", "01110"),
    '4' to listOf("00010", "00110", "01010", "10010", "11111", "00010", "00010"),
    '5' to listOf("11111", "10000", "11110", "00001", "00001", "10001", "01110"),
    '6' to listOf("00110", "01000", "10000", "11110", "10001", "10001", "01110"),
    '7' to listOf("11111", "00001", "00010", "00100", "01000", "01000", "01000"),
    '8' to listOf("01110", "10001", "10001", "01110", "10001", "10001", "01110"),
    '9' to listOf("01110", "10001", "10001", "01111", "00001", "00010", "01100"),
    'a' to listOf("00000", "00000", "01110", "00001", "01111", "10001", "01111"),
    '°' to listOf("01100", "10010", "10010", "01100", "00000", "00000", "00000"),
    '-' to listOf("00000", "00000", "00000", "01110", "00000", "00000", "00000"),
    '.' to listOf("00000", "00000", "00000", "00000", "00000", "00000", "00100"),
    ':' to listOf("00000", "00100", "00000", "00000", "00100", "00000", "00000"),
    ' ' to listOf("00000", "00000", "00000", "00000", "00000", "00000", "00000"),
)

/**
 * Renders text as a dot-matrix (LED) display. [pitch] is the center-to-center dot spacing;
 * each character cell is 6 columns wide (5 + 1 gap) and 7 rows tall.
 */
@Composable
fun DotMatrixText(
    text: String,
    pitch: Dp,
    color: Color,
    modifier: Modifier = Modifier,
    dotRatio: Float = 0.74f,
    lineGapRows: Int = 3,
    glow: Boolean = false,
    glowColor: Color = color,
) {
    val lines = text.split("\n")
    val maxChars = lines.maxOf { it.length }
    val cols = maxChars * 6 - 1
    val rows = lines.size * 7 + (lines.size - 1) * lineGapRows
    Canvas(modifier.size(pitch * cols, pitch * rows)) {
        val p = pitch.toPx()
        val r = p * dotRatio / 2f
        lines.forEachIndexed { li, line ->
            val y0 = li * (7 + lineGapRows) * p
            line.forEachIndexed { ci, ch ->
                val glyph = DOT_FONT[ch] ?: DOT_FONT[ch.uppercaseChar()] ?: return@forEachIndexed
                for (row in 0..6) {
                    val bits = glyph[row]
                    for (col in 0..4) {
                        if (bits[col] == '1') {
                            val c = Offset((ci * 6 + col) * p + p / 2f, y0 + row * p + p / 2f)
                            if (glow) {
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        listOf(glowColor.copy(alpha = 0.45f), Color.Transparent),
                                        center = c, radius = r * 3.2f
                                    ),
                                    radius = r * 3.2f, center = c
                                )
                            }
                            drawCircle(color, r, c)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Dot-matrix text you can fidget with: tap to make the dots pop and jiggle back,
 * drag to stir them around your finger. Same glyphs and metrics as [DotMatrixText].
 */
@Composable
fun FidgetDotMatrixText(
    text: String,
    pitch: Dp,
    color: Color,
    modifier: Modifier = Modifier,
    dotRatio: Float = 0.74f,
    lineGapRows: Int = 3,
) {
    val lines = text.split("\n")
    val cells = remember(text, lineGapRows) {
        buildList {
            lines.forEachIndexed { li, line ->
                line.forEachIndexed { ci, ch ->
                    val glyph = DOT_FONT[ch] ?: DOT_FONT[ch.uppercaseChar()] ?: return@forEachIndexed
                    for (row in 0..6) for (col in 0..4) {
                        if (glyph[row][col] == '1') add(ci * 6 + col to li * (7 + lineGapRows) + row)
                    }
                }
            }
        }
    }
    val cols = lines.maxOf { it.length } * 6 - 1
    val rows = lines.size * 7 + (lines.size - 1) * lineGapRows
    val pulse = remember { Animatable(0f) }
    var origin by remember { mutableStateOf(Offset.Zero) }
    val scope = rememberCoroutineScope()
    val view = LocalView.current
    val settle = spring<Float>(dampingRatio = 0.16f, stiffness = 140f)
    Canvas(
        modifier
            .size(pitch * cols, pitch * rows)
            .pointerInput(Unit) {
                detectTapGestures { o ->
                    origin = o
                    view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                    scope.launch {
                        pulse.snapTo(1f)
                        pulse.animateTo(0f, settle)
                    }
                }
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { o ->
                        origin = o
                        view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                        scope.launch { pulse.animateTo(0.75f, spring(stiffness = 900f)) }
                    },
                    onDragEnd = { scope.launch { pulse.animateTo(0f, settle) } },
                    onDragCancel = { scope.launch { pulse.animateTo(0f, settle) } },
                ) { change, _ ->
                    origin = change.position
                    change.consume()
                }
            }
    ) {
        val p = pitch.toPx()
        val r = p * dotRatio / 2f
        val reach = 22f * p
        cells.forEach { (cx, cy) ->
            val c = Offset(cx * p + p / 2f, cy * p + p / 2f)
            val dv = c - origin
            val dist = max(dv.getDistance(), 0.001f)
            val amp = pulse.value * exp(-dist / reach)
            val pos = c + (dv / dist) * (amp * 3.2f * p)
            drawCircle(color, r * (1f + 1.1f * amp), pos)
        }
    }
}

/** Small decorative grid of dots, driven by a bit pattern. */
@Composable
fun DotGlyph(
    pattern: List<String>,
    pitch: Dp,
    color: Color,
    modifier: Modifier = Modifier,
    dotRatio: Float = 0.68f,
) {
    val cols = pattern.maxOf { it.length }
    Canvas(modifier.size(pitch * cols, pitch * pattern.size)) {
        val p = pitch.toPx()
        val r = p * dotRatio / 2f
        pattern.forEachIndexed { row, bits ->
            bits.forEachIndexed { col, b ->
                if (b == '1') drawCircle(color, r, Offset(col * p + p / 2f, row * p + p / 2f))
            }
        }
    }
}

/** A horizontal progress bar made of dots. */
@Composable
fun DotBar(
    fill: Float,
    modifier: Modifier = Modifier,
    count: Int = 28,
    litColor: Color = Color(0xFFEDE4D6),
    dimColor: Color = Color(0xFF3B3733),
    dotRadius: Dp = 1.4.dp,
    fade: Boolean = false,
) {
    Canvas(modifier) {
        val r = dotRadius.toPx()
        val step = size.width / count
        for (i in 0 until count) {
            val lit = i < (count * fill)
            val base = if (lit) litColor else dimColor
            val c = if (fade && lit) base.copy(alpha = 1f - 0.55f * (i.toFloat() / count)) else base
            drawCircle(c, r, Offset(step * i + step / 2f, size.height / 2f))
        }
    }
}
