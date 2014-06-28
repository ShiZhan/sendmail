object SendMail {
  import kernel.Parser._

  val usage = """usage: sendmail [-hftcbs] [message]
 -h   print this message
 -f   [from],    sender information (typical config file)
 -t   [to],      receiver addresses
 -c   [cc],      cc addresses
 -b   [bcc],     bcc addresses
 -s   [subject], mail subject
      [message], mail content

Use ',' to separate multiple addresses, without spaces."""
  val incorrectArgs = "Incorrect parameters, see help (Present -h)."

  def main(args: Array[String]) = {
    println("SendMail program")
    args.toList match {
      case "-h" :: Nil => println(usage)
      case optList => parse(optList).options match {
        case Some((from, to, cc, bc, subject, message)) => from.send(to, cc, bc, subject, message)
        case _ => println(incorrectArgs)
      }
    }
  }
}
