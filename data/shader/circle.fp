// Draws a mouse controlled circle, which radius changes over time
// Its color is dependent on its position
// Pierre-André Mudry, 2014

// What we get from Java, *once*
uniform vec2 resolution;
uniform float time;
uniform vec2 positions[2];
uniform float radius[2];      // ← un radius par cercle
uniform int numPositions;

in vec4 v_color;

const float antialias_distance = 80.0;

void main() {
    vec2 uv = gl_FragCoord.xy;
    float light = 0.0;

    for (int i = 0; i < 2; i++) {
        if (i >= numPositions) break;

        float dist = length(uv - positions[i]);
        float r = radius[i];
        light = max(light, 1.0 - smoothstep(r - antialias_distance, r, dist));
    }

    gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0 - light);
}