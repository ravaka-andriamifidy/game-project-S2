package `trait`

import com.badlogic.gdx.math.Vector2

trait Movable {

  var SPRITE_WIDTH: Int
  var SPRITE_HEIGHT: Int

  // position of the entity
  var lastPosition: Vector2
  var newPosition: Vector2
  var position: Vector2

  var speed: Double

  var isMoving: Boolean

  // Movement
  def go(): Unit
  def turn(): Unit
}
