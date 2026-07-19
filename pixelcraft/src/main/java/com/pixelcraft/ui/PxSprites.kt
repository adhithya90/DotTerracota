package com.pixelcraft.ui

import androidx.compose.ui.graphics.Color

/** All sprites are authored cell-by-cell, ported verbatim from the approved board. */
object Sprites {
    val inkPal = mapOf('1' to Px.ink)
    val whitePal = mapOf('1' to Px.white)

    val cat = listOf(
        "..11........11..",
        ".1K1.......1K1..",
        ".1KK1.....1KK1..",
        ".1KKK11111KKK1..",
        ".1KKKKKKKKKKK1..",
        "1KKKKKKKKKKKKK1.",
        "1KKGGKKKKKGGKK1.",
        "1KKGGKKKKKGGKK1.",
        "1KKKKKKKKKKKKK1.",
        "1KKKKKPPKKKKKK1.",
        "1KKKKPKKPKKKKK1.",
        ".1KKKKKKKKKKK1..",
        ".1KKKKKKKKKKK1..",
        "..11111111111...",
    )
    val catPal = mapOf('K' to Color(0xFF20262F), '1' to Color(0xFF20262F), 'G' to Color(0xFF3DDBAD), 'P' to Px.pink)

    val heart = listOf("01010", "11111", "11111", "01110", "00100")
    val heartOutline = listOf(".11.11.", "1..1..1", "1.....1", "1.....1", ".1...1.", "..1.1..", "...1...")
    val heartBurst = listOf("........s.", "s.11.11...", ".1111111.s", ".1111111..", "..11111...", "s..111....", "....1...s.")
    val heartBurstPal = mapOf('1' to Px.pink, 's' to Px.yellow)

    val ditherHeart: List<String> = run {
        val rows = listOf(".XX..XX.", "XXXX.XXX", "XXXXXXXX", "XXXXXXXX", ".XXXXXX.", "..XXXX..", "...XX...")
        (0 until rows.size * 2).map { y ->
            (0 until 16).joinToString("") { x ->
                val c = rows[y / 2][x / 2]
                if (c == 'X' && (x + y) % 2 == 0) "D" else "."
            }
        }
    }
    val ditherPal = mapOf('D' to Color(0xFFB9B4AC))

    val icons: List<List<String>> = listOf(
        listOf("....11....", "...1111...", "..111111..", ".11111111.", "1111111111", "..111111..", "..11..11..", "..11..11..", "..11..11..", "..111111.."),
        listOf(".11...11..", "1111.1111.", "111111111.", "111111111.", ".1111111..", "..11111...", "...111....", "....1....."),
        listOf("..111111..", ".11111111.", "1111111111", "1101101011", "1101101011", "1111111111", ".11111111.", "..11..11.."),
        listOf("11111111..", "11111111..", "11111111..", "11111111..", "..11......", ".11.......", "1........."),
        listOf("...11.....", "1111111111", "1111111111", "111.11.111", "11.1111.11", "11.1111.11", "111.11.111", "1111111111"),
        listOf("....1111..", "....1111..", "....11....", "....11....", "....11....", "..1111....", ".11111....", "..111....."),
        listOf("11111111..", "11111111..", "11111111..", "11111111..", "11111111..", "111..111..", "11....11..", "1......1.."),
        listOf("....11....", "..111111..", ".11111111.", ".11111111.", ".11111111.", "1111111111", "....11....", "....11...."),
        listOf("1111111111", "1111111111", "1.1.1.1..1", "1111111111", "1.1.1.1..1", "1111111111", "1.1.1.1..1", "1111111111"),
        listOf("...1111...", "..111111..", ".11111111.", "1111111111", "1111111111", ".11111111."),
        listOf("....11....", "....11....", "....11....", "..111111..", "...1111...", "....11....", "1111111111", "1111111111"),
        listOf("...11.....", ".1111111..", "11111111..", ".111..111.", ".11....11.", ".111..111.", "11111111..", ".1111111.."),
    )

    val trophy = listOf("1.11111.1", "111111111", "1.11111.1", "..11111..", "...111...", "....1....", "...111...", "..11111..")
    val flame = listOf("...1...", "...11..", "..111..", ".1111..", ".11111.", "1111111", "1111111", ".11111.")

    // toggle glyphs: name, tile color, lit
    data class Toggle(val rows: List<String>, val bg: Color, val on: Boolean)
    val toggles = listOf(
        Toggle(listOf(".11111.", "1.....1", "..111..", ".1...1.", "...1...", "...1..."), Px.blue, true),
        Toggle(listOf("..1....", "..11...", "1.1.1..", ".111...", "..1....", ".111...", "1.1.1..", "..11...", "..1...."), Color(0xFF242C38), false),
        Toggle(listOf("...1...", "..11...", "1111.1.", "1111..1", "1111.1.", "..11...", "...1..."), Px.pink, true),
        Toggle(listOf(".1111..", "1....1.", "1......", "1...11.", "......1", ".11...1", ".1....1", "..1111."), Color(0xFF242C38), false),
        Toggle(listOf("..111..", ".11....", "111....", "111....", "111....", ".11....", "..111.."), Color(0xFF242C38), false),
        Toggle(listOf("...1...", "...1...", ".11111.", "1111111", "...1...", "..111.."), Color(0xFF242C38), false),
        Toggle(listOf(".111...", ".111...", "..1....", "..1....", "..1....", "..1....", "..111.."), Px.green, true),
        Toggle(listOf(".111...", "11111..", "11.11..", "11111..", ".111...", "..1....", "..1...."), Px.yellow, true),
    )

    // transport glyphs
    val prev = listOf("11...1", "11..11", "11.111", "111111", "11.111", "11..11", "11...1")
    val rew = listOf("...1...1", "..11..11", ".111.111", "11111111", ".111.111", "..11..11", "...1...1")
    val ff = listOf("1...1...", "11..11..", "111.111.", "11111111", "111.111.", "11..11..", "1...1...")
    val next = listOf("1...11", "11..11", "111.11", "111111", "111.11", "11..11", "1...11")
    val play = listOf(".1...", ".11..", ".111.", ".1111", ".111.", ".11..", ".1...")
    val chev = listOf("1...", "11..", "111.", "11..", "1...")
    val chevBig = listOf("1...1...", "11..11..", "111.111.", "11..11..", "1...1...")
    val cross = listOf("1...1", ".1.1.", "..1..", ".1.1.", "1...1")
    val sun = listOf("1.1.1", ".111.", "11111", ".111.", "1.1.1")

    val dock = listOf(
        Triple("PHONE", listOf("11....", "111...", ".11...", ".111..", "..1111", "...111"), Px.white),
        Triple("MESSAGES", listOf("111111", "111111", "111111", ".11...", "11...."), Px.white),
        Triple("APPS", listOf("11.11.", "11.11.", "......", "11.11.", "11.11."), Px.green),
        Triple("CAMERA", listOf("..11..", "111111", "11..11", "11..11", "111111"), Px.white),
    )

    val badges: List<Pair<List<String>, Map<Char, Color>>> = listOf(
        listOf("111111111", "1YYYYYYY1", "1YYYWYYY1", "1YYWWWYY1", "1YYYWYYY1", ".1YYYYY1.", ".1YYYYY1.", "..1YYY1..", "...111...") to
            mapOf('1' to Px.ink, 'Y' to Px.yellow, 'W' to Px.white),
        listOf("...111...", "..1CCC1..", ".1CBBBC1.", "1CBBWBBC1", ".1BBWBB1.", "..1BWB1..", "...1B1...", "....1....") to
            mapOf('1' to Px.ink, 'B' to Color(0xFF2D6FE4), 'C' to Color(0xFF8FC1F7), 'W' to Px.white),
        listOf("..11111..", ".1YYYYY1.", "1YYDDDYY1", "1YDYYYDY1", "1YDYYYDY1", "1YDYYYDY1", "1YYDDDYY1", ".1YYYYY1.", "..11111..") to
            mapOf('1' to Color(0xFF8A6A1E), 'Y' to Px.yellow, 'D' to Color(0xFFD9A62E)),
    )

    val avatars: List<Pair<List<String>, Map<Char, Color>>> = listOf(
        listOf("..RRRRRR..", ".RRRRRRRR.", ".RRSSSSRR.", "..SSSSSS..", "..SGSSGS..", "..SSSSSS..", "..SSPPSS..", "...SSSS...") to
            mapOf('R' to Color(0xFFD84B3E), 'S' to Color(0xFFE8B98A), 'G' to Px.ink, 'P' to Color(0xFFC96B4A)),
        listOf("..PPPPPP..", ".PPPPPPPP.", ".PPSSSSPP.", ".PPSSSSPP.", "..SGSSGS..", "..SSSSSS..", "..SSMMSS..", "...SSSS...") to
            mapOf('P' to Color(0xFFF286A4), 'S' to Color(0xFFF0C69A), 'G' to Px.ink, 'M' to Color(0xFFC96B4A)),
        listOf("..MMMMMM..", ".MMMMMMMM.", ".MWWWWWWM.", ".MWGWWGWM.", ".MWWWWWWM.", ".MWWMMWWM.", ".MMMMMMMM.", "...M..M...") to
            mapOf('M' to Color(0xFF9AA3AE), 'W' to Color(0xFFE7EBF0), 'G' to Px.green),
        listOf("..BBBBBB..", ".BBBBBBBB.", ".BBSSSSBB.", "..SSSSSS..", "..SGSSGS..", "..SSSSSS..", "..SSPPSS..", "...SSSS...") to
            mapOf('B' to Color(0xFF3F6FD8), 'S' to Color(0xFFE2A97D), 'G' to Px.ink, 'P' to Color(0xFFB45A3C)),
    )

    val avatarLevel = listOf(
        "..111111..", ".11111111.", ".1KKKKKK1.", ".1KKKKKK1.", "1KSSSSSSK1",
        "1KSGSSGSK1", "1KSSSSSSK1", ".1SSPPSS1.", ".1SSSSSS1.", "..111111..",
    )
    val avatarLevelPal = mapOf(
        '1' to Px.ink, 'K' to Color(0xFF2B3442), 'S' to Color(0xFFE8B98A),
        'G' to Px.ink, 'P' to Color(0xFFC96B4A),
    )

    val footCat = listOf(
        "..1.........1...",
        ".1.1.......1.1..",
        ".1..111111..1...",
        ".1..........1...",
        "1............1..",
        "1..11....11..1..",
        "1............1..",
        "1.....11.....1..",
        ".1...P..P...1...",
        "..111....111....",
    )
    val footCatPal = mapOf('1' to Px.white, 'P' to Px.pink)

    val loadFrames = listOf(
        listOf(".11.", "1..1", "1..1", ".1.."),
        listOf(".11.", "...1", "1..1", ".11."),
        listOf(".1..", "1..1", "1..1", ".11."),
        listOf("..1.", "1..1", "1..1", ".11."),
        listOf(".11.", "1...", "1..1", ".11."),
    )
}
