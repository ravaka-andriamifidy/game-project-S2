package map

import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.maps.tiled.{TiledMap, TiledMapRenderer, TiledMapTile, TiledMapTileLayer, TiledMapTileSets, TmxMapLoader}
import com.badlogic.gdx.math.Vector2
import game.GameLayer

import scala.collection.mutable

case class Vec2(x: Int, y: Int)

class TileRender {
  // tiles management
  private var _tiledMap: TiledMap = new TmxMapLoader().load("data/maps/MapLayoutDungeons.tmx")
  private var _tiledMapRenderer: TiledMapRenderer= new OrthogonalTiledMapRenderer(tiledMap)
  private var _tiledLayer: TiledMapTileLayer = _tiledMap.getLayers.get(0).asInstanceOf[TiledMapTileLayer]
  private var _tileSets: TiledMapTileSets = _tiledMap.getTileSets

  def tiledMapRenderer: TiledMapRenderer = _tiledMapRenderer
  def tiledMap: TiledMap = _tiledMap
  def tileSets: TiledMapTileSets = _tileSets


  /**
   * exemple : getTile(myPosition,0,1) get the tile over myPosition
   *
   * @param position
   * The position on map (not on screen)
   * @param offsetX
   * The number of cells at right of the given position.
   * @param offsetY
   * The number of cells over the given position.
   * @return The tile around the given position | null
   */
  def getTile(position: Vector2, offsetX: Int, offsetY: Int): Option[TiledMapTile] = {
    try {
      val x = (position.x / _tiledLayer.getTileWidth).toInt + offsetX
      val y = (position.y / _tiledLayer.getTileHeight).toInt + offsetY
      Some(_tiledLayer.getCell(x, y).getTile)
    } catch {
      case e: Exception => None
    }
  }

  /**
   * Get the "walkable" property of the given tile.
   *
   * @param tile
   * The tile to know the property
   * @return true if the property is set to "true", false otherwise
   */
  def isWalkable(tile: Option[TiledMapTile]): Boolean = {
    if(tile.isDefined){
      val test = tile.get.getProperties.get("walkable").toString
      return test.toBoolean
    }
      false
  }

  /**
   * Get the "speed" property of the given tile.
   *
   * @param tile
   * The tile to know the property
   * @return The value of the property
   */
  def getTileSpeed(tile: TiledMapTile): Float = {
    val test = tile.getProperties.get("speed").toString
    test.toFloat
  }
}
