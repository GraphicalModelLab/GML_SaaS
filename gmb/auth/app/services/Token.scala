package services

import java.util.Date

/**
 * Created by ito_m on 9/17/16.
 */
class Token {
  val accessToken = MD5Generator.generate()
  val generatedTime = new Date()

}
