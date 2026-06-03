package listener

import ch.hevs.gdx2d.desktop.Xbox
import com.badlogic.gdx.math.Vector2

class ControllerInputHandler {
  var leftStickVal : Vector2 = Vector2.Zero.cpy()

  def handleAxisMove(axisCode: Int, value: Float): Unit = {
    if (axisCode == Xbox.L_STICK_HORIZONTAL_AXIS) {
      leftStickVal.x = value
    }
    if (axisCode == Xbox.L_STICK_VERTICAL_AXIS) {
      leftStickVal.y = -value
    }
  }
}
