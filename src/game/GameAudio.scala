package game

import ch.hevs.gdx2d.components.audio.{MusicPlayer, SoundSample}

import java.util

class GameAudio {
  // audio management
  val keyShortAudios: util.Map[String, SoundSample] = new util.TreeMap[String, SoundSample]

  def initAudioHomescreen(): Unit = {
    // Load the WAV files
    keyShortAudios.put("gameintro", new SoundSample("data/music/audio/game_intro.wav"))
  }

  def initAudioGameScreen(): Unit = {
    // Load the WAV files
    keyShortAudios.put("gamestart", new SoundSample("data/music/audio/start_game.wav"))
    keyShortAudios.put("gamefootstep", new SoundSample("data/music/audio/footstep_2.wav"))
    keyShortAudios.put("gamebonusplayer", new SoundSample("data/music/audio/bonus.wav"))
    keyShortAudios.put("gamebonuschaser", new SoundSample("data/music/audio/bonus_2.wav"))
    keyShortAudios.put("gamemiddle", new SoundSample("data/music/audio/middle_game.wav"))
    keyShortAudios.put("gameticktoc", new SoundSample("data/music/audio/ticktoc.wav"))
  }

  def initAudioEndScreen(): Unit = {
    // Load the WAV files
    keyShortAudios.put("gameover", new SoundSample("data/music/audio/game_over.wav"))
    keyShortAudios.put("gamewin", new SoundSample("data/music/audio/game_win.wav"))
  }

  def playAudio(audio: String): Unit = {
    if (audio == "gamemiddle" || audio == "gameintro") {
      keyShortAudios.get(audio).loop()
    }
    if(audio == "gamefootstep") {
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
