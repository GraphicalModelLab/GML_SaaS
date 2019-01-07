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

package modules

import com.google.inject.AbstractModule
import org.graphicalmodellab.auth.{AuthDBClient, AuthDBClientEncryptingImpl}
import org.graphicalmodellab.cassandra.{CassandraClient, CassandraClientImpl}
import org.graphicalmodellab.elastic.{ElasticSearchClient, ElasticSearchClientImpl}
import org.graphicalmodellab.encryption.{AESImpl, Encryption}
import org.graphicalmodellab.hash.{Hash, SHA256Impl}
import services._

class ProdModule extends AbstractModule {

  override def configure() = {
    bind(classOf[GraphicalModelLabService]).to(classOf[GraphicalModelLabServiceImpl])
    bind(classOf[CassandraClient]).to(classOf[CassandraClientImpl])
    bind(classOf[GmlDBClient]).to(classOf[GmlDBClientImpl])
    bind(classOf[ElasticSearchClient]).to(classOf[ElasticSearchClientImpl])
    bind(classOf[GmlElasticSearchClient]).to(classOf[GmlElasticSearchClientImpl])
    bind(classOf[Hash]).to(classOf[SHA256Impl])
    bind(classOf[AuthDBClient]).to(classOf[AuthDBClientEncryptingImpl])
    bind(classOf[Encryption]).to(classOf[AESImpl])
  }
}