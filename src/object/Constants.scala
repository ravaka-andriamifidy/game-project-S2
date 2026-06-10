package `object`

object Constants {
  val SPRITE_WIDTH: Int = 32
  val SPRITE_HEIGHT: Int = 32
  val FRAME_TIME: Float = 0.1f // Duration of each frame
  val BASE_SPEED: Double = 1.0
  val BASE_RADIUS: Float = 110.0f
  val MAX_BONUSES: Int = 10
  val PLAYING_TIME: Float = 60.0f
  val INITIAL_ZOOM: Float = 1.0f
  val bonusTypes = List(BonusType.SPEED, BonusType.TORCH, BonusType.SLOW, BonusType.BLACKOUT, BonusType.IMMUNITY, BonusType.IMMOBILUS)
}