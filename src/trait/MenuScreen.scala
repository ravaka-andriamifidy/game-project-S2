package `trait`

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.{Skin, TextButton}

import scala.collection.mutable.ArrayBuffer

trait MenuScreen {
  private val BUTTON_WIDTH = 180
  private val BUTTON_HEIGHT = 60

  private var _skin: Skin = _
  private var _stage: Stage = _
  var buttons: ArrayBuffer[TextButton] = ArrayBuffer.empty[TextButton]

  /**
   * setter and getter of skin
   */
  def skin: Skin = _skin
  def skin_= (value: Skin): Unit = _skin = value

  /**
   * setter and getter of stage
   */
  def stage: Stage = _stage
  def stage_= (value: Stage): Unit = _stage = value

  def addButton(text: String, position: (Float, Float), skin: Skin): Unit = {
    val newGameButton = new TextButton(text, skin) // Use the initialized skin
    newGameButton.setWidth(BUTTON_WIDTH)
    newGameButton.setHeight(BUTTON_HEIGHT)
    newGameButton.setPosition(position._1, position._2)
    buttons.append(newGameButton)
  }

}
