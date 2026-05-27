package game

import `object`.{Chaser, Direction, Player}
import com.badlogic.gdx.{Gdx, Input}
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.desktop.PortableApplication
import map.{Shader, TileRender}
import com.badlogic.gdx.maps.tiled.{TiledMapTile, TiledMapTileLayer}
import com.badlogic.gdx.math.{Vector2, Vector3}
import listener.InputHandler
import utils.Utils

class GameApplication(width: Int, height: Int) extends PortableApplication(width, height, false){
  var tileRender: TileRender = _
  var tiledLayer: TiledMapTileLayer = _
  var chaser: Chaser = _
  var player: Player = _
  var zoom: Float = _  // must be Float

  // Input keys
  var keyHandlerPlayer: InputHandler = new InputHandler()
  var keyHandlerChaser: InputHandler = new InputHandler()

  // Shader
  var radius: Float = 100.0f
  var shader: Shader = new Shader()

  /**
   * Initialize the application
   */
  override def onInit(): Unit = {
    // Sets the window title
    setTitle("Saint-Mudry: What's Yours is Mine")

    // Tile management
    tileRender = new TileRender()
    tiledLayer = tileRender.tiledMap.getLayers.get(0).asInstanceOf[TiledMapTileLayer]
    val tileSet =  tileRender.tiledMap.getTileSets.getTileSet("tilesheet_finale")
    val tile = tileSet.getProperties

    // Set initial zoom
    zoom = 0.6f

    // create Chaser
    chaser = new Chaser(14,10)

    //create Player
    player = new Player(1,3)

    // init keys status for player and chaser
    keyHandlerPlayer.initInput(Array(Input.Keys.UP, Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.RIGHT))
    keyHandlerChaser.initInput(Array(Input.Keys.W, Input.Keys.S, Input.Keys.A, Input.Keys.D))
  }

  /**
   * Render all the elements in the graphic with 60FPS
   * @param g, Graphics
   */
  override def onGraphicRender(g: GdxGraphics): Unit = {
    g.clear()

    // 1. Camera
    g.zoom(zoom)
    g.moveCamera(player.getPosition.x, player.getPosition.y, tiledLayer.getWidth * tiledLayer.getTileWidth, tiledLayer.getHeight * tiledLayer.getTileHeight)

    // 2. Tilemap render
    tileRender.tiledMapRenderer.setView(g.getCamera)
    tileRender.tiledMapRenderer.render()

    // 3. Shader overlay
    // Calculate the position in the screen because the player position is based on the tilemap
    val worldPlayerPos = new Vector3(player.getPosition.x, player.getPosition.y, 0)
    val worldPlayerChaser = new Vector3(chaser.getPosition.x, chaser.getPosition.y, 0)

    if(g.getShaderRenderer == null){
      shader.setShader(g)
    }

    g.getCamera.project(worldPlayerPos)
    g.getCamera.project(worldPlayerChaser)
    shader.drawShader(g, Array(
      new Vector2(worldPlayerPos.x + 19, worldPlayerPos.y + 19),
      new Vector2(worldPlayerChaser.x + 19, worldPlayerChaser.y + 19)
    ), radius)

    // 4. Draw chaser and player
    chaser.draw(g)
    player.animate(Gdx.graphics.getDeltaTime())
    player.draw(g)

    // 6. Manager the player movement
    managePlayer()

    // 7. Manager the chaser movement
    manageChaser()
  }

  /**
   * Manage keyboard events - KEYUP
   * @param keycode
   */
  override def onKeyUp(keycode: Int): Unit = {
    // handle the key event from the player
    if(keyHandlerPlayer.keyStatus.containsKey(keycode)) {
      super.onKeyUp(keycode)
      keyHandlerPlayer.keyStatus.put(keycode, false)
    }

    // handle the key event from the chaser
    if(keyHandlerChaser.keyStatus.containsKey(keycode)) {
      super.onKeyUp(keycode)
      keyHandlerChaser.keyStatus.put(keycode, false)
    }
  }

  /**
   * Manage keyboard events - KEYDOWN
   * @param keycode
   */
  override def onKeyDown(keycode: Int): Unit = {
    // handle the key event from the player
    if(keyHandlerPlayer.keyStatus.containsKey(keycode)) {
      super.onKeyDown(keycode)
      keyHandlerPlayer.keyStatus.put(keycode, true)
    }

    // handle the key event from the player
    if(keyHandlerChaser.keyStatus.containsKey(keycode)) {
      super.onKeyDown(keycode)
      keyHandlerChaser.keyStatus.put(keycode, true)
    }
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

      if (keyHandlerPlayer.keyStatus.get(Input.Keys.RIGHT)) {
        goalDirection = Direction.RIGHT
        nextCell = tileRender.getTile(player.getPosition, 1, 0)
      }
      else if (keyHandlerPlayer.keyStatus.get(Input.Keys.LEFT)) {
        goalDirection = Direction.LEFT
        nextCell = tileRender.getTile(player.getPosition, -1, 0)
      }
      else if (keyHandlerPlayer.keyStatus.get(Input.Keys.UP)) {
        goalDirection = Direction.UP
        nextCell = tileRender.getTile(player.getPosition, 0, 1)
      }
      else if (keyHandlerPlayer.keyStatus.get(Input.Keys.DOWN)) {
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

  /**
   * Manage the movements of the chaser depending on the player position
   */
  private def manageChaser(): Unit = {
    val tileW = tiledLayer.getTileWidth.toFloat
    val tileH = tiledLayer.getTileHeight.toFloat
    val chaserTile = Utils.pixelToTileID(tileW, tileH, chaser.getLastPosition.x, chaser.getLastPosition.y)
    val playerTile = Utils.pixelToTileID(tileW, tileH, player.getNewPosition.x, player.getNewPosition.y)

    // Do nothing if hero is already moving
    if (!chaser.move) {
      // Compute direction and next cell
      var nextCell: Option[TiledMapTile] = None
      var goalDirection = Direction.NULL

      if (keyHandlerChaser.keyStatus.get(Input.Keys.D)) {
        goalDirection = Direction.RIGHT
        nextCell = tileRender.getTile(chaser.getPosition, 1, 0)
      }
      else if (keyHandlerChaser.keyStatus.get(Input.Keys.A)) {
        goalDirection = Direction.LEFT
        nextCell = tileRender.getTile(chaser.getPosition, -1, 0)
      }
      else if (keyHandlerChaser.keyStatus.get(Input.Keys.W)) {
        goalDirection = Direction.UP
        nextCell = tileRender.getTile(chaser.getPosition, 0, 1)
      }
      else if (keyHandlerChaser.keyStatus.get(Input.Keys.S)) {
        goalDirection = Direction.DOWN
        nextCell = tileRender.getTile(chaser.getPosition, 0, -1)
      }
      // Is the move valid ?
      if (tileRender.isWalkable(nextCell)) {
        // Go
        chaser.setSpeed(tileRender.getTileSpeed(nextCell.get))
        chaser.go(goalDirection)
      }
      else {
        // Face the wall
        chaser.turn(goalDirection)
      }
    }
    chaser.animated(Gdx.graphics.getDeltaTime())
  }
}
