package `object`

import ch.hevs.gdx2d.components.bitmaps.Spritesheet

abstract class Entity{

  var dt = 0
  private var currentFrame = 0
  private val nFrames = 4

  // Spritesheet
  private val SPRITE_WIDTH = 32
  private val SPRITE_HEIGHT = 32
  private var ss: Option[Spritesheet] = None
}
