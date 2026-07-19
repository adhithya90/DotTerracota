package com.dotterracota.ui

import android.graphics.RuntimeShader
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

object Palette {
    val terra = Color(0xFFC75A39)
    val terraLight = Color(0xFFE09B7A)
    val terraDeep = Color(0xFF8A3A22)
    val sand = Color(0xFFEAD9C6)
    val cream = Color(0xFFF7F4F1)
    val creamWarm = Color(0xFFEFE7DA)
    val ink = Color(0xFF111112)
    val card = Color(0xFF141414)
    val pageBg = Color(0xFFE8DECF)
    val cardLight = Color(0xFFF5EFE4)
}

@Composable
fun MonoText(
    text: String,
    size: TextUnit,
    color: Color,
    modifier: Modifier = Modifier,
    weight: FontWeight = FontWeight.Medium,
    spacingEm: Float = 0.12f,
    lineHeight: TextUnit = TextUnit.Unspecified,
    align: TextAlign = TextAlign.Start,
) {
    BasicText(
        text = text,
        modifier = modifier,
        style = TextStyle(
            color = color,
            fontSize = size,
            fontFamily = FontFamily.Monospace,
            fontWeight = weight,
            letterSpacing = (size.value * spacingEm).sp,
            lineHeight = lineHeight,
            textAlign = align,
        ),
    )
}

/** Seconds since composition start, updated every frame. */
@Composable
fun rememberTimeSeconds(): State<Float> = produceState(0f) {
    var start = -1L
    while (true) {
        withFrameNanos { now ->
            if (start < 0) start = now
            value = (now - start) / 1e9f
        }
    }
}

/**
 * Fills its size with an AGSL shader. Sets `uRes` and (optionally animated) `uTime`;
 * [uniforms] can set extra shader-specific uniforms and is re-read on every draw.
 */
@Composable
fun ShaderPanel(
    src: String,
    modifier: Modifier = Modifier,
    animated: Boolean = false,
    uniforms: (RuntimeShader.() -> Unit)? = null,
) {
    val shader = remember(src) { RuntimeShader(src) }
    val time = if (animated) rememberTimeSeconds() else remember { mutableFloatStateOf(0f) }
    Canvas(modifier) {
        shader.setFloatUniform("uRes", size.width, size.height)
        shader.setFloatUniform("uTime", time.value)
        uniforms?.invoke(shader)
        drawRect(ShaderBrush(shader))
    }
}
