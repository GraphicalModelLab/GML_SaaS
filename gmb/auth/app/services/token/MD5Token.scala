package services.token

import java.util.UUID

/**
  * Created by itomao on 12/28/18.
  */
class MD5Token extends Token{
  def generate() : String = {
    return generateValue(UUID.randomUUID().toString());
  }

  def generateValue(param : String ) : String = {
    UUID.fromString(UUID.nameUUIDFromBytes(param.getBytes).toString).toString;
  }

  override def generateToken(): String = {
    generate()
  }
}
