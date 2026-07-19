package com.softmachine.ui

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.designlens.core.ShaderPanel
import com.designlens.core.rememberTimeSeconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object SoftPages {
    const val HERO = 0
    const val HOME = 1
    const val CUSHION = 2
    const val SILICONE = 3
    const val BRAND = 4
    const val SYSTEM = 5
    const val SOUND = 6
    const val MATERIAL = 7
    const val TYPOGRAPHY = 8
    const val PLAYER = 9
    const val MOTION = 10
    const val COLOR = 11
    const val COUNT = 12
}

/** The full Soft Machine style: a 12-page horizontal pager. */
@Composable
fun SoftMachineStyle() {
    val pager = rememberPagerState { SoftPages.COUNT }
    Box(Modifier.fillMaxSize().background(SoftColors.bg)) {
        HorizontalPager(pager, Modifier.fillMaxSize()) { page ->
            when (page) {
                SoftPages.HERO -> SoftHeroPage()
                SoftPages.HOME -> SoftHomePage()
                SoftPages.CUSHION -> CushionPage()
                SoftPages.SILICONE -> SiliconePage()
                SoftPages.BRAND -> SoftBrandPage()
                SoftPages.SYSTEM -> SoftSystemPage()
                SoftPages.SOUND -> SoftSoundPage()
                SoftPages.COLOR -> SoftColorPage()
                SoftPages.MATERIAL -> SoftMaterialPage()
                SoftPages.TYPOGRAPHY -> SoftTypographyPage()
                SoftPages.PLAYER -> SoftPlayerPage()
                SoftPages.MOTION -> SoftMotionPage()
            }
        }
        SoftPageIndicator(pager, Modifier.align(Alignment.BottomCenter))
    }
}

/** Live miniature for the landing card: the phone home screen. */
@Composable
fun SoftMachinePreview(modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier) {
        val scale = maxWidth / 130.dp
        val base = LocalDensity.current
        CompositionLocalProvider(LocalDensity provides Density(base.density * scale, 1f)) {
            SoftHome(Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun SoftPageIndicator(state: PagerState, modifier: Modifier) {
    Row(
        modifier
            .navigationBarsPadding()
            .padding(bottom = 10.dp)
            .softShadow(corner = 50.dp, offset = 2.dp, blur = 6.dp)
            .clip(RoundedCornerShape(50))
            .background(Color(0xF0EFEAE5))
            .padding(horizontal = 9.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(state.pageCount) { i ->
            val active = state.currentPage == i
            Box(
                Modifier
                    .size(if (active) 12.dp else 4.dp, 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (active) SoftColors.lavenderDeep else SoftColors.faint)
            )
        }
    }
}

@Composable
private fun SoftScaffold(
    label: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    BoxWithConstraints(Modifier.fillMaxSize().background(SoftColors.bg)) {
        val scale = maxWidth / 160.dp
        val base = LocalDensity.current
        CompositionLocalProvider(LocalDensity provides Density(base.density * scale, 1f)) {
            Column(Modifier.fillMaxSize().padding(horizontal = 18.dp)) {
                Spacer(Modifier.height(WindowInsets.statusBars.asPaddingValues().calculateTopPadding()))
                Spacer(Modifier.height(16.dp))
                SoftLabel(label, 7.sp, SoftColors.faint)
                Spacer(Modifier.weight(0.8f))
                content()
                Spacer(Modifier.weight(1f))
                Spacer(Modifier.height(26.dp))
            }
        }
    }
}

// --------------------------------------------------------------------- pages

@Composable
private fun SoftHeroPage() {
    BoxWithConstraints(Modifier.fillMaxSize().background(SoftColors.bg)) {
        val scale = maxWidth / 190.dp
        val base = LocalDensity.current
        CompositionLocalProvider(LocalDensity provides Density(base.density * scale, 1f)) {
            Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                Spacer(Modifier.height(WindowInsets.statusBars.asPaddingValues().calculateTopPadding()))
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.Top) {
                    SoftText(
                        "SOFT\nMACHINE", 26.sp, Color(0xFF5A554F),
                        weight = FontWeight.Light, spacingEm = 0.05f, lineHeight = 32.sp
                    )
                    Spacer(Modifier.width(12.dp))
                    SoftFlower(Modifier.padding(top = 6.dp).size(16.dp))
                }
                Spacer(Modifier.weight(0.85f))
                CloudCard(Modifier.fillMaxWidth().aspectRatio(1.05f))
                Spacer(Modifier.weight(1f))
                Spacer(Modifier.height(34.dp))
            }
        }
    }
}

@Composable
private fun SoftHomePage() {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val scale = maxWidth / 162.dp
        val base = LocalDensity.current
        CompositionLocalProvider(LocalDensity provides Density(base.density * scale, 1f)) {
            val top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
            val bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            SoftHome(Modifier.fillMaxSize(), topPad = top, bottomPad = bottom + 14.dp)
        }
    }
}

@Composable
private fun CushionPage() {
    SoftScaffold("AIR CORE") {
        var knob by remember { mutableFloatStateOf(0.25f) }
        val view = LocalView.current
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(100f / 141f)
                .softShadow(corner = 22.dp)
                .clip(RoundedCornerShape(22.dp))
                .pointerInput(Unit) {
                    // vertical-only so the pager can still swipe horizontally
                    detectVerticalDragGestures { change, _ ->
                        val h = size.height.toFloat()
                        val top = h * (50f / 141f)
                        val bottom = h * (90f / 141f)
                        val new = ((change.position.y - top) / (bottom - top)).coerceIn(0f, 1f)
                        if ((new * 10).toInt() != (knob * 10).toInt()) {
                            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                        }
                        knob = new
                        change.consume()
                    }
                }
        ) {
            ShaderPanel(SoftShaders.CUSHION_PLATE, Modifier.fillMaxSize()) {
                setFloatUniform("uKnob", knob)
            }
        }
    }
}

@Composable
private fun SiliconePage() {
    SoftScaffold("SOFT TOUCH") {
        val press = remember { Animatable(0f) }
        var touch by remember { mutableStateOf(Offset(0.5f, 0.5f)) }
        val scope = rememberCoroutineScope()
        val view = LocalView.current
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(1.25f)
                .softShadow(corner = 24.dp)
                .clip(RoundedCornerShape(24.dp))
                .pointerInput(Unit) {
                    awaitEachGesture {
                        trackSiliconePress(view, scope, press) { touch = it }
                    }
                }
        ) {
            ShaderPanel(SoftShaders.SILICONE_PAD, Modifier.fillMaxSize()) { drawSize ->
                setFloatUniform("uTouch", touch.x * drawSize.width, touch.y * drawSize.height)
                setFloatUniform("uPress", press.value)
            }
        }
    }
}

@Composable
private fun SoftBrandPage() {
    SoftScaffold("PRINCIPLES") {
        SoftFeaturesCard(Modifier.fillMaxWidth().height(112.dp))
    }
}

@Composable
private fun SoftSystemPage() {
    SoftScaffold("SYSTEM") {
        SystemStatusCard(Modifier.fillMaxWidth().height(108.dp))
        Spacer(Modifier.height(14.dp))
        SoftButtonsRow(Modifier.fillMaxWidth().padding(horizontal = 10.dp))
        Spacer(Modifier.height(14.dp))
        SoftBrightness(Modifier.fillMaxWidth().height(30.dp))
    }
}

@Composable
private fun SoftSoundPage() {
    SoftScaffold("SOUND") {
        SoftEqualizerCard(Modifier.fillMaxWidth().height(124.dp))
        Spacer(Modifier.height(18.dp))
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            PebbleButton(Modifier.size(54.dp, 40.dp))
        }
    }
}

@Composable
private fun SoftColorPage() {
    SoftScaffold("COLOR") {
        SoftPaletteCard(Modifier.fillMaxWidth().height(146.dp))
    }
}

@Composable
private fun SoftMaterialPage() {
    SoftScaffold("MATERIAL") {
        val tiles = listOf(
            Triple("SOFT\nSILICONE", SoftShaders.SILICONE_PAD, TileMaterial.SILICONE),
            Triple("MILKY\nGLASS", SoftShaders.MILKY_GLASS, TileMaterial.GLASS),
            Triple("MICRO\nTEXTURE", SoftShaders.MICRO_TEXTURE, TileMaterial.MICRO),
        )
        tiles.forEachIndexed { i, (_, shader, material) ->
            InteractiveTile(material, shader, Modifier.fillMaxWidth().height(36.dp))
            if (i < tiles.lastIndex) Spacer(Modifier.height(12.dp))
        }
    }
}

private enum class TileMaterial { SILICONE, GLASS, MICRO }

/** A material swatch whose touch physics match the material it renders. */
@Composable
private fun InteractiveTile(material: TileMaterial, shader: String, modifier: Modifier) {
    val press = remember { Animatable(0f) }
    val spread = remember { Animatable(0f) }
    var touch by remember { mutableStateOf(Offset(0.5f, 0.5f)) }
    val scope = rememberCoroutineScope()
    val view = LocalView.current
    Box(
        modifier
            .softShadow(corner = 12.dp, offset = 3.dp, blur = 7.dp)
            .clip(RoundedCornerShape(12.dp))
            .pointerInput(material) {
                awaitEachGesture {
                    when (material) {
                        TileMaterial.SILICONE -> trackSiliconePress(view, scope, press) { touch = it }
                        TileMaterial.GLASS -> trackGlassWipe(view, scope, press, spread) { touch = it }
                        TileMaterial.MICRO -> trackMicroPress(view, scope, press) { touch = it }
                    }
                }
            }
    ) {
        ShaderPanel(shader, Modifier.fillMaxSize()) { drawSize ->
            setFloatUniform("uTouch", touch.x * drawSize.width, touch.y * drawSize.height)
            setFloatUniform("uPress", press.value)
            if (material == TileMaterial.GLASS) {
                setFloatUniform("uSpread", spread.value)
            }
        }
    }
}

/**
 * Rigid glass: the wipe appears immediately and spreads while held; on release
 * the wiped patch stays put and the fog slowly re-condenses over it. No bounce —
 * glass does not deform.
 */
private suspend fun AwaitPointerEventScope.trackGlassWipe(
    view: android.view.View,
    scope: CoroutineScope,
    clarity: Animatable<Float, AnimationVector1D>,
    spread: Animatable<Float, AnimationVector1D>,
    setTouch: (Offset) -> Unit,
) {
    val down = awaitFirstDown()
    setTouch(Offset(down.position.x / size.width, down.position.y / size.height))
    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
    scope.launch { clarity.animateTo(1f, tween(70)) }
    scope.launch {
        spread.snapTo(0.2f)
        spread.animateTo(1f, tween(1500, easing = LinearOutSlowInEasing))
    }
    var released = false
    while (!released) {
        val event = awaitPointerEvent()
        val change = event.changes.first()
        if (change.pressed) {
            setTouch(Offset(change.position.x / size.width, change.position.y / size.height))
            change.consume()
        } else {
            released = true
        }
    }
    // fog re-forms slowly where it was wiped
    scope.launch { clarity.animateTo(0f, tween(1900, easing = FastOutSlowInEasing)) }
}

/**
 * Stiff micro-bump sheet: compresses fast and bottoms out, snaps back crisply,
 * and ticks under the finger as it crosses bump rows.
 */
private suspend fun AwaitPointerEventScope.trackMicroPress(
    view: android.view.View,
    scope: CoroutineScope,
    press: Animatable<Float, AnimationVector1D>,
    setTouch: (Offset) -> Unit,
) {
    val down = awaitFirstDown()
    setTouch(Offset(down.position.x / size.width, down.position.y / size.height))
    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
    scope.launch { press.animateTo(1f, tween(160, easing = LinearOutSlowInEasing)) }
    var last = down.position
    var acc = 0f
    val bumpPx = size.width / 13f
    var released = false
    while (!released) {
        val event = awaitPointerEvent()
        val change = event.changes.first()
        if (change.pressed) {
            acc += (change.position - last).getDistance()
            last = change.position
            if (acc > bumpPx) {
                acc = 0f
                view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
            }
            setTouch(Offset(change.position.x / size.width, change.position.y / size.height))
            change.consume()
        } else {
            released = true
        }
    }
    // thin bumps snap straight back
    scope.launch { press.animateTo(0f, spring(dampingRatio = 0.55f, stiffness = 1400f)) }
}

/**
 * Shared press physics: fast shallow contact, slow sink while held,
 * springy wobble on release. Depth = how long the finger stayed down.
 */
private suspend fun AwaitPointerEventScope.trackSiliconePress(
    view: android.view.View,
    scope: CoroutineScope,
    press: Animatable<Float, AnimationVector1D>,
    setTouch: (Offset) -> Unit,
) {
    val down = awaitFirstDown()
    setTouch(Offset(down.position.x / size.width, down.position.y / size.height))
    view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
    scope.launch {
        press.animateTo(0.35f, tween(110, easing = LinearOutSlowInEasing))
        press.animateTo(1f, tween(1100, easing = FastOutSlowInEasing))
    }
    var released = false
    while (!released) {
        val event = awaitPointerEvent()
        val change = event.changes.first()
        if (change.pressed) {
            setTouch(Offset(change.position.x / size.width, change.position.y / size.height))
            change.consume()
        } else {
            released = true
        }
    }
    view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
    scope.launch { press.animateTo(0f, spring(dampingRatio = 0.30f, stiffness = 260f)) }
}

@Composable
private fun SoftTypographyPage() {
    SoftScaffold("TYPOGRAPHY") {
        SoftTypographyCard(Modifier.fillMaxWidth().height(142.dp))
    }
}

@Composable
private fun SoftPlayerPage() {
    SoftScaffold("PLAYER") {
        val time by rememberTimeSeconds()
        SoftPlayerCard(Modifier.fillMaxWidth().height(148.dp))
        Spacer(Modifier.height(14.dp))
        SoftTiles(Modifier.fillMaxWidth(), time)
    }
}

@Composable
private fun SoftMotionPage() {
    SoftScaffold("MOTION") {
        val time by rememberTimeSeconds()
        SoftMicroCard(Modifier.fillMaxWidth().height(112.dp), time)
    }
}
