package map

import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2

class Shader() {
  var urlShader: String = "data/shader/circle.fp"
  var time: Float = 0

  /**
   * Set the shader
   * @param g, Graphics
   * @param url, url of the ".cp" file
   */
  def setShader(g: GdxGraphics, url: String = this.urlShader): Unit = {
    if (g.getShaderRenderer == null) {
      g.setShader(url)
    }
  }

  /**
   *  This function is already written before we see how to SET MULTIPLE SHADERS WITH AI.
   *  Basically, we have just add some lines to set:
   *  - NUMBER POSITION :  renderer.setUniform("numPositions", positions.length)
   *  - EACH POSITION:
   *      for (i <- positions.indices) {
   *        renderer.setUniform(s"positions[$i]", positions(i))
   *      }
   * @param g, Graphics
   * @param positions, list of positions to draw each a shader
   * @param radius, radius of the shader
   */
  def drawShader(g: GdxGraphics, positions: Array[Vector2], radius: Float): Unit = {
    val renderer = g.getShaderRenderer
    // Pass the number of positions
    renderer.setUniform("resolution", new Vector2(Gdx.graphics.getWidth.toFloat, Gdx.graphics.getHeight.toFloat))
    renderer.setUniform("numPositions", positions.length)
    renderer.setUniform("radius", radius)

    // set uniform for each position
    for (i <- positions.indices) {
      renderer.setUniform(s"positions[$i]", positions(i))
    }

    time += Gdx.graphics.getDeltaTime
    g.drawShader(time)
  }
}
