object SendMail {
  import kernel.Parser._

  lazy val sample = getClass.getResourceAsStream("sample-sender-gmail.conf")
  lazy val functions = getClass.getResourceAsStream("functions.txt")
  lazy val usage =
    io.Source.fromInputStream(functions).mkString + io.Source.fromInputStream(sample).mkString

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