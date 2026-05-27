package game

import com.badlogic.gdx.maps.tiled.{TiledMap, TiledMapTileLayer}

class GameMap(map: TiledMap, layerName: String) {
  def isWalkable(x: Float, y: Float): Boolean = {
    val groundLayer = map.getLayers.get(layerName).asInstanceOf[TiledMapTileLayer]
    val cell = groundLayer.getCell(x.toInt, y.toInt)

    if (cell == null) return false
    val tile = cell.getTile
    if (tile == null) return false

    Option(tile.getProperties.get("walkable")).exists(_.toString.toBoolean)
  }

  def inBounds(x: Float, y: Float): Boolean = {
    x >= 0 && x < map.getLayers.get(layerName).asInstanceOf[TiledMapTileLayer].getWidth && y >= 0 && y < map.getLayers.get(layerName).asInstanceOf[TiledMapTileLayer].getHeight
  }
}
