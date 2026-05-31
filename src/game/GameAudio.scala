package game

import ch.hevs.gdx2d.components.audio.{MusicPlayer, SoundSample}

import java.util

class GameAudio {
  // audio management
  val keyShortAudios: util.Map[String, SoundSample] = new util.TreeMap[String, SoundSample]

  def initAudioGame(): Unit = {
    // Load the WAV files
    keyShortAudios.put("gameintro", new SoundSample("data/music/audio/game_intro.wav"))
    keyShortAudios.put("gameover", new SoundSample("data/music/audio/game_over.wav"))
    keyShortAudios.put("gamewin", new SoundSample("data/music/audio/game_win.wav"))
    keyShortAudios.put("gamefootstep", new SoundSample("data/music/audio/footstep_2.wav"))
    keyShortAudios.put("gamebonusplayer", new SoundSample("data/music/audio/bonus.wav"))
    keyShortAudios.put("gamebonuschaser", new SoundSample("data/music/audio/bonus_2.wav"))
    keyShortAudios.put("gamemiddle", new SoundSample("data/music/audio/middle_game.wav"))
    keyShortAudios.put("gamestart", new SoundSample("data/music/audio/start_game.wav"))
  }

  def playAudio(audio: String): Unit = {
    if (audio == "gamemiddle") {
      keyShortAudios.get(audio).loop()
    }
    if(audio == "gamefootstep") {
      keyShortAudios.get(audio).setPitch(2.0f) // max. to 2.0f
      keyShortAudios.get(audio).play()
    }
    else{
      keyShortAudios.get(audio).play()
    }
  }

  def stopAudio(audio: String): Unit = {
    keyShortAudios.get(audio).stop()
  }
}
