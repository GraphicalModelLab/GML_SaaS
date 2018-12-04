# Generate KeyStore and Truststore for Server side (i.e. playframework)

```
# Generate KeyStore with a pair of key
keytool -genkey -keystore serverKeystore.jks -alias server

# Get Public Key
keytool -export -keystore serverKeystore.jks -alias server -file server.cer

# Generate Truststore
keytool -import -file ./server.cer -alias servercer -keystore serverTrustStore.jks

```

-Djavax.net.ssl.trustStore=/Users/itomao/git/gmb/auth/conf/cert/serverTrustStore.jks
