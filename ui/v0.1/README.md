# How to Develop UI

## 1. install npm command

## 2. install the required modules

```
cd <Your Path>/ui/v0.1
./init_npm_modules.sh
```

## 3. compile UI
```
cd <Your Path>/ui/v0.1
npm run build:company-dev
```

## 4. Beautify JSX files
```
./beautifyJSX_osx.sh
```

This script just execute "esformatter" to formatt JSX files under src folder.

# How to Deploy UI
"infra" directory also shows how to deploy UI.

The following is an example httpd vhosts conf file pointing to UI:

```
[ /etc/apache2/vhosts/servers.conf in OSX ]
<VirtualHost *:443>
   ServerAdmin webmaster@dummy-host.example.com
   DocumentRoot "/Users/itomao/git/GML_SaaS/ui"
   ServerName local.graphicalmodellab.org
   SSLCertificateFile /private/etc/apache2/server.crt
   SSLCertificateKeyFile /private/etc/apache2/server.key
   ErrorLog "/private/var/log/apache2/dummy-host.example.com-error_log"
   CustomLog "/private/var/log/apache2/dummy-host.example.com-access_log" common

   <Directory /Users/itomao/git/GML_SaaS/ui>
        AllowOverride None
        Require all granted
   </Directory>

   <FilesMatch "\.(php|html|htm|js|css|json)$">
    FileETag None

    <IfModule mod_headers.c>
      Header unset ETag
      Header set Cache-Control "max-age=0, no-cache, no-store, must-revalidate"
      Header set Pragma "no-cache"
      Header set Note "CACHING IS DISABLED ON LOCALHOST"
      Header set Expires "Wed, 11 Jan 1984 05:00:00 GMT"
    </IfModule>
   </FilesMatch>

   RewriteEngine on
   RewriteCond %{REQUEST_URI} =/v0.1/company/test
   RewriteRule ^ /v0.1/company/indexDev.php?companyid=test&%{QUERY_STRING}

   RewriteCond %{REQUEST_URI} =/v0.1/company/good
   RewriteRule ^ /v0.1/company/indexDev.php?companyid=good&%{QUERY_STRING}

   RewriteCond %{REQUEST_URI} =/v0.1/company/magicword
   RewriteRule ^ /v0.1/company/indexDev.php?companyid=magicword&%{QUERY_STRING}


</VirtualHost>
```

# Setup HTTPs for Apache side (i.e. PHP)

Recently, Facebook Federated Login becomes strict about https stuff.
So, we need https. Callback from Facebook is pointing to PHP module, which is commonModules/modules/Auth.php.

## 1. Enable SSL in Apache2 (e.g. httpd)

```
[ /private/etc/apache2/httpd.conf ]
..
LoadModule ssl_module libexec/apache2/mod_ssl.so
..
LoadModule socache_shmcb_module libexec/apache2/mod_socache_shmcb.so
..
Include /private/etc/apache2/extra/httpd-ssl.conf
..
```

## 2. Generate key stuff
```
openssl genrsa -aes128 2048 > server.key
openssl req -new -key server.key > server.csr
openssl x509 -in server.csr -days 365000 -req -signkey server.key > server.crt

```

## 3. Setup generated keys in VHost stuff

```
[ /etc/apache2/vhosts/servers.conf ]
# Change 80 to 443 port like <VirtualHost *:443>

# add the below to lines
SSLCertificateFile /Users/itomao/git/gmb/auth/conf/cert/server.crt
SSLCertificateKeyFile /Users/itomao/git/gmb/auth/conf/cert/server.key
..
```

# Tips for Deploying UI
## 1. SetEnvIf Authorization "(.*)" HTTP_AUTHORIZATION=$1 in httpd.conf of Apache

If you dont setup this in apache, Symfony framework cannot get Authorization header from ajax

$request->headers->get('Authorization')

## 2. Run npm for dev mode

npm run build:company-dev
