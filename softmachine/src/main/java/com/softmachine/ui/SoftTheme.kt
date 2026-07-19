@file:OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)

package com.softmachine.ui

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.softmachine.style.R

/**
 * Measured from the reference board: warm greige page, ivory cards,
 * desaturated mauve accents. Nothing here is a pure white or a blue lavender.
 */
object SoftColors {
    val bg = Color(0xFFE7DFDA)
    val panel = Color(0xFFEDE8E3)
    val panelAlt = Color(0xFFE9E3DE)
    val lavender = Color(0xFFC9BBD8)
    val lavenderDeep = Color(0xFF9C8DB4)
    val lavenderPale = Color(0xFFD8C4E0)
    val mauve = Color(0xFFB3A4C6)
    val peach = Color(0xFFF6D2BE)
    val ink = Color(0xFF4A4642)
    val dim = Color(0xFF8B8580)
    val faint = Color(0xFFB3ACA5)
}

/** Montserrat variable font, the board's "Soft Neue". */
val Montserrat = FontFamily(
    Font(
        R.font.montserrat_wght, weight = FontWeight.Light,
        variationSettings = FontVariation.Settings(FontVariation.weight(300)),
    ),
    Font(
        R.font.montserrat_wght, weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(FontVariation.weight(400)),
    ),
    Font(
        R.font.montserrat_wght, weight = FontWeight.Medium,
        variationSettings = FontVariation.Settings(FontVariation.weight(500)),
    ),
    Font(
        R.font.montserrat_wght, weight = FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(FontVariation.weight(600)),
    ),
)

/** Soft sans body/display text. */
@Composable
fun SoftText(
    text: String,
    size: TextUnit,
    color: Color = SoftColors.ink,
    modifier: Modifier = Modifier,
    weight: FontWeight = FontWeight.Normal,
    spacingEm: Float = 0.01f,
    lineHeight: TextUnit = TextUnit.Unspecified,
    align: TextAlign = TextAlign.Start,
) {
    BasicText(
        text = text,
        modifier = modifier,
        style = TextStyle(
            color = color,
            fontSize = size,
            fontFamily = Montserrat,
            fontWeight = weight,
            letterSpacing = (size.value * spacingEm).sp,
            lineHeight = lineHeight,
            textAlign = align,
        ),
    )
}

/** Wide-tracked caps label, the board's utility voice. */
@Composable
fun SoftLabel(
    text: String,
    size: TextUnit = 6.5.sp,
    color: Color = SoftColors.dim,
    modifier: Modifier = Modifier,
    lineHeight: TextUnit = TextUnit.Unspecified,
) {
    SoftText(
        text, size, color, modifier,
        weight = FontWeight.Medium, spacingEm = 0.22f, lineHeight = lineHeight,
    )
}
