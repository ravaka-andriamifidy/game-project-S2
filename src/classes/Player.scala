package classes

import `object`.{Constants, Direction}
import `object`.Direction.Direction
import `trait`.Movable
import ch.hevs.gdx2d.components.bitmaps.Spritesheet
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.interfaces.DrawableObject
import com.badlogic.gdx.math.{Interpolation, Vector2}

/*
* This class is to improve the movement of the player
* I take it from the file Hero.java in the demoDesktop project, which was given at the beginning of the project
*
* */
class Player(_initialPosition: Vector2) extends Entity(_initialPosition) with DrawableObject with Movable {

  /**
   * Constructor
   * Create the chaser at the given start tile.
   *
   * @param x Column
   * @param y Line
   */
  def this(x: Int, y: Int) = {
    this(new Vector2(Constants.SPRITE_WIDTH * x, Constants.SPRITE_HEIGHT * y))
    ss = new Spritesheet("SpriteSheet/MudrySpriteNoBGFinal.png", Constants.SPRITE_WIDTH, Constants.SPRITE_HEIGHT)
  }

  /**
   * Update the position and the texture of the chaser.
   *
   * @param elapsedTime The time [s] elapsed since the last time which this method was called.
   */
  def animate(elapsedTime: Double): Unit = {
    val frameTime = Constants.FRAME_TIME / this.getSpeed

    this.setPosition(new Vector2(this.getLastPosition))
    if (move) {
      dt = (dt + elapsedTime)
      val alpha: Float = ((dt + frameTime * currentFrame) / (frameTime * nFrames)).toFloat

      this.getPosition.interpolate(this.getNewPosition, alpha, Interpolation.linear)
    } else dt = 0

    if (dt > frameTime) {
      dt -= frameTime;
      currentFrame = (currentFrame + 1) % nFrames;

      if (currentFrame == 0) {
        move = false;
        this.setLastPosition(new Vector2(this.getNewPosition))
        this.setPosition(new Vector2(this.getNewPosition))
      }
    }
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
        this.getNewPosition.add(Constants.SPRITE_WIDTH, 0)

      case Direction.LEFT =>
        this.getNewPosition.add(-Constants.SPRITE_WIDTH, 0)

      case Direction.UP =>
        this.getNewPosition.add(0, Constants.SPRITE_HEIGHT)

      case Direction.DOWN =>
        this.getNewPosition.add(0, -Constants.SPRITE_HEIGHT)

      case Direction.NULL =>

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
    g.draw(ss.sprites(textureY)(currentFrame), this.getPosition.x, this.getPosition.y)
  }
}
