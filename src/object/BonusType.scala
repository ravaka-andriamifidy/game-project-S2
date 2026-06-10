package `object`

/**
 * Inspired by the objet DIRECTION with the Enumeration
 */
object BonusType extends Enumeration{
  type BonusType = Value
  val TORCH,SPEED,SLOW,BLACKOUT,IMMOBILUS,IMMUNITY = Value
}
