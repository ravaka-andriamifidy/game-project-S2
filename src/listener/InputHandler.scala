package listener

import java.util

class InputHandler {
  // key management
  val keyStatus: util.Map[Integer, Boolean] = new util.TreeMap[Integer, Boolean]

  def initInput(inputs: Array[Int]): Unit = {
    // init keys status
    for(i <- inputs){
      keyStatus.put(i, false)
    }
  }

}
