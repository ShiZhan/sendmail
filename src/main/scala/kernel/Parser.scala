package kernel

object Parser extends helper.Logging {
  type Options = Map[Symbol, Any]

  def loadSender(from: String) = try {
    val config = com.typesafe.config.ConfigFactory.load(from)
    val hostName = config.getString("email.hostname")
    val smtpPort = config.getInt("email.port")
    val userName = config.getString("email.username")
    val password = config.getString("email.password")
    val isSSL = config.getBoolean("email.is.ssl")
    val sender = config.getString("email.sender")
    Some(new Sender(hostName, smtpPort, userName, password, isSSL, sender))
  } catch {
    case e: Exception =>
      logger.error("Sender config error")
      None
  }

  def isValidEmail(email: String): Boolean =
    if ("""(?=[^\s]+)(?=(\w+)@([\w\.]+))""".r.findFirstIn(email) == None) false else true

  def parse(optList: List[String]): Options =
    optList match {
      case Nil => Map()
      case "-f" :: from :: more => loadSender(from) match {
        case Some(sender) => parse(more) ++ Map('from -> sender)
        case _ => parse(more)
      }
      case "-t" :: to :: more =>
        val receivers = to.split(',')
        if (receivers.forall(isValidEmail)) parse(more) ++ Map('to -> receivers) else parse(more)
      case "-c" :: cc :: more =>
        val copiers = cc.split(',')
        if (copiers.forall(isValidEmail)) parse(more) ++ Map('cc -> copiers) else parse(more)
      case "-b" :: bc :: more =>
        val whispers = bc.split(',')
        if (whispers.forall(isValidEmail)) parse(more) ++ Map('bc -> whispers) else parse(more)
      case "-s" :: subject :: more => parse(more) ++ Map('subject -> subject)
      case message :: more => parse(more) ++ Map('message -> message)
    }

  implicit class OptionsWrapper(o: Options) {
    val to = o.get('to) match { case Some(t: Array[String]) => t; case _ => Array[String]() }
    val cc = o.get('cc) match { case Some(c: Array[String]) => c; case _ => Array[String]() }
    val bc = o.get('bc) match { case Some(b: Array[String]) => b; case _ => Array[String]() }
    val subject = o.get('subject) match { case Some(s: String) => s; case _ => "Untitled" }
    val message = o.get('message) match { case Some(m: String) => Some(m); case _ => None }
    def options = o.get('from) match {
      case Some(from: Sender) if o.contains('to) => Some(from, to, cc, bc, subject, message)
      case _ => None
    }
  }
}
