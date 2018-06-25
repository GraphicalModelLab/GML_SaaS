<?php
require_once __DIR__.'/../../../commonLibs/silex/vendor/autoload.php';

function login($data)
 {
    $curl = curl_init();

    curl_setopt($curl, CURLOPT_URL, "http://localhost:9097/auth/".$data["companyid"]."/login");
    curl_setopt($curl, CURLOPT_CUSTOMREQUEST, 'POST');
 	curl_setopt($curl, CURLOPT_HTTPHEADER, array('Content-Type: application/json'));
    curl_setopt($curl, CURLOPT_RETURNTRANSFER,true);
 	curl_setopt($curl, CURLOPT_POSTFIELDS, json_encode($data));
    curl_setopt($curl, CURLOPT_SSL_VERIFYHOST,false);

    $response = curl_exec($curl);
    //$result = json_decode($response, true);

    curl_close($curl);
    return $response;
}

function register($data)
{
   $curl = curl_init();

   curl_setopt($curl, CURLOPT_URL, "http://localhost:9097/auth/".$data["companyid"]."/register");
   curl_setopt($curl, CURLOPT_CUSTOMREQUEST, 'POST');
	curl_setopt($curl, CURLOPT_HTTPHEADER, array('Content-Type: application/json'));
   curl_setopt($curl, CURLOPT_RETURNTRANSFER,true);
	curl_setopt($curl, CURLOPT_POSTFIELDS, json_encode($data));
   curl_setopt($curl, CURLOPT_SSL_VERIFYHOST,false);

   $response = curl_exec($curl);
   //$result = json_decode($response, true);

   curl_close($curl);
   return $response;
}

function validate($data)
{
   $curl = curl_init();

   curl_setopt($curl, CURLOPT_URL, "http://localhost:9097/auth/".$data["companyid"]."/validation");
   curl_setopt($curl, CURLOPT_CUSTOMREQUEST, 'POST');
   curl_setopt($curl, CURLOPT_HTTPHEADER, array('Content-Type: application/json'));
   curl_setopt($curl, CURLOPT_RETURNTRANSFER,true);
   curl_setopt($curl, CURLOPT_POSTFIELDS, json_encode($data));
   curl_setopt($curl, CURLOPT_SSL_VERIFYHOST,false);

   $response = curl_exec($curl);
   //$result = json_decode($response, true);

   curl_close($curl);
   return $response;
}
function registerCompany($data)
{
   $curl = curl_init();

   curl_setopt($curl, CURLOPT_URL, "http://localhost:9097/auth/".$data["companyid"]."/registerCompany");
   curl_setopt($curl, CURLOPT_CUSTOMREQUEST, 'POST');
   curl_setopt($curl, CURLOPT_HTTPHEADER, array('Content-Type: application/json'));
   curl_setopt($curl, CURLOPT_RETURNTRANSFER,true);
   curl_setopt($curl, CURLOPT_POSTFIELDS, json_encode($data));
   curl_setopt($curl, CURLOPT_SSL_VERIFYHOST,false);

   $response = curl_exec($curl);
   //$result = json_decode($response, true);

   curl_close($curl);
   return $response;
}
function changePassword($data)
{
   $curl = curl_init();

   curl_setopt($curl, CURLOPT_URL, "http://localhost:9097/auth/".$data["companyid"]."/changePassword");
   curl_setopt($curl, CURLOPT_CUSTOMREQUEST, 'POST');
   curl_setopt($curl, CURLOPT_HTTPHEADER, array('Content-Type: application/json'));
   curl_setopt($curl, CURLOPT_RETURNTRANSFER,true);
   curl_setopt($curl, CURLOPT_POSTFIELDS, json_encode($data));
   curl_setopt($curl, CURLOPT_SSL_VERIFYHOST,false);

   $response = curl_exec($curl);
   //$result = json_decode($response, true);

   curl_close($curl);
   return $response;
}
function changeRole($data)
{
   $curl = curl_init();

   curl_setopt($curl, CURLOPT_URL, "http://localhost:9097/auth/".$data["companyid"]."/changeRole");
   curl_setopt($curl, CURLOPT_CUSTOMREQUEST, 'POST');
   curl_setopt($curl, CURLOPT_HTTPHEADER, array('Content-Type: application/json'));
   curl_setopt($curl, CURLOPT_RETURNTRANSFER,true);
   curl_setopt($curl, CURLOPT_POSTFIELDS, json_encode($data));
   curl_setopt($curl, CURLOPT_SSL_VERIFYHOST,false);

   $response = curl_exec($curl);
   //$result = json_decode($response, true);

   curl_close($curl);
   return $response;
}

function removeAccount($data)
{
   $curl = curl_init();

   curl_setopt($curl, CURLOPT_URL, "http://localhost:9097/auth/".$data["companyid"]."/removeAccount");
   curl_setopt($curl, CURLOPT_CUSTOMREQUEST, 'POST');
   curl_setopt($curl, CURLOPT_HTTPHEADER, array('Content-Type: application/json'));
   curl_setopt($curl, CURLOPT_RETURNTRANSFER,true);
   curl_setopt($curl, CURLOPT_POSTFIELDS, json_encode($data));
   curl_setopt($curl, CURLOPT_SSL_VERIFYHOST,false);

   $response = curl_exec($curl);
   //$result = json_decode($response, true);

   curl_close($curl);
   return $response;
}

function googleAppsAuthenticate($data)
{
   $curl = curl_init();

   curl_setopt($curl, CURLOPT_URL, "http://localhost:9097/auth/".$data["state"]."/googleApps/authenticate");
   curl_setopt($curl, CURLOPT_CUSTOMREQUEST, 'POST');
   curl_setopt($curl, CURLOPT_HTTPHEADER, array('Content-Type: application/json'));
   curl_setopt($curl, CURLOPT_RETURNTRANSFER,true);
   curl_setopt($curl, CURLOPT_POSTFIELDS, json_encode($data));
   curl_setopt($curl, CURLOPT_SSL_VERIFYHOST,false);

   $response = curl_exec($curl);
   //$result = json_decode($response, true);

   curl_close($curl);
   return $response;
}

function facebookAppsAuthenticate($data)
{
   $curl = curl_init();

   curl_setopt($curl, CURLOPT_URL, "http://localhost:9097/auth/".$data["state"]."/facebookApps/authenticate");
   curl_setopt($curl, CURLOPT_CUSTOMREQUEST, 'POST');
   curl_setopt($curl, CURLOPT_HTTPHEADER, array('Content-Type: application/json'));
   curl_setopt($curl, CURLOPT_RETURNTRANSFER,true);
   curl_setopt($curl, CURLOPT_POSTFIELDS, json_encode($data));
   curl_setopt($curl, CURLOPT_SSL_VERIFYHOST,false);

   $response = curl_exec($curl);
   //$result = json_decode($response, true);

   curl_close($curl);
   return $response;
}

function Redirect($url, $permanent = false)
{
    if (headers_sent() === false)
    {
    	header('Location: ' . $url, true, ($permanent === true) ? 301 : 302);
    }

    exit();
}

use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

$CloudCareerSheetAuth = new Silex\Application();

$CloudCareerSheetAuth->post('/auth/login', function (Request $request) use ($CloudCareerSheetAuth) {
    $data_request = json_decode(file_get_contents("php://input"),true);

    mb_internal_encoding('UTF-8');

    $decodeJSON = json_decode(
      login($data_request)
      ,
      true);

    return $CloudCareerSheetAuth->json(array(
          "success"=>true,
          "body" =>$decodeJSON,
          "request"=>$data_request),201);
});

$CloudCareerSheetAuth->post('/auth/register', function (Request $request) use ($CloudCareerSheetAuth) {
    $data_request = json_decode(file_get_contents("php://input"),true);

    mb_internal_encoding('UTF-8');

    $decodeJSON = json_decode(
      register($data_request)
      ,
      true);

    return $CloudCareerSheetAuth->json(array(
          "success"=>true,
          "body" =>$decodeJSON,
          "request"=>$data_request),201);
});

$CloudCareerSheetAuth->get('/auth/validation', function (Request $request) use ($CloudCareerSheetAuth) {
    mb_internal_encoding('UTF-8');

    $config = parse_ini_file(__DIR__."/../config/MailApp.ini");

    $data = array();

    foreach ( $request->query->keys() as $key){
        $data[$key] = $request->query->get($key);
    }

    $decodeJSON = json_decode(
      validate($data)
      ,
      true);

    $success = $decodeJSON["code"];

    if($success == 200){
        Redirect($config["registrationCallBackDomain"].'/company/'.$data["companyid"].'?userid='.$data["email"].'&token='.$decodeJSON["token"], false);
    }else{
        Redirect($config["registrationCallBackDomain"].'/registrationFailure.php/#/'.$data["companyid"], false);
    }
});

$CloudCareerSheetAuth->post('/auth/changePassword', function (Request $request) use ($CloudCareerSheetAuth) {
     $data_request = json_decode(file_get_contents("php://input"),true);

     mb_internal_encoding('UTF-8');

     $decodeJSON = json_decode(
          changePassword($data_request)
          ,
          true);

     return $CloudCareerSheetAuth->json(array(
              "success"=>true,
              "body" =>$decodeJSON,
              "request"=>$data_request),201);
});

$CloudCareerSheetAuth->post('/auth/changeRole', function (Request $request) use ($CloudCareerSheetAuth) {
     $data_request = json_decode(file_get_contents("php://input"),true);

     mb_internal_encoding('UTF-8');

     $decodeJSON = json_decode(
          changeRole($data_request)
          ,
          true);

     return $CloudCareerSheetAuth->json(array(
              "success"=>true,
              "body" =>$decodeJSON,
              "request"=>$data_request),201);
});

$CloudCareerSheetAuth->post('/auth/removeAccount', function (Request $request) use ($CloudCareerSheetAuth) {
     $data_request = json_decode(file_get_contents("php://input"),true);

     mb_internal_encoding('UTF-8');

     $decodeJSON = json_decode(
          removeAccount($data_request)
          ,
          true);

     return $CloudCareerSheetAuth->json(array(
              "success"=>true,
              "body" =>$decodeJSON,
              "request"=>$data_request),201);
});

$CloudCareerSheetAuth->get('/auth/googleAppsLogin/login', function (Request $request) use ($CloudCareerSheetAuth) {
    $config = parse_ini_file(__DIR__."/../config/OpenIDConnect/GoogleApps.ini",true);

    $data_request = array();

    foreach ( $request->query->keys() as $key){
        $data_request[$key] = $request->query->get($key);
    }

    Redirect(
        'https://accounts.google.com/o/oauth2/auth'
        .'?client_id='.$config["login"]["client_id"]
        .'&scope='.$config["login"]["scope"]
        .'&response_type='.$config["login"]["response_type"]
        .'&redirect_uri='.urlencode($config["login"]["redirect_uri"])
        .'&state='.$data_request["companyid"], false);
});

$CloudCareerSheetAuth->get('/auth/googleAppsLogin/authenticate', function (Request $request) use ($CloudCareerSheetAuth) {
     $config = parse_ini_file(__DIR__."/../config/OpenIDConnect/GoogleApps.ini",true);

     $data_request = array();

     foreach ( $request->query->keys() as $key){
        $data_request[$key] = $request->query->get($key);
     }

     $decodeJSON = json_decode(
          googleAppsAuthenticate($data_request)
          ,
          true);

     mb_internal_encoding('UTF-8');

     if($decodeJSON["code"] == 200){
        Redirect($config["login"]["loginCallback"].'/company/'.$data_request["state"].'?userid='.$decodeJSON["email"].'&token='.$decodeJSON["token"].'&role='.$decodeJSON["role"].'#/top', false);
     }else{
        Redirect($config["login"]["loginCallback"].'/company/'.$data_request["state"],false);
     }

});

$CloudCareerSheetAuth->get('/auth/facebookAppsLogin/login', function (Request $request) use ($CloudCareerSheetAuth) {
    $config = parse_ini_file(__DIR__."/../config/OpenIDConnect/FacebookApps.ini",true);

    $data_request = array();

    foreach ( $request->query->keys() as $key){
        $data_request[$key] = $request->query->get($key);
    }

    Redirect(
        'https://www.facebook.com/v2.8/dialog/oauth'
        .'?client_id='.$config["login"]["client_id"]
        .'&redirect_uri='.urlencode($config["login"]["redirect_uri"])
        .'&scope=email'
        .'&state='.$data_request["companyid"], false);

});

$CloudCareerSheetAuth->get('/auth/facebookAppsLogin/authenticate', function (Request $request) use ($CloudCareerSheetAuth) {
     $config = parse_ini_file(__DIR__."/../config/OpenIDConnect/FacebookApps.ini",true);

     $data_request = array();

     foreach ( $request->query->keys() as $key){
        $data_request[$key] = $request->query->get($key);
     }

     $decodeJSON = json_decode(
          facebookAppsAuthenticate($data_request)
          ,
          true);

     mb_internal_encoding('UTF-8');


     if($decodeJSON["code"] == 200){
        Redirect($config["login"]["loginCallback"].'/company/'.$data_request["state"].'?userid='.$decodeJSON["email"].'&token='.$decodeJSON["token"].'&role='.$decodeJSON["role"].'#/top', false);
     }else if($decodeJSON["code"] == 901){
        print_r("The API Key is invalid");
     }else{
        Redirect($config["login"]["loginCallback"].'/company/'.$data_request["state"].'#/top',false);
     }
});

$CloudCareerSheetAuth->post('/auth/registerCompany', function (Request $request) use ($CloudCareerSheetAuth) {
     $data_request = json_decode(file_get_contents("php://input"),true);

     mb_internal_encoding('UTF-8');

     $decodeJSON = json_decode(
               registerCompany($data_request)
               ,
     true);

     return $CloudCareerSheetAuth->json(array(
                   "success"=>true,
                   "body" =>$decodeJSON,
                   "request"=>$data_request),201);
});

$CloudCareerSheetAuth->run();
?>