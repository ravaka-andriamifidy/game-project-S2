package map

import ch.hevs.gdx2d.lib.GdxGraphics
import classes.{Chaser, Entity, Player}
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.{Vector2, Vector3}
import scala.collection.mutable.ArrayBuffer

class Shader() {
  var urlShader: String = "data/shader/circle.fp"
  var time: Float = 0

  /**
   * Set the shader
   * @param g, Graphics
   * @param url, url of the ".cp" file
   */
  def setShader(g: GdxGraphics): Unit = {
    if (g.getShaderRenderer == null) {
      g.setShader(urlShader)
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
   *   - modify the file data/shader/circle.fp
   * @param g, Graphics
   * @param entities, list of entities
   */
  def drawShader(g: GdxGraphics, entities: Array[Entity]): Unit = {
    var positions: ArrayBuffer[Vector2] = ArrayBuffer.empty[Vector2]
    var radius: ArrayBuffer[Float] = ArrayBuffer.empty[Float]
    // Calculate the position in the screen because the player position is based on the tilemap
    entities.foreach {
      case player: Player => {
        val worldPlayerPos = new Vector3(player.getPosition.x, player.getPosition.y, 0)
        g.getCamera.project(worldPlayerPos)
        positions.append(new Vector2(worldPlayerPos.x + 19, worldPlayerPos.y + 19))
        radius.append(player.radius)
      }
      case chaser: Chaser => {
        val worldChaserPos = new Vector3(chaser.getPosition.x, chaser.getPosition.y, 0)
        g.getCamera.project(worldChaserPos)
        positions.append(new Vector2(worldChaserPos.x + 19, worldChaserPos.y + 19))
        radius.append(chaser.radius)
      }
    }

    val renderer = g.getShaderRenderer  // the shaderRenderer is already set
    renderer.setUniform("resolution", new Vector2(Gdx.graphics.getWidth.toFloat, Gdx.graphics.getHeight.toFloat))
    // set uniform for each position
    for (i <- positions.indices) {
      renderer.setUniform(s"positions[$i]", positions(i))
    }
    // set uniform for each radius
    for (i <- radius.indices) {
      renderer.setUniform(s"radius[$i]", radius(i))
    }
    // Pass the number of positions
    renderer.setUniform("numPositions", positions.length)
    time += Gdx.graphics.getDeltaTime
    g.drawShader(time)
  }
}
