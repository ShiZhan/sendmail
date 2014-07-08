package kernel

case class Configuration(
  sender: String, userName: String, password: String, hostName: String, smtpPort: Int,
  ssl: Option[Boolean] = None, starttls: Option[Boolean] = None) {
  def toSender = new Sender(this)

  override def toString = {
    val encryptedPassword = helper.Encryption.encrypt(password)
    val smtpPortString = smtpPort.toString
    val sslString =
      ssl match { case Some(v: Boolean) => s"email.ssl=$v\n"; case _ => "" }
    val starttlsString =
      starttls match { case Some(v: Boolean) => s"email.starttls=$v\n"; case _ => "" }
    s"""email.sender="$sender"
email.username="$userName"
email.password="$encryptedPassword"
email.hostname="$hostName"
email.port=$smtpPortString
$sslString$starttlsString"""
  }

  def saveTo(fileName: String) = {
    val pw = new java.io.PrintWriter(new java.io.File(fileName))
    pw.print(toString)
    pw.close
  }
}

object Configurator {
  def readLine =
    io.Source.fromInputStream(System.in).takeWhile(_ != '\n').mkString

  def senderConfGenerator(fileName: String) = {
    print("Sender (foo@gmail.com):")
    val sender = readLine

    print("User Name (foo):")
    val userName = readLine
    print("Password:")
    val password = readLine

    print("SMTP Host (smtp.gmail.com):")
    val hostName = readLine
    print("SMTP Port (465):")
    val smtpPort = try { readLine.toInt } catch { case e: Exception => 465 }

    print("SSL (true|false):")
    val ssl = try { Some(readLine.toBoolean) } catch { case e: Exception => None }
    print("StartTLS (true|false):")
    val starttls = try { Some(readLine.toBoolean) } catch { case e: Exception => None }

    Configuration(sender, userName, password, hostName, smtpPort, ssl, starttls).saveTo(fileName)
  }
}