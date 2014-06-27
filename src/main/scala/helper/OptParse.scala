package helper

object OptParse {
  import java.io.File

  type Options = Map[Symbol, Any]

  implicit class IntTranslator(value: String) {
    def toIntOrElse(default: Int) =
      try { value.toInt } catch { case e: Exception => default }
  }

  def isValidEmail(email: String): Boolean =
    if ("""(?=[^\s]+)(?=(\w+)@([\w\.]+))""".r.findFirstIn(email) == None) false else true

  def parse(optList: List[String]): Options = {
    optList match {
      case Nil => Map()
      case "-f" :: from :: more =>
        if (new File(from).exists) parse(more) ++ Map('from -> from) else parse(more)
      case "-t" :: to :: more =>
        if (isValidEmail(to)) parse(more) ++ Map('to -> to) else parse(more)
      case "-c" :: cc :: more => parse(more) ++ Map('cc -> cc)
      case "-b" :: bcc :: more => parse(more) ++ Map('bcc -> bcc)
      case "-s" :: subject :: more => parse(more) ++ Map('subject -> subject)
      case message :: more => parse(more) ++ Map('message -> message)
    }
  }

  case class Config(
    from: String, to: String, cc: String, bcc: String, subject: String, message: String)

  implicit class OptionsWrapper(o: Options) { // set defaults and get values
    val isValid = o.contains('from) && o.contains('to)
    val from = o.get('from) match { case Some(f: String) => f; case _ => "" }
    val to = o.get('to) match { case Some(t: String) => t; case _ => "" }
    val cc = o.get('cc) match { case Some(c: String) => c; case _ => "" }
    val bcc = o.get('bcc) match { case Some(b: String) => b; case _ => "" }
    val subject = o.get('subject) match { case Some(s: String) => s; case _ => "" }
    val message = o.get('mail) match { case Some(m: String) => m; case _ => null }
    def toConfig =
      if (isValid) Some(Config(from, to, cc, bcc, subject, message)) else None
  }
}
