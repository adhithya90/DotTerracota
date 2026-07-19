package com.dotterracota

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.designlens.core.MonoText
import com.designlens.core.ShaderPanel
import com.dotterracota.ui.DotTerracotaPreview
import com.softmachine.ui.SoftMachinePreview
import com.pixelcraft.ui.PixelCraftPreview
import kotlin.random.Random

private val bg = Color(0xFF111113)
private val cardBg = Color(0xFF1B1B1E)
private val paper = Color(0xFFE9E4DA)
private val paperDim = Color(0xFF9C978E)

@Composable
fun LandingScreen(onOpen: (Int) -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .background(bg)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(14.dp))
        TopBar()
        Spacer(Modifier.height(36.dp))
        Headline()
        Spacer(Modifier.height(36.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            LensCard("01", "DOT\nTERRACOTA", "Warm. Industrial. Honest.\nTerracotta futurism\nin dot matrix.",
                Modifier.weight(1f), onTap = { onOpen(Lens.TERRACOTA) }) {
                DotTerracotaPreview(Modifier.fillMaxSize())
            }
            LensCard("02", "SOFT\nMACHINE", "Gentle. Organic. Intuitive.\nTechnology that feels\nhuman and warm.",
                Modifier.weight(1f), onTap = { onOpen(Lens.SOFT_MACHINE) }) {
                SoftMachinePreview(Modifier.fillMaxSize())
            }
        }
        Spacer(Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            LensCard("03", "PIXEL\nCRAFT", "Playful by design.\nPrecision by pixels.",
                Modifier.weight(1f), onTap = { onOpen(Lens.PIXEL_CRAFT) }) {
                PixelCraftPreview(Modifier.fillMaxSize())
            }
            LensCard("04", "LIQUID\nCHROME", "Fluid. Futuristic.\nWhere technology feels\nlike liquid metal.",
                Modifier.weight(1f), soon = true) {
                ShaderPanel(CHROME_THUMB, Modifier.fillMaxSize(), animated = true)
            }
        }
        Spacer(Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            LensCard("05", "MONASTIC\nTECH", "Calm. Focused. Timeless.\nWhere minimalism\nmeets performance.",
                Modifier.weight(1f), soon = true) {
                MonasticThumb()
            }
            LensCard("06", "DARK CLAY\nFUTURISM", "Ancient. Advanced.\nWhere history and\nthe future coexist.",
                Modifier.weight(1f), soon = true) {
                DarkClayThumb()
            }
        }

        Spacer(Modifier.height(26.dp))
        SurpriseRow(onOpen)
        Spacer(Modifier.height(20.dp))
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            MonoText("DESIGN DIFFERENTLY. DESIGN ON PURPOSE.", 8.sp, paperDim.copy(alpha = 0.6f))
        }
        Spacer(Modifier.height(18.dp))
        Spacer(Modifier.navigationBarsPadding())
    }
}

@Composable
private fun TopBar() {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        BasicText(
            "D /",
            style = TextStyle(color = paper, fontSize = 20.sp, fontFamily = FontFamily.Serif)
        )
        Spacer(Modifier.weight(1f))
        Row(
            Modifier
                .clip(RoundedCornerShape(50))
                .border(0.7.dp, Color(0x33E9E4DA), RoundedCornerShape(50))
                .padding(horizontal = 14.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MonoText("ABOUT", 8.5.sp, paper.copy(alpha = 0.85f))
            Spacer(Modifier.width(7.dp))
            Canvas(Modifier.size(9.dp)) {
                drawCircle(paper.copy(alpha = 0.85f), style = Stroke(1.dp.toPx()))
                drawCircle(paper.copy(alpha = 0.85f), radius = size.minDimension * 0.16f)
            }
        }
    }
}

@Composable
private fun Headline() {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
        BasicText(
            buildAnnotatedString {
                append("Choose Your\n")
                withStyle(SpanStyle(fontStyle = FontStyle.Italic)) { append("Design") }
                append(" Lens")
            },
            style = TextStyle(
                color = paper,
                fontSize = 38.sp,
                lineHeight = 44.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Normal,
            )
        )
        Spacer(Modifier.weight(1f))
        BasicText(
            "Different worlds.\nDifferent rules.\nSame purpose: clarity.",
            style = TextStyle(
                color = paperDim, fontSize = 11.sp, lineHeight = 16.sp,
                fontFamily = FontFamily.SansSerif,
            ),
            modifier = Modifier.padding(bottom = 4.dp)
        )
    }
}

@Composable
private fun LensCard(
    number: String,
    title: String,
    description: String,
    modifier: Modifier,
    soon: Boolean = false,
    onTap: (() -> Unit)? = null,
    preview: @Composable () -> Unit,
) {
    Column(
        modifier
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .then(
                if (onTap != null)
                    Modifier.clickable(interactionSource = null, indication = null) { onTap() }
                else Modifier
            )
            .padding(8.dp)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(0.78f)
                .clip(RoundedCornerShape(10.dp))
        ) {
            preview()
            if (soon) {
                Box(Modifier.fillMaxSize().background(Color(0x8C111113)))
                Box(
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(50))
                        .border(0.6.dp, Color(0x55E9E4DA), RoundedCornerShape(50))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    MonoText("SOON", 7.sp, paper.copy(alpha = 0.75f))
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Column(Modifier.padding(horizontal = 6.dp)) {
            MonoText(number, 8.sp, paperDim)
            Spacer(Modifier.height(4.dp))
            BasicText(
                title.replace("\n", " "),
                style = TextStyle(
                    color = if (soon) paper.copy(alpha = 0.55f) else paper,
                    fontSize = 16.sp, lineHeight = 20.sp,
                    fontFamily = FontFamily.Serif,
                )
            )
            Spacer(Modifier.height(7.dp))
            BasicText(
                description,
                style = TextStyle(
                    color = paperDim.copy(alpha = if (soon) 0.6f else 1f),
                    fontSize = 10.5.sp, lineHeight = 15.sp,
                    fontFamily = FontFamily.SansSerif,
                )
            )
            Spacer(Modifier.height(10.dp))
            if (!soon) {
                Canvas(Modifier.size(18.dp, 10.dp)) {
                    val y = size.height / 2f
                    drawLine(paper, Offset(0f, y), Offset(size.width - 1f, y), 1.2.dp.toPx(), StrokeCap.Round)
                    drawLine(paper, Offset(size.width - 5.dp.toPx(), y - 4.dp.toPx()),
                        Offset(size.width - 1f, y), 1.2.dp.toPx(), StrokeCap.Round)
                    drawLine(paper, Offset(size.width - 5.dp.toPx(), y + 4.dp.toPx()),
                        Offset(size.width - 1f, y), 1.2.dp.toPx(), StrokeCap.Round)
                }
            }
            Spacer(Modifier.height(4.dp))
        }
    }
}

@Composable
private fun SurpriseRow(onOpen: (Int) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF19191C))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Canvas(Modifier.size(14.dp)) {
            // four-point sparkle
            val c = center
            val r = size.minDimension / 2f
            for (a in listOf(0f, 90f)) {
                rotate(a, c) {
                    drawLine(paper, Offset(c.x, c.y - r), Offset(c.x, c.y + r), 1.dp.toPx(), StrokeCap.Round)
                }
            }
            drawCircle(paper, 1.2.dp.toPx(), c)
        }
        Spacer(Modifier.width(12.dp))
        Column {
            BasicText(
                "Not sure which to choose?",
                style = TextStyle(color = paper, fontSize = 12.sp, fontFamily = FontFamily.SansSerif)
            )
            BasicText(
                "Let the experience guide you.",
                style = TextStyle(color = paperDim, fontSize = 10.5.sp, fontFamily = FontFamily.SansSerif)
            )
        }
        Spacer(Modifier.weight(1f))
        Box(
            Modifier
                .clip(RoundedCornerShape(50))
                .border(0.7.dp, Color(0x44E9E4DA), RoundedCornerShape(50))
                .clickable(interactionSource = null, indication = null) {
                    onOpen(listOf(Lens.TERRACOTA, Lens.SOFT_MACHINE, Lens.PIXEL_CRAFT).random())
                }
                .padding(horizontal = 14.dp, vertical = 9.dp)
        ) {
            MonoText("SURPRISE ME", 8.5.sp, paper)
        }
    }
}

// ------------------------------------------------------- coming-soon thumbs

private const val CHROME_THUMB = """
uniform float2 uRes;
uniform float uTime;
half4 main(float2 fc) {
    float2 uv = fc / uRes;
    float a = uv.x * 4.0 + sin(uv.y * 6.0 + uTime * 0.5) * 1.3;
    float b = uv.y * 5.0 + cos(uv.x * 5.0 - uTime * 0.35) * 1.1;
    float v = sin(a) + cos(b) + sin((a + b) * 0.7);
    float t = v * 0.25 + 0.5;
    float band = pow(abs(sin(t * 9.42)), 1.4);
    float3 col = mix(float3(0.04, 0.05, 0.07), float3(0.88, 0.91, 0.96), band);
    col += float3(0.15, 0.25, 0.45) * pow(max(0.0, sin(t * 6.28 + 1.0)), 4.0) * 0.35;
    return half4(col, 1.0);
}
"""

@Composable
private fun PixelThumb() {
    val map = listOf(
        "SSSSSSSSSS",
        "SSSCCSSSSS",
        "SSSSSSSCCS",
        "SSSTTSSSSS",
        "SSTTTTSSSS",
        "SSSTTSSSSS",
        "SSSHHSSSSS",
        "GGGGGGGGGG",
        "DDDDDDDDDD",
        "SDDDDDDDDS",
    )
    val colors = mapOf(
        'S' to Color(0xFF87B5C9), 'C' to Color(0xFFDDEAF0), 'T' to Color(0xFF4E8A4E),
        'H' to Color(0xFF6E4A2F), 'G' to Color(0xFF6FA84F), 'D' to Color(0xFF54432E),
    )
    Canvas(Modifier.fillMaxSize()) {
        val cw = size.width / 10f
        val ch = size.height / 10f
        map.forEachIndexed { r, row ->
            row.forEachIndexed { c, ch2 ->
                drawRect(colors[ch2]!!, Offset(c * cw, r * ch), androidx.compose.ui.geometry.Size(cw + 1f, ch + 1f))
            }
        }
    }
}

@Composable
private fun MonasticThumb() {
    Column(Modifier.fillMaxSize().background(Color(0xFFECE7DC)).padding(12.dp)) {
        Row(Modifier.fillMaxWidth()) {
            MonoText("TUE", 6.sp, Color(0xFF7A756B))
            Spacer(Modifier.weight(1f))
            MonoText("JUN", 6.sp, Color(0xFF7A756B))
        }
        Spacer(Modifier.height(6.dp))
        BasicText("18", style = TextStyle(Color(0xFF2E2B26), 30.sp, fontFamily = FontFamily.Serif))
        Spacer(Modifier.height(6.dp))
        BasicText(
            "Focus\nis a form of\nrespect.",
            style = TextStyle(Color(0xFF2E2B26), 11.sp, fontFamily = FontFamily.Serif, lineHeight = 15.sp)
        )
        Spacer(Modifier.weight(1f))
        listOf("09:30", "10:45", "14:00").forEach {
            Row(Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                MonoText(it, 6.sp, Color(0xFF7A756B))
                Spacer(Modifier.width(6.dp))
                Box(Modifier.weight(1f).height(0.6.dp).background(Color(0x332E2B26)))
            }
        }
    }
}

@Composable
private fun DarkClayThumb() {
    Box(Modifier.fillMaxSize()) {
        ShaderPanel(com.dotterracota.ui.Shaders.CLAY, Modifier.fillMaxSize())
        Box(Modifier.fillMaxSize().background(Color(0xB31A0F08)))
        Canvas(Modifier.fillMaxSize()) {
            val c = Offset(center.x, size.height * 0.55f)
            for (i in 0..2) {
                drawCircle(
                    Color(0xFFB56A32).copy(alpha = 0.5f - i * 0.13f),
                    size.minDimension * (0.18f + i * 0.09f), c,
                    style = Stroke((1.2 - i * 0.3).dp.toPx())
                )
            }
            drawCircle(Color(0xFFE8A050), size.minDimension * 0.05f, c)
        }
        MonoText(
            "CORE TEMP. 56.2°", 6.sp, Color(0xFFB56A32),
            modifier = Modifier.align(Alignment.TopStart).padding(10.dp)
        )
    }
}
