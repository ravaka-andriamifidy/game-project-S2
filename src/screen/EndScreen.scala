package screen

import `object`.Constants
import `trait`.{MenuScreen, ScreenGame}
import ch.hevs.gdx2d.components.screen_management.RenderingScreen
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import com.badlogic.gdx.scenes.scene2d.{InputEvent, Stage}
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import font.Font

class EndScreen extends RenderingScreen  with ScreenGame  with MenuScreen{

  var gameIsLost: Boolean = false
  var toReplay: Boolean = false
  var toMenu: Boolean = false
  private var soundPlayed: Boolean = false
  private var firstLoad: Boolean = true

  override def onInit(): Unit = {
    audio.initAudioEndScreen()

    // Init FONT
    font = new Font("data/fonts/Starjedi.ttf")

    stage = new Stage()
    Gdx.input.setInputProcessor(stage) // Make the stage consume events
    // Load the default skin (which can be configured in the JSON file)
    skin = new Skin(Gdx.files.internal("data/ui/uiskin.json"))

    addButton("Quit", (Gdx.graphics.getWidth / 2 - 100, (Gdx.graphics.getHeight * .5 - 150).toInt), skin)
    addButton("Go to the menu", (Gdx.graphics.getWidth / 2 - 100, (Gdx.graphics.getHeight * .6 - 150).toInt), skin)
    addButton("Replay", (Gdx.graphics.getWidth / 2 - 100, (Gdx.graphics.getHeight * .7 - 150).toInt), skin)

     // Adds the buttons to the stage
    buttons.foreach(b => {
      stage.addActor(b)
    })

    // Set listener
    buttons.foreach(b => {
      if( b.getText.toString == "Replay") {
        b.addListener(new ClickListener() {
          override def clicked(event: InputEvent, x: Float, y: Float): Unit = {
            super.clicked(event, x, y)
            if (b.isChecked) toReplay = true
          }
        })
      }
      if(b.getText.toString == "Quit"){
        b.addListener(new ClickListener() {
          override def clicked(event: InputEvent, x: Float, y: Float): Unit = {
            super.clicked(event, x, y)
            if (b.isChecked) Gdx.app.exit()
          }
        })
      }
      if(b.getText.toString == "Go to the menu"){
        b.addListener(new ClickListener() {
          override def clicked(event: InputEvent, x: Float, y: Float): Unit = {
            super.clicked(event, x, y)
            if (b.isChecked) toMenu = true
          }
        })
      }
    })
  }

  override def onGraphicRender(g: GdxGraphics): Unit = {
    // skip le premier frame pour laisser GameApplication setter gameIsLost
    if (firstLoad) {
      firstLoad = false
      return
    }
    g.setShaderRenderer(null) // set shader to null before
    g.zoom(1f) // restore the default zoom
    g.getCamera.position.set(g.getScreenWidth / 2, g.getScreenHeight / 2, 0) // set the camera position to the middle of the screen
    g.clear(Color.BLACK)

    // This is required for having the GUI work properly
    stage.act()
    stage.draw()

    if (!soundPlayed) {
      if (gameIsLost) {
        audio.playAudio("gameover")
        val parameter: FreeTypeFontParameter = new FreeTypeFontParameter()
        parameter.color = Color.WHITE
        parameter.borderColor = Color.RED
        parameter.borderWidth = 3
        parameter.shadowOffsetY = -5
        parameter.shadowColor = Color.LIGHT_GRAY
        font.setParameter(100, parameter)
      }
      else {
        audio.playAudio("gamewin")
        val parameter: FreeTypeFontParameter = new FreeTypeFontParameter()
        parameter.color = Color.WHITE
        parameter.borderColor = Color.GREEN
        parameter.borderWidth = 3
        parameter.shadowOffsetY = -5
        parameter.shadowColor = Color.LIGHT_GRAY
        font.setParameter(100, parameter)
      }
      soundPlayed = true
    }

    if (gameIsLost) {
      g.drawStringCentered(g.getScreenHeight / 2 + 230, "Mudry is pythonized!!!!!!", font.bitMap)
    } else {
      g.drawStringCentered(g.getScreenHeight / 2 + 230, "Mudry is unstoppable!!!!!", font.bitMap)
    }
  }

  override def dispose(): Unit = {
    super.dispose()
    font.bitMap.dispose()
    stage.dispose()
    skin.dispose()
  }
}
