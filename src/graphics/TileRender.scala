package graphics

import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.maps.tiled.{TiledMap, TiledMapRenderer, TiledMapTileLayer, TmxMapLoader}

class TileRender {
  // tiles management
  private var tiledMap: TiledMap = new TmxMapLoader().load("data/maps/MapLayoutDungeons.tmx")
  private var _tiledMapRenderer: TiledMapRenderer= new OrthogonalTiledMapRenderer(tiledMap)
  private var _tiledLayer: TiledMapTileLayer = tiledMap.getLayers.get(0).asInstanceOf[TiledMapTileLayer]
  private var _zoom = 0.0

  def tiledMapRenderer: TiledMapRenderer = _tiledMapRenderer
}
