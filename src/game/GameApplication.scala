package game

import `object`.{BonusType, Constants, Direction}
import `trait`.Movable
import com.badlogic.gdx.{Gdx, Input}
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.desktop.PortableApplication
import classes.{Bonus, Chaser, Entity, Player}
import map.{Shader, TileRender}
import com.badlogic.gdx.maps.tiled.{TiledMapTile, TiledMapTileLayer}
import com.badlogic.gdx.math.Vector2
import listener.InputHandler
import utils.Utils

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

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

  // Ground layer
  var groundLayer: GameLayer = _

  // Bonus
  var bonuses: List[Bonus] = List.empty[Bonus]

  // Audio
  var audio: GameAudio = new GameAudio()
  var endSoundPlayed: Boolean = false

  // Check game status
  var gameIsLost: Boolean = false

  /**
   * Initialize the application
   */
  override def onInit(): Unit = {
    // Sets the window title
    setTitle("Saint-Mudry: CATCH - CATCH")

    // init audio files
    audio.initAudioGame()

    // Tile management
    tileRender = new TileRender()
    tiledLayer = tileRender.tiledMap.getLayers.get(0).asInstanceOf[TiledMapTileLayer]
    groundLayer = new GameLayer(tileRender, "Ground")

    // Ground layer
    initBonuses()

    // Set initial zoom
    zoom = 0.6f

    // create Chaser
    chaser = new Chaser(14,10)

    //create Player
    player = new Player(1,3)
    Thread.sleep(2000)

    // init keys status for player and chaser
    keyHandlerPlayer.initInput(Array(Input.Keys.UP, Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.RIGHT))
    keyHandlerChaser.initInput(Array(Input.Keys.W, Input.Keys.S, Input.Keys.A, Input.Keys.D))

    audio.playAudio("gamestart")
    Thread.sleep(3500)
    audio.playAudio("gamemiddle")
  }

  /**
   * Render all the elements in the graphic with 60FPS
   * @param g, Graphics
   */
  override def onGraphicRender(g: GdxGraphics): Unit = {
    g.clear()
    updateBonuses(Gdx.graphics.getDeltaTime)

    // 1. End game management
    if(endGame()){
      audio.stopAudio("gamemiddle")
      if (!endSoundPlayed) {
        if (gameIsLost) audio.playAudio("gameover")
        else audio.playAudio("gamewin")
        endSoundPlayed = true
      }

      if(gameIsLost){ // check if LOOSE
        g.drawString(width / 2 - 100, height / 2, "GAME OVER")
      }
      else{ // OR WIN
        g.drawString(width / 2 - 100, height / 2, "PYTHON IS SCALA-ABLE.....!!!!")
      }
      return
    }

    resetEntityAttribute(Gdx.graphics.getDeltaTime, player)
    resetEntityAttribute(Gdx.graphics.getDeltaTime, chaser)

    // 2. Camera
    g.zoom(zoom)
    g.moveCamera(player.getPosition.x, player.getPosition.y, tiledLayer.getWidth * tiledLayer.getTileWidth, tiledLayer.getHeight * tiledLayer.getTileHeight)

    // 2. Tilemap render
    tileRender.tiledMapRenderer.setView(g.getCamera)
    tileRender.tiledMapRenderer.render()

    // 3. Shader overlay
    if(g.getShaderRenderer == null){
      shader.setShader(g)
    }
    shader.drawShader(g, Array(player, chaser))

    // 4. Draw chaser and player
    chaser.draw(g)
    player.draw(g)

    // 6. Manager the player movement
    managePlayer()

    // 7. Manager the chaser movement
    manageChaser()
    checkBonusCollection(g, bonuses)
    manageDrawingBonuses(g, bonuses)
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
        if(!player.hasSpeedBonus && !player.hasSlowBonus) { // check if the player caught speed(bonus) or slow(malus)
          player.setSpeed(tileRender.getTileSpeed(nextCell.get))
        }
        player.go(goalDirection)
        audio.playAudio("gamefootstep")
      }
      else {
        // Face the wall
        player.turn(goalDirection)
      }
    }
    player.animate(Gdx.graphics.getDeltaTime())
  }

  /**
   * Manage the movements of the chaser depending on the player position
   */
  private def manageChaser(): Unit = {
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
        if(!chaser.hasSpeedBonus && !chaser.hasSlowBonus) { // check if the chaser caught speed(bonus) or slow(malus)
          chaser.setSpeed(tileRender.getTileSpeed(nextCell.get))
        }
        chaser.go(goalDirection)
      }
      else {
        // Face the wall
        chaser.turn(goalDirection)
      }
    }
    chaser.animated(Gdx.graphics.getDeltaTime())
  }

  private def endGame(): Boolean = {
    val tileW = tiledLayer.getTileWidth.toFloat
    val tileH = tiledLayer.getTileHeight.toFloat
    val chaserTile = Utils.pixelToTileID(tileW, tileH, chaser.getLastPosition.x, chaser.getLastPosition.y)
    val playerTile = Utils.pixelToTileID(tileW, tileH, player.getLastPosition.x, player.getLastPosition.y)

    if(chaserTile == playerTile){
      gameIsLost = true
      return true
    }
    false
  }

  /**
   * Init all bonuses with random position in the map
   */
  private def initBonuses(): Unit = {
    var array_bonus: ArrayBuffer[Bonus] = ArrayBuffer.empty[Bonus]
    val shuffledTiles = Random.shuffle(groundLayer.getAllWalkableTileIDs)
    val shuffledTypes = Random.shuffle(Constants.bonusTypes)

    for(i <- 0 until Constants.MAX_BONUSES){
      val tile: Vector2 = shuffledTiles(i)
      val bonusType = shuffledTypes(i % shuffledTypes.length)
      array_bonus.append( new Bonus(bonusType,   tile.x.toInt, tile.y.toInt))
    }
    bonuses = array_bonus.toList
  }

  /**
   *
   * @param g, Graphics
   * @param bonuses, List of bonuses to display
   */
  private def manageDrawingBonuses(g: GdxGraphics, bonuses: List[Bonus]): Unit = {
    bonuses.filter(b => !b.isCollected && !b.isExpired).foreach( b => {
      // Distance between the bonus and each entity
      val dist_chaser_bonus = chaser.getPosition.dst(b.position)
      val dist_player_bonus = player.getPosition.dst(b.position)
      val realRadiusPlayer = player.radius / zoom - 90  // calculate the real radius depending on the zoom factor
      val realRadiusChaser = chaser.radius / zoom - 90  // calculate the real radius depending on the zoom factor
      val alpha_chaser = 1.0f - math.min(1.0f, math.max(0.0f, (dist_chaser_bonus - (realRadiusChaser * 0.7f)) / (realRadiusChaser * 0.3f)))
      val alpha_player = 1.0f - math.min(1.0f, math.max(0.0f, (dist_player_bonus - (realRadiusPlayer * 0.7f)) / (realRadiusPlayer * 0.3f)))

      if (alpha_chaser > 0) {
        if(!chaser.hasBlackOutMalus){
          g.sbSetColor(1, 1, 1, alpha_chaser)
          b.draw(g)
          g.sbSetColor(1, 1, 1, 1)
        }
      }

      if (alpha_player > 0) {
        if(!player.hasBlackOutMalus) {
          g.sbSetColor(1, 1, 1, alpha_player)
          b.draw(g)
          g.sbSetColor(1, 1, 1, 1)
        }
      }
    })
  }

  private def updateBonuses(dt: Float): Unit = {
    bonuses.filter(b => !b.isCollected && !b.isExpired).foreach { b =>
      b.lifetime -= dt
      if (b.lifetime <= 0) b.isExpired = true
    }

    // Shuffler UNE seule fois avant le map
    val shuffledTiles = Random.shuffle(groundLayer.getAllWalkableTileIDs)
    val shuffledTypes = Random.shuffle(Constants.bonusTypes)
    var tileIndex = 0

    bonuses = bonuses.map { b =>
      if (b.isExpired || b.isCollected) {
        val tile = shuffledTiles(tileIndex % shuffledTiles.length)
        val bonusType = shuffledTypes(tileIndex % shuffledTypes.length)
        tileIndex += 1
        new Bonus(bonusType, tile.x.toInt, tile.y.toInt)
      } else b
    }
  }

  private def checkBonusCollection(g: GdxGraphics, bonuses: List[Bonus]): Unit = {
    bonuses.filter(b => !b.isCollected && (b.isCaughtBy(player, groundLayer) || b.isCaughtBy(chaser, groundLayer))).foreach( b => {
      if(b.isCaughtBy(player, groundLayer)){
        changeEntityAttribute(player, b)
        audio.playAudio("gamebonusplayer")
      }
      if( b.isCaughtBy(chaser, groundLayer)){
        changeEntityAttribute(chaser, b)
        audio.playAudio("gamebonuschaser")
      }
      b.isCollected = true
    })
  }

  /**
   * Change the attribute of an entity when a bonus is caught
   * @param entity, chaser or player
   * @param b, the bonus
   */
  private def changeEntityAttribute(entity: Entity, b: Bonus): Unit = {
    b.bonusType match {
      case BonusType.TORCH =>
        entity match {
          case player: Player =>
            player.radiusTimer = 3.5f
            player.radius = Constants.BASE_RADIUS * 1.7f
          case chaser: Chaser =>
            chaser.radiusTimer = 3.5f
            chaser.radius = Constants.BASE_RADIUS * 1.7f
        }

      case BonusType.SPEED =>
        entity match {
          case player: Player =>
            player.hasSpeedBonus = true
            player.setSpeed(1.5f)
            player.speedTimer = 3f

          case chaser: Chaser =>
            chaser.hasSpeedBonus = true
            chaser.setSpeed(1.5f)
            chaser.speedTimer = 3f
        }

      case BonusType.SLOW =>
        entity match {
          case player: Player =>
            player.hasSlowBonus = true
            player.setSpeed(0.75f)
            player.slowTimer = 3.5f

          case chaser: Chaser =>
            chaser.hasSlowBonus = true
            chaser.setSpeed(0.75f)
            chaser.slowTimer = 3.5f
        }

      case BonusType.BLACKOUT =>
        entity match {
          case player: Player =>
            player.hasBlackOutMalus = true
            player.blackOutTimer = 4f
            player.radius = Constants.BASE_RADIUS * 0.4f
          case chaser: Chaser =>
            chaser.hasBlackOutMalus = true
            chaser.blackOutTimer = 4f
            chaser.radius = Constants.BASE_RADIUS * 0.4f
        }
    }
  }

  /**
   * Reset all attributes after the bonus/malus time is elapsed
   * @param dt, the Graphics deltaTime
   * @param entity, player or chaser
   * "with Movable" because abstract class Entity doesn't have attributes: radiusTimer/blackOutTimer/speedTimer/slowTimer/hasSpeedBonus/hasSlowBonus
   */
  private def resetEntityAttribute(dt: Float, entity: Entity with Movable): Unit = {
    if (entity.radiusTimer > 0) {
      entity.radiusTimer -= dt
      if (entity.radiusTimer <= 0) entity.radius = Constants.BASE_RADIUS
    }

    if (entity.speedTimer > 0) {
      entity.speedTimer -= dt
      if (entity.speedTimer <= 0) {
        entity.hasSpeedBonus = false
        entity.setSpeed(Constants.BASE_SPEED)
      }
    }

    if (entity.slowTimer > 0) {
      entity.slowTimer -= dt
      if (entity.slowTimer <= 0) {
        entity.hasSlowBonus = false
        entity.setSpeed(Constants.BASE_SPEED)
      }
    }

    if (entity.blackOutTimer > 0) {
      entity.blackOutTimer -= dt
      if (entity.blackOutTimer <= 0) {
        entity.hasBlackOutMalus = false
        entity.radius = Constants.BASE_RADIUS
      }
    }
  }
}
