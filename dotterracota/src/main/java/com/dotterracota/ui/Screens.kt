package com.dotterracota.ui

import com.designlens.core.MonoText
import com.designlens.core.ShaderPanel
import com.designlens.core.rememberTimeSeconds

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

object Pages {
    const val HERO = 0
    const val HOME = 1
    const val HARDWARE = 2
    const val BRAND = 3
    const val SYSTEM = 4
    const val SOUND = 5
    const val MATERIAL = 6
    const val TYPOGRAPHY = 7
    const val SPEAKER = 8
    const val PLAYER = 9
    const val MOTION = 10
    const val COLOR = 11
    const val COUNT = 12
}

/** The full Dot Terracota style: a 12-page horizontal pager. */
@Composable
fun DotTerracotaStyle() {
    val pager = rememberPagerState { Pages.COUNT }
    Box(Modifier.fillMaxSize().background(Palette.pageBg)) {
        HorizontalPager(pager, Modifier.fillMaxSize()) { page ->
            when (page) {
                Pages.HERO -> HeroPage()
                Pages.HOME -> HomePage()
                Pages.HARDWARE -> HardwarePage()
                Pages.BRAND -> BrandPage()
                Pages.SYSTEM -> SystemPage()
                Pages.SOUND -> SoundPage()
                Pages.COLOR -> ColorPage()
                Pages.MATERIAL -> MaterialPage()
                Pages.TYPOGRAPHY -> TypographyPage()
                Pages.SPEAKER -> SpeakerPage()
                Pages.PLAYER -> PlayerPage()
                Pages.MOTION -> MotionPage()
            }
        }
        PageIndicator(pager, Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
private fun PageIndicator(state: PagerState, modifier: Modifier) {
    Row(
        modifier
            .navigationBarsPadding()
            .padding(bottom = 10.dp)
            .clip(RoundedCornerShape(50))
            .background(Color(0x55111112))
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
                    .background(if (active) Palette.terra else Color(0x99F7F4F1))
            )
        }
    }
}

/**
 * A detail page: rescales density so [designWidth] design units fill the screen width,
 * giving each artifact large real estate while reusing the poster components verbatim.
 */
@Composable
private fun ScreenScaffold(
    designWidth: Float,
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    BoxWithConstraints(Modifier.fillMaxSize().background(Palette.pageBg)) {
        val scale = maxWidth / designWidth.dp
        val base = LocalDensity.current
        CompositionLocalProvider(LocalDensity provides Density(base.density * scale, 1f)) {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 13.dp)
            ) {
                Spacer(Modifier.height(WindowInsets.statusBars.asPaddingValues().calculateTopPadding()))
                Spacer(Modifier.height(8.dp))
                DotMatrixText(title, pitch = 2.2.dp, color = Color(0xFF23201C))
                Spacer(Modifier.height(13.dp))
                content()
                Spacer(Modifier.height(34.dp))
            }
        }
    }
}

/**
 * Live miniature of the style for the landing card: the hero headline over the
 * planet, rendered at a small design width via the density-rescale trick.
 */
@Composable
fun DotTerracotaPreview(modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier) {
        val scale = maxWidth / 130.dp
        val base = LocalDensity.current
        CompositionLocalProvider(LocalDensity provides Density(base.density * scale, 1f)) {
            Box(Modifier.fillMaxSize()) {
                ShaderPanel(Shaders.HERO_BG, Modifier.fillMaxSize())
                Column(Modifier.fillMaxSize().padding(12.dp)) {
                    DotMatrixText(
                        "DOT\nTERRACOTA",
                        pitch = 1.9.dp,
                        color = Color(0xFFF3E9DD),
                        lineGapRows = 3,
                        glow = true,
                    )
                    Spacer(Modifier.weight(1f))
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        OrbitPlanet(Modifier.size(96.dp))
                    }
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

// --------------------------------------------------------------------- pages

@Composable
private fun HeroPage() {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val scale = maxWidth / 200.dp
        val base = LocalDensity.current
        CompositionLocalProvider(LocalDensity provides Density(base.density * scale, 1f)) {
            Box(Modifier.fillMaxSize()) {
                ShaderPanel(Shaders.HERO_BG, Modifier.fillMaxSize())
                Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                    Spacer(Modifier.height(WindowInsets.statusBars.asPaddingValues().calculateTopPadding()))
                    Spacer(Modifier.height(14.dp))
                    DotMatrixText(
                        "DOT\nTERRACOTA",
                        pitch = 2.6.dp,
                        color = Color(0xFFF3E9DD),
                        lineGapRows = 3,
                        glow = true,
                    )
                    Spacer(Modifier.weight(1f))
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        OrbitPlanet(Modifier.size(168.dp))
                    }
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun HomePage() {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val scale = maxWidth / 138.dp
        val base = LocalDensity.current
        CompositionLocalProvider(LocalDensity provides Density(base.density * scale, 1f)) {
            val top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
            LockScreen(Modifier.fillMaxSize(), topPad = top)
        }
    }
}

@Composable
private fun HardwarePage() {
    ScreenScaffold(166f, "DOT MATRIX\nINTERFACE") {
        val scope = rememberCoroutineScope()
        val thump = remember { Animatable(0f) }
        var lightsOn by remember { mutableStateOf(true) }
        val lights by animateFloatAsState(if (lightsOn) 1f else 0f, tween(420), label = "lights")
        val view = LocalView.current
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(140f / 170f)
                .pointerInput(Unit) {
                    detectTapGestures { o ->
                        // terracotta disc = light switch; anywhere else = subwoofer thump
                        val u = size.width / 140f
                        if ((o - Offset(100f * u, 36f * u)).getDistance() < 30f * u) {
                            lightsOn = !lightsOn
                            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                        } else {
                            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                            scope.launch {
                                thump.snapTo(1f)
                                thump.animateTo(0f, spring(dampingRatio = 0.13f, stiffness = 170f))
                            }
                        }
                    }
                }
        ) {
            ShaderPanel(Shaders.TOP_PLATE, Modifier.fillMaxSize(), animated = true) {
                setFloatUniform("uPulse", thump.value)
                setFloatUniform("uLight", lights)
            }
        }
    }
}

@Composable
private fun BrandPage() {
    ScreenScaffold(166f, "WARM\nBY DESIGN") {
        FutureIsWarmCard(Modifier.fillMaxWidth().height(100.dp))
        Spacer(Modifier.height(12.dp))
        FeaturesCard(Modifier.fillMaxWidth().height(92.dp))
    }
}

@Composable
private fun SystemPage() {
    ScreenScaffold(150f, "STATUS") {
        BatteryCard(Modifier.fillMaxWidth().height(122.dp))
        Spacer(Modifier.height(14.dp))
        TogglesRow(Modifier.fillMaxWidth())
        Spacer(Modifier.height(14.dp))
        BrightnessBar(Modifier.fillMaxWidth().height(35.dp))
    }
}

@Composable
private fun SoundPage() {
    ScreenScaffold(150f, "EQUALIZER") {
        var volume by remember { mutableIntStateOf(50) }
        EqualizerCard(Modifier.fillMaxWidth().height(138.dp))
        Spacer(Modifier.height(18.dp))
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            KnobDial(Modifier.size(66.dp), interactive = true, onVolume = { volume = it })
        }
        Spacer(Modifier.height(8.dp))
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            DotBar(
                volume / 100f, Modifier.width(66.dp).height(5.dp), count = 20,
                litColor = Color(0xFF2B2721), dimColor = Color(0x332B2721)
            )
        }

    }
}

@Composable
private fun ColorPage() {
    ScreenScaffold(132f, "PALETTE") {
        PaletteCard(Modifier.fillMaxWidth())
    }
}

@Composable
private fun MaterialPage() {
    ScreenScaffold(164f, "TEXTURE") {
        MaterialTextureCard(Modifier)
    }
}

@Composable
private fun TypographyPage() {
    ScreenScaffold(193f, "DOT TYPE") {
        TypographyCard(Modifier.fillMaxWidth().height(152.dp))
    }
}

@Composable
private fun SpeakerPage() {
    ScreenScaffold(150f, "BACKLIGHT") {
        val scope = rememberCoroutineScope()
        val thump = remember { Animatable(0f) }
        var lightsOn by remember { mutableStateOf(true) }
        val lights by animateFloatAsState(if (lightsOn) 1f else 0f, tween(420), label = "ringLight")
        val view = LocalView.current
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(103f / 140f)
                .pointerInput(Unit) {
                    detectTapGestures { o ->
                        // terracotta dot = light switch; anywhere else = subwoofer thump
                        val u = size.width / 103f
                        if ((o - Offset(84f * u, 100f * u)).getDistance() < 14f * u) {
                            lightsOn = !lightsOn
                            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                        } else {
                            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                            scope.launch {
                                thump.snapTo(1f)
                                thump.animateTo(0f, spring(dampingRatio = 0.13f, stiffness = 170f))
                            }
                        }
                    }
                }
        ) {
            ShaderPanel(Shaders.DISC_PLATE, Modifier.fillMaxSize(), animated = true) {
                setFloatUniform("uPulse", thump.value)
                setFloatUniform("uLight", lights)
            }
        }
    }
}

@Composable
private fun PlayerPage() {
    ScreenScaffold(160f, "AMBIENT\nCLAY") {
        val time by rememberTimeSeconds()
        Row(Modifier.fillMaxWidth()) {
            PlayerCard(Modifier.weight(1f).height(162.dp))
            Spacer(Modifier.width(10.dp))
            SmallTiles(Modifier.width(41.dp), time)
        }
    }
}

@Composable
private fun MotionPage() {
    ScreenScaffold(160f, "MOTION") {
        val time by rememberTimeSeconds()
        MicroInteractionsCard(Modifier.fillMaxWidth().height(172.dp), time)
    }
}
