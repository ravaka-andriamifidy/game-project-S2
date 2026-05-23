package game

import `object`.{Chaser, Direction, Player}
import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import com.badlogic.gdx.{Gdx, Input}
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.desktop.PortableApplication
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.{Color, GL20, Pixmap}
import graphics.TileRender
import com.badlogic.gdx.maps.tiled.{TiledMapTile, TiledMapTileLayer}
import com.badlogic.gdx.math.{Vector2, Vector3}
import listener.InputHandler

class GameApplication(width: Int, height: Int) extends PortableApplication(width, height, false){
  var tileRender: TileRender = _
  var tiledLayer: TiledMapTileLayer = _
  var chaser: Chaser = _
  var player: Player = _
  var zoom: Float = _  // must be Float
  var keyHandler: InputHandler = new InputHandler()
  var radius: Float = 220.0f
  var time: Float = 0
  var fbo: FrameBuffer = _

  /**
   * Initialize the application
   */
  override def onInit(): Unit = {

    fbo = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth, Gdx.graphics.getHeight, false)

    // Sets the window title
    setTitle("Saint-Mudry: What's Yours is Mine")
    tileRender = new TileRender()
    tiledLayer = tileRender.tiledMap.getLayers.get(0).asInstanceOf[TiledMapTileLayer]
    // Loads the image that will be displayed in the middle of the screen

    // Set initial zoom
    zoom = 0.6f

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
    g.clear()

    // 1. Camera
    g.zoom(zoom)
    g.moveCamera(player.getPosition.x, player.getPosition.y,
      tiledLayer.getWidth * tiledLayer.getTileWidth,
      tiledLayer.getHeight * tiledLayer.getTileHeight)

    // 2. Tilemap render
    tileRender.tiledMapRenderer.setView(g.getCamera)
    tileRender.tiledMapRenderer.render()

    // 3. Shader overlay
    if (g.getShaderRenderer == null) {
      g.setShader("data/shader/circle.fp")
    }
    // Calculate the position in the screen because the player position is based on the tilemap
    val worldPos = new Vector3(player.getPosition.x, player.getPosition.y, 0)
    g.getCamera.project(worldPos)
    g.getShaderRenderer.setUniform("position", new Vector2(worldPos.x + 23, worldPos.y + 23))
    g.getShaderRenderer.setUniform("radius", radius)
    g.drawShader(time)
    time += Gdx.graphics.getDeltaTime

    // 4. Chaser : visible only when close to the player
    val dist: Float = chaser.getPosition.dst(player.getPosition)
    val worldRadius: Float = radius / zoom - 187.0f
    // Alpha value between 0 and 1 depending on the distance
    val alpha: Float = 1.0f - math.min(1.0f, math.max(0.0f, (dist - (worldRadius * 0.7f)) / (worldRadius * 0.3f)))
    if (alpha > 0) {
      g.sbSetColor(1, 1, 1, alpha) // apply alpha
      chaser.draw(g) // draw the chaser progressively
      g.sbSetColor(1, 1, 1, 1) // reset alpha
    }

    // 5. Player always visible
    player.animate(Gdx.graphics.getDeltaTime())
    player.draw(g)

    // 6. Manager the player movement
    managePlayer()
  }

  // Manage keyboard events
  override def onKeyUp(keycode: Int): Unit = {
    super.onKeyUp(keycode)
    keyHandler.keyStatus.put(keycode, false)
  }

  override def onKeyDown(keycode: Int): Unit = {
    super.onKeyDown(keycode)
    keyHandler.keyStatus.put(keycode, true)
  }

  override def onDispose(): Unit = {
    super.onDispose()
  }

  /**
   * Manage the movements of the hero using the keyboard.
   */
  private def managePlayer(): Unit = {
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
