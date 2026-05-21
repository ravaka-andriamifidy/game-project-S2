package game

import `object`.{Chaser, Player}
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.desktop.PortableApplication

class GameApplication extends PortableApplication{
  var chaser : Chaser = new Chaser
  var player : Player = new Player
  override def onInit(): Unit = {
    // Sets the window title
    setTitle("Try display chaser")
    //Create chaser
    chaser.chaser(10,10)
    //create Player
    player.player(10,5)
  }

  override def onGraphicRender(g: GdxGraphics): Unit = {
    // Clears the screen
    g.clear()

    //draw the chaser
    chaser.animated(Gdx.graphics.getDeltaTime)
    chaser.draw(g)

    player.animated(Gdx.graphics.getDeltaTime)
    player.draw(g)
  }
}
