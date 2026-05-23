package listener

import com.badlogic.gdx.Input

import java.util
import java.util.{Map, TreeMap}

class InputHandler {
  // key management
  val keyStatus: util.Map[Integer, Boolean] = new util.TreeMap[Integer, Boolean]

  def init(): Unit = {
    // init keys status
    keyStatus.put(Input.Keys.UP, false)
    keyStatus.put(Input.Keys.DOWN, false)
    keyStatus.put(Input.Keys.LEFT, false)
    keyStatus.put(Input.Keys.RIGHT, false)
  }

}
