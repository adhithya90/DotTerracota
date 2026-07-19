package com.pixelcraft.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.floor
import kotlin.math.sqrt

/**
 * Procedural pixel scenes, ported from the approved HTML board with the same
 * LCG seeds so every building and cloud lands in the same place.
 * Painters draw in scene-pixel units; callers scale via [cell].
 */
object Scenes {
    const val LOCK_W = 94
    const val LOCK_H = 235

    /** Daytime lock wallpaper: sky, clouds, ridges, flag tower, skyline, lake, sailboat. */
    fun DrawScope.lockWallpaper(cell: Float, boatShift: Int = 0, wCols: Int = LOCK_W) {
        fun r(x: Int, y: Int, w: Int, h: Int, c: Color) =
            drawRect(c, Offset(x * cell, y * cell), Size(w * cell + 0.5f, h * cell + 0.5f))
        val rng = Lcg(7)
        for (y in 0 until LOCK_H) {
            val t = y.toFloat() / LOCK_H
            r(0, y, wCols, 1, if (t < 0.28f) Color(0xFFBFD6EE) else if (t < 0.4f) Color(0xFFA9C6E6) else Color(0xFF8FB2DC))
        }
        repeat(7) {
            val cx = (rng.next() * wCols).toInt(); val cy = (6 + rng.next() * 45).toInt(); val w = (8 + rng.next() * 14).toInt()
            r(cx - w / 2, cy, w, 3, Color(0xFFF2F6FB))
            r(cx - w / 3, cy - 2, (w * 0.6f).toInt(), 2, Color(0xFFF2F6FB))
            r(cx - w / 3, cy + 3, (w * 0.7f).toInt(), 2, Color(0xFFF2F6FB))
        }
        var h1 = 70f
        for (x in 0 until wCols) {
            h1 += (rng.next() - 0.5f) * 4; h1 = h1.coerceIn(58f, 84f)
            val yy = h1.toInt()
            r(x, yy, 1, LOCK_H - yy, Color(0xFF8AA6D6))
            r(x, yy, 1, 2, Color(0xFFA3BCE4))
            if ((x + yy) % 2 == 0) r(x, yy + 2, 1, 3, Color(0xFF7B99CC))
        }
        var h2 = 94f
        for (x in 0 until wCols) {
            h2 += (rng.next() - 0.5f) * 5; h2 = h2.coerceIn(82f, 108f)
            val yy = h2.toInt()
            r(x, yy, 1, LOCK_H - yy, Color(0xFF5B7BB6))
            r(x, yy, 1, 2, Color(0xFF6E8CC6))
            if ((x * 2 + yy) % 3 == 0) r(x, yy + 3, 1, 4, Color(0xFF50709F))
        }
        // flag tower
        r(70, 80, 8, 22, Color(0xFF3C5A94))
        r(72, 70, 4, 12, Color(0xFF33507F))
        r(73, 62, 1, 9, Color(0xFF33507F))
        r(74, 62, 5, 4, Px.pink)
        // skyline strip
        var x = 0
        while (x < wCols) {
            val bw = 4 + (rng.next() * 7).toInt(); val bh = 7 + (rng.next() * 13).toInt()
            r(x, 119 - bh, bw, bh, Color(0xFF2C4470))
            var wy = 119 - bh + 2
            while (wy < 116) {
                var wx = x + 1
                while (wx < x + bw - 1) { if (rng.next() > 0.55f) r(wx, wy, 1, 1, Px.yellow); wx += 3 }
                wy += 3
            }
            x += bw + 1
        }
        repeat(26) {
            val tx = (rng.next() * wCols).toInt(); val tw = (3 + rng.next() * 4).toInt()
            r(tx, (115 + rng.next() * 4).toInt(), tw, 4, Color(0xFF2E7D5B))
        }
        for (y in 120 until LOCK_H) {
            val t = (y - 120f) / (LOCK_H - 120f)
            r(0, y, wCols, 1, if (t < 0.5f) Color(0xFF2E4E86) else Color(0xFF27436F))
        }
        repeat(60) {
            val c = if (rng.next() > 0.5f) Color(0xFF456AA6) else Color(0xFF5B7FBC)
            r((rng.next() * wCols).toInt(), (122 + rng.next() * (LOCK_H - 126)).toInt(), (2 + rng.next() * 4).toInt(), 1, c)
        }
        // sailboat (drifts in whole-pixel steps)
        val bx = 45 + boatShift
        r(bx, 124, 1, 7, Px.white)
        r(bx + 1, 125, 4, 2, Px.white); r(bx + 1, 127, 3, 2, Px.white); r(bx + 1, 129, 2, 1, Px.white)
        r(bx - 3, 131, 9, 3, Color(0xFFB8452E))
        r(bx - 4, 134, 11, 1, Color(0xFF1F3557))
    }

    const val NIGHT_W = 160
    const val NIGHT_H = 70

    /** Night city wallpaper with twinkling stars. */
    fun DrawScope.nightCity(cell: Float, frame: Int = 0) {
        fun r(x: Int, y: Int, w: Int, h: Int, c: Color) =
            drawRect(c, Offset(x * cell, y * cell), Size(w * cell + 0.5f, h * cell + 0.5f))
        val rng = Lcg(23)
        for (y in 0 until NIGHT_H) {
            val t = y.toFloat() / NIGHT_H
            r(0, y, NIGHT_W, 1, if (t < 0.3f) Color(0xFF0F1B45) else if (t < 0.55f) Color(0xFF16255C) else Color(0xFF1B2C6B))
        }
        repeat(50) { i ->
            val c = if (rng.next() > 0.8f) Px.yellow else Color(0xFFDCE4F5)
            val sx = (rng.next() * NIGHT_W).toInt(); val sy = (rng.next() * NIGHT_H * 0.55f).toInt()
            if ((i + frame) % 9 != 0) r(sx, sy, 1, 1, c)
        }
        for (y in -6..6) for (x in -6..6) {
            val d = sqrt((x * x + y * y).toFloat()); val d2 = sqrt(((x + 3) * (x + 3) + y * y).toFloat())
            if (d <= 6 && d2 > 5.4f) r(136 + x, 12 + y, 1, 1, Color(0xFFF3D97C))
        }
        var x = 0
        while (x < NIGHT_W) {
            val bw = 5 + (rng.next() * 8).toInt(); val bh = 16 + (rng.next() * 22).toInt()
            r(x, NIGHT_H - 14 - bh, bw, bh, Color(0xFF232F6E))
            var wy = NIGHT_H - 12 - bh
            while (wy < NIGHT_H - 18) {
                var wx = x + 1
                while (wx < x + bw - 1) { if (rng.next() > 0.6f) r(wx, wy, 1, 1, Color(0xFF3F4E9C)); wx += 3 }
                wy += 4
            }
            x += bw + 1
        }
        x = 0
        while (x < NIGHT_W) {
            val bw = 7 + (rng.next() * 10).toInt(); val bh = 8 + (rng.next() * 18).toInt()
            r(x, NIGHT_H - 6 - bh, bw, bh, Color(0xFF101A3F))
            var wy = NIGHT_H - 4 - bh
            while (wy < NIGHT_H - 8) {
                var wx = x + 1
                while (wx < x + bw - 1) {
                    if (rng.next() > 0.55f) {
                        val c = if (rng.next() > 0.8f) Px.pink else if (rng.next() > 0.5f) Px.yellow else Color(0xFF7FE3C8)
                        r(wx, wy, 1, 1, c)
                    }
                    wx += 2
                }
                wy += 3
            }
            if (rng.next() > 0.6f) r(x + 1, NIGHT_H - 6 - bh + 2, 3, 4, if (rng.next() > 0.5f) Px.pink else Px.green)
            x += bw + 1
        }
        r(0, NIGHT_H - 6, NIGHT_W, 6, Color(0xFF0B1230))
        repeat(20) { if (rng.next() > 0.75f) r((rng.next() * NIGHT_W).toInt(), (NIGHT_H - 5 + rng.next() * 3).toInt(), 1, 1, Px.green) }
    }

    const val THUMB_W = 34
    const val THUMB_H = 58

    /** Night-drive album thumbnail. */
    fun DrawScope.nightThumb(cell: Float) {
        fun r(x: Int, y: Int, w: Int, h: Int, c: Color) =
            drawRect(c, Offset(x * cell, y * cell), Size(w * cell + 0.5f, h * cell + 0.5f))
        val rng = Lcg(11)
        for (y in 0 until THUMB_H) {
            val t = y.toFloat() / THUMB_H
            r(0, y, THUMB_W, 1, if (t < 0.35f) Color(0xFF232B57) else if (t < 0.6f) Color(0xFF3A3167) else Color(0xFF20264A))
        }
        repeat(20) {
            val c = if (rng.next() > 0.7f) Px.yellow else Px.white
            r((rng.next() * THUMB_W).toInt(), (rng.next() * THUMB_H * 0.4f).toInt(), 1, 1, c)
        }
        var x = 0
        while (x < THUMB_W) {
            val bw = 3 + (rng.next() * 5).toInt(); val bh = 10 + (rng.next() * 22).toInt()
            r(x, THUMB_H - bh, bw, bh, Color(0xFF141A33))
            var wy = THUMB_H - bh + 2
            while (wy < THUMB_H - 2) {
                var wx = x + 1
                while (wx < x + bw - 1) {
                    if (rng.next() > 0.6f) r(wx, wy, 1, 1, if (rng.next() > 0.5f) Px.yellow else Px.pink)
                    wx += 2
                }
                wy += 3
            }
            x += bw + 1
        }
    }

    const val DEV_W = 66
    const val DEV_H = 96

    /** Pixel phone back: camera module plate, ringed lens, flash, protruding buttons. */
    fun DrawScope.deviceBack(cell: Float) {
        fun r(x: Int, y: Int, w: Int, h: Int, c: Color) =
            drawRect(c, Offset(x * cell, y * cell), Size(w * cell + 0.5f, h * cell + 0.5f))
        fun p(x: Int, y: Int, c: Color) = r(x, y, 1, 1, c)
        val rng = Lcg(3)
        val bx = 3; val bw = DEV_W - 6
        for (y in 0 until DEV_H) for (x in bx until bx + bw) {
            if ((x < bx + 2 && y < 2) || (x > bx + bw - 3 && y < 2) || (x < bx + 2 && y > DEV_H - 3) || (x > bx + bw - 3 && y > DEV_H - 3)) continue
            val diag = (x - bx + y).toFloat() / (bw + DEV_H) + (rng.next() - 0.5f) * 0.05f
            var col = Color(0xFFE7E5E1)
            if (diag > 0.46f && (x + y) % 2 == 0) col = Color(0xFFD9D7D3)
            if (diag > 0.56f && (x + y * 2) % 3 == 0) col = Color(0xFFCFCDC9)
            if (diag > 0.66f) col = if ((x + y) % 2 == 0) Color(0xFFCBC9C5) else Color(0xFFD4D2CE)
            else if (diag > 0.40f && diag <= 0.46f && (x * 3 + y) % 7 == 0) col = Color(0xFFDCDAD6)
            p(x, y, col)
        }
        val outline = Color(0xFF8E8C88)
        r(bx, 2, 1, DEV_H - 4, outline); r(bx + bw - 1, 2, 1, DEV_H - 4, outline)
        r(bx + 2, 0, bw - 4, 1, outline); r(bx + 2, DEV_H - 1, bw - 4, 1, outline)
        p(bx + 1, 1, outline); p(bx + bw - 2, 1, outline); p(bx + 1, DEV_H - 2, outline); p(bx + bw - 2, DEV_H - 2, outline)

        val mx = bx + 5; val my = 6; val mw = 32; val mh = 50
        r(mx + mw, my + 3, 1, mh - 3, Color(0xFFAEACA8)); r(mx + 3, my + mh, mw - 2, 1, Color(0xFFAEACA8))
        r(mx + 1, my + 1, mw - 2, mh - 2, Color(0xFFECEAE6))
        val mo = Color(0xFF55534F)
        r(mx, my + 3, 1, mh - 6, mo); r(mx + mw - 1, my + 3, 1, mh - 6, mo)
        r(mx + 3, my, mw - 6, 1, mo); r(mx + 3, my + mh - 1, mw - 6, 1, mo)
        r(mx + 1, my + 1, 2, 1, mo); p(mx + 1, my + 2, mo)
        r(mx + mw - 3, my + 1, 2, 1, mo); p(mx + mw - 2, my + 2, mo)
        r(mx + 1, my + mh - 2, 2, 1, mo); p(mx + 1, my + mh - 3, mo)
        r(mx + mw - 3, my + mh - 2, 2, 1, mo); p(mx + mw - 2, my + mh - 3, mo)

        val cx = mx + mw / 2f - 0.5f; val cy = my + 16f; val rr = 13.5f
        for (y in 0 until DEV_H) for (x in 0 until DEV_W) {
            val d = sqrt((x - cx) * (x - cx) + (y - cy) * (y - cy))
            if (d <= rr) {
                var col = when {
                    d > rr - 1.4f -> Color(0xFF55534F)
                    d > rr - 2.8f -> Color(0xFFD6D4D0)
                    d > rr - 4.2f -> Color(0xFF2A2E35)
                    d > rr - 5.2f -> Color(0xFF171C24)
                    else -> Color(0xFF1B2740)
                }
                if (d <= rr - 5.2f) {
                    val hx = x - (cx - 3.6f); val hy = y - (cy - 3.6f)
                    val hd = sqrt(hx * hx + hy * hy)
                    if (hd < 3.2f) col = Color(0xFFB9CFF2)
                    else if (hd < 4.6f) col = Color(0xFF3D5C8F)
                    val lx = x - (cx + 2.5f); val ly = y - (cy + 2.5f)
                    if (sqrt(lx * lx + ly * ly) < 2.2f) col = Color(0xFF2B4A79)
                }
                p(x, y, col)
            }
        }
        r((cx - 3).toInt(), (cy - 4).toInt(), 2, 1, Px.white); r((cx - 4).toInt(), (cy - 3).toInt(), 1, 2, Px.white)

        r(mx + 3, my + 33, 12, 12, mo)
        r(mx + 4, my + 34, 10, 10, Color(0xFF1B1F26))
        r(mx + 6, my + 36, 6, 6, Color(0xFF0F1319))
        r(mx + 7, my + 37, 4, 4, Color(0xFF2B4A79))
        p(mx + 7, my + 37, Color(0xFF8FB4EE))
        r(mx + 19, my + 35, 9, 9, Color(0xFF8E8C88))
        r(mx + 20, my + 36, 7, 7, Color(0xFFF6F4EF))
        r(mx + 22, my + 37, 3, 5, Color(0xFFF2D98B))
        r(mx + 22, my + 40, 3, 2, Color(0xFFE4B94E))

        r(bx + bw - 9, 12, 2, 2, Color(0xFF9A9894)); r(bx + bw - 9, 18, 2, 2, Color(0xFF9A9894))
        p(bx + bw - 8, 28, Color(0xFF8E8C88)); p(bx + bw - 8, 32, Color(0xFF8E8C88))
        p(bx + bw - 10, 30, Color(0xFF8E8C88)); p(bx + bw - 6, 30, Color(0xFF8E8C88))

        fun btnL(y: Int, len: Int) {
            r(0, y, 4, len, Color(0xFF3A3F46))
            r(1, y + 1, 2, len - 2, Color(0xFFF08A63))
            r(1, y + len - 4, 2, 3, Color(0xFFD8734E))
        }
        btnL(24, 17); btnL(46, 12)
        r(DEV_W - 4, 33, 4, 15, Color(0xFF3A3F46))
        r(DEV_W - 3, 34, 2, 13, Px.green)
        r(DEV_W - 3, 42, 2, 4, Color(0xFF1A9A81))

        for (ry in 0 until 4) for (rx in 0 until 6) r(bx + bw - 24 + rx * 3, 70 + ry * 3, 2, 2, Color(0xFF20262F))
    }

    /** Texture tiles. */
    fun DrawScope.tileDither(cell: Float) {
        val s = 32
        for (y in 0 until s) for (x in 0 until s) {
            val t = (x + y).toFloat() / (2 * s)
            val on = when {
                t < 0.25f -> true
                t < 0.45f -> (x + y) % 2 == 0 || (x % 2 == 0 && y % 2 == 0)
                t < 0.6f -> (x + y) % 2 == 0
                t < 0.8f -> x % 2 == 0 && y % 2 == 0
                else -> false
            }
            val c = if (on) Px.blue else if (t > 0.6f) Color(0xFF1E3A8F) else Color(0xFF274BB0)
            drawRect(c, Offset(x * cell, y * cell), Size(cell + 0.5f, cell + 0.5f))
        }
    }

    fun DrawScope.tileGrid(cell: Float) {
        val s = 32
        drawRect(Color(0xFF181F2A), Offset.Zero, Size(s * cell, s * cell))
        var i = 0
        while (i < s) {
            drawRect(Color(0xFF2A3340), Offset(i * cell, 0f), Size(cell, s * cell))
            drawRect(Color(0xFF2A3340), Offset(0f, i * cell), Size(s * cell, cell))
            i += 4
        }
    }

    fun DrawScope.tileNoise(cell: Float) {
        val s = 32
        val rng = Lcg(5)
        for (y in 0 until s) for (x in 0 until s) {
            val v = rng.next()
            val c = if (v > 0.85f) Color(0xFFC9C5BC) else if (v > 0.6f) Color(0xFFDEDAD2) else Color(0xFFEBE8E1)
            drawRect(c, Offset(x * cell, y * cell), Size(cell + 0.5f, cell + 0.5f))
        }
    }

    /** Footer/device bottom edge. */
    fun DrawScope.deviceEdge(cell: Float) {
        fun r(x: Int, y: Int, w: Int, h: Int, c: Color) =
            drawRect(c, Offset(x * cell, y * cell), Size(w * cell + 0.5f, h * cell + 0.5f))
        r(0, 4, 200, 18, Color(0xFFC9C7C3))
        r(0, 4, 200, 4, Color(0xFFE4E2DE))
        r(0, 20, 200, 2, Color(0xFF8F8D89))
        for (i in 0 until 5) r(16 + i * 7, 11, 3, 3, Color(0xFF6E6C68))
        r(88, 9, 30, 8, Color(0xFF20262F))
        r(90, 11, 26, 4, Color(0xFF3A4048))
        r(150, 9, 16, 8, Color(0xFFF08A63))
        r(178, 10, 6, 6, Color(0xFF20262F))
    }
}
