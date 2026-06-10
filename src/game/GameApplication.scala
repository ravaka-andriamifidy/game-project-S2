package game

import com.badlogic.gdx.{Gdx, Input, InputAdapter, Screen}
import ch.hevs.gdx2d.lib.{GdxGraphics, ScreenManager}
import ch.hevs.gdx2d.desktop.PortableApplication
import screen.{EndScreen, GameScreen, HomeScreen}

import java.util

class GameApplication(width: Int, height: Int) extends PortableApplication(width: Int, height: Int, false){

  // screen manager
  var s: ScreenManager = new ScreenManager()
  // key management
  val screenMap: util.Map[Class[_], Int] = new util.LinkedHashMap [Class[_], Int]

  // Game status
  var gameIsLost: Boolean = false
  var gameIsWin: Boolean = false

  /**
   * Initialize the application
   */
  override def onInit(): Unit = {
    // Sets the window title
    setTitle("Saint-Mudry: CATCH - CATCH")

    initMappingScreen()
    screenMap.forEach((screenClass, index) => {
      s.registerScreen(screenClass)
    })
  }

  /**
   * Render all the elements in the graphic with 60FPS
   * Manage how we proceed depending on the active screen
   * @param g, Graphics
   */
  override def onGraphicRender(g: GdxGraphics): Unit = {
    s.render(g)

    // Get the active screen
    val active = s.getActiveScreen
    if (active == null) return

    // Check if we are in the HomeScreen / GameScreen / EndScreen
    active.getClass() match {
      // HomeScreen
      case c if c == classOf[HomeScreen] =>
        val h = active.asInstanceOf[HomeScreen]
        if (h.toStart) {
          h.audio.stopAudio("gameintro")
          s.transitionTo(screenMap.get(classOf[GameScreen]), ScreenManager.TransactionType.SLICE)
          // Reload the InputProcessor after switching screens
          // Help with AI because we saw that the input key handler doesn't work after switching the screen
          Gdx.input.setInputProcessor(new InputAdapter() {
            override def keyDown(keycode: Int): Boolean = { onKeyDown(keycode); true }
            override def keyUp(keycode: Int): Boolean = { onKeyUp(keycode); true }
          })
        }

      // GameScreen
      case c if c == classOf[GameScreen] =>
        val game = active.asInstanceOf[GameScreen]
        if (game.gameIsLost || game.gameIsWin) {
          game.audio.stopAudio("gamemiddle")
          game.audio.stopAudio("gameticktoc")
          gameIsLost = game.gameIsLost
          gameIsWin = game.gameIsWin
          Thread.sleep(1000)
          s.transitionTo(screenMap.get(classOf[EndScreen]), ScreenManager.TransactionType.SMOOTH)
        }

      // EndScreen
      case c if c == classOf[EndScreen] =>
        val e = active.asInstanceOf[EndScreen]
        e.gameIsLost = gameIsLost
        if (e.toMenu) {
          s.transitionTo(screenMap.get(classOf[HomeScreen]), ScreenManager.TransactionType.SMOOTH)
        }
        if (e.toReplay) {
          // Reset all status before a new game
          gameIsLost = false
          gameIsWin = false
          s.transitionTo(screenMap.get(classOf[GameScreen]), ScreenManager.TransactionType.SLIDE)
          Gdx.input.setInputProcessor(new InputAdapter() {
            override def keyDown(keycode: Int): Boolean = { onKeyDown(keycode); true }
            override def keyUp(keycode: Int): Boolean = { onKeyUp(keycode); true }
          })
        }

      case _ =>
    }
  }

  /**
   * Init screen mapping with index to do the transition
   */
  def initMappingScreen (): Unit = {
    screenMap.put(classOf[HomeScreen], 0)
    screenMap.put(classOf[GameScreen], 1)
    screenMap.put(classOf[EndScreen], 2)
  }

  override def onKeyDown(keycode: Int): Unit = {
    if(s.getActiveScreen != null) s.getActiveScreen.onKeyDown(keycode)
  }

  override def onKeyUp(keycode: Int): Unit = {
    if(s.getActiveScreen != null) s.getActiveScreen.onKeyUp(keycode)
  }

  override def onDispose(): Unit = {
    super.onDispose()
  }
}
