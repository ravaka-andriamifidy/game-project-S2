package `object`

import `object`.Direction.Direction
import ch.hevs.gdx2d.components.bitmaps.Spritesheet
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.interfaces.DrawableObject
import com.badlogic.gdx.math.{Interpolation, Vector2}
/*
* This class is to improve the movement of the player
* I take it from the file Hero.java in the demoDesktop project, which was given at the beginning of the project
*
* */

class Player extends DrawableObject {
  private val SPRITE_WIDTH: Int = 32
  private val SPRITE_HEIGHT: Int = 32
  private var textureY: Int = 1
  private var speed : Int = 1
  private val FRAME_TIME: Float = 0.1f // Duration of each frame

  private var dt : Float = 0
  private var currentFrame: Int = 0
  private val nFrames: Int = 4

  private var move: Boolean = false

  var mudrySprite: Spritesheet = _

  var lastPosition: Vector2 = new Vector2(0.0f, 0.0f)
  var newPosition: Vector2 = new Vector2(0.0f, 0.0f)
  var position: Vector2 = new Vector2(0.0f, 0.0f)


  def player(x: Int, y: Int): Unit = {
    chaserInit(new Vector2(SPRITE_WIDTH * x, SPRITE_HEIGHT * y))
    mudrySprite = new Spritesheet("game-project-S2/SpriteSheet/MudrySpriteNoBGFinal.png", SPRITE_WIDTH, SPRITE_HEIGHT)
  }
  /**
   * Create the chaser at the start position
   *
   * @param initialPosition Start position [px] on the map.
   */
  def chaserInit(initialPosition: Vector2): Unit = {
    lastPosition = new Vector2(initialPosition)
    newPosition = new Vector2(initialPosition)
    position = new Vector2(initialPosition)

  }

  /**
   * Update the position and the texture of the chaser.
   *
   * @param elapsedTime The time [s] elapsed since the last time which this method was called.
   */
  def animated(elapsedTime: Double): Unit = {
    val frameTime = FRAME_TIME / speed

    position = new Vector2(lastPosition)
    if (isMoving()) {
      dt = (dt + elapsedTime).toFloat
      val alpha = (dt + frameTime * currentFrame) / (frameTime * nFrames)

      position.interpolate(newPosition, alpha, Interpolation.linear)
    } else dt = 0

    if (dt > frameTime) {
      dt -= frameTime;
      currentFrame = (currentFrame + 1) % nFrames;

      if (currentFrame == 0) {
        move = false;
        lastPosition = new Vector2(newPosition);
        position = new Vector2(newPosition);
      }
    }
  }

  /**
   * @return True if the chaser is actually doing a step.
   */
  def isMoving(): Boolean = {
    return move
  }

  /**
   * Do a step on the given direction
   *
   * @param direction The direction to go from object Direction.
   */
  def go(direction: Direction): Unit = {
    move = true
    direction match {
      case Direction.RIGHT =>
        newPosition.add(SPRITE_WIDTH, 0)

      case Direction.LEFT =>
        newPosition.add(-SPRITE_WIDTH, 0)

      case Direction.UP =>
        newPosition.add(0, SPRITE_HEIGHT)

      case Direction.DOWN =>
        newPosition.add(0, -SPRITE_HEIGHT)

      case _ =>

    }
    turn(direction)
  }

  /**
   * Turn the chaser on the given direction without do any step.
   *
   * @param direction The direction to turn.
   */
  def turn(direction: Direction): Unit = {
    direction match {
      case Direction.RIGHT =>
        textureY = 1

      case Direction.LEFT =>
        textureY = 2

      case Direction.UP =>
        textureY = 3

      case Direction.DOWN =>
        textureY = 0

      case _ =>

    }
  }

  /**
   * Draw the chaser on the graphic object.
   *
   * @param g Graphic object.
   */
  def draw(g: GdxGraphics): Unit = {
    g.draw(mudrySprite.sprites(textureY)(currentFrame), position.x, position.y)
  }
}
