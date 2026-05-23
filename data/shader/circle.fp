// Draws a mouse controlled circle, which radius changes over time
// Its color is dependent on its position
// Pierre-André Mudry, 2014

// What we get from Java, *once*
uniform vec2 position;
uniform float radius;

in vec4 v_color;

const float antialias_distance = 15.0;
const float default_radius = 100.0;

void main() {
    float dist = distance(gl_FragCoord.xy, position.xy);

    float r = radius <= 0.1 ? default_radius : radius;

    // Hors du cercle = noir opaque
    // Dedans = transparent (on voit la tilemap)
    float alpha = smoothstep(r - antialias_distance, r, dist);

    gl_FragColor = vec4(0.0, 0.0, 0.0, alpha);
}