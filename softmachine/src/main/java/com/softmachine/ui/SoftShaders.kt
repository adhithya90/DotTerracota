package com.softmachine.ui

/**
 * AGSL shaders for the Soft Machine style, ported 1:1 from the approved HTML/WebGL
 * board. All colors are measured from the reference (warm greige / mauve — never
 * pure white, never blue-lavender). AGSL fragCoord is top-down, matching the
 * board's flipped coordinates, except PUFFY_CLOUD which works in y-up camera space.
 */
object SoftShaders {

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
float sminP(float a, float b, float k) {
    float h = clamp(0.5 + 0.5 * (b - a) / k, 0.0, 1.0);
    return mix(b, a, h) - k * h * (1.0 - h);
}
"""

    /** Raymarched gooey cloud on its warm plate. uPulse squishes it. */
    const val PUFFY_CLOUD = "uniform float uPulse;\n" + PRELUDE + """
float cloudSdf(float3 p) {
    p.y /= (1.0 - 0.14 * uPulse);
    p.x /= (1.0 + 0.07 * uPulse);
    float2 yz = float2(p.y, p.z - 0.26);
    p = float3(p.x, 0.891 * yz.x - 0.454 * yz.y, 0.454 * yz.x + 0.891 * yz.y + 0.26);
    float br = 0.010 * sin(uTime * 0.9);
    float br2 = 0.010 * sin(uTime * 0.9 + 2.1);
    float d = length(p - float3(0.0, 0.02, 0.33)) - (0.225 + br);
    d = sminP(d, length(p - float3(-0.24, 0.22, 0.25)) - (0.190 + br2), 0.095);
    d = sminP(d, length(p - float3(0.21, 0.24, 0.25)) - (0.205 + br), 0.095);
    d = sminP(d, length(p - float3(-0.45, 0.03, 0.22)) - (0.160 + br), 0.085);
    d = sminP(d, length(p - float3(0.46, 0.04, 0.22)) - (0.160 + br2), 0.085);
    d = sminP(d, length(p - float3(-0.21, -0.19, 0.25)) - (0.185 + br2), 0.085);
    d = sminP(d, length(p - float3(0.12, -0.21, 0.25)) - (0.185 + br), 0.085);
    d = sminP(d, length(p - float3(0.38, -0.12, 0.23)) - (0.150 + br2), 0.085);
    return d * 0.85;
}
float sceneSdf(float3 p) { return min(p.z, cloudSdf(p)); }
float3 calcNormal(float3 p) {
    float2 e = float2(0.0015, -0.0015);
    return normalize(
        e.xyy * sceneSdf(p + e.xyy) + e.yyx * sceneSdf(p + e.yyx) +
        e.yxy * sceneSdf(p + e.yxy) + e.xxx * sceneSdf(p + e.xxx)
    );
}
float calcAO(float3 p, float3 n) {
    float occ = 0.0;
    float sca = 1.0;
    for (int i = 1; i <= 5; i++) {
        float hgt = 0.02 + 0.045 * float(i);
        float d = sceneSdf(p + n * hgt);
        occ += (hgt - d) * sca;
        sca *= 0.75;
    }
    return clamp(1.0 - 2.2 * occ, 0.0, 1.0);
}
float softShadowM(float3 ro, float3 rd) {
    float res = 1.0;
    float t = 0.02;
    for (int i = 0; i < 28; i++) {
        float h = cloudSdf(ro + rd * t);
        res = min(res, 4.5 * h / t);
        t += clamp(h, 0.01, 0.10);
        if (res < 0.005 || t > 2.0) { break; }
    }
    return clamp(res, 0.0, 1.0);
}
half4 main(float2 fc) {
    float2 uv = (fc - 0.5 * uRes) / uRes.y;
    uv.y = -uv.y;
    uv.y -= 0.10;
    float3 ro = float3(uv * 1.72, 2.0);
    float3 rd = normalize(float3(uv * 0.06, -1.0));
    float t = 0.0;
    float3 pos = ro;
    for (int i = 0; i < 72; i++) {
        float d = sceneSdf(pos);
        if (d < 0.0015 || t > 4.0) { break; }
        t += d;
        pos = ro + rd * t;
    }
    float3 lig = normalize(float3(-0.10, 0.36, 0.92));
    float3 n = calcNormal(pos);
    bool isCloud = cloudSdf(pos) < pos.z;
    float2 np = pos.xy * 26.0 + float2(pos.z * 9.0, 3.0);
    float3 grain = float3(fbm(np) - 0.5, fbm(np + 11.3) - 0.5, 0.0);
    n = normalize(n + grain * (isCloud ? 0.05 : 0.02));
    float ao = calcAO(pos, n);
    float sh = softShadowM(pos + n * 0.01, lig);
    float dif = clamp(dot(n, lig) * 0.42 + 0.58, 0.0, 1.0);
    float3 col;
    if (isCloud) {
        float aoC = clamp(0.52 + 0.48 * ao, 0.0, 1.0);
        col = float3(0.948, 0.936, 0.921) * (0.58 * aoC + 0.45 * dif * mix(0.90, 1.0, sh));
    } else {
        float aoP = clamp(0.62 + 0.38 * ao, 0.0, 1.0);
        float difP = clamp(dot(n, lig) * 0.30 + 0.70, 0.0, 1.0);
        col = float3(0.920, 0.904, 0.888) * (0.55 * aoP + 0.47 * difP * mix(0.90, 1.0, sh));
    }
    float3 hal = normalize(lig + float3(0.0, 0.0, 1.0));
    if (isCloud) {
        col += float3(1.0) * pow(clamp(dot(n, hal), 0.0, 1.0), 8.0) * 0.06 * sh;
        col += float3(1.0) * pow(clamp(dot(n, hal), 0.0, 1.0), 52.0) * 0.028 * sh;
        col += float3(0.04) * pow(1.0 - clamp(n.z, 0.0, 1.0), 3.0);
    } else {
        col += float3(1.0) * pow(clamp(dot(n, hal), 0.0, 1.0), 42.0) * 0.03 * sh;
    }
    col += float3(0.018) * exp(-length((fc - uRes * float2(0.5, 0.30)) / uRes.y) * 1.6);
    float cr = 0.16 * min(uRes.x, uRes.y);
    float2 q = abs(fc - 0.5 * uRes) - (0.5 * uRes - cr);
    float edge = length(max(q, float2(0.0))) + min(max(q.x, q.y), 0.0) - cr;
    float topW = clamp(-(fc.y - 0.5 * uRes.y) / uRes.y + 0.5, 0.0, 1.0);
    col += float3(0.030) * smoothstep(-30.0, -4.0, edge) * topW;
    col -= float3(0.022) * smoothstep(-26.0, -4.0, edge) * (1.0 - topW);
    return half4(half3(col), 1.0);
}
"""

    /** Home wallpaper: warm milk with a mauve blob flowing from the top-right. */
    const val BLOB_WALLPAPER = PRELUDE + """
half4 main(float2 fc) {
    float2 uv = fc / uRes;
    float3 col = mix(float3(0.922, 0.906, 0.903), float3(0.888, 0.868, 0.874), uv.y);
    float2 p = (fc - uRes * float2(0.70, 0.17)) / uRes.y;
    float ang = atan(p.y, p.x);
    float r = length(p);
    float wob = fbm(float2(ang * 1.2 + 3.0, uTime * 0.05)) * 0.055 + fbm(p * 2.5 + uTime * 0.02) * 0.035;
    float d = r - (0.145 + wob);
    float2 p2 = (fc - uRes * float2(0.52, 0.40)) / uRes.y;
    float wob2 = fbm(float2(atan(p2.y, p2.x) * 1.4 + 7.0, uTime * 0.04)) * 0.05;
    float d2 = length(p2 * float2(1.2, 0.85)) - (0.105 + wob2);
    float k = 0.06;
    float hmix = clamp(0.5 + 0.5 * (d2 - d) / k, 0.0, 1.0);
    d = mix(d2, d, hmix) - k * hmix * (1.0 - hmix);
    col -= float3(0.045, 0.045, 0.040) * exp(-max(d, 0.0) * 7.0) * 0.4;
    float body = smoothstep(0.06, -0.10, d);
    float3 pc = mix(float3(0.762, 0.718, 0.822), float3(0.478, 0.428, 0.556),
                    clamp(p.y * 1.3 + r * 0.5 + 0.30, 0.0, 1.0));
    pc += float3(0.05) * exp(-length(p - float2(-0.08, -0.14)) * 3.0);
    col = mix(col, pc, body);
    col += (fbm(fc * 0.5) - 0.5) * 0.015;
    return half4(half3(col), 1.0);
}
"""

    /**
     * Air Core cushion unit: thermoformed blister pack. Felt knob with gloss mauve
     * ring, keyhole blister with double walls, racetrack channel, bead chain,
     * cord + connector. uKnob (0..1) positions the puck along the channel.
     * Local units: width = 100, height ≈ 141.
     */
    const val CUSHION_PLATE = "uniform float uKnob;\n" + PRELUDE + """
float tubePath(float2 P) {
    float d = sdSeg(P, float2(56.0, 40.0), float2(56.0, 94.0));
    float2 q = P - float2(66.0, 94.0);
    float ar = (q.y > 0.0) ? abs(length(q) - 10.0)
        : min(length(P - float2(56.0, 94.0)), length(P - float2(76.0, 94.0)));
    d = min(d, ar);
    d = min(d, sdSeg(P, float2(76.0, 94.0), float2(76.0, 46.0)));
    return d;
}
float cordPath(float2 P) {
    float d = sdSeg(P, float2(18.0, -4.0), float2(18.0, 26.0));
    d = min(d, sdSeg(P, float2(18.0, 26.0), float2(23.0, 38.0)));
    d = min(d, sdSeg(P, float2(23.0, 38.0), float2(34.0, 46.0)));
    return d;
}
float blisterSdf(float2 P) {
    float c = length(P - float2(56.0, 30.0)) - 25.5;
    float t = tubePath(P) - 10.5;
    return sminP(c, t, 5.0);
}
float2 gradB(float2 P) {
    float e = 0.5;
    return normalize(float2(
        blisterSdf(P + float2(e, 0.0)) - blisterSdf(P - float2(e, 0.0)),
        blisterSdf(P + float2(0.0, e)) - blisterSdf(P - float2(0.0, e))));
}
float cavSdf(float2 P) { return tubePath(P) - 5.6; }
float2 gradC(float2 P) {
    float e = 0.5;
    return normalize(float2(
        cavSdf(P + float2(e, 0.0)) - cavSdf(P - float2(e, 0.0)),
        cavSdf(P + float2(0.0, e)) - cavSdf(P - float2(0.0, e))));
}
half4 main(float2 fc) {
    float2 P = fc / uRes.x * 100.0;
    float H = uRes.y / uRes.x * 100.0;
    float2 L2 = normalize(float2(-0.42, -0.91));

    float3 col = mix(float3(0.838, 0.808, 0.792), float3(0.802, 0.772, 0.758), P.y / H);
    col += (fbm(fc * 0.25) - 0.5) * 0.030;
    col += (hash(fc) - 0.5) * 0.016;
    col += float3(0.035) * exp(-length((P - float2(26.0, 18.0)) / 55.0) * 1.3);

    // tray outline
    float tE = sdRR(P - float2(50.0, H * 0.5 + 1.0), float2(45.0, H * 0.5 - 6.0), 22.0);
    float2 tn = normalize(float2(
        sdRR(P + float2(0.5, 0.0) - float2(50.0, H * 0.5 + 1.0), float2(45.0, H * 0.5 - 6.0), 22.0)
      - sdRR(P - float2(0.5, 0.0) - float2(50.0, H * 0.5 + 1.0), float2(45.0, H * 0.5 - 6.0), 22.0),
        sdRR(P + float2(0.0, 0.5) - float2(50.0, H * 0.5 + 1.0), float2(45.0, H * 0.5 - 6.0), 22.0)
      - sdRR(P - float2(0.0, 0.5) - float2(50.0, H * 0.5 + 1.0), float2(45.0, H * 0.5 - 6.0), 22.0)));
    float tl = dot(tn, L2);
    col += float3(0.085) * smoothstep(0.9, 0.15, abs(tE)) * clamp(tl, 0.0, 1.0);
    col -= float3(0.06) * smoothstep(0.9, 0.15, abs(tE)) * clamp(-tl, 0.0, 1.0);
    col += float3(0.04) * smoothstep(0.8, 0.15, abs(tE + 2.2)) * clamp(-tl, 0.0, 1.0);

    // cord
    float cd = cordPath(P) - 1.9;
    float2 cn = normalize(float2(
        cordPath(P + float2(0.4, 0.0)) - cordPath(P - float2(0.4, 0.0)),
        cordPath(P + float2(0.0, 0.4)) - cordPath(P - float2(0.0, 0.4))));
    col -= float3(0.07) * exp(-max(cordPath(P - float2(0.8, 1.6)) - 1.9, 0.0) * 1.2) * 0.5;
    if (cd < 0.0) { col = mix(col, float3(0.846, 0.818, 0.804), 0.85); }
    float cb = smoothstep(1.0, 0.15, abs(cd));
    col += float3(0.10) * cb * clamp(dot(cn, L2), 0.0, 1.0);
    col -= float3(0.07) * cb * clamp(-dot(cn, L2), 0.0, 1.0);
    // connector block
    float cc = sdRR(P - float2(18.0, 13.0), float2(2.8, 5.6), 1.4);
    col -= float3(0.08) * exp(-max(sdRR(P - float2(18.8, 14.6), float2(2.8, 5.6), 1.4), 0.0) * 1.1) * 0.5;
    if (cc < 0.0) {
        col = mix(float3(0.826, 0.798, 0.784), float3(0.788, 0.760, 0.748),
                  clamp((P.y - 13.0) / 11.0 + 0.5, 0.0, 1.0));
        col -= float3(0.05) * smoothstep(0.6, 0.1, abs(P.y - 11.0)) * step(abs(P.x - 18.0), 2.2);
        col -= float3(0.05) * smoothstep(0.6, 0.1, abs(P.y - 15.0)) * step(abs(P.x - 18.0), 2.2);
    }
    float2 ccn = normalize(float2(
        sdRR(P + float2(0.4, 0.0) - float2(18.0, 13.0), float2(2.8, 5.6), 1.4)
      - sdRR(P - float2(0.4, 0.0) - float2(18.0, 13.0), float2(2.8, 5.6), 1.4),
        sdRR(P + float2(0.0, 0.4) - float2(18.0, 13.0), float2(2.8, 5.6), 1.4)
      - sdRR(P - float2(0.0, 0.4) - float2(18.0, 13.0), float2(2.8, 5.6), 1.4)));
    float ccb = smoothstep(0.9, 0.15, abs(cc));
    col += float3(0.11) * ccb * clamp(dot(ccn, L2), 0.0, 1.0);
    col -= float3(0.08) * ccb * clamp(-dot(ccn, L2), 0.0, 1.0);

    // blister
    float e = blisterSdf(P);
    float2 n2 = gradB(P);
    float bl = dot(n2, L2);
    float esh = blisterSdf(P - float2(1.3, 2.6));
    col -= float3(0.080, 0.077, 0.071) * exp(-max(esh, 0.0) * 0.30) * 0.55;
    col -= float3(0.095, 0.092, 0.085) * exp(-max(esh, 0.0) * 0.85) * 0.5;
    col += float3(0.05) * exp(-max(blisterSdf(P - float2(-1.0, -1.6)), 0.0) * 0.6) * 0.5;
    if (e < 0.0) {
        col += float3(0.020);
        col += float3(0.045) * smoothstep(0.45, 0.95, fbm(float2(P.x * 0.28 + 9.0, P.y * 0.045)));
        float drop = vnoise(P * 1.7 + 3.0);
        col += float3(0.05) * smoothstep(0.90, 0.975, drop);
        col -= float3(0.04) * smoothstep(0.90, 0.975, vnoise(P * 1.9 + 11.0));
    }
    col += float3(0.17) * smoothstep(1.2, 0.2, abs(e + 0.9)) * clamp(bl, 0.0, 1.0);
    col -= float3(0.10) * smoothstep(1.2, 0.2, abs(e + 0.9)) * clamp(-bl, 0.0, 1.0);
    col += float3(0.08) * smoothstep(1.0, 0.2, abs(e + 3.4)) * clamp(-bl, 0.0, 1.0);
    col -= float3(0.06) * smoothstep(1.0, 0.2, abs(e + 3.4)) * clamp(bl, 0.0, 1.0);
    col += float3(0.11) * smoothstep(4.5, 0.5, abs(e + 2.0)) * pow(clamp(bl, 0.0, 1.0), 2.0);

    // channel cavity
    float c2 = cavSdf(P);
    float2 nc = gradC(P);
    float cl = dot(nc, L2);
    if (c2 < 0.0) {
        col *= 0.952;
        col += (fbm(fc * 0.18) - 0.5) * 0.02;
        col *= 1.0 - 0.04 * clamp((P.y - 46.0) / 60.0, 0.0, 1.0);
    }
    float cband = smoothstep(1.8, 0.25, abs(c2));
    col -= float3(0.095) * cband * clamp(cl, 0.0, 1.0);
    col += float3(0.105) * cband * clamp(-cl, 0.0, 1.0);
    col -= float3(0.05) * smoothstep(0.8, 0.15, abs(c2 - 1.1));

    // beads (skip around the interactive puck)
    float py = mix(50.0, 90.0, clamp(uKnob, 0.0, 1.0));
    for (int i = 0; i < 14; i++) {
        float fy = 46.0 + float(i) * 3.55;
        if (abs(fy - py) < 3.2) { continue; }
        float2 bc = float2(56.0, fy);
        float br2 = 1.30 - 0.045 * float(i);
        float bd = length(P - bc);
        col -= float3(0.07) * exp(-max(length(P - bc - float2(0.5, 1.2)) - br2, 0.0) * 2.2) * 0.5;
        if (bd < br2) {
            float2 np2 = (P - bc) / br2;
            float z = sqrt(max(0.0, 1.0 - dot(np2, np2)));
            float dl = clamp(dot(normalize(float3(L2, 1.1)), float3(np2, z)), 0.0, 1.0);
            col = float3(0.858, 0.832, 0.818) * (0.80 + 0.30 * dl);
        }
    }
    // puck
    float2 pc2 = float2(56.0, py);
    float pk = length(P - pc2) - 3.3;
    col -= float3(0.09) * exp(-max(length(P - pc2 - float2(0.8, 1.8)) - 3.3, 0.0) * 1.1) * 0.55;
    if (pk < 0.0) {
        float2 np3 = (P - pc2) / 3.3;
        float z3 = sqrt(max(0.0, 1.0 - dot(np3, np3)));
        float dl3 = clamp(dot(normalize(float3(L2, 1.1)), float3(np3, z3)), 0.0, 1.0);
        col = float3(0.864, 0.838, 0.824) * (0.78 + 0.34 * dl3);
        col += float3(0.20) * pow(dl3, 8.0);
        col = mix(col, float3(0.746, 0.700, 0.762), smoothstep(0.7, -0.7, length(P - pc2) - 1.1));
    }

    // knob
    float2 kc = float2(56.0, 30.0);
    float kr = length(P - kc);
    col -= float3(0.085, 0.082, 0.076) * exp(-max(length(P - kc - float2(1.6, 4.4)) - 23.6, 0.0) * 0.16) * 0.7;
    col -= float3(0.10, 0.097, 0.090) * exp(-max(length(P - kc - float2(1.0, 2.4)) - 23.6, 0.0) * 0.5) * 0.55;
    if (kr < 23.6) {
        float2 nk = normalize(P - kc);
        float rl = dot(nk, L2);
        col = mix(float3(0.874, 0.848, 0.834), float3(0.794, 0.768, 0.756),
                  clamp((P.y - kc.y) / 34.0 + 0.5, 0.0, 1.0));
        col += float3(0.13) * pow(clamp(rl, 0.0, 1.0), 2.5) * smoothstep(23.6, 20.5, kr);
        col -= float3(0.08) * clamp(-rl, 0.0, 1.0) * smoothstep(19.2, 22.0, kr);
        col += float3(0.16) * smoothstep(0.8, 0.15, abs(kr - 22.8)) * pow(clamp(rl, 0.0, 1.0), 2.0);
        col -= float3(0.06) * smoothstep(0.8, 0.15, abs(kr - 22.8)) * clamp(-rl, 0.0, 1.0);
    }
    if (kr < 19.4) {
        float2 nk = normalize(P - kc);
        float rl = dot(nk, L2);
        col = mix(float3(0.764, 0.720, 0.784), float3(0.668, 0.624, 0.692),
                  clamp((P.y - kc.y) / 28.0 + 0.5, 0.0, 1.0));
        col += float3(0.24) * pow(clamp(rl, 0.0, 1.0), 3.0);
        col -= float3(0.06) * clamp(-rl, 0.0, 1.0);
        col -= float3(0.06) * smoothstep(0.5, 0.1, abs(kr - 19.25));
    }
    if (kr < 17.2) {
        float mottle = fbm(P * 1.1 + 4.0);
        float curl = fbm(float2(P.x * 5.5 + fbm(P * 2.2) * 5.0, P.y * 5.5));
        float fiber = curl * 0.55 + fbm(P * 13.0) * 0.45;
        float3 felt = mix(float3(0.816, 0.776, 0.796), float3(0.764, 0.724, 0.750), mottle * 0.8);
        felt += (fiber - 0.5) * 0.075;
        felt += (hash(fc) - 0.5) * 0.022;
        float rr2 = kr / 17.2;
        felt *= 1.0 - 0.11 * smoothstep(0.5, 1.0, rr2);
        felt *= 1.0 - 0.07 * clamp((P.y - kc.y) / 17.0, 0.0, 1.0);
        felt += float3(0.055) * exp(-length((P - kc - float2(-5.0, -6.0)) / 10.0) * 1.3);
        felt -= float3(0.05) * smoothstep(0.6, 0.1, abs(kr - 17.05));
        col = felt;
    }
    return half4(half3(col), 1.0);
}
"""

    /**
     * Mauve silicone pad. Pristine at rest; a touch dimple grows with uPress
     * (0..1) — both deeper and wider, like a finger sinking into silicone.
     */
    const val SILICONE_PAD = PRELUDE + """
uniform float2 uTouch;
uniform float uPress;
half4 main(float2 fc) {
    float2 uv = fc / uRes;
    float3 col = mix(float3(0.792, 0.742, 0.802), float3(0.702, 0.650, 0.718), uv.y);
    col += (fbm(fc * 0.35) - 0.5) * 0.02;
    float2 p = fc / uRes.y;
    float2 tc = uTouch / uRes.y;
    float sig = 0.006 + 0.014 * uPress;
    float tg = exp(-dot(p - tc, p - tc) / sig) * uPress;
    float g = tg;
    float2 grad = (p - tc) * tg / (sig * 0.5);
    float2 gp = fc * 0.020;
    grad += float2(fbm(gp) - 0.5, fbm(gp + 9.1) - 0.5) * 1.6;
    float3 n = normalize(float3(-grad * 0.05, 1.0));
    float3 l = normalize(float3(-0.4, -0.5, 0.75));
    float lightAmt = clamp(dot(n, l) - dot(float3(0.0, 0.0, 1.0), l), -1.0, 1.0);
    col *= 1.0 - 0.24 * clamp(g, 0.0, 1.2);
    col += lightAmt * 0.38;
    float3 hal = normalize(l + float3(0.0, 0.0, 1.0));
    col += float3(0.9, 0.88, 0.95) * pow(clamp(dot(n, hal), 0.0, 1.0), 26.0) * 0.05;
    col += float3(0.05) * exp(-length(uv - float2(0.2, 0.15)) * 2.2);
    float cr = 0.16 * min(uRes.x, uRes.y);
    float2 q = abs(fc - 0.5 * uRes) - (0.5 * uRes - cr);
    float edge = length(max(q, float2(0.0))) + min(max(q.x, q.y), 0.0) - cr;
    float topW = clamp(-(fc.y - 0.5 * uRes.y) / uRes.y + 0.5, 0.0, 1.0);
    col += float3(0.06) * smoothstep(-10.0, -2.0, edge) * topW;
    col -= float3(0.05) * smoothstep(-10.0, -2.0, edge) * (1.0 - topW);
    return half4(half3(col), 1.0);
}
"""

    /**
     * Frosted milky glass — rigid, so nothing dents. A touch wipes the frost:
     * uSpread is the wiped radius (grows while held, stays after release) and
     * uPress is the clarity (fades slowly as the fog re-condenses).
     */
    const val MILKY_GLASS = PRELUDE + """
uniform float2 uTouch;
uniform float uPress;
uniform float uSpread;
half4 main(float2 fc) {
    float2 uv = fc / uRes;
    float3 col = float3(0.888, 0.872, 0.862);
    col += (fbm(uv * 5.0) - 0.5) * 0.055;
    col += (fbm(uv * 14.0 + 5.0) - 0.5) * 0.03;
    float b = vnoise(uv * float2(14.0, 10.0) + 2.0);
    col += float3(0.05) * smoothstep(0.78, 0.95, b);
    col -= float3(0.03) * smoothstep(0.78, 0.95, vnoise(uv * float2(12.0, 9.0) + 9.0));
    col += float3(0.05) * exp(-abs(uv.y - 0.35) * 5.0);
    // wiped-clear patch: hard-ish edge like a finger squeegee, not a dent
    float2 p = fc / uRes.y;
    float2 tc = uTouch / uRes.y;
    float rad = 0.05 + 0.13 * uSpread;
    float d = length(p - tc);
    float wipe = smoothstep(rad, rad * 0.55, d) * uPress;
    // clear glass: deeper, cooler, slight vertical reflection streak
    float3 clearCol = float3(0.742, 0.732, 0.748);
    clearCol += float3(0.10) * smoothstep(0.3, 0.9, fbm(float2(p.x * 3.0 + 5.0, p.y * 0.6)));
    col = mix(col, clearCol, wipe * 0.92);
    // condensation ring at the wipe boundary
    col += float3(0.07) * smoothstep(0.02, 0.0, abs(d - rad)) * uPress;
    return half4(half3(col), 1.0);
}
"""

    /** Fine micro-bump texture. Pressing irons the bumps flat under the finger. */
    const val MICRO_TEXTURE = PRELUDE + """
uniform float2 uTouch;
uniform float uPress;
half4 main(float2 fc) {
    float2 uv = fc / uRes;
    float3 col = float3(0.884, 0.868, 0.856);
    float2 p = fc / uRes.y;
    float2 tc = uTouch / uRes.y;
    float sig = 0.010 + 0.018 * uPress;
    float flat0 = exp(-dot(p - tc, p - tc) / sig) * uPress;
    float2 g = fract(fc / (uRes.x / 26.0)) - 0.5;
    float d = length(g);
    float hgt = exp(-d * d * 9.0) * (1.0 - 0.92 * flat0);
    float2 gg = g * hgt * 6.0;
    float lightAmt = clamp(-gg.x * 0.5 - gg.y * 0.8, -1.0, 1.0);
    col += lightAmt * 0.05;
    col -= 0.02 * hgt;
    // pressed depression: shaded center, lit lower lip
    col -= float3(0.055) * flat0;
    col += float3(0.07) * exp(-dot(p - tc - float2(0.0, 0.016), p - tc - float2(0.0, 0.016)) / (sig * 0.6)) * uPress * 0.5;
    col *= 1.0 - 0.08 * uv.y;
    return half4(half3(col), 1.0);
}
"""

    /** Dreamy rolling cloudscape for the AMBIENT CLOUD player art. */
    const val CLOUDSCAPE = PRELUDE + """
half4 main(float2 fc) {
    float2 uv = fc / uRes;
    float3 col = mix(float3(0.898, 0.878, 0.886), float3(0.746, 0.718, 0.756), uv.y * 1.1);
    col += float3(0.9, 0.85, 0.9) * exp(-length((uv - float2(0.7, 0.1)) * float2(1.4, 1.0)) * 2.5) * 0.10;
    for (int i = 0; i < 4; i++) {
        float fi = float(i);
        float y0 = 0.38 + fi * 0.17;
        float hgt = y0 + (fbm(float2(uv.x * (2.2 + fi), fi * 7.7 + uTime * 0.02)) - 0.5) * 0.22;
        float m = smoothstep(hgt, hgt + 0.10, uv.y);
        float3 lc = mix(float3(0.868, 0.848, 0.856), float3(0.552, 0.524, 0.578), fi / 3.5);
        lc += 0.05 * (1.0 - fi / 3.0);
        col = mix(col, lc, m);
    }
    return half4(half3(col), 1.0);
}
"""

    /** Frosted glass speaker disc with a faint mauve glow ring. */
    const val SOFT_DISC = "uniform float uPulse;\nuniform float uLight;\n" + PRELUDE + """
half4 main(float2 fc) {
    float2 P = fc / uRes.x * 100.0;
    float H = uRes.y / uRes.x * 100.0;
    float3 col = float3(0.880, 0.862, 0.852);
    col += (fbm(fc * 0.15) - 0.5) * 0.03;
    col += float3(0.04) * exp(-length((P - float2(30.0, 26.0)) / 40.0) * 1.2);
    float2 C = float2(50.0, H * 0.46);
    float2 d = P - C;
    float r = length(d);
    float seamShade = clamp((P.y - C.y) / 60.0 + 0.5, 0.0, 1.0);
    col -= float3(0.09) * smoothstep(1.3, 0.0, abs(r - 41.0)) * (1.0 - seamShade * 0.5);
    col += float3(0.08) * smoothstep(1.3, 0.0, abs(r - 42.8)) * seamShade;
    if (r < 31.0) {
        float rb = r * (1.0 - 0.05 * uPulse);
        col *= 0.94 + 0.06 * sin(rb * 2.4);
        col *= 0.93 + 0.09 * (1.0 - r / 31.0);
        col *= 1.0 + 0.05 * clamp(-(P.y - C.y) / 31.0, -1.0, 1.0);
        col += (fbm(P * 1.4) - 0.5) * 0.025;
        col *= 1.0 + uPulse * (0.05 * cos(r * 0.5) + 0.03);
    }
    float dsh = length(P - C - float2(0.5, 2.2)) - 31.0;
    col -= float3(0.10) * exp(-max(dsh, 0.0) * 0.30) * 0.8;
    col += float3(0.07) * smoothstep(1.2, 0.0, abs(r - 29.2)) * 0.8;
    float hubR = 9.0 + 1.0 * uPulse;
    col -= float3(0.07) * smoothstep(1.2, 0.0, abs(r - hubR)) * 0.7;
    col = mix(col, col * 0.80, smoothstep(2.0, 0.0, r));
    float ga = abs(r - 35.5) - 1.4;
    float amp = uLight * (0.9 + 0.1 * sin(uTime * 1.1)) * (1.0 + 0.35 * max(uPulse, 0.0));
    float3 ringCol = mix(float3(0.842, 0.826, 0.816), float3(0.92, 0.89, 0.94), clamp(amp, 0.0, 1.0));
    col = mix(col, ringCol, smoothstep(0.8, -0.8, ga));
    col += float3(0.74, 0.68, 0.84) * exp(-max(ga, 0.0) * 0.22) * 0.30 * amp;
    float tt = length(P - float2(80.0, H * 0.46 + 34.0)) - 5.0;
    if (tt < 1.0) {
        float3 lav = mix(float3(0.746, 0.700, 0.762), float3(0.650, 0.606, 0.672),
                         clamp((P.y - H * 0.46 - 34.0) / 10.0 + 0.5, 0.0, 1.0));
        col = mix(lav, col, smoothstep(-1.0, 1.0, tt));
    }
    for (int i = 0; i < 4; i++) {
        float2 rp = float2(i < 2 ? 9.0 : 91.0, (i == 0 || i == 2) ? 9.0 : H - 9.0);
        float rd2 = length(P - rp);
        col -= float3(0.05) * smoothstep(2.6, 1.4, rd2) * 0.5;
        col += float3(0.04) * smoothstep(1.4, 0.4, rd2) * 0.7;
    }
    return half4(half3(col), 1.0);
}
"""
}
