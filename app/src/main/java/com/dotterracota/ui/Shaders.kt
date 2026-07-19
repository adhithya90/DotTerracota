package com.dotterracota.ui

/**
 * AGSL shader sources. All shaders share a prelude with noise helpers and declare
 * `uRes` + `uTime` so the host can always set both uniforms.
 */
object Shaders {

    private const val PRELUDE = """
uniform float2 uRes;
uniform float uTime;

float hash(float2 p) {
    p = fract(p * float2(123.34, 456.21));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}
float vnoise(float2 p) {
    float2 i = floor(p);
    float2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    float a = hash(i);
    float b = hash(i + float2(1.0, 0.0));
    float c = hash(i + float2(0.0, 1.0));
    float d = hash(i + float2(1.0, 1.0));
    return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
}
float fbm(float2 p) {
    float v = 0.0;
    float a = 0.5;
    for (int i = 0; i < 5; i++) {
        v += a * vnoise(p);
        p = p * 2.03 + float2(17.13, 9.71);
        a *= 0.5;
    }
    return v;
}
float sdRR(float2 p, float2 b, float r) {
    float2 q = abs(p) - b + r;
    return length(max(q, float2(0.0))) + min(max(q.x, q.y), 0.0) - r;
}
float sdSeg(float2 p, float2 a, float2 b) {
    float2 pa = p - a;
    float2 ba = b - a;
    float h = clamp(dot(pa, ba) / dot(ba, ba), 0.0, 1.0);
    return length(pa - ba * h);
}
float3 screw(float3 col, float2 p, float2 c) {
    float d = length(p - c) - 3.0;
    col = mix(col, float3(0.42, 0.41, 0.40), smoothstep(0.9, -0.3, abs(d) - 0.5));
    col = mix(col, float3(0.66, 0.65, 0.63), smoothstep(0.8, -0.8, d + 1.2));
    float slot = sdSeg(p, c - float2(1.6, -1.1), c + float2(1.6, 1.1));
    col = mix(col, float3(0.38, 0.37, 0.36), smoothstep(0.7, 0.2, slot) * smoothstep(0.0, -0.5, d + 1.0));
    return col;
}
float3 brushedBase(float2 p, float2 res) {
    float2 uv = p / res;
    float brush = vnoise(float2(p.x * 0.9, p.y * 0.08));
    float grain = fbm(p * 0.35);
    float3 col = mix(float3(0.780, 0.768, 0.750), float3(0.885, 0.874, 0.858), uv.y * 0.35 + brush * 0.30);
    col += (grain - 0.5) * 0.045;
    col *= 0.90 + 0.13 * exp(-length(uv - float2(0.25, 0.10)) * 1.4);
    return col;
}
"""

    /**
     * Top-right hardware plate: brushed metal, terracotta speaker disc, glowing neon tubes,
     * recessed stadium with micro-perforation, corner screws. Bottom-left corner is cut
     * transparent so the "INTEGRATED DOT MATRIX INTERFACE" label sits on the page.
     * Local units: width = 140.
     */
    const val TOP_PLATE = "uniform float uPulse;\nuniform float uLight;\n" + PRELUDE + """
half4 main(float2 fc) {
    float2 P = fc / uRes.x * 140.0;
    float H = uRes.y / uRes.x * 140.0;

    float3 col = brushedBase(fc, uRes);

    // panel seams
    float seam = min(abs(P.y - 62.0) - 0.4, abs(P.x - 24.0) - 0.4);
    seam = max(seam, -1.0);
    col *= 1.0 - 0.13 * smoothstep(0.8, 0.0, abs(P.y - 62.0)) * step(P.x, 40.0);
    col *= 1.0 - 0.10 * smoothstep(0.8, 0.0, abs(P.x - 20.0)) * step(P.y, 62.0);

    // recessed stadium
    float st = sdRR(P - float2(72.0, 112.0), float2(30.0, 50.0), 30.0);
    col *= 1.0 - 0.28 * exp(-abs(st) * 0.55) * step(0.0, st);
    if (st < 0.0) { col *= 0.965; }
    col *= 1.0 - 0.22 * smoothstep(1.4, 0.0, abs(st + 4.0));
    col += 0.10 * smoothstep(1.2, 0.0, abs(st - 1.2));

    // micro perforation inside stadium (upper part); the membrane breathes with uPulse
    float2 mc = float2(72.0, 106.0);
    if (st < -7.0 && P.y < 132.0) {
        float2 mp = mc + (P - mc) / (1.0 + 0.09 * uPulse);
        float2 g = fract(mp / 3.1) - 0.5;
        float hole = smoothstep(1.05, 0.55, length(g) * 3.1);
        col = mix(col, col * 0.60, hole * 0.9);
    }
    if (st < 0.0) {
        col *= 1.0 + uPulse * (0.10 * cos(length(P - mc) * 0.45 - 1.0) + 0.05);
    }
    // small port circle at stadium bottom; pumps with the pulse
    float port = length(P - float2(72.0, 148.0)) - (5.5 + 1.8 * uPulse);
    col *= 1.0 - 0.35 * smoothstep(1.2, 0.0, abs(port));
    col = mix(col, col * 0.8, smoothstep(0.0, -1.5, port));

    // terracotta disc, top-right
    float2 cc = float2(100.0, 36.0);
    float c1 = length(P - cc) - 26.0;
    col *= 1.0 - 0.30 * exp(-abs(c1) * 0.5) * step(0.0, c1);
    if (c1 < 1.5) {
        float3 clay = mix(float3(0.70, 0.32, 0.19), float3(0.80, 0.43, 0.28), fbm(P * 1.2) * 0.6 + 0.2);
        float2 n2 = (P - cc) / 26.0;
        float z = sqrt(max(0.0, 1.0 - dot(n2, n2)));
        float diff = clamp(dot(normalize(float3(-0.4, -0.55, 0.73)), float3(n2, z)), 0.0, 1.0);
        clay *= 0.78 + 0.34 * diff;
        col = mix(clay, col, smoothstep(-1.5, 1.5, c1));
    }

    // glowing tubes: horizontal (left of disc) + vertical (right edge of stadium);
    // brightness follows uLight and kicks with the subwoofer pulse
    float amp = uLight * (0.92 + 0.08 * sin(uTime * 1.6)) * (1.0 + 0.4 * max(uPulse, 0.0));
    float g1 = sdSeg(P, float2(14.0, 20.0), float2(66.0, 20.0)) - 3.0;
    float g2 = sdSeg(P, float2(126.0, 88.0), float2(126.0, 150.0)) - 2.6;
    float gd = min(g1, g2);
    // dark recess channel so the white tube reads against the light metal
    col = mix(col, col * 0.55, smoothstep(7.0, 1.5, gd));
    float3 tubeCol = mix(float3(0.82, 0.81, 0.79), float3(1.0, 0.99, 0.955), clamp(amp, 0.0, 1.0));
    col = mix(col, tubeCol, smoothstep(1.2, -1.2, gd));
    col += float3(1.0, 0.94, 0.82) * exp(-max(gd, 0.0) * 0.075) * 0.65 * amp;

    // screws
    col = screw(col, P, float2(8.0, 8.0));
    col = screw(col, P, float2(132.0, 8.0));
    col = screw(col, P, float2(9.0, 74.0));
    col = screw(col, P, float2(8.0, H - 8.0));
    col = screw(col, P, float2(132.0, H - 8.0));

    // edge shading
    float ed = sdRR(P - float2(70.0, H * 0.5), float2(70.0, H * 0.5), 8.0);
    col *= 1.0 - smoothstep(-5.0, 0.5, ed) * 0.18;
    return half4(half3(col), 1.0);
}
"""

    /**
     * Bottom-left hardware plate: circular speaker plate with concentric grooves,
     * C-shaped glowing light ring, terracotta accent dot, corner screws.
     * Local units: width = 103.
     */
    const val DISC_PLATE = "uniform float uPulse;\nuniform float uLight;\n" + PRELUDE + """
half4 main(float2 fc) {
    float2 P = fc / uRes.x * 103.0;
    float H = uRes.y / uRes.x * 103.0;
    float3 col = brushedBase(fc, uRes);
    col *= 0.86;

    float2 C = float2(51.5, 64.0);
    float2 d = P - C;
    float r = length(d);

    // outer seam ring
    col *= 1.0 - 0.22 * smoothstep(1.5, 0.0, abs(r - 43.5));

    // main disc with concentric grooves; the cone breathes with uPulse
    if (r < 33.0) {
        float rb = r * (1.0 - 0.05 * uPulse);
        col *= 0.93 + 0.07 * sin(rb * 3.2);
        col *= 0.90 + 0.10 * (1.0 - r / 33.0);
        col *= 1.0 + uPulse * (0.10 * cos(r * 0.5) + 0.04);
    }
    col *= 1.0 - 0.32 * exp(-abs(r - 33.0) * 0.8);
    col += 0.08 * smoothstep(1.2, 0.0, abs(r - 31.4));

    // hub pops forward on the pulse
    float hubR = 10.0 + 1.2 * uPulse;
    col *= 1.0 - 0.30 * smoothstep(1.3, 0.0, abs(r - hubR));
    if (r < hubR) { col *= 0.93; }
    col = mix(col, col * 0.70, smoothstep(2.2, 0.0, r));

    // glowing C-ring (gap at top-right); dimmable, kicks with the pulse
    float ang = atan(d.y, d.x);
    float da = abs(atan(sin(ang + 0.85), cos(ang + 0.85)));
    float mask = smoothstep(0.50, 0.90, da);
    float ga = abs(r - 37.5) - 1.8;
    float amp = uLight * (0.92 + 0.08 * sin(uTime * 1.3 + 1.0)) * (1.0 + 0.4 * max(uPulse, 0.0));
    float3 ringCol = mix(float3(0.70, 0.69, 0.67), float3(1.0, 0.99, 0.955), clamp(amp, 0.0, 1.0));
    col = mix(col, ringCol, smoothstep(0.8, -0.8, ga) * mask);
    col += float3(1.0, 0.93, 0.80) * exp(-max(ga, 0.0) * 0.18) * 0.42 * mask * amp;

    // terracotta accent dot
    float t = length(P - float2(84.0, 100.0)) - 6.0;
    if (t < 1.0) {
        float3 clay = mix(float3(0.68, 0.30, 0.18), float3(0.82, 0.45, 0.29), fbm(P * 2.0));
        clay *= 0.8 + 0.3 * clamp(0.5 - (P.y - 112.0) / 13.0, 0.0, 1.0);
        col = mix(clay, col, smoothstep(-1.0, 1.0, t));
    }

    col = screw(col, P, float2(8.0, 8.0));
    col = screw(col, P, float2(95.0, 8.0));
    col = screw(col, P, float2(8.0, H - 8.0));
    col = screw(col, P, float2(95.0, H - 8.0));

    float ed = sdRR(P - float2(51.5, H * 0.5), float2(51.5, H * 0.5), 8.0);
    col *= 1.0 - smoothstep(-5.0, 0.5, ed) * 0.18;
    return half4(half3(col), 1.0);
}
"""

    /** Terracotta planet: lit sphere with fbm clay surface, transparent outside. */
    const val PLANET = PRELUDE + """
half4 main(float2 fc) {
    float2 uv = (fc - 0.5 * uRes) / (0.5 * min(uRes.x, uRes.y));
    float r = length(uv);
    if (r > 1.0) { return half4(0.0); }
    float a = smoothstep(1.0, 0.985, r);
    float z = sqrt(max(0.0, 1.0 - r * r));
    float3 n = float3(uv.x, uv.y, z);
    float2 sp = uv / (z + 0.55);
    float t = fbm(sp * 3.2 + float2(7.0, 3.0));
    float t2 = fbm(sp * 8.5 + float2(1.0, 21.0));
    float3 base = mix(float3(0.40, 0.17, 0.095), float3(0.80, 0.42, 0.27), t);
    base = mix(base, float3(0.29, 0.115, 0.065), smoothstep(0.55, 0.85, t2) * 0.55);
    float3 l = normalize(float3(-0.45, -0.55, 0.72));
    float diff = clamp(dot(n, l), 0.0, 1.0);
    float3 col = base * (0.22 + 1.0 * diff);
    float spec = pow(clamp(dot(reflect(-l, n), float3(0.0, 0.0, 1.0)), 0.0, 1.0), 8.0);
    col += float3(1.0, 0.85, 0.70) * spec * 0.10;
    col *= 1.0 - 0.30 * smoothstep(0.72, 1.0, r);
    return half4(half3(col), 1.0) * a;
}
"""

    /** Rough clay texture swatch (also used for the knob face). */
    const val CLAY = PRELUDE + """
half4 main(float2 fc) {
    float2 uv = fc / uRes.x;
    float h0 = fbm(uv * 14.0);
    float e = 0.012;
    float hx = fbm((uv + float2(e, 0.0)) * 14.0);
    float hy = fbm((uv + float2(0.0, e)) * 14.0);
    float2 g = float2(hx - h0, hy - h0) / e;
    float light = clamp(0.88 - g.x * 0.05 - g.y * 0.09, 0.6, 1.15);
    float3 col = mix(float3(0.68, 0.30, 0.175), float3(0.80, 0.43, 0.28), h0);
    float cr = abs(fbm(uv * 5.0 + float2(4.2, 8.8)) - 0.5) * 2.0;
    col *= 0.92 + 0.08 * smoothstep(0.0, 0.3, cr);
    col *= light;
    return half4(half3(col), 1.0);
}
"""

    /** Mini "transparent tech" swatch: glass-over-metal with a glowing strip. */
    const val TECH = PRELUDE + """
half4 main(float2 fc) {
    float2 P = fc / uRes.x * 100.0;
    float H = uRes.y / uRes.x * 100.0;
    float3 col = brushedBase(fc, uRes) * float3(0.82, 0.82, 0.84);
    col *= 1.0 - 0.15 * smoothstep(0.9, 0.0, abs(P.y - H * 0.32));
    col *= 1.0 - 0.12 * smoothstep(0.9, 0.0, abs(P.x - 68.0)) * step(P.y, H * 0.6);
    float2 gg = fract(P / 5.0) - 0.5;
    float hole = smoothstep(1.1, 0.6, length(gg) * 5.0);
    col = mix(col, col * 0.72, hole * step(P.x, 34.0) * step(H * 0.45, P.y) * 0.8);
    float gd = sdSeg(P, float2(12.0, H * 0.62), float2(88.0, H * 0.62)) - 1.6;
    col = mix(col, float3(1.0, 0.99, 0.95), smoothstep(0.8, -0.8, gd));
    col += float3(1.0, 0.93, 0.80) * exp(-max(gd, 0.0) * 0.12) * 0.5;
    col = screw(col, P, float2(90.0, H - 9.0));
    return half4(half3(col), 1.0);
}
"""

    /** Dark anodized metal swatch. */
    const val ANODIZED = PRELUDE + """
half4 main(float2 fc) {
    float2 uv = fc / uRes;
    float3 col = mix(float3(0.085, 0.085, 0.095), float3(0.165, 0.165, 0.175), uv.y);
    col += (fbm(fc * float2(0.06, 0.5)) - 0.5) * 0.05;
    col += float3(0.06, 0.06, 0.065) * exp(-abs(uv.y - 0.30) * 7.0);
    col *= 1.0 - 0.35 * smoothstep(0.75, 1.0, uv.y);
    return half4(half3(col), 1.0);
}
"""

    /** Mars landscape for the AMBIENT CLAY player artwork. */
    const val MARS = PRELUDE + """
half4 main(float2 fc) {
    float2 uv = fc / uRes;
    float3 col = mix(float3(0.87, 0.63, 0.46), float3(0.76, 0.45, 0.30), clamp(uv.y * 1.5, 0.0, 1.0));
    col += float3(1.0, 0.82, 0.62) * exp(-length((uv - float2(0.78, 0.16)) * float2(1.5, 1.0)) * 3.2) * 0.30;

    // big mesa
    float mesaTop = mix(0.66, 0.34, smoothstep(0.06, 0.14, uv.x) * (1.0 - smoothstep(0.46, 0.56, uv.x)));
    mesaTop += (vnoise(float2(uv.x * 22.0, 3.0)) - 0.5) * 0.03;
    if (uv.y > mesaTop) {
        float3 mc = mix(float3(0.62, 0.31, 0.19), float3(0.44, 0.19, 0.11), (uv.y - mesaTop) * 1.6);
        mc *= 0.88 + 0.12 * vnoise(float2(uv.x * 60.0, uv.y * 8.0));
        col = mix(col, mc, smoothstep(mesaTop, mesaTop + 0.012, uv.y));
    }
    // mid ridge
    float h1 = 0.72 + (fbm(float2(uv.x * 5.0 + 3.7, 1.0)) - 0.5) * 0.10;
    col = mix(col, float3(0.50, 0.235, 0.135), smoothstep(h1, h1 + 0.012, uv.y));
    // foreground
    float h2 = 0.86 + (fbm(float2(uv.x * 3.0 + 9.1, 5.0)) - 0.5) * 0.08;
    col = mix(col, float3(0.33, 0.14, 0.08), smoothstep(h2, h2 + 0.015, uv.y));
    return half4(half3(col), 1.0);
}
"""

    /** Dark warm gradient + grain for the hero panel background. */
    const val HERO_BG = PRELUDE + """
half4 main(float2 fc) {
    float2 uv = fc / uRes;
    float3 a = float3(0.145, 0.062, 0.036);
    float3 b = float3(0.565, 0.270, 0.155);
    float g = smoothstep(0.0, 1.0, 1.25 - length(uv - float2(0.88, 0.12)) * 1.05);
    float3 col = mix(a, b, g * 0.95);
    col += (fbm(fc * 0.35) - 0.5) * 0.028;
    col *= 0.88 + 0.12 * smoothstep(1.3, 0.2, length(uv - float2(0.9, 0.1)));
    return half4(half3(col), 1.0);
}
"""

    /** Lock-screen wallpaper: terracotta gradient with a soft clay sphere. */
    const val WALLPAPER = PRELUDE + """
half4 main(float2 fc) {
    float2 uv = fc / uRes;
    float3 col = mix(float3(0.735, 0.375, 0.225), float3(0.545, 0.245, 0.135), uv.y);
    float2 p = uv - float2(0.86, 0.36);
    p.x *= uRes.x / uRes.y;
    float d = length(p);
    float sph = smoothstep(0.315, 0.295, d);
    float3 sc = mix(float3(0.85, 0.51, 0.33), float3(0.55, 0.25, 0.14), clamp((p.y + 0.24) * 2.2, 0.0, 1.0));
    col = mix(col, sc, sph);
    col += float3(0.9, 0.6, 0.4) * exp(-d * 4.0) * 0.14;
    col += (fbm(fc * 0.4) - 0.5) * 0.03;
    return half4(half3(col), 1.0);
}
"""
}
