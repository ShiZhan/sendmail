object SendMail {
  import kernel.Parser._
  import kernel.Configurator.senderConfGenerator
  import helper.Resource

  lazy val usage =
    Resource.getString("functions.txt") + Resource.getString("sample-sender-gmail.conf")

  val incorrectArgs = "Incorrect parameters, see help (sendmail -h)."

  def main(args: Array[String]) = args.toList match {
    case "-h" :: Nil => println(usage)
    case "-i" :: fileName :: Nil => senderConfGenerator(fileName)
    case optList => parse(optList).options match {
      case Some((senderConf, to, cc, bcc, subject, message, attachment)) =>
        senderConf.toSender.send(to, cc, bcc, subject, message, attachedFile = attachment)
      case _ => println(incorrectArgs)
    }
  }
}