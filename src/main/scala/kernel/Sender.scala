package kernel

class Sender(senderConf: Configuration) extends helper.Logging {
  import org.apache.commons.mail._
  val Configuration(sender, userName, password, hostName, smtpPort, ssl, starttls) = senderConf

  sealed abstract class MailType
  case object Plain extends MailType
  case object Rich extends MailType
  case object MultiPart extends MailType

  def send(
    to: Array[String], cc: Array[String], bcc: Array[String],
    subject: String, message: Option[String],
    richMessage: Option[String] = None, attachedFile: Option[(java.io.File)] = None) = {
    val format =
      if (attachedFile.isDefined) MultiPart
      else if (richMessage.isDefined) Rich
      else Plain

    val content =
      if (message.isDefined) message.get
      else io.Source.fromInputStream(System.in).takeWhile(_ != 0.toChar).mkString

    val commonsMail: Email = format match {
      case Plain => new SimpleEmail().setMsg(content)
      case Rich => new HtmlEmail().setHtmlMsg(richMessage.get).setTextMsg(content)
      case MultiPart => {
        val attachment = new EmailAttachment()
        attachment.setPath(attachedFile.get.getAbsolutePath)
        attachment.setDisposition(EmailAttachment.ATTACHMENT)
        attachment.setName(attachedFile.get.getName)
        new MultiPartEmail().attach(attachment).setMsg(content)
      }
    }

    commonsMail.setHostName(hostName)
    commonsMail.setSmtpPort(smtpPort)
    commonsMail.setAuthentication(userName, password)
    if (ssl.isDefined) commonsMail.setSSLOnConnect(ssl.get)
    if (starttls.isDefined) commonsMail.setStartTLSEnabled(starttls.get)

    to foreach (commonsMail.addTo)
    cc foreach (commonsMail.addCc)
    bcc foreach (commonsMail.addBcc)
    commonsMail.setFrom(sender, "")
    commonsMail.setSubject(subject)

    val result = commonsMail.send()

    logger.info(result)
  }
}