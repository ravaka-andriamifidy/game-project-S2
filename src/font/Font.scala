package font

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter

class Font(file: String) {
  private var fileHandler: FileHandle = Gdx.files.internal(file)
  var bitMap: BitmapFont = _
  var parameter: FreeTypeFontParameter = new FreeTypeFontParameter()
  var generator: FreeTypeFontGenerator = new FreeTypeFontGenerator(fileHandler)


  def setParameter(size: Int, p: FreeTypeFontParameter): Unit = {
    parameter = p
    parameter.size = generator.scaleForPixelHeight(size)
    bitMap = generator.generateFont(parameter)
    generator.dispose()
  }
}