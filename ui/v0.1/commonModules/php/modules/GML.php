<?php
require_once __DIR__.'/../../../commonLibs/silex/vendor/autoload.php';

use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

$GMLService = new Silex\Application();

function historyModel($data)
{
    $curl = curl_init();

    curl_setopt($curl, CURLOPT_URL, "http://localhost:9098/gml/".$data["companyid"]."/model/history");
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

function training($data)
{
    $curl = curl_init();

    curl_setopt($curl, CURLOPT_URL, "http://localhost:9098/gml/".$data["companyid"]."/model/training");
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

function test($data)
{
    $curl = curl_init();

    curl_setopt($curl, CURLOPT_URL, "http://localhost:9098/gml/".$data["companyid"]."/model/test");
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

function saveModel($data)
{
    $curl = curl_init();

    curl_setopt($curl, CURLOPT_URL, "http://localhost:9098/gml/".$data["companyid"]."/model/save");
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

function listModel($data)
{
    $curl = curl_init();

    curl_setopt($curl, CURLOPT_URL, "http://localhost:9098/gml/".$data["companyid"]."/model/list");
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

function getModel($data)
{
    $curl = curl_init();

    curl_setopt($curl, CURLOPT_URL, "http://localhost:9098/gml/".$data["companyid"]."/model/get");
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

function getHistoryModel($data)
{
    $curl = curl_init();

    curl_setopt($curl, CURLOPT_URL, "http://localhost:9098/gml/".$data["companyid"]."/model/history/get");
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

function searchModel($data)
{
    $curl = curl_init();

    curl_setopt($curl, CURLOPT_URL, "http://localhost:9098/gml/".$data["companyid"]."/model/search");
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

$GMLService->post('/gml/training', function (Request $request) use ($GMLService) {
     $data_request = json_decode(file_get_contents("php://input"),true);

     mb_internal_encoding('UTF-8');

     $decodeJSON = json_decode(
               training($data_request)
     ,
     true);

     return $GMLService->json(array(
                   "success"=>true,
                   "body" =>$decodeJSON,
                   "request"=>$data_request),201);
});

$GMLService->post('/gml/test', function (Request $request) use ($GMLService) {
     $data_request = json_decode(file_get_contents("php://input"),true);

     mb_internal_encoding('UTF-8');

     $decodeJSON = json_decode(
               test($data_request)
     ,
     true);

     return $GMLService->json(array(
                   "success"=>true,
                   "body" =>$decodeJSON,
                   "request"=>$data_request),201);
});

$GMLService->post('/gml/model/save', function (Request $request) use ($GMLService) {
    $data_request = json_decode(file_get_contents("php://input"),true);

     mb_internal_encoding('UTF-8');

     $decodeJSON = json_decode(
               saveModel($data_request)
     ,
     true);

     return $GMLService->json(array(
                   "success"=>true,
                   "body" =>$decodeJSON,
                   "request"=>$data_request),201);
});

$GMLService->post('/gml/model/list', function (Request $request) use ($GMLService) {
    $data_request = json_decode(file_get_contents("php://input"),true);

     mb_internal_encoding('UTF-8');

     $decodeJSON = json_decode(
               listModel($data_request)
     ,
     true);

     return $GMLService->json(array(
                   "success"=>true,
                   "body" =>$decodeJSON,
                   "request"=>$data_request),201);
});

$GMLService->post('/gml/model/get', function (Request $request) use ($GMLService) {
    $data_request = json_decode(file_get_contents("php://input"),true);

     mb_internal_encoding('UTF-8');

     $decodeJSON = json_decode(
               getModel($data_request)
     ,
     true);

     return $GMLService->json(array(
                   "success"=>true,
                   "body" =>$decodeJSON,
                   "request"=>$data_request),201);
});

$GMLService->post('/gml/model/search', function (Request $request) use ($GMLService) {
    $data_request = json_decode(file_get_contents("php://input"),true);

     mb_internal_encoding('UTF-8');

     $decodeJSON = json_decode(
               searchModel($data_request)
     ,
     true);

     return $GMLService->json(array(
                   "success"=>true,
                   "body" =>$decodeJSON,
                   "request"=>$data_request),201);
});

$GMLService->post('/gml/model/history', function (Request $request) use ($GMLService) {
    $data_request = json_decode(file_get_contents("php://input"),true);

     mb_internal_encoding('UTF-8');

     $decodeJSON = json_decode(
               historyModel($data_request)
     ,
     true);

     return $GMLService->json(array(
                   "success"=>true,
                   "body" =>$decodeJSON,
                   "request"=>$data_request),201);
});

$GMLService->post('/gml/model/history/get', function (Request $request) use ($GMLService) {
    $data_request = json_decode(file_get_contents("php://input"),true);

     mb_internal_encoding('UTF-8');

     $decodeJSON = json_decode(
               getHistoryModel($data_request)
     ,
     true);

     return $GMLService->json(array(
                   "success"=>true,
                   "body" =>$decodeJSON,
                   "request"=>$data_request),201);
});

$GMLService->run();
?>