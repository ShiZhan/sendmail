package kernel

object Parser extends helper.Logging {
  import java.io.File
  import util.Try

  def loadSender(from: String) = try {
    val configFile = new File(from)
    val config = com.typesafe.config.ConfigFactory.parseFile(configFile)
    val sender = config.getString("email.sender")
    val userName = config.getString("email.username")
    val password = helper.Encryption.decrypt(config.getString("email.password"))
    val hostName = config.getString("email.hostname")
    val smtpPort = config.getInt("email.port")
    val ssl = Try(Some(config.getBoolean("email.ssl"))).getOrElse(None)
    val starttls = Try(Some(config.getBoolean("email.starttls"))).getOrElse(None)
    Some(Configuration(sender, userName, password, hostName, smtpPort, ssl, starttls))
  } catch {
    case e: Exception => { logger.error("Sender config error"); None }
  }

  def isValidEmail(email: String): Boolean =
    if ("""(?=[^\s]+)(?=(\w+)@([\w\.]+))""".r.findFirstIn(email) == None) false else true

  type Options = Map[Symbol, Any]

  def parse(optList: List[String]): Options =
    optList match {
      case Nil => Map()
      case "-f" :: from :: more => loadSender(from) match {
        case Some(senderConf) => parse(more) ++ Map('from -> senderConf)
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
      case "-a" :: attachment :: more =>
        val attachedFile = new File(attachment)
        if (attachedFile.exists) parse(more) ++ Map('attachment -> attachedFile)
        else parse(more)
      case "-s" :: subject :: more => parse(more) ++ Map('subject -> subject)
      case message :: more => parse(more) ++ Map('message -> message)
    }

  implicit class OptionsWrapper(o: Options) {
    val to = o.get('to) match { case Some(t: Array[String]) => t; case _ => Array[String]() }
    val cc = o.get('cc) match { case Some(c: Array[String]) => c; case _ => Array[String]() }
    val bc = o.get('bc) match { case Some(b: Array[String]) => b; case _ => Array[String]() }
    val attachment = o.get('attachment) match { case Some(f: File) => Some(f); case _ => None }
    val subject = o.get('subject) match { case Some(s: String) => s; case _ => "Untitled" }
    val message = o.get('message) match { case Some(m: String) => Some(m); case _ => None }
    def options = o.get('from) match {
      case Some(from: Configuration) if o.contains('to) =>
        Some(from, to, cc, bc, subject, message, attachment)
      case _ => None
    }
  }
}