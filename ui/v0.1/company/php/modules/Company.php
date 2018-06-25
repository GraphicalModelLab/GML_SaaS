<?php
require_once __DIR__.'/../../../commonLibs/silex/vendor/autoload.php';

function update($data)
{
   $curl = curl_init();

   curl_setopt($curl, CURLOPT_URL, "http://localhost:9095/company/".$data["companyid"]."/update/engineer");
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

function get($data)
{
   $curl = curl_init();

   curl_setopt($curl, CURLOPT_URL, "http://localhost:9095/company/".$data["companyid"]."/get/engineer");
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

function getProject($data)
{
   $curl = curl_init();

   curl_setopt($curl, CURLOPT_URL, "http://localhost:9095/company/".$data["companyid"]."/get/project");
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

function getEntryProject($data)
{
   $curl = curl_init();

   curl_setopt($curl, CURLOPT_URL, "http://localhost:9095/company/".$data["companyid"]."/get/entryproject");
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

function searchEngineer($data)
{
   $curl = curl_init();

   curl_setopt($curl, CURLOPT_URL, "http://localhost:9095/company/".$data["companyid"]."/search/engineer");
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

function buildPdf($data)
{
   $curl = curl_init();

   curl_setopt($curl, CURLOPT_URL, "http://localhost:9095/company/".$data["companyid"]."/build/pdf");
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

function teamEntry($data)
{
   $curl = curl_init();

   curl_setopt($curl, CURLOPT_URL, "http://localhost:9095/company/".$data["companyid"]."/entry/team");
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

function submitProject($data)
{
   $curl = curl_init();

   curl_setopt($curl, CURLOPT_URL, "http://localhost:9095/company/".$data["companyid"]."/submit/project");
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

function changeProject($data)
{
   $curl = curl_init();

   curl_setopt($curl, CURLOPT_URL, "http://localhost:9095/company/".$data["companyid"]."/change/project");
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

function getProjectAssignment($data)
{
   $curl = curl_init();

   curl_setopt($curl, CURLOPT_URL, "http://localhost:9095/company/".$data["companyid"]."/get/projectassignment");
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

$SES = new Silex\Application();

$SES->post('/careersheet/update', function (Request $request) use ($SES) {
    $data_request = json_decode(file_get_contents("php://input"),true);

    mb_internal_encoding('UTF-8');

    $decodeJSON = json_decode(
      update($data_request)
      ,
      true);

    return $SES->json(array(
          "success"=>true,
          "body" =>$decodeJSON,
          "request"=>$data_request),201);
});

$SES->post('/careersheet/get', function (Request $request) use ($SES) {
    $data_request = json_decode(file_get_contents("php://input"),true);

    mb_internal_encoding('UTF-8');

    $decodeJSON = json_decode(
      get($data_request)
      ,
      true);

    return $SES->json(array(
          "success"=>true,
          "body" =>$decodeJSON,
          "request"=>$data_request),201);
});

$SES->post('/project/get', function (Request $request) use ($SES) {
    $data_request = json_decode(file_get_contents("php://input"),true);

    mb_internal_encoding('UTF-8');

    $decodeJSON = json_decode(
      getProject($data_request)
      ,
      true);

    return $SES->json(array(
          "success"=>true,
          "body" =>$decodeJSON,
          "request"=>$data_request),201);
});

$SES->post('/entryproject/get', function (Request $request) use ($SES) {
    $data_request = json_decode(file_get_contents("php://input"),true);

    mb_internal_encoding('UTF-8');

    $decodeJSON = json_decode(
      getEntryProject($data_request)
      ,
      true);

    return $SES->json(array(
          "success"=>true,
          "body" =>$decodeJSON,
          "request"=>$data_request),201);
});

$SES->post('/engineer/search', function (Request $request) use ($SES) {
    $data_request = json_decode(file_get_contents("php://input"),true);

    mb_internal_encoding('UTF-8');

    $decodeJSON = json_decode(
      searchEngineer($data_request)
      ,
      true);

    return $SES->json(array(
          "success"=>true,
          "body" =>$decodeJSON,
          "request"=>$data_request),201);
});

$SES->post('/pdf/build', function (Request $request) use ($SES) {
    $data_request = json_decode(file_get_contents("php://input"),true);

    mb_internal_encoding('UTF-8');

    $decodeJSON = json_decode(
      buildPdf($data_request)
      ,
      true);

    return $SES->json(array(
          "success"=>true,
          "body" =>$decodeJSON,
          "request"=>$data_request),201);
});

$SES->post('/team/entry', function (Request $request) use ($SES) {
    $data_request = json_decode(file_get_contents("php://input"),true);

    mb_internal_encoding('UTF-8');

    $decodeJSON = json_decode(
      teamEntry($data_request)
      ,
      true);

    return $SES->json(array(
          "success"=>true,
          "body" =>$decodeJSON,
          "request"=>$data_request),201);
});

$SES->post('/project/submit', function (Request $request) use ($SES) {
    $data_request = json_decode(file_get_contents("php://input"),true);

    mb_internal_encoding('UTF-8');

    $decodeJSON = json_decode(
      submitProject($data_request)
      ,
      true);

    return $SES->json(array(
          "success"=>true,
          "body" =>$decodeJSON,
          "request"=>$data_request),201);
});

$SES->post('/project/change', function (Request $request) use ($SES) {
    $data_request = json_decode(file_get_contents("php://input"),true);

    mb_internal_encoding('UTF-8');

    $decodeJSON = json_decode(
      changeProject($data_request)
      ,
      true);

    return $SES->json(array(
          "success"=>true,
          "body" =>$decodeJSON,
          "request"=>$data_request),201);
});

$SES->post('/projectassignment/get', function (Request $request) use ($SES) {
    $data_request = json_decode(file_get_contents("php://input"),true);

    mb_internal_encoding('UTF-8');

    $decodeJSON = json_decode(
      getProjectAssignment($data_request)
      ,
      true);

    return $SES->json(array(
          "success"=>true,
          "body" =>$decodeJSON,
          "request"=>$data_request),201);
});

$SES->run();
?>