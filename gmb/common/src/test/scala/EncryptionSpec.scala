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

import javax.xml.bind.DatatypeConverter

import org.graphicalmodellab.encryption.{AESImpl, Encryption}
import org.scalatest.FlatSpec
import org.scalatest.mockito.MockitoSugar
import org.scalatest.mockito._
import org.scalatest._
import play.api.Configuration
import org.mockito.Mockito._

class EncryptionSpec extends FlatSpec with MockitoSugar{
  "AESImpl" should "Decryption byte array of encrypted string should be equal to the original String" in {
    val config = mock[Configuration]

    when(config.has("encryption.secret.read.location")).thenReturn(false)
    when(config.has("encryption.secret.write.location")).thenReturn(false)

    val aes:Encryption = new AESImpl(config);

    val testString = "{\"HelloWorld\":\"gggod\",\"gg\":29}";
    val decryptedString = aes.decrypt(aes.encrypt(testString));
    assert(testString.equals(decryptedString))
  }
}
