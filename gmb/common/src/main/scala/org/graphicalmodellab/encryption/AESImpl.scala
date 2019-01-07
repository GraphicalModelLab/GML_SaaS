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

package org.graphicalmodellab.encryption

import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}
import java.security.{Key, MessageDigest}
import javax.crypto.{Cipher, KeyGenerator, SecretKey}
import javax.xml.bind.DatatypeConverter

import com.google.inject.Inject
import play.api.Configuration

class AESImpl @Inject () (config: Configuration) extends Encryption{
  val generator = KeyGenerator.getInstance("AES");
  generator.init(128); // The AES key size in number of bits

  var secKey:Key = null;
  if(config.has("encryption.secret.read.location")){
    val fIStream= new FileInputStream(config.get[String]("encryption.secret.read.location"))
    val oin:ObjectInputStream = new ObjectInputStream(fIStream);
    try {
      secKey = oin.readObject().asInstanceOf[Key];
    } finally {
      oin.close();
    }
  }else {
    secKey = generator.generateKey();

    if(config.has("encryption.secret.write.location")){
      val secKeyFile = new FileOutputStream(config.get[String]("encryption.secret.write.location"));
      val keyOut = new ObjectOutputStream(secKeyFile);
      try {
        keyOut.writeObject(secKey);
      } finally {
        keyOut.close();
      }
    }
  }


  val encCipher = Cipher.getInstance("AES");
  encCipher.init(Cipher.ENCRYPT_MODE, secKey);

  val decCipher = Cipher.getInstance("AES");
  decCipher.init(Cipher.DECRYPT_MODE, secKey);

  override def encrypt(stringVal: String): Array[Byte] = {
    val byteCipherText = encCipher.doFinal(stringVal.getBytes());

    return byteCipherText;
  }

  override def decrypt(byte: Array[Byte]): String = {
    val bytePlainText = decCipher.doFinal(byte);
    return new String(bytePlainText);
  }
}
