<?php
require_once __DIR__.'/../../../commonLibs/silex/vendor/autoload.php';

use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

$GMLService = new Silex\Application();

function getTestHistoryList($data,$authorization)
{
    $curl = curl_init();

    curl_setopt($curl, CURLOPT_URL, "http://localhost:9098/gml/".$data["companyid"]."/model/test/history/list?userid=".$data["userid"]."&model_userid=".$data["model_userid"]."&modelid=".$data["modelid"]);
    curl_setopt($curl, CURLOPT_CUSTOMREQUEST, 'GET');
 	curl_setopt($curl, CURLOPT_HTTPHEADER, array('Authorization: '.$authorization));
    curl_setopt($curl, CURLOPT_RETURNTRANSFER,true);
    curl_setopt($curl, CURLOPT_SSL_VERIFYHOST,false);

    $response = curl_exec($curl);
    //$result = json_decode($response, true);

    curl_close($curl);
    return $response;
}

function training($data,$authorization)
{
    $curl = curl_init();

    curl_setopt($curl, CURLOPT_URL, "http://localhost:9098/gml/".$data["companyid"]."/model/training");
    curl_setopt($curl, CURLOPT_CUSTOMREQUEST, 'POST');
 	curl_setopt($curl, CURLOPT_HTTPHEADER, array('Content-Type: application/json','Authorization: '.$authorization));
    curl_setopt($curl, CURLOPT_RETURNTRANSFER,true);
 	curl_setopt($curl, CURLOPT_POSTFIELDS, json_encode($data));
    curl_setopt($curl, CURLOPT_SSL_VERIFYHOST,false);

    $response = curl_exec($curl);
    //$result = json_decode($response, true);

    curl_close($curl);
    return $response;
}

function test($data,$authorization)
{
    $curl = curl_init();

    curl_setopt($curl, CURLOPT_URL, "http://localhost:9098/gml/".$data["companyid"]."/model/test");
    curl_setopt($curl, CURLOPT_CUSTOMREQUEST, 'POST');
 	curl_setopt($curl, CURLOPT_HTTPHEADER, array('Content-Type: application/json','Authorization: '.$authorization));
    curl_setopt($curl, CURLOPT_RETURNTRANSFER,true);
 	curl_setopt($curl, CURLOPT_POSTFIELDS, json_encode($data));
    curl_setopt($curl, CURLOPT_SSL_VERIFYHOST,false);

    $response = curl_exec($curl);
    //$result = json_decode($response, true);

    curl_close($curl);
    return $response;
}

function saveModel($data,$authorization)
{
    $curl = curl_init();

    curl_setopt($curl, CURLOPT_URL, "http://localhost:9098/gml/".$data["companyid"]."/model/save");
    curl_setopt($curl, CURLOPT_CUSTOMREQUEST, 'POST');
 	curl_setopt($curl, CURLOPT_HTTPHEADER, array('Content-Type: application/json','Authorization: '.$authorization));
    curl_setopt($curl, CURLOPT_RETURNTRANSFER,true);
 	curl_setopt($curl, CURLOPT_POSTFIELDS, json_encode($data));
    curl_setopt($curl, CURLOPT_SSL_VERIFYHOST,false);

    $response = curl_exec($curl);
    //$result = json_decode($response, true);

    curl_close($curl);
    return $response;
}

function listModel($data,$authorization)
{
    $curl = curl_init();

    curl_setopt($curl, CURLOPT_URL, "http://localhost:9098/gml/".$data["companyid"]."/model/list?userid=".$data["userid"]);
    curl_setopt($curl, CURLOPT_CUSTOMREQUEST, 'GET');
 	curl_setopt($curl, CURLOPT_HTTPHEADER, array('Authorization: '.$authorization));
    curl_setopt($curl, CURLOPT_RETURNTRANSFER,true);
 	curl_setopt($curl, CURLOPT_SSL_VERIFYHOST,false);

    $response = curl_exec($curl);
    //$result = json_decode($response, true);

    curl_close($curl);
    return $response;
}

function getModel($data,$authorization)
{
    $curl = curl_init();

    curl_setopt($curl, CURLOPT_URL, "http://localhost:9098/gml/".$data["companyid"]."/model?userid=".$data["userid"]."&modelid=".$data["modelid"]);
    curl_setopt($curl, CURLOPT_CUSTOMREQUEST, 'GET');
 	curl_setopt($curl, CURLOPT_HTTPHEADER, array('Authorization: '.$authorization));
    curl_setopt($curl, CURLOPT_RETURNTRANSFER,true);
    curl_setopt($curl, CURLOPT_SSL_VERIFYHOST,false);

    $response = curl_exec($curl);
    //$result = json_decode($response, true);

    curl_close($curl);
    return $response;
}

function getTestHistoryModel($data,$authorization)
{
    $curl = curl_init();

    curl_setopt($curl, CURLOPT_URL, "http://localhost:9098/gml/".$data["companyid"]."/model/test/history?userid=".$data["userid"]."&modelid=".$data["modelid"]."&datetime=".$data["datetime"]);
    curl_setopt($curl, CURLOPT_CUSTOMREQUEST, 'GET');
 	curl_setopt($curl, CURLOPT_HTTPHEADER, array('Authorization: '.$authorization));
    curl_setopt($curl, CURLOPT_RETURNTRANSFER,true);
    curl_setopt($curl, CURLOPT_SSL_VERIFYHOST,false);

    $response = curl_exec($curl);
    //$result = json_decode($response, true);

    curl_close($curl);
    return $response;
}

function searchModel($data,$authorization)
{
    $curl = curl_init();

    curl_setopt($curl, CURLOPT_URL, "http://localhost:9098/gml/".$data["companyid"]."/model/search?userid=".$data["userid"]."&query=".$data["query"]);
    curl_setopt($curl, CURLOPT_CUSTOMREQUEST, 'GET');
 	curl_setopt($curl, CURLOPT_HTTPHEADER, array('Authorization: '.$authorization));
    curl_setopt($curl, CURLOPT_RETURNTRANSFER,true);
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
        training($data_request,$request->headers->get("Authorization"))
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
               test($data_request,$request->headers->get("Authorization"))
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
               saveModel($data_request,$request->headers->get("Authorization"))
     ,
     true);

     return $GMLService->json(array(
                   "success"=>true,
                   "body" =>$decodeJSON,
                   "request"=>$data_request),201);
});

$GMLService->get('/gml/model/list', function (Request $request) use ($GMLService) {
     mb_internal_encoding('UTF-8');

     $data_request = array();

     foreach ( $request->query->keys() as $key){
        $data_request[$key] = $request->query->get($key);
     }

     $decodeJSON = json_decode(
        listModel($data_request,$request->headers->get("Authorization"))
     ,
     true);

     return $GMLService->json(array(
                   "success"=>true,
                   "body" =>$decodeJSON,
                   "request"=>$data_request,
                   ),201);
});

$GMLService->get('/gml/model', function (Request $request) use ($GMLService) {

     mb_internal_encoding('UTF-8');
     $data_request = array();

     foreach ( $request->query->keys() as $key){
        $data_request[$key] = $request->query->get($key);
     }

     $decodeJSON = json_decode(
        getModel($data_request,$request->headers->get("Authorization"))
     ,
     true);

     return $GMLService->json(array(
                   "success"=>true,
                   "body" =>$decodeJSON,
                   "request"=>$data_request),201);
});

$GMLService->get('/gml/model/search', function (Request $request) use ($GMLService) {

     mb_internal_encoding('UTF-8');
     $data_request = array();

     foreach ( $request->query->keys() as $key){
        $data_request[$key] = $request->query->get($key);
     }

     $decodeJSON = json_decode(
               searchModel($data_request,$request->headers->get("Authorization"))
     ,
     true);

     return $GMLService->json(array(
                   "success"=>true,
                   "body" =>$decodeJSON,
                   "request"=>$data_request),201);
});

$GMLService->get('/gml/model/test/history/list', function (Request $request) use ($GMLService) {

     mb_internal_encoding('UTF-8');
     $data_request = array();

     foreach ( $request->query->keys() as $key){
        $data_request[$key] = $request->query->get($key);
     }
     $decodeJSON = json_decode(
               getTestHistoryList($data_request,$request->headers->get("Authorization"))
     ,
     true);

     return $GMLService->json(array(
                   "success"=>true,
                   "body" =>$decodeJSON,
                   "request"=>$data_request),201);
});

$GMLService->get('/gml/model/test/history', function (Request $request) use ($GMLService) {
     mb_internal_encoding('UTF-8');
     $data_request = array();

     foreach ( $request->query->keys() as $key){
        $data_request[$key] = $request->query->get($key);
     }

     $decodeJSON = json_decode(
               getTestHistoryModel($data_request,$request->headers->get("Authorization"))
     ,
     true);

     return $GMLService->json(array(
                   "success"=>true,
                   "body" =>$decodeJSON,
                   "request"=>$data_request),201);
});

$GMLService->run();
?>