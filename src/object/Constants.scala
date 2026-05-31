package `object`

object Constants {
  val SPRITE_WIDTH: Int = 32
  val SPRITE_HEIGHT: Int = 32
  val FRAME_TIME: Float = 0.1f // Duration of each frime
  val BASE_SPEED: Double = 1.0
  val BASE_RADIUS: Float = 110.0f
  val MAX_BONUSES: Int = 8
  val bonusTypes = List(BonusType.SPEED, BonusType.TORCH, BonusType.SLOW, BonusType.BLACKOUT)
}