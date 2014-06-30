object SendMail {
  import kernel.Parser._
  import helper.Resource

  lazy val usage =
    Resource.getString("functions.txt") + Resource.getString("sample-sender-gmail.conf")

  val incorrectArgs = "Incorrect parameters, see help (sendmail -h)."

  def main(args: Array[String]) = args.toList match {
    case "-h" :: Nil => println(usage)
    case optList => parse(optList).options match {
      case Some((from, to, cc, bc, subject, message, attachment)) =>
        from.send(to, cc, bc, subject, message, attachedFile = attachment)
      case _ => println(incorrectArgs)
    }
  }
}