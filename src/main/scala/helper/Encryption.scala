package helper

object Encryption {
  import javax.crypto.{ Cipher, SecretKey, SecretKeyFactory }
  import javax.crypto.spec.{ PBEKeySpec, PBEParameterSpec, SecretKeySpec }
  import org.apache.commons.codec.binary.Base64

  private val password = "Generated password".toCharArray
  private val kspec = new PBEKeySpec(password)
  private val salt = "s-a-l-t-".getBytes("UTF-8")
  private val pspec = new PBEParameterSpec(salt, 20)
  private val factory = SecretKeyFactory.getInstance("PBEWithMD5AndDES")
  private val secret = factory.generateSecret(kspec)

  val encrypter = Cipher.getInstance("PBEWithMD5AndDES/CBC/PKCS5Padding")
  val decrypter = Cipher.getInstance("PBEWithMD5AndDES/CBC/PKCS5Padding")

  def encrypt(s: String) = {
    encrypter.init(Cipher.ENCRYPT_MODE, secret, pspec)
    Base64.encodeBase64String(encrypter.doFinal(s.getBytes("UTF-8")))
  }

  def decrypt(s: String) = {
    decrypter.init(Cipher.DECRYPT_MODE, secret, pspec)
    new String(decrypter.doFinal(Base64.decodeBase64(s)))
  }
}