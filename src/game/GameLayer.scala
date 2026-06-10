package game

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell
import com.badlogic.gdx.maps.tiled.{TiledMap, TiledMapTileLayer}
import com.badlogic.gdx.math.Vector2
import map.TileRender

import scala.collection.mutable.ArrayBuffer

class GameLayer(var tileRender: TileRender, var layerName: String) {
  val groundLayer: TiledMapTileLayer = tileRender.tiledMap.getLayers.get(layerName).asInstanceOf[TiledMapTileLayer]

  def isWalkable(x: Float, y: Float): Boolean = {
    val cell: Cell = groundLayer.getCell(x.toInt, y.toInt)

    if (cell == null) return false
    val tile = cell.getTile
    if (tile == null) return false

    tile.getProperties.get("walkable").toString.toBoolean
  }

  def inBounds(x: Float, y: Float): Boolean = {
    x >= 0 && x < tileRender.tiledMap.getLayers.get(layerName).asInstanceOf[TiledMapTileLayer].getWidth && y >= 0 && y < tileRender.tiledMap.getLayers.get(layerName).asInstanceOf[TiledMapTileLayer].getHeight
  }

  def getAllWalkableTileIDs: List[Vector2] = {
    var res: ArrayBuffer[Vector2] = ArrayBuffer.empty[Vector2]
    val tileW = groundLayer.getTileWidth.toInt
    val tileH = groundLayer.getTileHeight.toInt

    for( x <- 0 until tileW){
      for (y <- 0 until tileH){
        var cell: Cell = groundLayer.getCell(x, y)
        if (cell != null && cell.getTile != null){
          if (cell.getTile.getProperties.get("walkable").toString.toBoolean) res.append( new Vector2(x, y))
        }
      }
    }
    res.toList
  }
}
