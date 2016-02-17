package geotrellis.raster.render

import geotrellis.raster._
import geotrellis.raster.render.png._
import geotrellis.raster.histogram.Histogram
import geotrellis.raster.op.stats._

trait PngRenderMethods extends TileMethods {
  /** Generate a PNG from a raster of RGBA integer values.
    *
    * Use this operation when you have created a raster whose values are already
    * RGBA color values that you wish to render into a PNG. If you have a raster
    * with data that you wish to render, you should use RenderPng instead.
    *
    * An RGBA value is a 32 bit integer with 8 bits used for each component:
    * the first 8 bits are the red value (between 0 and 255), then green, blue,
    * and alpha (with 0 being transparent and 255 being opaque).
    */
  def renderPng(): Png =
    new PngEncoder(Settings(RgbaPngEncoding, PaethFilter)).writeByteArray(tile)

  def renderPng(colorClassifier: ColorClassifier[_]): Png =
    renderPng(colorClassifier, None)

  def renderPng(colorClassifier: ColorClassifier[_], histogram: Histogram): Png =
    renderPng(colorClassifier, Some(histogram))

  /**
    * Generate a PNG image from a raster.
    *
    * Use this operation when you have a raster of data that you want to visualize
    * with an image.
    *
    * To render a data raster into an image, the operation needs to know which
    * values should be painted with which colors.  To that end, you'll need to
    * generate a ColorBreaks object which represents the value ranges and the
    * assigned color.  One way to create these color breaks is to use the
    * [[geotrellis.raster.stats.op.stat.GetClassBreaks]] operation to generate
    * quantile class breaks.
    */
  private
  def renderPng(colorClassifier: ColorClassifier[_], histogram: Option[Histogram]): Png = {
    val cmap = colorClassifier.toColorMap(histogram)
    val r2 = cmap.render(tile).convert(TypeByte)
    val colorEncoding = PngColorEncoding(colorClassifier.getColors, colorClassifier.getNoDataColor)
    new PngEncoder(Settings(colorEncoding, PaethFilter)).writeByteArray(r2)
  }
}
