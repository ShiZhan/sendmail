package helper

object OptParse {
  import java.io.File

  type Options = Map[Symbol, Any]

  def isValidEmail(email: String): Boolean =
    if ("""(?=[^\s]+)(?=(\w+)@([\w\.]+))""".r.findFirstIn(email) == None) false else true

  def parse(optList: List[String]): Options = {
    optList match {
      case Nil => Map()
      case "-f" :: from :: more =>
        if (new File(from).exists) parse(more) ++ Map('from -> from) else parse(more)
      case "-t" :: to :: more =>
        val toArray = to.split(',')
        if (toArray.forall(isValidEmail)) parse(more) ++ Map('to -> toArray) else parse(more)
      case "-c" :: cc :: more =>
        val ccArray = cc.split(',')
        if (ccArray.forall(isValidEmail)) parse(more) ++ Map('cc -> ccArray) else parse(more)
      case "-b" :: bc :: more =>
        val bcArray = bc.split(',')
        if (bcArray.forall(isValidEmail)) parse(more) ++ Map('bc -> bcArray) else parse(more)
      case "-s" :: subject :: more => parse(more) ++ Map('subject -> subject)
      case message :: more => parse(more) ++ Map('message -> message)
    }
  }

  case class Config(
    from: String, to: Array[String], cc: Array[String], bc: Array[String],
    subject: String, message: String)

  implicit class OptionsWrapper(o: Options) { // set defaults and get values
    val isValid = o.contains('from) && o.contains('to)
    val from = o.get('from) match { case Some(f: String) => f; case _ => null }
    val to = o.get('to) match { case Some(t: Array[String]) => t; case _ => Array[String]() }
    val cc = o.get('cc) match { case Some(c: Array[String]) => c; case _ => Array[String]() }
    val bc = o.get('bc) match { case Some(b: Array[String]) => b; case _ => Array[String]() }
    val subject = o.get('subject) match { case Some(s: String) => s; case _ => "Untitled" }
    val message = o.get('message) match { case Some(m: String) => m; case _ => null }
    def toConfig = if (isValid) Some(Config(from, to, cc, bc, subject, message)) else None
  }
}
