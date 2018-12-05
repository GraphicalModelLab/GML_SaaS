# How to package gmb service

```
cd <Your Path>/gmb
sbt dist
```

After sbt dist command, zip file is generated, which can be unzipped/deployed to the desired directory.
After unzipping the file, bin folder includes shell script to run gmb service.

infra/ansible/roles/gml/template/supervisor/gml-company.conf shows an example about how to start the service

# How to Debug
If you use IntelliJ IDEA or other stuff like Eclipse, you can create SBT task like the below:

![top-page](https://github.com/GraphicalModelLab/GML_SaaS/blob/master-with-infra/doc/DebugGMB.png)

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