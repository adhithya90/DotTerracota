package com.softmachine.ui

import android.graphics.BlurMaskFilter
import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * Neumorphic dual shadow: soft dark below-right, soft light above-left.
 * Draw before clipping so the blur can bleed outside the shape.
 */
fun Modifier.softShadow(
    corner: Dp = 18.dp,
    offset: Dp = 4.dp,
    blur: Dp = 10.dp,
    dark: Color = Color(0x38887A72),
    light: Color = Color(0xD9FBF7F3),
): Modifier = drawBehind {
    val r = corner.toPx()
    val o = offset.toPx()
    val paint = Paint()
    val fw = paint.asFrameworkPaint()
    drawIntoCanvas { canvas ->
        // light from above-left
        fw.maskFilter = BlurMaskFilter(blur.toPx(), BlurMaskFilter.Blur.NORMAL)
        fw.color = light.toArgb()
        canvas.nativeCanvas.drawRoundRect(-o, -o, size.width - o, size.height - o, r, r, fw)
        // wide faint ambient shadow
        fw.maskFilter = BlurMaskFilter(blur.toPx() * 2.2f, BlurMaskFilter.Blur.NORMAL)
        fw.color = dark.copy(alpha = dark.alpha * 0.45f).toArgb()
        canvas.nativeCanvas.drawRoundRect(
            o * 1.8f, o * 2.2f, size.width + o * 1.8f, size.height + o * 2.2f, r, r, fw
        )
        // tight contact shadow
        fw.maskFilter = BlurMaskFilter(blur.toPx() * 0.7f, BlurMaskFilter.Blur.NORMAL)
        fw.color = dark.toArgb()
        canvas.nativeCanvas.drawRoundRect(o, o, size.width + o, size.height + o, r, r, fw)
    }
}

/** The standard raised panel of this style. */
@Composable
fun SoftPanel(
    modifier: Modifier = Modifier,
    corner: Dp = 18.dp,
    color: Color = SoftColors.panel,
    content: @Composable () -> Unit,
) {
    Box(
        modifier
            .softShadow(corner)
            .clip(RoundedCornerShape(corner))
            .background(color)
    ) {
        content()
    }
}

/** Press-to-squash: scales down while held, springs back with a wobble on release. */
@Composable
fun Modifier.pressSquash(
    depth: Float = 0.90f,
    onTap: (() -> Unit)? = null,
): Modifier {
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()
    val view = LocalView.current
    return this
        .graphicsLayer { scaleX = scale.value; scaleY = scale.value }
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    scope.launch { scale.animateTo(depth, spring(stiffness = 1200f)) }
                    val released = tryAwaitRelease()
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                    scope.launch {
                        scale.animateTo(1f, spring(dampingRatio = 0.32f, stiffness = 420f))
                    }
                    if (released) onTap?.invoke()
                }
            )
        }
}
