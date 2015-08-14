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
}

object Configurator {
  import java.io.{ File, PrintWriter }
  import scala.io.StdIn.readLine

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

    val conf = Configuration(sender, userName, password, hostName, smtpPort, ssl, starttls)

    val pw = new PrintWriter(new File(fileName))
    pw.print(conf.toString)
    pw.close
  }
}