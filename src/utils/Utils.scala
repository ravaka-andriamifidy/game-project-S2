package utils

import com.badlogic.gdx.math.Vector2

object Utils {
 def pixelToTileID(mapWidth: Float, mapHeight: Float, pixelX: Float, pixelY: Float): Vector2 ={
   new Vector2((pixelX / mapWidth).toInt, (pixelY / mapHeight).toInt)
 }
}
