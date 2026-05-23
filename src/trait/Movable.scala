package `trait`

import `object`.Direction.Direction

trait Movable {
  private var speed: Double = 1.0
  private var _move : Boolean = false

  var textureX: Int = 0
  var textureY: Int = 1

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

  def go(direction: Direction): Unit
  def turn(direction: Direction): Unit
}
