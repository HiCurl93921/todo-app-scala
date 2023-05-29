package models

import play.api.libs.json.{JsPath, JsString, Json, Writes}
import play.api.libs.functional.syntax._

import scala.util.matching.Regex

trait Color {
  val R: Hex
  val G: Hex
  val B: Hex
  val A: Hex
  lazy val code: String = s"#${R.code}${G.code}${B.code}"
  lazy val withAlpha: String = s"#${A.code}${R.code}${G.code}${B.code}"
}

object Color {
  private val hexPattern: Regex = "[0-9A-Za-z]{2}".r
  private final val ALPHA_DEFAULT = "FF"
  private final val DEFAULT = "00"

  implicit val colorWriter: Writes[Color] = Writes[Color](color => JsString(color.code))

  def apply (r: String, g: String, b: String, a: String = ALPHA_DEFAULT): Color = new Color {
    override val A: Hex = Hex(a)
    override val R: Hex = Hex(r)
    override val G: Hex = Hex(g)
    override val B: Hex = Hex(b)
  }

  def apply (rgb: String, a: String): Color = Color(rgb, rgb, rgb, a)

  def apply(value: String): Color = hexPattern.findAllIn(value).toList match {
    case r::g::b::Nil => Color(r, g, b)
    case a::r::g::b::_ => Color(r, g, b, a)
    case _ => WHITE
  }

  def colorType(colorType: Short): Color = if (colorType < 0 || types.length <= colorType) WHITE else types(colorType)

  final val WHITE: Color = Color(DEFAULT,ALPHA_DEFAULT)
  final val SILVER: Color = Color("C0", ALPHA_DEFAULT)
  final val GRAY: Color = Color("80", ALPHA_DEFAULT)
  final val BLACK: Color = Color("00", ALPHA_DEFAULT)
  final val RED: Color = Color("FF0000")
  final val MAROON: Color = Color("800000")
  final val YELLOW: Color = Color("FFFF00")
  final val OLIVE: Color = Color("808000")
  final val LIME: Color = Color("00FF00")
  final val GREEN: Color = Color("008000")
  final val AQUA: Color = Color("00FFFF")
  final val TEAL: Color = Color("008080")
  final val BLUE: Color = Color("0000FF")
  final val NAVY: Color = Color("000080")
  final val FUCHSIA: Color = Color("FF00FF")
  final val PURPLE: Color = Color("800080")

  private final val types: IndexedSeq[Color] = IndexedSeq(
    WHITE,
    SILVER,
    GRAY,
    BLACK,
    RED,
    MAROON,
    YELLOW,
    OLIVE,
    LIME,
    GREEN,
    AQUA,
    TEAL,
    BLUE,
    NAVY,
    FUCHSIA,
    PURPLE
  )
}

trait Hex {
  val code: String
}

object Hex {
  private final val DEFAULT_VALUE: String = "00"
  private       val hexPattern: Regex = "[0-9A-Fa-f]{2}".r

  def apply (value: String): Hex = hexPattern.findFirstIn(value) match {
      case Some(hex) => new Hex { val code: String = hex }
      case None => DEFAULT
  }

  final val DEFAULT: Hex = Hex(DEFAULT_VALUE)
}
