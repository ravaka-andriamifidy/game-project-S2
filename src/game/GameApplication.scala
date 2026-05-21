package game

import `object`.{Chaser, Player}
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.desktop.PortableApplication
import graphics.TileRender
import com.badlogic.gdx.math.Vector2
import ch.hevs.gdx2d.lib.utils.Logger
import com.badlogic.gdx.Input

class GameApplication extends PortableApplication{
  var radius = 5
  var speed = 1
  var tileRender: TileRender = _

  var chaser: Chaser = new Chaser
  var player: Player = new Player

  /**
   * Initialize the application
   */
  override def onInit(): Unit = {
    // Sets the window title
    setTitle("Saint-Mudry: What's Yours is Mine")
    tileRender = new TileRender()
    
    // create Chaser
    chaser.chaser(10,10)
    //create Player
    player.player(10,5)
  }

  /**
   * Render all the elements in the graphic with 60FPS
   * @param g, Graphics
   */
  override def onGraphicRender(g: GdxGraphics): Unit = {
    // Clears the screen
    g.clear()

    // Render the tilemap
    tileRender.tiledMapRenderer.setView(g.getCamera)
    tileRender.tiledMapRenderer.render()
    //draw the chaser
    chaser.animated(Gdx.graphics.getDeltaTime)
    chaser.draw(g)

    player.animated(Gdx.graphics.getDeltaTime)
    player.draw(g)
  }
}
