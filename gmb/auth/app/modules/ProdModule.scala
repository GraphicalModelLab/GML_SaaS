package modules

import com.google.inject.AbstractModule
import org.graphicalmodellab.auth.facebookapps.{FacebookAppsOpenIDConnector, FacebookAppsOpenIDConnectorImplV1}
import org.graphicalmodellab.auth.googleapps.{GoogleAppsOpenIDConnector, GoogleAppsOpenIDConnectorImplV1}
import org.graphicalmodellab.auth.{AuthDBClient, AuthDBClientEncryptingImpl, AuthDBClientImpl}
import org.graphicalmodellab.cassandra.{CassandraClient, CassandraClientImpl}
import org.graphicalmodellab.elastic.{ElasticSearchClient, ElasticSearchClientImpl}
import org.graphicalmodellab.encryption.{AESImpl, Encryption}
import org.graphicalmodellab.hash.{Hash, SHA256Impl}
import org.graphicalmodellab.httpclient.{HttpClient, HttpClientImpl}
import services.token.{MD5Token, Token}
import services.{AuthService, AuthServiceImpl}

class ProdModule extends AbstractModule {

  override def configure() = {
    bind(classOf[AuthService]).to(classOf[AuthServiceImpl])
    bind(classOf[CassandraClient]).to(classOf[CassandraClientImpl])
    bind(classOf[AuthDBClient]).to(classOf[AuthDBClientEncryptingImpl])
    bind(classOf[GoogleAppsOpenIDConnector]).to(classOf[GoogleAppsOpenIDConnectorImplV1])
    bind(classOf[FacebookAppsOpenIDConnector]).to(classOf[FacebookAppsOpenIDConnectorImplV1])
    bind(classOf[Hash]).to(classOf[SHA256Impl])
    bind(classOf[Token]).to(classOf[MD5Token])
    bind(classOf[Encryption]).to(classOf[AESImpl])
    bind(classOf[HttpClient]).to(classOf[HttpClientImpl])
  }

}