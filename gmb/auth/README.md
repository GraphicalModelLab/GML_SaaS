# Configuration for Federated Login via Facebook and Google

## Configuration for Facebook Federated Login
```
[ application.conf ]
facebookapps.client_id="XXXX"
facebookapps.client_secret="XXXX"
facebookapps.redirect_uri="https://local.graphicalmodellab.org/v0.1/commonModules/php/modules/Auth.php/auth/facebookAppsLogin/authenticate"
# get it by https://graph.facebook.com/oauth/access_token?client_id=XXXX&client_secret=XXX&grant_type=client_credentials
facebookapps.application_accesstoken="1564451690249349|KA02-70YURgagvZC6TXLjpTt9Tg"

```

### Create OAuth 2.0 Account
You can create an account in https://developers.facebook.com.

### Configuration for PHP Side
You also need to change some configuration for PHP side, i.e. ui/v0.1/commonModules/php/config/FacebookApps.ini.

PHP is the endpoint which is exposed to Google like Facebook.

So, when users login through Google Web Site, Google redirect your browser to this PHP endpoint.

Then, PHP calls this Auth Service to validate the token.

If token is valid, then users can finally login the service.

```
[ ui/v0.1/commonModules/php/config/FacebookApps.ini ]

[login]
client_id=XXXXX

scope=email
response_type=code

redirect_uri=https://local.graphicalmodellab.org/v0.1/commonModules/php/modules/Auth.php/auth/facebookAppsLogin/authenticate
loginCallback=https://local.graphicalmodellab.org/v0.1

```


## Configuration for Google Federated Login

```
[ application.conf ]

googleapps.client_id="XXXXXXXX"
googleapps.client_secret="XXXXXXXX"
googleapps.redirect_uri="http://local.graphicalmodellab.org/v0.1/commonModules/php/modules/Auth.php/auth/googleAppsLogin/authenticate"

```

### Create OAuth 2.0 Account
You can create an account in https://console.cloud.google.com.

### Configuration for PHP Side
You also need to change some configuration for PHP side, i.e. ui/v0.1/commonModules/php/config/GoogleApps.ini.

PHP is the endpoint which is exposed to Google like Facebook.

So, when users login through Google Web Site, Google redirect your browser to this PHP endpoint.

Then, PHP calls this Auth Service to validate the token.

If token is valid, then users can finally login the service.

```
[ ui/v0.1/commonModules/php/config/GoogleApps.ini ]

[login]
client_id=XXXXX

scope=openid%20email
response_type=code

redirect_uri=http://local.graphicalmodellab.org/v0.1/commonModules/php/modules/Auth.php/auth/googleAppsLogin/authenticate
loginCallback=http://local.graphicalmodellab.org/v0.1

```
