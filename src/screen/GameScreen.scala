package screen

import `object`.{BonusType, Constants, Direction}
import `trait`.{Movable, ScreenGame}
import ch.hevs.gdx2d.components.screen_management.RenderingScreen
import ch.hevs.gdx2d.lib.GdxGraphics
import classes.{Bonus, Chaser, Entity, Player}
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import com.badlogic.gdx.graphics.{Color, OrthographicCamera}
import com.badlogic.gdx.maps.tiled.{TiledMapTile, TiledMapTileLayer}
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.{Gdx, Input}
import font.Font
import game.GameLayer
import listener.InputHandler
import map.{Shader, TileRender}
import utils.Utils

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

class GameScreen extends RenderingScreen with ScreenGame{

  var tileRender: TileRender = _
  private var tiledLayer: TiledMapTileLayer = _
  private var chaser: Chaser = _
  private var player: Player = _
  var zoom: Float = _ // must be Float

  // Input keys
  private var keyHandlerPlayer: InputHandler = new InputHandler()
  private var keyHandlerChaser: InputHandler = new InputHandler()

  // Shader
  private var radius: Float = 100.0f
  private var shader: Shader = new Shader()

  // Ground layer
  private var groundLayer: GameLayer = _

  // Bonus
  private var bonuses: List[Bonus] = List.empty[Bonus]

  // Check game status
  private var _gameIsLost: Boolean = false
  private var _gameIsWin: Boolean = false

  // Time to play
  private var playingTime: Float = Constants.PLAYING_TIME
  private var endTime: Float = -1f

  // For displaying the timer
  // IMPORTANT becase we used shader
  // Inspired by the code existing in the class GdxGraphics to display the FPS and the LOGO
  private var spriteBatch: SpriteBatch = _
  private var camera: OrthographicCamera = _
  private var timerFont: Font = _

  // ticktoc soung
  private var endSoundPlayed: Boolean = false

  def gameIsLost: Boolean = _gameIsLost
  def gameIsLost_= (value: Boolean): Unit = _gameIsLost = value

  def gameIsWin: Boolean = _gameIsWin
  def gameIsWin_= (value: Boolean): Unit = _gameIsWin = value

  override def onInit(): Unit = {
    // Init audio for GameScreen
    audio.initAudioGameScreen()
    audio.playAudio("gamestart")

    // Inspired by the code existing in the class GdxGraphics
    spriteBatch = new SpriteBatch()
    camera = new OrthographicCamera()
    camera.setToOrtho(false, Gdx.graphics.getWidth, Gdx.graphics.getHeight)

    timerFont = new Font("data/fonts/Starjedi.ttf")
    val parameter = new FreeTypeFontParameter()
    parameter.color = Color.WHITE
    parameter.borderColor = Color.BLUE
    parameter.borderWidth = 3
    timerFont.setParameter(40, parameter)

    // Tile management
    tileRender = new TileRender()
    tiledLayer = tileRender.tiledMap.getLayers.get(0).asInstanceOf[TiledMapTileLayer]
    groundLayer = new GameLayer(tileRender, "Ground")

    // Ground layer
    initBonuses()

    // Set initial zoom
    zoom = 0.7f

    // Create Chaser and Player
    chaser = new Chaser(14, 10)
    player = new Player(1, 3)

    Thread.sleep(2000)

    // Init keys status for player and chaser
    keyHandlerPlayer.initInput(Array(Input.Keys.UP, Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.RIGHT))
    keyHandlerChaser.initInput(Array(Input.Keys.W, Input.Keys.S, Input.Keys.A, Input.Keys.D))
    audio.playAudio("gamemiddle")
  }

  override def onGraphicRender(g: GdxGraphics): Unit = {
    g.clear()

    // 1. Update the position (before collision)
    manageChaser()
    managePlayer()

    checkBonusCollection(g, bonuses)
    updateBonuses(Gdx.graphics.getDeltaTime)
    playingTime -= Gdx.graphics.getDeltaTime

    resetEntityAttribute(Gdx.graphics.getDeltaTime, player)
    resetEntityAttribute(Gdx.graphics.getDeltaTime, chaser)

    // 2. Collision / endgame check
    val isOver = endGame()

    // 3. Camera + render
    g.zoom(zoom)
    g.moveCamera(
      player.getPosition.x,
      player.getPosition.y,
      tiledLayer.getWidth * tiledLayer.getTileWidth,
      tiledLayer.getHeight * tiledLayer.getTileHeight
    )

    tileRender.tiledMapRenderer.setView(g.getCamera)
    tileRender.tiledMapRenderer.render()

    // 4. Shader display
    if (g.getShaderRenderer == null) {
      shader.setShader(g)
    }
    shader.drawShader(g, Array(player, chaser))

    chaser.draw(g)
    player.draw(g)

    manageDrawingBonuses(g, bonuses)
    showDisplayTimer(g)

    if (playingTime > 0 && playingTime <= 10f && !endSoundPlayed) {
      audio.playAudio("gameticktoc")
      endSoundPlayed = true
    }
  }

  /**
   * Manage keyboard events - KEYUP
   *
   * @param keycode
   */
  override def onKeyUp(keycode: Int): Unit = {
    // handle the key event from the player
    if (keyHandlerPlayer.keyStatus.containsKey(keycode)) {
      super.onKeyUp(keycode)
      keyHandlerPlayer.keyStatus.put(keycode, false)
    }

    // handle the key event from the chaser
    if (keyHandlerChaser.keyStatus.containsKey(keycode)) {
      super.onKeyUp(keycode)
      keyHandlerChaser.keyStatus.put(keycode, false)
    }
  }

  /**
   * Manage keyboard events - KEYDOWN
   *
   * @param keycode
   */
  override def onKeyDown(keycode: Int): Unit = {
    // handle the key event from the player
    if (keyHandlerPlayer.keyStatus.containsKey(keycode)) {
      super.onKeyDown(keycode)
      keyHandlerPlayer.keyStatus.put(keycode, true)
    }

    // handle the key event from the player
    if (keyHandlerChaser.keyStatus.containsKey(keycode)) {
      super.onKeyDown(keycode)
      keyHandlerChaser.keyStatus.put(keycode, true)
    }
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
      if (tileRender.isWalkable(nextCell) && chaser.immobilusTimer <= 0) {
        // Go
        if (!player.hasSpeedBonus && !player.hasSlowBonus) { // check if the player caught speed(bonus) or slow(malus)
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
        if (!chaser.hasSpeedBonus && !chaser.hasSlowBonus) { // check if the chaser caught speed(bonus) or slow(malus)
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
    if (playingTime > 0){
      val playerRadius = player.radius
      val chaserRadius = chaser.radius

      val dist = player.getPosition.dst(chaser.getPosition)
      // Distance if speed is increase
      val distanceOnSpeed = if ((player.getSpeed > 1.0f || chaser.getSpeed > 1.0f) ) 32f else 25f
      if ( dist < 12f && player.immunityTimer <= 0 )  {
        endTime -= Gdx.graphics.getDeltaTime
          gameIsLost = true
          return true
      }
    }
    else{
      gameIsWin = true
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

    for (i <- 0 until Constants.MAX_BONUSES) {
      val tile: Vector2 = shuffledTiles(i)
      val bonusType = shuffledTypes(i % shuffledTypes.length)
      array_bonus.append(new Bonus(bonusType, tile.x.toInt, tile.y.toInt))
    }
    bonuses = array_bonus.toList
  }

  /**
   *
   * @param g       , Graphics
   * @param bonuses , List of bonuses to display
   */
  private def manageDrawingBonuses(g: GdxGraphics, bonuses: List[Bonus]): Unit = {
    bonuses.filter(b => !b.isCollected && !b.isExpired).foreach(b => {
      // Distance between the bonus and each entity
      val dist_chaser_bonus = chaser.getPosition.dst(b.position)
      val dist_player_bonus = player.getPosition.dst(b.position)
      val realRadiusPlayer = player.radius / zoom - 90 // calculate the real radius depending on the zoom factor
      val realRadiusChaser = chaser.radius / zoom - 90 // calculate the real radius depending on the zoom factor
      val alpha_chaser = 1.0f - math.min(1.0f, math.max(0.0f, (dist_chaser_bonus - (realRadiusChaser * 0.7f)) / (realRadiusChaser * 0.3f)))
      val alpha_player = 1.0f - math.min(1.0f, math.max(0.0f, (dist_player_bonus - (realRadiusPlayer * 0.7f)) / (realRadiusPlayer * 0.3f)))

      if (alpha_chaser > 0) {
        if (!chaser.hasBlackOutMalus) {
          g.sbSetColor(1, 1, 1, alpha_chaser)
          b.draw(g)
          g.sbSetColor(1, 1, 1, 1)
        }
      }

      if (alpha_player > 0) {
        if (!player.hasBlackOutMalus) {
          g.sbSetColor(1, 1, 1, alpha_player)
          b.draw(g)
          g.sbSetColor(1, 1, 1, 1)
        }
      }
    })
  }

  private def updateBonuses(dt: Float): Unit = {
    bonuses.filter(b => !b.isCollected && !b.isExpired).foreach ( b => {
      b.lifetime -= dt
      if (b.lifetime <= 0) b.isExpired = true
    })

    // Shuffle ONCE before mapping
    val shuffledTiles = Random.shuffle(groundLayer.getAllWalkableTileIDs)
    val shuffledTypes = Random.shuffle(Constants.bonusTypes)
    var tileIndex = 0

    bonuses = bonuses.map { b =>
      if (b.isExpired || b.isCollected) {
        var bonus: Bonus = new Bonus() // create a new bonus
        do{ // if the bonus is at the same positon of an existing bonus, find a new position
          val tile = shuffledTiles(tileIndex % shuffledTiles.length)
          val bonusType = shuffledTypes(tileIndex % shuffledTypes.length)
          bonus = new Bonus(bonusType, tile.x.toInt, tile.y.toInt)
          tileIndex += 1
        }while(checkBonusPosition(bonus))
        bonus
      } else b
    }
  }

  /**
   * Check if the bonus position is free
   * @param bonus the bonus to add
   * @return True if the actual position is already busy
   */
  private def checkBonusPosition(bonus: Bonus): Boolean = {
    bonuses.foreach(b =>{
      if(b.position == bonus.position){
        true
      }
    })
    false
  }

  private def checkBonusCollection(g: GdxGraphics, bonuses: List[Bonus]): Unit = {
    bonuses.filter(b => !b.isCollected && (b.isCaughtBy(player, groundLayer) || b.isCaughtBy(chaser, groundLayer))).foreach(b => {
      if (b.isCaughtBy(player, groundLayer)) {
        if (changeEntityAttribute(player, b)) {
          audio.playAudio("gamebonusplayer")
          b.isCollected = true
        }
      }
      if (b.isCaughtBy(chaser, groundLayer)) {
        if (changeEntityAttribute(chaser, b)) {
          audio.playAudio("gamebonuschaser")
          b.isCollected = true
        }
      }
    })
  }

  /**
   * Change the attribute of an entity when a bonus is caught
   *
   * @param entity , chaser or player
   * @param b      , the bonus
   */
  private def changeEntityAttribute(entity: Entity, b: Bonus): Boolean = {
    b.bonusType match {
      case BonusType.TORCH =>
        entity match {
          case player: Player =>
            player.radiusTimer = 3.5f
            player.radius = Constants.BASE_RADIUS * 1.7f
            true
          case chaser: Chaser =>
            chaser.radiusTimer = 3.5f
            chaser.radius = Constants.BASE_RADIUS * 1.7f
            true
        }

      case BonusType.SPEED =>
        entity match {
          case player: Player =>
            player.hasSpeedBonus = true
            player.setSpeed(1.5f)
            player.speedTimer = 3f
            true

          case chaser: Chaser =>
            chaser.hasSpeedBonus = true
            chaser.setSpeed(1.5f)
            chaser.speedTimer = 3f
            true
        }

      case BonusType.SLOW =>
        entity match {
          case player: Player =>
            player.hasSlowBonus = true
            player.setSpeed(0.75f)
            player.slowTimer = 3.5f
            true

          case chaser: Chaser =>
            chaser.hasSlowBonus = true
            chaser.setSpeed(0.75f)
            chaser.slowTimer = 3.5f
            true
        }

      case BonusType.BLACKOUT =>
        entity match {
          case player: Player =>
            player.hasBlackOutMalus = true
            player.blackOutTimer = 4f
            player.radius = Constants.BASE_RADIUS * 0.4f
            true
          case chaser: Chaser =>
            chaser.hasBlackOutMalus = true
            chaser.blackOutTimer = 4f
            chaser.radius = Constants.BASE_RADIUS * 0.4f
            true
        }

      case BonusType.IMMUNITY =>
        entity match {
          case player: Player =>
            player.immunityTimer = 3.5f
            true
          case chaser: Chaser => false

        }

      case BonusType.IMMOBILUS =>
        entity match {
          case player: Player => false
          case chaser: Chaser =>
            chaser.immobilusTimer = 3.5f
            true
        }
    }
  }

  /**
   * Reset all attributes after the bonus/malus time is elapsed
   *
   * @param dt     , the Graphics deltaTime
   * @param entity , player or chaser
   *               "with Movable" because abstract class Entity doesn't have attributes: radiusTimer/blackOutTimer/speedTimer/slowTimer/hasSpeedBonus/hasSlowBonus
   */
  private def resetEntityAttribute(dt: Float, entity: Entity with Movable): Unit = {
    if (entity.radiusTimer > 0) {
      entity.radiusTimer -= dt
      if (entity.radiusTimer <= 0) {
        entity.radiusTimer = 0f
        entity.radius = Constants.BASE_RADIUS
      }
    }

    if (entity.speedTimer > 0) {
      entity.speedTimer -= dt
      if (entity.speedTimer <= 0) {
        entity.speedTimer = 0f
        entity.hasSpeedBonus = false
        entity.setSpeed(Constants.BASE_SPEED)
      }
    }

    if (entity.slowTimer > 0) {
      entity.slowTimer -= dt
      if (entity.slowTimer <= 0) {
        entity.slowTimer = 0f
        entity.hasSlowBonus = false
        entity.setSpeed(Constants.BASE_SPEED)
      }
    }

    if (entity.blackOutTimer > 0) {
      entity.blackOutTimer -= dt
      if (entity.blackOutTimer <= 0) {
        entity.blackOutTimer = 0f
        entity.hasBlackOutMalus = false
        entity.radius = Constants.BASE_RADIUS
      }
    }

    if (entity.immunityTimer > 0) {
      entity.immunityTimer -= dt
      if (entity.immunityTimer <= 0) {
        entity.immunityTimer = 0f
      }
    }

    if (entity.immobilusTimer > 0) {
      entity.immobilusTimer -= dt
      if (entity.immobilusTimer <= 0) {
        entity.immobilusTimer = 0f
      }
    }
  }

  /**
   * Display the timer on the top left
   * Inspired by the code existing in the class GdxGraphics (e.g. drawFPS)
   * @param g
   */
  def showDisplayTimer(g: GdxGraphics): Unit = {
    val seconds = playingTime.toInt
    val minutes = seconds / 60
    val displayTime = f"$minutes%02d:${seconds % 60}%02d"

    g.end()  // flush GdxGraphics batch

    camera.update()
    spriteBatch.setProjectionMatrix(camera.combined)
    spriteBatch.begin()
    timerFont.bitMap.draw(spriteBatch, s"Time left: $displayTime", 20, Gdx.graphics.getHeight - 20)
    spriteBatch.end()
  }
}
