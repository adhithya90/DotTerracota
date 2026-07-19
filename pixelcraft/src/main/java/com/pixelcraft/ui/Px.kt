package com.pixelcraft.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import com.designlens.core.rememberTimeSeconds

/** Canonical Pixel Craft palette, straight from the board's swatches. */
object Px {
    val paper = Color(0xFFF9F6F1)
    val ink = Color(0xFF1A1F2B)
    val blue = Color(0xFF3B82F6)
    val pink = Color(0xFFFF6B8A)
    val yellow = Color(0xFFF7C948)
    val green = Color(0xFF22C1A2)
    val gray = Color(0xFF8B949E)
    val white = Color(0xFFF5F7FA)
    val navy = Color(0xFF171E26)
    val label = Color(0xFF4A5058)
    val dim = Color(0xFF6A7078)
    val line = Color(0xFFD9D4CB)
}

/** Deterministic LCG matching the HTML board, so scenes render identically. */
class Lcg(private var seed: Int) {
    fun next(): Float {
        seed = (seed * 1103515245 + 12345) and 0x7fffffff
        return seed / 2147483647f
    }
}

/** Draws a character-mapped sprite as filled cells. '.' and unmapped chars are transparent. */
fun DrawScope.sprite(
    rows: List<String>,
    pal: Map<Char, Color>,
    cell: Float,
    topLeft: Offset = Offset.Zero,
) {
    rows.forEachIndexed { y, row ->
        row.forEachIndexed { x, ch ->
            val c = pal[ch] ?: return@forEachIndexed
            drawRect(c, Offset(topLeft.x + x * cell, topLeft.y + y * cell), Size(cell + 0.5f, cell + 0.5f))
        }
    }
}

@Composable
fun Sprite(rows: List<String>, pal: Map<Char, Color>, cell: Dp, modifier: Modifier = Modifier) {
    val w = rows.maxOf { it.length }
    val h = rows.size
    Canvas(modifier.size(cell * w, cell * h)) { sprite(rows, pal, cell.toPx()) }
}

/** Discrete frame counter: pixel animation steps, never tweens. */
@Composable
fun rememberFrame(fps: Float): State<Float> = rememberTimeSeconds()

fun frameOf(time: Float, fps: Float, frames: Int): Int = ((time * fps).toInt()) % frames

// ------------------------------------------------------------- 5x7 pixel font
val PXFONT: Map<Char, List<String>> = mapOf(
    'A' to listOf("01110","10001","10001","11111","10001","10001","10001"),
    'B' to listOf("11110","10001","10001","11110","10001","10001","11110"),
    'C' to listOf("01110","10001","10000","10000","10000","10001","01110"),
    'D' to listOf("11110","10001","10001","10001","10001","10001","11110"),
    'E' to listOf("11111","10000","10000","11110","10000","10000","11111"),
    'F' to listOf("11111","10000","10000","11110","10000","10000","10000"),
    'G' to listOf("01110","10001","10000","10111","10001","10001","01111"),
    'H' to listOf("10001","10001","10001","11111","10001","10001","10001"),
    'I' to listOf("01110","00100","00100","00100","00100","00100","01110"),
    'J' to listOf("00111","00010","00010","00010","00010","10010","01100"),
    'K' to listOf("10001","10010","10100","11000","10100","10010","10001"),
    'L' to listOf("10000","10000","10000","10000","10000","10000","11111"),
    'M' to listOf("10001","11011","10101","10101","10001","10001","10001"),
    'N' to listOf("10001","11001","10101","10011","10001","10001","10001"),
    'O' to listOf("01110","10001","10001","10001","10001","10001","01110"),
    'P' to listOf("11110","10001","10001","11110","10000","10000","10000"),
    'Q' to listOf("01110","10001","10001","10001","10101","10010","01101"),
    'R' to listOf("11110","10001","10001","11110","10100","10010","10001"),
    'S' to listOf("01111","10000","10000","01110","00001","00001","11110"),
    'T' to listOf("11111","00100","00100","00100","00100","00100","00100"),
    'U' to listOf("10001","10001","10001","10001","10001","10001","01110"),
    'V' to listOf("10001","10001","10001","10001","10001","01010","00100"),
    'W' to listOf("10001","10001","10001","10101","10101","10101","01010"),
    'X' to listOf("10001","10001","01010","00100","01010","10001","10001"),
    'Y' to listOf("10001","10001","01010","00100","00100","00100","00100"),
    'Z' to listOf("11111","00001","00010","00100","01000","10000","11111"),
    '0' to listOf("01110","10001","10011","10101","11001","10001","01110"),
    '1' to listOf("00100","01100","00100","00100","00100","00100","01110"),
    '2' to listOf("01110","10001","00001","00010","00100","01000","11111"),
    '3' to listOf("11111","00010","00100","00010","00001","10001","01110"),
    '4' to listOf("00010","00110","01010","10010","11111","00010","00010"),
    '5' to listOf("11111","10000","11110","00001","00001","10001","01110"),
    '6' to listOf("00110","01000","10000","11110","10001","10001","01110"),
    '7' to listOf("11111","00001","00010","00100","01000","01000","01000"),
    '8' to listOf("01110","10001","10001","01110","10001","10001","01110"),
    '9' to listOf("01110","10001","10001","01111","00001","00010","01100"),
    '!' to listOf("00100","00100","00100","00100","00100","00000","00100"),
    '?' to listOf("01110","10001","00001","00110","00100","00000","00100"),
    '%' to listOf("11001","11010","00010","00100","01000","01011","10011"),
    '&' to listOf("01100","10010","10100","01000","10101","10010","01101"),
    '*' to listOf("00000","10101","01110","11111","01110","10101","00000"),
    '+' to listOf("00000","00100","00100","11111","00100","00100","00000"),
    '-' to listOf("00000","00000","00000","11111","00000","00000","00000"),
    '=' to listOf("00000","00000","11111","00000","11111","00000","00000"),
    '.' to listOf("00000","00000","00000","00000","00000","00000","00100"),
    ',' to listOf("00000","00000","00000","00000","00110","00100","01000"),
    ':' to listOf("00000","00100","00000","00000","00000","00100","00000"),
    '/' to listOf("00001","00001","00010","00100","01000","10000","10000"),
    '°' to listOf("01100","10010","10010","01100","00000","00000","00000"),
    ' ' to listOf("00000","00000","00000","00000","00000","00000","00000"),
)

fun DrawScope.pxText(str: String, color: Color, cell: Float, topLeft: Offset = Offset.Zero, gap: Int = 1) {
    str.uppercase().forEachIndexed { i, ch ->
        val gl = PXFONT[ch] ?: PXFONT[' ']!!
        gl.forEachIndexed { y, row ->
            row.forEachIndexed { x, b ->
                if (b == '1') drawRect(
                    color,
                    Offset(topLeft.x + (i * (5 + gap) + x) * cell, topLeft.y + y * cell),
                    Size(cell + 0.5f, cell + 0.5f)
                )
            }
        }
    }
}

@Composable
fun PxText(str: String, cell: Dp, color: Color, modifier: Modifier = Modifier, gap: Int = 1) {
    val w = str.length * (5 + gap) - gap
    Canvas(modifier.size(cell * w, cell * 7)) { pxText(str, color, cell.toPx()) }
}

// -------------------------------------------------------- 7x8 heavy headline
val PXHEAVY: Map<Char, List<String>> = mapOf(
    'P' to listOf("1111110","1111111","1100011","1111111","1111110","1100000","1100000","1100000"),
    'I' to listOf("1111110","1111110","0011000","0011000","0011000","0011000","1111110","1111110"),
    'X' to listOf("1100011","1110111","0111110","0011100","0111110","1110111","1100011","1100011"),
    'E' to listOf("1111111","1111111","1100000","1111110","1111110","1100000","1111111","1111111"),
    'L' to listOf("1100000","1100000","1100000","1100000","1100000","1100000","1111111","1111111"),
    'C' to listOf("0111111","1111111","1100000","1100000","1100000","1100000","1111111","0111111"),
    'R' to listOf("1111110","1111111","1100011","1111111","1111110","1101100","1100110","1100011"),
    'A' to listOf("0111110","1111111","1100011","1111111","1111111","1100011","1100011","1100011"),
    'F' to listOf("1111111","1111111","1100000","1111110","1111110","1100000","1100000","1100000"),
    'T' to listOf("1111111","1111111","0011000","0011000","0011000","0011000","0011000","0011000"),
    '-' to listOf("0000000","0000000","0000000","1111110","1111110","0000000","0000000","0000000"),
)

@Composable
fun PxTitle(str: String, cell: Dp, color: Color, modifier: Modifier = Modifier) {
    val w = str.length * 9 - 2
    Canvas(modifier.size(cell * w, cell * 8)) {
        val c = cell.toPx()
        str.forEachIndexed { i, ch ->
            val gl = PXHEAVY[ch] ?: return@forEachIndexed
            gl.forEachIndexed { y, row ->
                row.forEachIndexed { x, b ->
                    if (b == '1') drawRect(color, Offset((i * 9 + x) * c, y * c), Size(c + 0.5f, c + 0.5f))
                }
            }
        }
    }
}
