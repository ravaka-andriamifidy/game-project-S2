package map

import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.maps.tiled.{TiledMap, TiledMapRenderer, TiledMapTile, TiledMapTileLayer, TiledMapTileSets, TmxMapLoader}
import com.badlogic.gdx.math.Vector2
import game.GameMap

import scala.collection.mutable

case class Vec2(x: Int, y: Int)

class TileRender {
  // tiles management
  private var _tiledMap: TiledMap = new TmxMapLoader().load("data/maps/MapLayoutDungeons.tmx")
  private var _tiledMapRenderer: TiledMapRenderer= new OrthogonalTiledMapRenderer(tiledMap)
  private var _tiledLayer: TiledMapTileLayer = _tiledMap.getLayers.get(0).asInstanceOf[TiledMapTileLayer]
  private var _tileSets: TiledMapTileSets = _tiledMap.getTileSets
  var map: GameMap = new GameMap(_tiledMap, "Ground")
  private var _zoom = 0.0

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

object TileRender {
  private val directions = List(
    new Vector2(0, -1), new Vector2(0, 1),   // haut, bas
    new Vector2(-1, 0), new Vector2(1, 0),   // gauche, droite
  )

  /**
   *  distance between two tile
   * @param a position of the start tile
   * @param b position of the destination tile
   * @return The distance between the 2 tiles (straight-line distance)
   */
  def distanceInTiles(a: Vector2, b: Vector2): Float = {
    Math.abs(b.x - a.x) + Math.abs(b.y - a.y)
  }

  /**
   *
   * @param start
   * @param goal
   * @param gameMap
   * @return
   */
  def findPath(start: Vector2, goal: Vector2, gameMap: GameMap): Option[List[Vector2]] = {
    val openSet = mutable.PriorityQueue.empty[(Double, Vector2)](Ordering.by(-_._1))  // min-heap sur le coût
    val cameFrom = mutable.Map.empty[Vector2, Vector2]
    val gScore = mutable.Map(start -> 0.0).withDefaultValue(Double.MaxValue)

    openSet.enqueue((distanceInTiles(start, goal), start))

    while (openSet.nonEmpty) {
      val (_, current) = openSet.dequeue()

      if (current == goal) return Some(reconstructPath(cameFrom, current))

      for {
        dir      <- directions
        neighbor  = new Vector2(current.x + dir.x, current.y + dir.y)
        if gameMap.inBounds(neighbor.x, neighbor.y)
        if gameMap.isWalkable(neighbor.x, neighbor.y)
      } {
        val tentative = gScore(current) + distanceInTiles(current, neighbor)
        if (tentative < gScore(neighbor)) {
          cameFrom(neighbor) = current
          gScore(neighbor)   = tentative
          openSet.enqueue((tentative + distanceInTiles(neighbor, goal), neighbor))
        }
      }
    }
    None  // pas de chemin trouvé
  }

  private def reconstructPath(cameFrom: mutable.Map[Vector2, Vector2], end: Vector2): List[Vector2] = {
    Iterator.iterate(end)(cameFrom).takeWhile(cameFrom.contains).toList.reverse :+ end
  }
}
