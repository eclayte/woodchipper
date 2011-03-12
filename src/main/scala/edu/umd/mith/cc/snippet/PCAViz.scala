package edu.umd.mith.cc.snippet

import _root_.scala.xml.NodeSeq
import _root_.net.liftweb.http._
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.liftweb.mapper._
import _root_.java.util.Date
import edu.umd.mith.cc.lib._
import edu.umd.mith.cc.model._
import edu.umd.mith.cc.analysis._
import Helpers._
import net.liftweb.http.js.JsCmds._
import net.liftweb.widgets.flot._
import js._
import JsCmds._
import JE._
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Extraction._
import net.liftweb.json.JsonDSL._
import net.liftweb.json.JsonAST._
import net.liftweb.json.Printer.pretty 

case class JsCompactDouble(d: Double, n: Int) extends JsExp {
  def this(d: Double) = this(d, 6)
  def toJsCmd = JsCompactDouble.formatters(n).format(d)
}

object JsCompactDouble {
  import java.text.DecimalFormat
  val formatters = new DecimalFormat("#") +: (1 until 16).map { n =>
    new DecimalFormat("#." + "#" * n)
  }
}

trait PCAResult {
  def data: Array[Array[Double]] 
  def variance: Array[Double]
  def loadings: Array[Array[Double]]
  def k: Int = this.variance.length
  def n = this.data.length
  def m = this.variance(0).length
  def precision: Int = 6

  assert(this.data.length > 0)
  assert(this.data(0).length == this.variance.length)
  assert(this.variance.length == this.loadings.length)

  implicit def convertDouble1DArray(v: Array[Double]) = {
    new JsArray(v.map(JsCompactDouble(_, this.precision)).toList)
  }

  implicit def convertDouble2DArray(v: Array[Array[Double]]) = {
    new JsArray(v.map {
      r => new JsArray(r.map(JsCompactDouble(_, this.precision)).toList)
    }.toList)
  }

  def renderData(in: NodeSeq): NodeSeq = {
    Script(
      JsCrVar("pca_result", JsObj(
        "data" -> this.data,
        "variance" -> this.variance,
        "loadings" -> this.loadings
      ))
    )
  }
}
