package classes

import `object`.{BonusType, Constants}
import `object`.BonusType.BonusType
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import game.GameLayer
import utils.Utils

class Bonus() {

  var bonusType: BonusType = _
  // Position in pixels on the map
  var position: Vector2 = _
  private var _isCollected: Boolean = false
  var isExpired: Boolean = false

  // Spritesheet file
  private val spritesheet = new Texture("SpriteSheet/icons.png")

  // Coordinates in the spritesheet according to type
  private var spriteCol = 0
  private var spriteRow = 0

  private var _lifetime: Float = 0f

  // Create the texture region by the spritesheet position
  var region: TextureRegion = _

  /**
   * Constructor
   * Create the chaser at the given start tile.
   *
   *  @param bonusType bonus type
   * @param tileX Column in the "SpriteSheet/icons.png"
   * @param tileY Line in the "SpriteSheet/icons.png"
   */
  def this(bonusType: BonusType, tileX: Int, tileY: Int) = {
    this()
    this.bonusType = bonusType
    position = new Vector2(tileX * Constants.SPRITE_WIDTH, tileY * Constants.SPRITE_WIDTH)

    val (col, row, life) = bonusType match {
      case BonusType.TORCH     => (11, 10, 20f)
      case BonusType.SPEED     => (2, 8, 17f)
      case BonusType.SLOW      => (11, 21, 17f)
      case BonusType.BLACKOUT  => (2, 0, 22f)
      case BonusType.IMMUNITY  => (5, 3, 15f)
      case BonusType.IMMOBILUS => (13, 3, 18f)
    }

    spriteCol = col
    spriteRow = row
    _lifetime = life

    region = new TextureRegion(spritesheet, spriteCol * Constants.SPRITE_WIDTH, spriteRow * Constants.SPRITE_HEIGHT, 32, 32)
  }

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
   * Check if the bonus is caught by an entity (chaser or player)
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
