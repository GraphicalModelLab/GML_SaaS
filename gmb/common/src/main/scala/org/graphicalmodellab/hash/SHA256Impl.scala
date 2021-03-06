/**
  * Copyright (C) 2018 Mao Ito
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *     http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */

package org.graphicalmodellab.hash

import java.security.MessageDigest

class SHA256Impl extends Hash{
  val md = MessageDigest.getInstance("SHA-256");

  def toHashValue(stringVal: String): Array[Byte]={
    md.update(stringVal.getBytes)
    md.digest()
  }

  def toHashString(stringVal: String): String={
    val bytes = toHashValue(stringVal)

    val encryptedVal = new StringBuilder()

    bytes.foreach{
      byte =>
        encryptedVal.append("%02x".format(byte))
    }

    encryptedVal.toString
  }
}
