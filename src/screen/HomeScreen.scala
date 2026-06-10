package screen

import `object`.Constants
import `trait`.{MenuScreen, ScreenGame}
import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import ch.hevs.gdx2d.components.screen_management.RenderingScreen
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.utils.Logger
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import com.badlogic.gdx.scenes.scene2d.{InputEvent, Stage}
import com.badlogic.gdx.scenes.scene2d.ui.{Skin, TextButton}
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import font.Font

class HomeScreen() extends RenderingScreen  with ScreenGame with MenuScreen{

  private var mudryBitMap: BitmapImage = new BitmapImage("data/images/Mudry.png")
  private var pythonBitMap: BitmapImage = new BitmapImage("data/images/Python.png")
  var toStart: Boolean = false

  def onInit(): Unit = {
    // Init audio for HomeScreen
    audio.initAudioHomescreen()
    audio.playAudio("gameintro")

    // Init FONT
    font = new Font("data/fonts/ice_pixel-7.ttf")

    val parameter: FreeTypeFontParameter = new FreeTypeFontParameter()
    parameter.color = Color.WHITE
    parameter.borderColor = Color.BLUE
    parameter.borderWidth = 3
    parameter.shadowOffsetY = -8
    parameter.shadowColor = Color.LIGHT_GRAY
    font.setParameter(120, parameter)

    stage = new Stage()
    Gdx.input.setInputProcessor(stage) // Make the stage consume events
    skin = new Skin(Gdx.files.internal("data/ui/uiskin.json")) // Load the default skin (which can be configured in the JSON file)

    addButton("Quit", (Gdx.graphics.getWidth / 2 - 100, (Gdx.graphics.getHeight * .6 - 150).toInt), skin)
    addButton("Play", (Gdx.graphics.getWidth / 2 - 100, (Gdx.graphics.getHeight * .7 - 150).toInt), skin)

    // Add the buttons to the stage
    buttons.foreach(b => {
      stage.addActor(b)
    })

    // Set listener
    buttons.foreach(b => {
      if( b.getText.toString == "Play") {
        b.addListener(new ClickListener() {
          override def clicked(event: InputEvent, x: Float, y: Float): Unit = {
            super.clicked(event, x, y)
            if (b.isChecked) toStart = true
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
    })
  }

  override def onGraphicRender(g: GdxGraphics): Unit = {
    g.clear()
    g.zoom(Constants.INITIAL_ZOOM) // important because each screen has its own zoom

    // This is required for having the GUI work properly
    stage.act()
    stage.draw()

    // Display different fonts centered (X axis) on the screen
    g.drawStringCentered(Gdx.graphics.getHeight / 2 + 250, "Who can catch Mudry??", font.bitMap)

    g.drawPicture(g.getScreenWidth / 4.0f - 200f, g.getScreenHeight / 2.0f, mudryBitMap)
    g.drawPicture(g.getScreenWidth - 350.0f, g.getScreenHeight / 2.0f, pythonBitMap)
  }

  override def dispose(): Unit = {
    super.dispose()
    // Release what we've used
    mudryBitMap.dispose()
    pythonBitMap.dispose()
    font.bitMap.dispose()
    stage.dispose()
    skin.dispose()
  }
}
