object SendMail {
  import helper.OptParse._
  import kernel.Mailer._

  val usage = """usage: sendmail [-hftcbs] [message]
 -h   print this message
 -f   [from],    sender information (typical config file)
 -t   [to],      receiver addresses
 -c   [cc],      cc addresses
 -b   [bcc],     bcc addresses
 -s   [subject], mail subject
      [message], mail content

Use ',' to split multiple addresses, without spaces."""
  val incorrectArgs = "Incorrect parameters, see help (Present -h)."

  def main(args: Array[String]) = {
    println("SendMail program")
    args.toList match {
      case "-h" :: Nil => println(usage)
      case optList =>
        parse(optList).toConfig match {
          case Some(config) => send(Mail(from = (mailer, "wNotify"), to = Seq(""),
            subject = "[wNotify] content changed",
            message = "Hello World!"))
          case _ => println(incorrectArgs)
        }
    }
  }
}
