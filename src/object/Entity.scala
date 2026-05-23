package `object`

import ch.hevs.gdx2d.components.bitmaps.Spritesheet
import com.badlogic.gdx.math.Vector2

abstract class Entity{
  // spritesheet
  var dt: Double = 0
  private var _currentFrame: Int = 0
  val nFrames = 4
  var ss: Spritesheet = _

  // position of the entity
  private var lastPosition: Vector2 = _
  private var newPosition: Vector2 = _
  private var position: Vector2 = _

  def currentFrame: Int = _currentFrame
  def currentFrame_=(value: Int): Unit = {
    _currentFrame = value
  }

  def getPosition: Vector2 = return this.position
  def setPosition(value: Vector2): Unit = {
    this.position = value
  }

  def getNewPosition: Vector2 = return this.newPosition
  def setNewPosition(value: Vector2): Unit = {
    this.newPosition = value
  }

  def getLastPosition: Vector2 = return this.lastPosition
  def setLastPosition(value: Vector2): Unit = {
    this.lastPosition = value
  }
  def this(initialPosition: Vector2) = {
    this()
    lastPosition = new Vector2(initialPosition)
    newPosition = new Vector2(initialPosition)
    position = new Vector2(initialPosition)
  }
}

object Entity {
  val SPRITE_WIDTH: Int = 32
  val SPRITE_HEIGHT: Int = 32
  val FRAME_TIME = 0.1f // Duration of each frime
}
