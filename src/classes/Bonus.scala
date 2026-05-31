package classes

import `object`.{BonusType, Constants}
import `object`.BonusType.BonusType
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import game.GameLayer
import utils.Utils

class Bonus(var bonusType: BonusType, tileX: Int, tileY: Int) {
  // Position in pixels on the map
  val position: Vector2 = new Vector2(tileX * Constants.SPRITE_WIDTH, tileY * Constants.SPRITE_WIDTH)
  private var _isCollected: Boolean = false
  var isExpired: Boolean = false

  // Spritesheet file
  private val spritesheet = new Texture("SpriteSheet/icons.png")

  // Coordinates in the spritesheet according to type
  private val (spriteCol, spriteRow): (Int, Int) = bonusType match {
    case BonusType.TORCH    => (11, 10)
    case BonusType.SPEED    => (2, 8)
    case BonusType.SLOW     => (11, 21)
    case BonusType.BLACKOUT => (2, 0)
  }

  private var _lifetime: Float = bonusType match {
    case BonusType.TORCH => 20f
    case BonusType.SPEED => 15f
    case BonusType.SLOW => 15f
    case BonusType.BLACKOUT => 20f
  }

  // Create the texture region by the spritesheet position
  val region = new TextureRegion(spritesheet, spriteCol * Constants.SPRITE_WIDTH, spriteRow * Constants.SPRITE_HEIGHT, 32, 32)

  def lifetime: Float = _lifetime
  def lifetime_= (value: Float): Unit = _lifetime = value

  def isCollected: Boolean = _isCollected
  def isCollected_= (value: Boolean): Unit = _isCollected = value

  /**
   * Draw the bonus on the graphic object.
   *
   * @param g Graphic object.
   */
  def draw(g: GdxGraphics): Unit = {
    if (!isCollected) {
      g.draw(region, position.x, position.y)
    }
  }

  /**
   * Check if the bonus is caught by a entity (chaser or player)
   * @param entity, chaser or player
   * @param map, the layer on the map (Ground)
   * @return True if the entity and the bonus are on the same tile
   */
  def isCaughtBy(entity: Entity, map: GameLayer): Boolean = {
    val tileW = map.groundLayer.getTileWidth.toFloat
    val tileH = map.groundLayer.getTileHeight.toFloat
    val bonusTile = Utils.pixelToTileID(tileW, tileH, this.position.x, this.position.y)

    entity match {
      case player: Player =>
        val playerTile = Utils.pixelToTileID(tileW, tileH, player.getLastPosition.x, player.getLastPosition.y)
        !isCollected && (bonusTile == playerTile)

      case chaser: Chaser =>
        val chaserTile = Utils.pixelToTileID(tileW, tileH, chaser.getLastPosition.x, chaser.getLastPosition.y)
        !isCollected && (bonusTile == chaserTile)
    }
  }

}
