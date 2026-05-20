package game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.desktop.PortableApplication

class GameApplication extends PortableApplication{
  var radius = 5
  var speed = 1
  var screenHeight, screenWidth = 0

  override def onInit(): Unit = {
    // Sets the window title
    setTitle("Simple demo, mui 2013")

    screenHeight = Gdx.graphics.getHeight
    screenWidth = Gdx.graphics.getWidth
  }

  override def onGraphicRender(g: GdxGraphics): Unit = {
    // Clears the screen
    g.clear()
    g.drawAntiAliasedCircle(screenWidth / 2, screenHeight / 2, radius, Color.BLUE)

    // If reaching max or min size, invert the growing direction
    if (radius >= 100 || radius <= 3) {
      speed *= -1
    }

    // Modify the radius
    radius += speed

    g.drawSchoolLogo()
  }
}
