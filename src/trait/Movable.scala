package `trait`

import `object`.Constants
import `object`.Direction.Direction

trait Movable {
  private var speed: Double = Constants.BASE_SPEED
  private var _move : Boolean = false

  var textureX: Int = 0
  var textureY: Int = 0

  private var _radius: Float = 100.0f // for changes if TORCH BONUS is caught
  var radiusTimer: Float = 0f // timer for TORCH BONUS
  var blackOutTimer: Float = 0f // timer for BLACKOUT MALUS
  var speedTimer: Float = 0f // timer for SPEED BONUS
  var slowTimer: Float = 0f // timer for SLOW MALUS
  var immunityTimer: Float = 0f // timer for SLOW MALUS
  var immobilusTimer: Float = 0f // timer for SLOW MALUS


  var hasSpeedBonus: Boolean = false
  var hasSlowBonus: Boolean = false
  var hasBlackOutMalus: Boolean = false

  /**
   * setter and getter of move
   */
  def move: Boolean = _move
  def move_= (value: Boolean): Unit = _move = value

  /**
   * setter and getter of speed
   */
  def getSpeed: Double = speed
  def setSpeed (value: Double): Unit = {
    speed = value
  }

  /**
   * setter and getter of radius
   */
  def radius: Float = _radius
  def radius_= (value: Float): Unit = _radius = value

  def go(direction: Direction): Unit
  def turn(direction: Direction): Unit
}
