package org.graphicalmodellab.encryption

import java.security.MessageDigest

/**
  * Created by itomao on 11/14/16.
  */
object Encryption {
  val md = MessageDigest.getInstance("SHA-256");

  def toHashValue(stringVal: String): Array[Byte]={
    md.update(stringVal.getBytes)
    md.digest()
  }

  def toEncryptedString(stringVal: String): String={
    val bytes = toHashValue(stringVal)

    val encryptedVal = new StringBuilder()

    bytes.foreach{
      byte =>
        encryptedVal.append("%02x".format(byte))
    }

    encryptedVal.toString
  }
}
