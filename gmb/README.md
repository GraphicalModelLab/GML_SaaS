
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