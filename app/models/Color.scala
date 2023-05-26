package models

import scala.util.matching.Regex

trait Color {
  val A: Hex
  val R: Hex
  val G: Hex
  val B: Hex
  lazy val code: String = s"#${R.code}${G.code}${B.code}"
  lazy val withAlpha: String = s"#${A.code}${R.code}${G.code}${B.code}"
}

object Color {
  private val hexPattern: Regex = "[0-9A-Za-z]{2}".r
  private final val ALPHA_DEFAULT = "FF"
  private final val DEFAULT = "00"

  def apply (a: String, r: String, g: String, b: String): Color = new Color {
    override val A: Hex = Hex(a)
    override val R: Hex = Hex(r)
    override val G: Hex = Hex(g)
    override val B: Hex = Hex(b)
  }

  def apply (r: String, g: String, b: String): Color = Color(ALPHA_DEFAULT, r, g, b)

  def apply (a: String, rgb: String): Color = Color(a, rgb, rgb, rgb)

  def apply(value: String): Color = hexPattern.findAllIn(value).toList match {
    case r::g::b::Nil => Color(r, g, b)
    case a::r::g::b::_ => Color(a, r, g, b)
    case _ => WHITE
  }

  def colorType(colorType: Short): Color = if (colorType < 0 || types.length <= colorType) WHITE else types(colorType)

  final val WHITE: Color = Color(ALPHA_DEFAULT, DEFAULT)
  final val SILVER: Color = Color(ALPHA_DEFAULT, "C0")
  final val GRAY: Color = Color(ALPHA_DEFAULT, "80")
  final val BLACK: Color = Color(ALPHA_DEFAULT, "00")
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
