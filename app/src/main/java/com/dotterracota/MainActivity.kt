package com.dotterracota

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.dotterracota.ui.DotTerracotaStyle
import com.softmachine.ui.SoftMachineStyle
import com.pixelcraft.ui.PixelCraftStyle

object Lens {
    const val LANDING = 0
    const val TERRACOTA = 1
    const val SOFT_MACHINE = 2
    const val PIXEL_CRAFT = 3
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var current by rememberSaveable { mutableIntStateOf(Lens.LANDING) }
            StatusBarStyle(darkIcons = current != Lens.LANDING)
            Crossfade(current, label = "lens") { lens ->
                when (lens) {
                    Lens.LANDING -> LandingScreen(onOpen = { current = it })
                    Lens.TERRACOTA -> {
                        BackHandler { current = Lens.LANDING }
                        DotTerracotaStyle()
                    }
                    Lens.SOFT_MACHINE -> {
                        BackHandler { current = Lens.LANDING }
                        SoftMachineStyle()
                    }
                    Lens.PIXEL_CRAFT -> {
                        BackHandler { current = Lens.LANDING }
                        PixelCraftStyle()
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusBarStyle(darkIcons: Boolean) {
    val view = LocalView.current
    LaunchedEffect(darkIcons) {
        val window = (view.context as ComponentActivity).window
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkIcons
    }
}
