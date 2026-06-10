package `trait`

import font.Font
import game.GameAudio

trait ScreenGame {
  // Audio
  var audio: GameAudio = new GameAudio()
  // Font
  var font: Font = _
}
