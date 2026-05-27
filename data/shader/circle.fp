// Draws a mouse controlled circle, which radius changes over time
// Its color is dependent on its position
// Pierre-André Mudry, 2014

// What we get from Java, *once*
uniform vec2      resolution;
uniform float     time;
uniform vec2      positions[2];   // max 2 sources de lumière
uniform int       numPositions;
uniform float radius;

in vec4 v_color;

const float antialias_distance = 100.0;
const float default_radius = 200.0;

void main() {
  vec2 uv = gl_FragCoord.xy;
  float light = 0.0;

  for (int i = 0; i < 4; i++) {
    if (i >= numPositions) break;

    vec2 diff = uv - positions[i];
    float dist = length(diff);
    light = max(light, 1.0 - smoothstep(radius * 0.7, radius, dist));
  }

  gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0 - light);
}