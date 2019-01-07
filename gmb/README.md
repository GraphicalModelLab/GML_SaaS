# Directory Structure

![top-page](https://github.com/GraphicalModelLab/GML_SaaS/blob/master/doc/BigPictureOfGMB.png)

### auth
This directory corresponds to (1) Authentication Service in the above diagram.

This service handles processing about authentication, e.g. Federated Login, issuing Access token, etc.

### gml
This directory corresponds to (2) gml Service in the above diagram.

This service serves requests other than authentication, e.g. search the saved graph models, calculate graph stuff, etc.

### gml_api
This directory is separated from other services for plugins.

When you develop plugins for GML service, you include this dependency to develop your own plugin.

### common
This directory contains some common tools.

# Dependencies

| Dependencies        | Version           | Description |
| ------------- |-------------|-------------|
| Cassandra |  3.0.1 | Used to store all information, e.g. graph information |
| Elastic Search |  6.3.1 | Used to index graph information. Users can search graph by tag search menu in header menus. |

# How to package gmb service

```
cd <Your Path>/gmb
sbt dist
```

After sbt dist command, zip file is generated, which can be unzipped/deployed to the desired directory.
After unzipping the file, bin folder includes shell script to run gmb service.

infra/ansible/roles/gml/template/supervisor/gml-company.conf shows an example about how to start the service

# Encryption Mode
If you want to encrypt data stored in Cassandra (note: not all data is encrypted. Check out the ProdModule to see what implementation of AuthDB is used. AuthDB implemention shows which part of data is encrypted.), enable this mode.

Currently, AuthDBClientEncryptingImpl encrypt data related to credentials, e.g. Google Accesstoken, Facebook AccessToken.

```
[ auth/conf/application.conf ]
# This is ProdMode where some data in Cassandra is encrypted, which uses AuthDBClientEncryptingImpl
play.modules.enabled += "modules.ProdModule"

# This is DevMode where any data in Cassandra is not encrypted, which uses AuthDBClientImpl
#play.modules.enabled += "modules.DevModule"

...
# Encryption Secret key for encrypting data in Cassandra is stored in the below file in ProdMode. In DevMode, this config is ignored.
encryption.secret.write.location="/Users/itomao/gml_auth_secret"


[ gml/conf/application.conf ]
# This is ProdMode where some data in Cassandra is encrypted, which uses AuthDBClientEncryptingImpl
play.modules.enabled += "modules.ProdModule"

# This is DevMode where any data in Cassandra is not encrypted, which uses AuthDBClientImpl
#play.modules.enabled += "modules.DevModule"

...
# Encryption Secret key for encrypting data in Cassandra is read from the below file in ProdMode. In DevMode, this config is ignored.
encryption.secret.read.location="/Users/itomao/gml_auth_secret"

```

auth service generate secret key and write that key into a file located in "encryption.secret.write.location".

Then, gml service load secret key stored by auth service.

We need to make sure that two services are using the same secret key or gml fails to decrypt data in Cassandra.

# How to Debug
If you use IntelliJ IDEA or other stuff like Eclipse, you can create SBT task like the below:

![top-page](https://github.com/GraphicalModelLab/GML_SaaS/blob/master/doc/DebugGMB.png)

# Generate KeyStore and Truststore for Server side (i.e. playframework)
If you want to enable SSL between PHP and gmb services (Auth and GML),
then you need to setup keystore and truststore.

```
# Generate KeyStore with a pair of key
keytool -genkey -keystore serverKeystore.jks -alias server

# Get Public Key
keytool -export -keystore serverKeystore.jks -alias server -file server.cer

# Generate Truststore
keytool -import -file ./server.cer -alias servercer -keystore serverTrustStore.jks

```

```
# Specify Truststore to JVM
-Djavax.net.ssl.trustStore=/Users/itomao/git/gmb/auth/conf/cert/serverTrustStore.jks
```