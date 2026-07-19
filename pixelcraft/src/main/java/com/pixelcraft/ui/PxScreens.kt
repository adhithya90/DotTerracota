package com.pixelcraft.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.designlens.core.rememberTimeSeconds
import com.pixelcraft.ui.Scenes.deviceBack
import com.pixelcraft.ui.Scenes.deviceEdge
import com.pixelcraft.ui.Scenes.lockWallpaper
import com.pixelcraft.ui.Scenes.nightCity
import com.pixelcraft.ui.Scenes.tileDither
import com.pixelcraft.ui.Scenes.tileGrid
import com.pixelcraft.ui.Scenes.tileNoise

object PxPages {
    const val HERO = 0
    const val HOME = 1
    const val DEVICE = 2
    const val ICONS = 3
    const val TOGGLES = 4
    const val WIDGETS = 5
    const val UI = 6
    const val TYPE = 7
    const val TEXTURE = 8
    const val ARCADE = 9
    const val MOTION = 10
    const val COLOR = 11
    const val COUNT = 12
}

/** The full Pixel Craft style: a 12-page horizontal pager. */
@Composable
fun PixelCraftStyle() {
    val pager = rememberPagerState { PxPages.COUNT }
    Box(Modifier.fillMaxSize().background(Px.paper)) {
        HorizontalPager(pager, Modifier.fillMaxSize()) { page ->
            when (page) {
                PxPages.HERO -> HeroPage()
                PxPages.HOME -> HomePage()
                PxPages.DEVICE -> DevicePage()
                PxPages.ICONS -> IconsPage()
                PxPages.TOGGLES -> TogglesPage()
                PxPages.WIDGETS -> WidgetsPage()
                PxPages.UI -> UiPage()
                PxPages.TYPE -> TypePage()
                PxPages.TEXTURE -> TexturePage()
                PxPages.ARCADE -> ArcadePage()
                PxPages.MOTION -> MotionPage()
                PxPages.COLOR -> ColorPage()
            }
        }
        PxIndicator(pager, Modifier.align(Alignment.BottomCenter))
    }
}

/** Live miniature for the landing card. */
@Composable
fun PixelCraftPreview(modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier) {
        val scale = maxWidth / 130.dp
        val base = LocalDensity.current
        CompositionLocalProvider(LocalDensity provides Density(base.density * scale, 1f)) {
            Column(
                Modifier.fillMaxSize().background(Px.paper).padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.Top) {
                    Column {
                        PxTitle("PIXEL", 2.2.dp, Px.ink)
                        Spacer(Modifier.height(4.dp))
                        PxTitle("CRAFT", 2.2.dp, Px.ink)
                    }
                    Spacer(Modifier.width(4.dp))
                    Sprite(Sprites.heart, mapOf('1' to Px.pink), 1.6.dp)
                }
                Spacer(Modifier.weight(1f))
                Sprite(Sprites.cat, Sprites.catPal, 3.dp)
                Spacer(Modifier.weight(1f))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(Px.blue, Px.pink, Px.yellow, Px.green, Px.ink).forEach {
                        Box(Modifier.size(12.dp).background(it).border(1.dp, Px.ink))
                    }
                }
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun PxIndicator(state: PagerState, modifier: Modifier) {
    Row(
        modifier
            .navigationBarsPadding()
            .padding(bottom = 10.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(Px.white)
            .border(1.dp, Px.ink, RoundedCornerShape(3.dp))
            .padding(horizontal = 7.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(state.pageCount) { i ->
            val active = state.currentPage == i
            Box(
                Modifier
                    .size(if (active) 9.dp else 4.dp, 4.dp)
                    .background(if (active) Px.blue else Color(0xFFC9C4BB))
            )
        }
    }
}

@Composable
private fun PxScaffold(label: String, content: @Composable ColumnScope.() -> Unit) {
    BoxWithConstraints(Modifier.fillMaxSize().background(Px.paper)) {
        val scale = maxWidth / 160.dp
        val base = LocalDensity.current
        CompositionLocalProvider(LocalDensity provides Density(base.density * scale, 1f)) {
            Column(Modifier.fillMaxSize().padding(horizontal = 18.dp)) {
                Spacer(Modifier.height(WindowInsets.statusBars.asPaddingValues().calculateTopPadding()))
                Spacer(Modifier.height(16.dp))
                PxText(label, 1.15.dp, Px.label)
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
private fun HeroPage() {
    BoxWithConstraints(Modifier.fillMaxSize().background(Px.paper)) {
        val scale = maxWidth / 160.dp
        val base = LocalDensity.current
        CompositionLocalProvider(LocalDensity provides Density(base.density * scale, 1f)) {
            Column(Modifier.fillMaxSize().padding(horizontal = 18.dp)) {
                Spacer(Modifier.height(WindowInsets.statusBars.asPaddingValues().calculateTopPadding()))
                Spacer(Modifier.height(20.dp))
                Row(verticalAlignment = Alignment.Top) {
                    Column {
                        PxTitle("PIXEL", 2.6.dp, Px.ink)
                        Spacer(Modifier.height(5.dp))
                        PxTitle("CRAFT", 2.6.dp, Px.ink)
                    }
                    Spacer(Modifier.width(5.dp))
                    Sprite(Sprites.heart, mapOf('1' to Px.pink), 2.dp)
                }
                Spacer(Modifier.weight(0.8f))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Sprite(Sprites.cat, Sprites.catPal, 4.2.dp)
                    Spacer(Modifier.width(18.dp))
                    Sprite(Sprites.ditherHeart, Sprites.ditherPal, 3.4.dp)
                }
                Spacer(Modifier.weight(1f))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                        listOf(Px.blue, Px.pink, Px.yellow, Px.green, Px.ink).forEach {
                            Box(Modifier.size(15.dp).background(it).border(1.dp, Px.ink))
                        }
                    }
                }
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun HomePage() {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val scale = maxWidth / 160.dp
        val base = LocalDensity.current
        CompositionLocalProvider(LocalDensity provides Density(base.density * scale, 1f)) {
            val time by rememberTimeSeconds()
            Box(Modifier.fillMaxSize().clipToBounds()) {
                Canvas(Modifier.fillMaxSize()) {
                    val cell = size.height / Scenes.LOCK_H
                    val cols = (size.width / cell).toInt() + 1
                    lockWallpaper(cell, boatShift = frameOf(time, 0.5f, 24) - 12, wCols = cols)
                }
                Column(Modifier.fillMaxSize().padding(horizontal = 12.dp)) {
                    Spacer(Modifier.height(WindowInsets.statusBars.asPaddingValues().calculateTopPadding()))
                    Spacer(Modifier.height(8.dp))
                    Row {
                        PxText("TUE, 21 MAY", 1.15.dp, Color(0xFF22304A))
                        Spacer(Modifier.weight(1f))
                        Sprite(listOf("...1..1.111", "..11..1.111", ".111.11.111", "1111.11.111"), mapOf('1' to Color(0xFF22304A)), 1.1.dp)
                    }
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Sprite(Sprites.sun, mapOf('1' to Color(0xFFF0B429)), 1.4.dp)
                        Spacer(Modifier.width(4.dp))
                        PxText("24°", 1.15.dp, Color(0xFF22304A))
                    }
                    Spacer(Modifier.height(16.dp))
                    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        PxText("09", 4.dp, Px.blue, gap = 1)
                        Spacer(Modifier.height(4.dp))
                        PxText("30", 4.dp, Px.pink, gap = 1)
                    }
                    Spacer(Modifier.weight(1f))
                    Row(Modifier.fillMaxWidth().height(42.dp), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        Row(
                            Modifier.weight(1f).fillMaxSize()
                                .clip(RoundedCornerShape(6.dp)).background(Color(0xE0121824))
                                .border(0.5.dp, Color(0x22FFFFFF), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Sprite(Sprites.sun, mapOf('1' to Px.yellow), 1.6.dp)
                            Spacer(Modifier.width(6.dp))
                            Column {
                                PxText("24°", 1.5.dp, Px.white)
                                Spacer(Modifier.height(3.dp))
                                PxText("SUNNY", 0.95.dp, Color(0xFF93A0B4))
                            }
                        }
                        Row(
                            Modifier.weight(1f).fillMaxSize()
                                .clip(RoundedCornerShape(6.dp)).background(Color(0xE0121824))
                                .border(0.5.dp, Color(0x22FFFFFF), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Canvas(Modifier.size(17.dp)) {
                                var a = 0
                                while (a < 360) {
                                    val rad = Math.toRadians(a.toDouble() - 90)
                                    val r = size.minDimension * 0.42f
                                    val x = center.x + (r * Math.cos(rad)).toFloat()
                                    val y = center.y + (r * Math.sin(rad)).toFloat()
                                    drawRect(
                                        if (a < 245) Px.green else Color(0xFF2A3345),
                                        androidx.compose.ui.geometry.Offset(x - 1.dp.toPx() / 2, y - 1.dp.toPx() / 2),
                                        androidx.compose.ui.geometry.Size(1.6.dp.toPx(), 1.6.dp.toPx())
                                    )
                                    a += 10
                                }
                            }
                            Spacer(Modifier.width(5.dp))
                            Column {
                                PxText("68%", 1.2.dp, Px.white)
                                Spacer(Modifier.height(3.dp))
                                PxText("BATTERY", 0.62.dp, Color(0xFF93A0B4))
                            }
                        }
                    }
                    Spacer(Modifier.height(7.dp))
                    Column(
                        Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp)).background(Color(0xE8121824))
                            .border(0.5.dp, Color(0x22FFFFFF), RoundedCornerShape(6.dp))
                            .padding(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier.size(18.dp).clip(RoundedCornerShape(4.dp)).background(Px.blue),
                                contentAlignment = Alignment.Center
                            ) {
                                Sprite(listOf("...11", "...11", "...1.", "...1.", "..11.", "111..", "111.."), Sprites.whitePal, 1.2.dp)
                            }
                            Spacer(Modifier.width(6.dp))
                            Column {
                                PxText("8-BIT DAYS", 1.15.dp, Px.white)
                                Spacer(Modifier.height(2.dp))
                                PxText("PIXEL PILOTS", 0.95.dp, Color(0xFF93A0B4))
                            }
                            Spacer(Modifier.weight(1f))
                            Sprite(Sprites.chev, mapOf('1' to Color(0xFF93A0B4)), 1.3.dp)
                        }
                        Spacer(Modifier.height(5.dp))
                        Box(Modifier.padding(start = 24.dp).width(72.dp).height(2.dp).background(Color(0xFF2A3345))) {
                            Box(Modifier.fillMaxSize(0.38f).background(Px.pink))
                        }
                        Spacer(Modifier.height(7.dp))
                        Row(
                            Modifier.fillMaxWidth().padding(horizontal = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Sprite(Sprites.prev, Sprites.whitePal, 1.3.dp)
                            Sprite(Sprites.rew, Sprites.whitePal, 1.3.dp)
                            Sprite(Sprites.ff, Sprites.whitePal, 1.3.dp)
                            Sprite(Sprites.next, Sprites.whitePal, 1.3.dp)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Sprites.dock.forEach { (name, rows, col) ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Sprite(rows, mapOf('1' to col), 1.7.dp)
                                Spacer(Modifier.height(4.dp))
                                PxText(name, 0.7.dp, if (col == Px.green) Px.green else Color(0xFF93A0B4))
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Box(
                        Modifier.align(Alignment.CenterHorizontally)
                            .size(40.dp, 3.dp).clip(RoundedCornerShape(2.dp))
                            .background(Px.white.copy(alpha = 0.85f))
                    )
                    Spacer(Modifier.height(6.dp))
                    Spacer(Modifier.height(WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()))
                    Spacer(Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun DevicePage() {
    PxScaffold("DEVICE DETAIL") {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Canvas(Modifier.width(96.dp).aspectRatio(Scenes.DEV_W.toFloat() / Scenes.DEV_H)) {
                deviceBack(size.width / Scenes.DEV_W)
            }
        }
    }
}

@Composable
private fun IconsPage() {
    PxScaffold("ICON SET") {
        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Sprites.icons.chunked(4).forEach { rowIcons ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    rowIcons.forEach { Sprite(it, Sprites.inkPal, 2.dp) }
                }
            }
        }
    }
}

@Composable
private fun TogglesPage() {
    PxScaffold("TOGGLE BUTTONS") {
        PxPanel(Modifier.fillMaxWidth().height(84.dp), color = Px.navy, border = Color(0xFF10141B), corner = 6.dp) {
            Column(Modifier.fillMaxSize().padding(9.dp), verticalArrangement = Arrangement.SpaceBetween) {
                Sprites.toggles.chunked(4).forEach { rowT ->
                    Row(Modifier.fillMaxWidth().height(30.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        rowT.forEach { ToggleTile(it, Modifier.weight(1f).fillMaxSize()) }
                    }
                }
            }
        }
    }
}

@Composable
private fun WidgetsPage() {
    PxScaffold("DATA WIDGETS") {
        NotificationCard(Modifier.fillMaxWidth().height(58.dp))
        Spacer(Modifier.height(12.dp))
        StreakCard(Modifier.fillMaxWidth().height(48.dp))
        Spacer(Modifier.height(12.dp))
        LevelCard(Modifier.fillMaxWidth().height(48.dp))
    }
}

@Composable
private fun UiPage() {
    val time by rememberTimeSeconds()
    PxScaffold("UI COMPONENTS") {
        PxButton("PLAY GAME", Px.blue, Px.white, Modifier.fillMaxWidth().height(22.dp), Sprites.chev)
        Spacer(Modifier.height(9.dp))
        PxButton("OPTIONS", Px.white, Px.ink, Modifier.fillMaxWidth().height(22.dp), Sprites.icons[11], Sprites.inkPal)
        Spacer(Modifier.height(9.dp))
        PxButton("EXIT", Color(0xFF1E2635), Px.white, Modifier.fillMaxWidth().height(22.dp), Sprites.cross)
        Spacer(Modifier.height(14.dp))
        PxSlider(Modifier.fillMaxWidth())
        Spacer(Modifier.height(14.dp))
        ProgressBars(Modifier.fillMaxWidth(), time)
    }
}

@Composable
private fun TypePage() {
    PxScaffold("TYPOGRAPHY") {
        PxTitle("PX-CRAFT", 1.8.dp, Px.ink)
        Spacer(Modifier.height(12.dp))
        PxText("ABCDEFGHIJKLM", 1.5.dp, Px.ink)
        Spacer(Modifier.height(4.dp))
        PxText("NOPQRSTUVWXYZ", 1.5.dp, Px.ink)
        Spacer(Modifier.height(4.dp))
        PxText("0123456789", 1.5.dp, Px.ink)
        Spacer(Modifier.height(4.dp))
        PxText("!?%&*+-=", 1.5.dp, Px.ink)
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            PxText("REGULAR", 0.95.dp, Px.blue)
            PxText("MEDIUM", 0.95.dp, Px.pink)
            PxText("BOLD", 0.95.dp, Px.green)
        }
    }
}

@Composable
private fun TexturePage() {
    val time by rememberTimeSeconds()
    PxScaffold("MATERIAL & TEXTURE") {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Canvas(Modifier.weight(1f).aspectRatio(1f)) { tileDither(size.width / 32) }
            Canvas(Modifier.weight(1f).aspectRatio(1f)) { tileGrid(size.width / 32) }
            Canvas(Modifier.weight(1f).aspectRatio(1f)) { tileNoise(size.width / 32) }
        }
        Spacer(Modifier.height(14.dp))
        Canvas(Modifier.fillMaxWidth().aspectRatio(Scenes.NIGHT_W.toFloat() / Scenes.NIGHT_H)) {
            nightCity(size.width / Scenes.NIGHT_W, frameOf(time, 2f, 9))
        }
    }
}

@Composable
private fun ArcadePage() {
    PxScaffold("BADGE & AVATAR") {
        AppCard(Modifier.fillMaxWidth().height(84.dp))
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Sprites.badges.forEach { (rows, pal) -> Sprite(rows, pal, 2.6.dp) }
        }
        Spacer(Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Sprites.avatars.forEach { (rows, pal) -> Sprite(rows, pal, 2.6.dp) }
        }
    }
}

@Composable
private fun MotionPage() {
    val time by rememberTimeSeconds()
    PxScaffold("MICRO INTERACTIONS") {
        PxPanel(Modifier.fillMaxWidth()) {
            MicroRows(Modifier.padding(11.dp), time)
        }
    }
}

@Composable
private fun ColorPage() {
    PxScaffold("COLOR PALETTE") {
        val colors = listOf(
            Px.blue, Px.pink, Px.yellow, Px.green, Px.ink, Px.gray, Px.white,
        )
        Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
            colors.forEach { c ->
                Box(
                    Modifier.fillMaxWidth().height(15.dp)
                        .background(c)
                        .border(1.dp, Px.ink)
                )
            }
        }
    }
}

@Composable
fun PixelCraftFooterEdge(modifier: Modifier = Modifier) {
    Canvas(modifier) { deviceEdge(size.width / 200) }
}
