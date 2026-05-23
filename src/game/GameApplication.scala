package game

import `object`.{Chaser, Direction, Player}
import com.badlogic.gdx.{Gdx, Input}
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.desktop.PortableApplication
import graphics.TileRender
import com.badlogic.gdx.maps.tiled.{TiledMapTile, TiledMapTileLayer}
import listener.InputHandler

class GameApplication(width: Int, height: Int) extends PortableApplication(width, height, false){
  var tileRender: TileRender = _
  var tiledLayer: TiledMapTileLayer = _
  var chaser: Chaser = _
  var player: Player = _
  var zoom: Float = _  // must be Float
  var keyHandler: InputHandler = new InputHandler()

  /**
   * Initialize the application
   */
  override def onInit(): Unit = {
    // Sets the window title
    setTitle("Saint-Mudry: What's Yours is Mine")
    tileRender = new TileRender()
    tiledLayer = tileRender.tiledMap.getLayers.get(0).asInstanceOf[TiledMapTileLayer]

    // Set initial zoom
    zoom = 1

    // create Chaser
    chaser = new Chaser(14,10)

    //create Player
    player = new Player(1,3)

   // init keys status
   keyHandler.init()
  }

  /**
   * Render all the elements in the graphic with 60FPS
   * @param g, Graphics
   */
  override def onGraphicRender(g: GdxGraphics): Unit = {
    // Clears the screen
    g.clear()

    // Hero activity
    manageHero();

    // Camera follows the hero
    g.zoom(zoom)
    g.moveCamera(player.getPosition.x, player.getPosition.y, tiledLayer.getWidth * tiledLayer.getTileWidth, tiledLayer.getHeight * tiledLayer.getTileHeight)

    // Render the tilemap
    tileRender.tiledMapRenderer.setView(g.getCamera)
    tileRender.tiledMapRenderer.render()

    chaser.draw(g) //draw the chaser

    player.animate(Gdx.graphics.getDeltaTime());
    player.draw(g) //draw the chaser
  }

  // Manage keyboard events
  override def onKeyUp(keycode: Int): Unit = {
    super.onKeyUp(keycode)
    keyHandler.keyStatus.put(keycode, false)
  }

  override def onKeyDown(keycode: Int): Unit = {
    super.onKeyDown(keycode)
    keycode match {
      case Input.Keys.Z =>
        if (zoom == 1.0) zoom = .5f
        else if (zoom == .5) zoom = 2
        else zoom = 1
        return
      case _ =>

    }
    keyHandler.keyStatus.put(keycode, true)
  }

  /**
   * Manage the movements of the hero using the keyboard.
   */
  private def manageHero(): Unit = {
    // Do nothing if hero is already moving
    if (!player.move) {
      // Compute direction and next cell
      var nextCell: Option[TiledMapTile] = None
      var goalDirection = Direction.NULL

      if (keyHandler.keyStatus.get(Input.Keys.RIGHT)) {
        goalDirection = Direction.RIGHT
        nextCell = tileRender.getTile(player.getPosition, 1, 0)
      }
      else if (keyHandler.keyStatus.get(Input.Keys.LEFT)) {
        goalDirection = Direction.LEFT
        nextCell = tileRender.getTile(player.getPosition, -1, 0)
      }
      else if (keyHandler.keyStatus.get(Input.Keys.UP)) {
        goalDirection = Direction.UP
        nextCell = tileRender.getTile(player.getPosition, 0, 1)
      }
      else if (keyHandler.keyStatus.get(Input.Keys.DOWN)) {
        goalDirection = Direction.DOWN
        nextCell = tileRender.getTile(player.getPosition, 0, -1)
      }
      // Is the move valid ?
      if (tileRender.isWalkable(nextCell)) {
        // Go
        player.setSpeed(tileRender.getTileSpeed(nextCell.get))
        player.go(goalDirection)
      }
      else {
        // Face the wall
        player.turn(goalDirection)
      }
    }
  }
}
