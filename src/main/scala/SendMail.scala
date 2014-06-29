object SendMail {
  import kernel.Parser._

  val senderSample = getClass.getResourceAsStream("sample-sender-gmail.conf")

  val usage = """Usage: sendmail [-hftcbs] [message]

 -h   print this message
 -f   [from],       sender information (typical config file)
 -t   [to],         receiver addresses
 -c   [cc],         cc addresses
 -b   [bcc],        bcc addresses
 -a   [attachment], add a local file as attachment
 -s   [subject],    mail subject
      [message],    mail content (read from stdin if empty)

Use ',' to separate multiple addresses, without spaces.

Sender config sample:

""" + io.Source.fromInputStream(senderSample).mkString

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