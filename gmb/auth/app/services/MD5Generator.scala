package services

import java.util.UUID

/**
 * Created by ito_m on 9/17/16.
 */
object MD5Generator {

  def generate() : String = {
    return generateValue(UUID.randomUUID().toString());
  }

  def generateValue(param : String ) : String = {
    UUID.fromString(UUID.nameUUIDFromBytes(param.getBytes).toString).toString;
  }
}
