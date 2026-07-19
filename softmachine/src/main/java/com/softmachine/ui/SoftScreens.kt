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
    const val COLOR = 7
    const val MATERIAL = 8
    const val TYPOGRAPHY = 9
    const val PLAYER = 10
    const val MOTION = 11
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
    designWidth: Float,
    eyebrow: String,
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    BoxWithConstraints(Modifier.fillMaxSize().background(SoftColors.bg)) {
        val scale = maxWidth / designWidth.dp
        val base = LocalDensity.current
        CompositionLocalProvider(LocalDensity provides Density(base.density * scale, 1f)) {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp)
            ) {
                Spacer(Modifier.height(WindowInsets.statusBars.asPaddingValues().calculateTopPadding()))
                Spacer(Modifier.height(10.dp))
                SoftLabel(eyebrow, 6.5.sp, SoftColors.faint)
                Spacer(Modifier.height(4.dp))
                SoftText(title, 17.sp, SoftColors.ink, weight = FontWeight.Light, spacingEm = 0.06f, lineHeight = 21.sp)
                Spacer(Modifier.height(14.dp))
                content()
                Spacer(Modifier.height(36.dp))
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
                Spacer(Modifier.height(10.dp))
                SoftLabel("GENTLE SYSTEMS,\nHUMAN FUTURES.", 7.sp, lineHeight = 11.sp)
                Spacer(Modifier.weight(0.6f))
                CloudCard(Modifier.fillMaxWidth().aspectRatio(1.05f))
                Spacer(Modifier.weight(1f))
                SoftText(
                    "A design language inspired by\nsoftness and fluid mechanics.\nInterfaces that feel familiar,\nmaterials that invite touch,\nsystems that breathe with you.",
                    7.5.sp, SoftColors.dim, lineHeight = 12.5.sp
                )
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
    SoftScaffold(150f, "03 / 12 — HARDWARE", "AIR CORE") {
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
        Spacer(Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth()) {
            SoftLabel("AIR CORE\nCUSHION UNIT", 6.5.sp, lineHeight = 10.5.sp)
            Spacer(Modifier.weight(1f))
            SoftLabel("PRESSURE\nRELEASE\nCHANNELS", 6.5.sp, SoftColors.faint, lineHeight = 10.5.sp)
        }
    }
}

@Composable
private fun SiliconePage() {
    SoftScaffold(150f, "04 / 12 — MATERIAL", "SOFT TOUCH") {
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
            Box(Modifier.align(Alignment.TopStart).padding(14.dp)) {
                SoftText(
                    "SOFT TOUCH\nSILICONE", 9.sp, Color(0xFF4E4658).copy(alpha = 0.85f),
                    weight = FontWeight.Medium, spacingEm = 0.14f, lineHeight = 14.sp
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        SoftText("Press and hold. The longer you press, the deeper it sinks.", 7.sp, SoftColors.dim)
    }
}

@Composable
private fun SoftBrandPage() {
    SoftScaffold(150f, "05 / 12 — PRINCIPLES", "GENTLE BY\nDEFAULT") {
        SoftFeaturesCard(Modifier.fillMaxWidth().height(110.dp))
        Spacer(Modifier.height(14.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .softShadow(corner = 20.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(SoftColors.lavenderPale)
                .padding(16.dp)
        ) {
            SoftText(
                "Nothing snaps.\nEverything eases.",
                11.sp, Color(0xFF4E4658), weight = FontWeight.Light, lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun SoftSystemPage() {
    SoftScaffold(150f, "06 / 12 — SYSTEM", "STATUS") {
        SystemStatusCard(Modifier.fillMaxWidth().height(128.dp))
        Spacer(Modifier.height(16.dp))
        SoftButtonsRow(Modifier.fillMaxWidth().padding(horizontal = 4.dp))
        Spacer(Modifier.height(16.dp))
        SoftBrightness(Modifier.fillMaxWidth().height(34.dp))
    }
}

@Composable
private fun SoftSoundPage() {
    SoftScaffold(150f, "07 / 12 — SOUND", "EQUALIZER") {
        SoftEqualizerCard(Modifier.fillMaxWidth().height(130.dp))
        Spacer(Modifier.height(20.dp))
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            PebbleButton(Modifier.size(56.dp, 42.dp))
        }
        Spacer(Modifier.height(8.dp))
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            SoftLabel("SQUEEZE ME", 6.sp, SoftColors.faint)
        }
    }
}

@Composable
private fun SoftColorPage() {
    SoftScaffold(132f, "08 / 12 — COLOR", "PALETTE") {
        SoftPaletteCard(Modifier.fillMaxWidth())
    }
}

@Composable
private fun SoftMaterialPage() {
    SoftScaffold(150f, "09 / 12 — MATERIAL", "TEXTURES") {
        val tiles = listOf(
            Triple("SOFT\nSILICONE", SoftShaders.SILICONE_PAD, TileMaterial.SILICONE),
            Triple("MILKY\nGLASS", SoftShaders.MILKY_GLASS, TileMaterial.GLASS),
            Triple("MICRO\nTEXTURE", SoftShaders.MICRO_TEXTURE, TileMaterial.MICRO),
        )
        tiles.forEach { (label, shader, material) ->
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                InteractiveTile(material, shader, Modifier.size(68.dp, 34.dp))
                Spacer(Modifier.width(12.dp))
                SoftText(label, 6.sp, SoftColors.dim, weight = FontWeight.Medium, spacingEm = 0.12f, lineHeight = 10.sp)
            }
            Spacer(Modifier.height(12.dp))
        }
        Spacer(Modifier.height(2.dp))
        SoftText("Each material answers touch its own way.", 7.sp, SoftColors.dim)
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
    SoftScaffold(160f, "10 / 12 — TYPOGRAPHY", "SOFT NEUE") {
        SoftTypographyCard(Modifier.fillMaxWidth().height(158.dp))
    }
}

@Composable
private fun SoftPlayerPage() {
    SoftScaffold(160f, "11 / 12 — PLAYER", "AMBIENT\nCLOUD") {
        val time by rememberTimeSeconds()
        Row(Modifier.fillMaxWidth()) {
            SoftPlayerCard(Modifier.weight(1f).height(152.dp))
            Spacer(Modifier.width(12.dp))
            SoftTiles(Modifier.width(38.dp), time)
        }
    }
}

@Composable
private fun SoftMotionPage() {
    SoftScaffold(160f, "12 / 12 — MICRO INTERACTIONS", "MOTION") {
        val time by rememberTimeSeconds()
        SoftMicroCard(Modifier.fillMaxWidth().height(150.dp), time)
        Spacer(Modifier.height(12.dp))
        SoftText("Tap the bounce ball.", 7.sp, SoftColors.dim)
    }
}
