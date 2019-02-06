<?php
/**
 * Copyright (C) 2018 Mao Ito
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

require_once __DIR__.'/../../../commonLibs/silex/vendor/autoload.php';

use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

$GMLService = new Silex\Application();

function warmup()
{
    $gml_config = parse_ini_file(__DIR__."/../config/GML.ini",true);
    $gml_host_string = $gml_config["gml"]["host"].":".$gml_config["gml"]["port"];

    $curl = curl_init();

    curl_setopt($curl, CURLOPT_URL, "http://".$gml_host_string."/helloworld");
    curl_setopt($curl, CURLOPT_CUSTOMREQUEST, 'GET');
 	curl_setopt($curl, CURLOPT_HTTPHEADER, array('Authorization: '.$authorization));
    curl_setopt($curl, CURLOPT_RETURNTRANSFER,true);
    curl_setopt($curl, CURLOPT_SSL_VERIFYHOST,false);

    $response = curl_exec($curl);
    //$result = json_decode($response, true);

    curl_close($curl);
    return $response;
}

function getTestHistoryList($data,$authorization)
{
    $gml_config = parse_ini_file(__DIR__."/../config/GML.ini",true);
    $gml_host_string = $gml_config["gml"]["host"].":".$gml_config["gml"]["port"];

    $curl = curl_init();

    curl_setopt($curl, CURLOPT_URL, "http://".$gml_host_string."/gml/".$data["companyid"]."/model/test/history/list?userid=".$data["userid"]."&model_userid=".$data["model_userid"]."&modelid=".$data["modelid"]);
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
    $gml_config = parse_ini_file(__DIR__."/../config/GML.ini",true);
    $gml_host_string = $gml_config["gml"]["host"].":".$gml_config["gml"]["port"];

    $curl = curl_init();

    curl_setopt($curl, CURLOPT_URL, "http://".$gml_host_string."/gml/".$data["companyid"]."/model/training");
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
    $gml_config = parse_ini_file(__DIR__."/../config/GML.ini",true);
    $gml_host_string = $gml_config["gml"]["host"].":".$gml_config["gml"]["port"];

    $curl = curl_init();

    curl_setopt($curl, CURLOPT_URL, "http://".$gml_host_string."/gml/".$data["companyid"]."/model/test");
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

function exploreGraph($data,$authorization)
{
    $gml_config = parse_ini_file(__DIR__."/../config/GML.ini",true);
    $gml_host_string = $gml_config["gml"]["host"].":".$gml_config["gml"]["port"];

    $curl = curl_init();

    curl_setopt($curl, CURLOPT_URL, "http://".$gml_host_string."/gml/".$data["companyid"]."/model/graph/explore");
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
    $gml_config = parse_ini_file(__DIR__."/../config/GML.ini",true);
    $gml_host_string = $gml_config["gml"]["host"].":".$gml_config["gml"]["port"];

    $curl = curl_init();

    curl_setopt($curl, CURLOPT_URL, "http://".$gml_host_string."/gml/".$data["companyid"]."/model/save");
    curl_setopt($curl, CURLOPT_CUSTOMREQUEST, 'POST');
 	curl_setopt($curl, CURLOPT_HTTPHEADER, array('Content-Type: application/json','Authorization: '.$authorization));
    curl_setopt($curl, CURLOPT_RETURNTRANSFER,true);
 	curl_setopt($curl, CURLOPT_POSTFIELDS, json_encode($data));
    curl_setopt($curl, CURLOPT_SSL_VERIFYHOST,false);

    $response = curl_exec($curl);

    curl_close($curl);
    return $response;
}

function listModel($data,$authorization)
{
    $gml_config = parse_ini_file(__DIR__."/../config/GML.ini",true);
    $gml_host_string = $gml_config["gml"]["host"].":".$gml_config["gml"]["port"];

    $curl = curl_init();

    curl_setopt($curl, CURLOPT_URL, "http://".$gml_host_string."/gml/".$data["companyid"]."/model/list?userid=".$data["userid"]);
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
    $gml_config = parse_ini_file(__DIR__."/../config/GML.ini",true);
    $gml_host_string = $gml_config["gml"]["host"].":".$gml_config["gml"]["port"];

    $curl = curl_init();

    curl_setopt($curl, CURLOPT_URL, "http://".$gml_host_string."/gml/".$data["companyid"]."/model?userid=".$data["userid"]."&modelid=".$data["modelid"]);
    curl_setopt($curl, CURLOPT_CUSTOMREQUEST, 'GET');
 	curl_setopt($curl, CURLOPT_HTTPHEADER, array('Authorization: '.$authorization));
    curl_setopt($curl, CURLOPT_RETURNTRANSFER,true);
    curl_setopt($curl, CURLOPT_SSL_VERIFYHOST,false);

    $response = curl_exec($curl);
    //$result = json_decode($response, true);

    curl_close($curl);
    return $response;
}

function getModelParameter($data,$authorization)
{
    $gml_config = parse_ini_file(__DIR__."/../config/GML.ini",true);
    $gml_host_string = $gml_config["gml"]["host"].":".$gml_config["gml"]["port"];

    $curl = curl_init();

    curl_setopt($curl, CURLOPT_URL, "http://".$gml_host_string."/gml/".$data["companyid"]."/model/parameter?userid=".$data["userid"]."&algorithm=".$data["algorithm"]);
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
    $gml_config = parse_ini_file(__DIR__."/../config/GML.ini",true);
    $gml_host_string = $gml_config["gml"]["host"].":".$gml_config["gml"]["port"];

    $curl = curl_init();

    curl_setopt($curl, CURLOPT_URL, "http://".$gml_host_string."/gml/".$data["companyid"]."/model/test/history?userid=".$data["userid"]."&modelid=".$data["modelid"]."&datetime=".$data["datetime"]);
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
    $gml_config = parse_ini_file(__DIR__."/../config/GML.ini",true);
    $gml_host_string = $gml_config["gml"]["host"].":".$gml_config["gml"]["port"];

    $curl = curl_init();

    curl_setopt($curl, CURLOPT_URL, "http://".$gml_host_string."/gml/".$data["companyid"]."/model/search?userid=".$data["userid"]."&query=".$data["query"]);
    curl_setopt($curl, CURLOPT_CUSTOMREQUEST, 'GET');
 	curl_setopt($curl, CURLOPT_HTTPHEADER, array('Authorization: '.$authorization));
    curl_setopt($curl, CURLOPT_RETURNTRANSFER,true);
    curl_setopt($curl, CURLOPT_SSL_VERIFYHOST,false);

    $response = curl_exec($curl);
    //$result = json_decode($response, true);

    curl_close($curl);
    return $response;
}

function getListOfAlgorithm($data,$authorization)
{
    $gml_config = parse_ini_file(__DIR__."/../config/GML.ini",true);
    $gml_host_string = $gml_config["gml"]["host"].":".$gml_config["gml"]["port"];

    $curl = curl_init();

    curl_setopt($curl, CURLOPT_URL, "http://".$gml_host_string."/gml/".$data["companyid"]."/model/algorithm/list?userid=".$data["userid"]);
    curl_setopt($curl, CURLOPT_CUSTOMREQUEST, 'GET');
 	curl_setopt($curl, CURLOPT_HTTPHEADER, array('Authorization: '.$authorization));
    curl_setopt($curl, CURLOPT_RETURNTRANSFER,true);
    curl_setopt($curl, CURLOPT_SSL_VERIFYHOST,false);

    $response = curl_exec($curl);
    //$result = json_decode($response, true);

    curl_close($curl);
    return $response;
}

function getListOfExtractor($data,$authorization)
{
    $gml_config = parse_ini_file(__DIR__."/../config/GML.ini",true);
    $gml_host_string = $gml_config["gml"]["host"].":".$gml_config["gml"]["port"];

    $curl = curl_init();

    curl_setopt($curl, CURLOPT_URL, "http://".$gml_host_string."/gml/".$data["companyid"]."/data/extractor/list");
    curl_setopt($curl, CURLOPT_CUSTOMREQUEST, 'GET');
 	curl_setopt($curl, CURLOPT_HTTPHEADER, array('Authorization: '.$authorization));
    curl_setopt($curl, CURLOPT_RETURNTRANSFER,true);
    curl_setopt($curl, CURLOPT_SSL_VERIFYHOST,false);

    $response = curl_exec($curl);
    //$result = json_decode($response, true);

    curl_close($curl);
    return $response;
}

function executeExtractor($data,$authorization)
{
    $gml_config = parse_ini_file(__DIR__."/../config/GML.ini",true);
    $gml_host_string = $gml_config["gml"]["host"].":".$gml_config["gml"]["port"];

    $curl = curl_init();

    curl_setopt($curl, CURLOPT_URL, "http://".$gml_host_string."/gml/".$data["companyid"]."/data/extractor");
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

function getListOfDataCrawlerSearchEngine($data,$authorization)
{
    $gml_config = parse_ini_file(__DIR__."/../config/GML.ini",true);
    $gml_host_string = $gml_config["gml"]["host"].":".$gml_config["gml"]["port"];

    $curl = curl_init();

    curl_setopt($curl, CURLOPT_URL, "http://".$gml_host_string."/gml/".$data["companyid"]."/data/crawlersearchengine/list");
    curl_setopt($curl, CURLOPT_CUSTOMREQUEST, 'GET');
 	curl_setopt($curl, CURLOPT_HTTPHEADER, array('Authorization: '.$authorization));
    curl_setopt($curl, CURLOPT_RETURNTRANSFER,true);
    curl_setopt($curl, CURLOPT_SSL_VERIFYHOST,false);

    $response = curl_exec($curl);
    //$result = json_decode($response, true);

    curl_close($curl);
    return $response;
}

function executeCrawlerSearchEngine($data,$authorization)
{
    $gml_config = parse_ini_file(__DIR__."/../config/GML.ini",true);
    $gml_host_string = $gml_config["gml"]["host"].":".$gml_config["gml"]["port"];

    $curl = curl_init();

    curl_setopt($curl, CURLOPT_URL, "http://".$gml_host_string."/gml/".$data["companyid"]."/data/crawlersearchengine");
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

function executeCrawlerScrapingEngine($data,$authorization)
{
    $gml_config = parse_ini_file(__DIR__."/../config/GML.ini",true);
    $gml_host_string = $gml_config["gml"]["host"].":".$gml_config["gml"]["port"];

    $curl = curl_init();

    curl_setopt($curl, CURLOPT_URL, "http://".$gml_host_string."/gml/".$data["companyid"]."/data/crawlerscrapingengine");
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

function executeCrawlerEngine($data,$authorization)
{
    $gml_config = parse_ini_file(__DIR__."/../config/GML.ini",true);
    $gml_host_string = $gml_config["gml"]["host"].":".$gml_config["gml"]["port"];

    $curl = curl_init();

    curl_setopt($curl, CURLOPT_URL, "http://".$gml_host_string."/gml/".$data["companyid"]."/data/crawlerengine");
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

function executeHtmlConverterEngine($data,$authorization)
{
    $gml_config = parse_ini_file(__DIR__."/../config/GML.ini",true);
    $gml_host_string = $gml_config["gml"]["host"].":".$gml_config["gml"]["port"];

    $curl = curl_init();

    curl_setopt($curl, CURLOPT_URL, "http://".$gml_host_string."/gml/".$data["companyid"]."/data/htmlconverterengine");
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

function getListOfDataCrawlerScrapingEngine($data,$authorization)
{
    $gml_config = parse_ini_file(__DIR__."/../config/GML.ini",true);
    $gml_host_string = $gml_config["gml"]["host"].":".$gml_config["gml"]["port"];

    $curl = curl_init();

    curl_setopt($curl, CURLOPT_URL, "http://".$gml_host_string."/gml/".$data["companyid"]."/data/crawlerscrapingengine/list");
    curl_setopt($curl, CURLOPT_CUSTOMREQUEST, 'GET');
 	curl_setopt($curl, CURLOPT_HTTPHEADER, array('Authorization: '.$authorization));
    curl_setopt($curl, CURLOPT_RETURNTRANSFER,true);
    curl_setopt($curl, CURLOPT_SSL_VERIFYHOST,false);

    $response = curl_exec($curl);
    //$result = json_decode($response, true);

    curl_close($curl);
    return $response;
}

function getListOfDataCrawlerEngine($data,$authorization)
{
    $gml_config = parse_ini_file(__DIR__."/../config/GML.ini",true);
    $gml_host_string = $gml_config["gml"]["host"].":".$gml_config["gml"]["port"];

    $curl = curl_init();

    curl_setopt($curl, CURLOPT_URL, "http://".$gml_host_string."/gml/".$data["companyid"]."/data/crawlerengine/list");
    curl_setopt($curl, CURLOPT_CUSTOMREQUEST, 'GET');
 	curl_setopt($curl, CURLOPT_HTTPHEADER, array('Authorization: '.$authorization));
    curl_setopt($curl, CURLOPT_RETURNTRANSFER,true);
    curl_setopt($curl, CURLOPT_SSL_VERIFYHOST,false);

    $response = curl_exec($curl);
    //$result = json_decode($response, true);

    curl_close($curl);
    return $response;
}

function getListOfHtmlConverterEngine($data,$authorization)
{
    $gml_config = parse_ini_file(__DIR__."/../config/GML.ini",true);
    $gml_host_string = $gml_config["gml"]["host"].":".$gml_config["gml"]["port"];

    $curl = curl_init();

    curl_setopt($curl, CURLOPT_URL, "http://".$gml_host_string."/gml/".$data["companyid"]."/data/htmlconverterengine/list");
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

$GMLService->post('/gml/graph/explore', function (Request $request) use ($GMLService) {
     $data_request = json_decode(file_get_contents("php://input"),true);

     mb_internal_encoding('UTF-8');

     $decodeJSON = json_decode(
               exploreGraph($data_request,$request->headers->get("Authorization"))
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
     ,true);

     return $GMLService->json(array(
                   "success"=>true,
                   "body" =>$decodeJSON,
                   "request"=>$data_request,
                   "url" => "http://localhost:9098/gml/".$data_request["companyid"]."/model/save",
                   "authorization"=>$request->headers->get("Authorization")),201);
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

$GMLService->get('/gml/model/parameter', function (Request $request) use ($GMLService) {

     mb_internal_encoding('UTF-8');
     $data_request = array();

     foreach ( $request->query->keys() as $key){
        $data_request[$key] = $request->query->get($key);
     }

     $decodeJSON = json_decode(
        getModelParameter($data_request,$request->headers->get("Authorization"))
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

$GMLService->get('/gml/model/algorithm/list', function (Request $request) use ($GMLService) {
     mb_internal_encoding('UTF-8');
     $data_request = array();

     foreach ( $request->query->keys() as $key){
        $data_request[$key] = $request->query->get($key);
     }

     $decodeJSON = json_decode(
               getListOfAlgorithm($data_request,$request->headers->get("Authorization"))
     ,
     true);

     return $GMLService->json(array(
                   "success"=>true,
                   "body" =>$decodeJSON,
                   "request"=>$data_request),201);
});

$GMLService->get('/gml/data/extractor/list', function (Request $request) use ($GMLService) {
     mb_internal_encoding('UTF-8');
     $data_request = array();

     foreach ( $request->query->keys() as $key){
        $data_request[$key] = $request->query->get($key);
     }

     $decodeJSON = json_decode(
               getListOfExtractor($data_request,$request->headers->get("Authorization"))
     ,
     true);

     return $GMLService->json(array(
                   "success"=>true,
                   "body" =>$decodeJSON,
                   "request"=>$data_request),201);
});

$GMLService->post('/gml/data/extractor', function (Request $request) use ($GMLService) {
     $data_request = json_decode(file_get_contents("php://input"),true);

     mb_internal_encoding('UTF-8');

     $decodeJSON = json_decode(
               executeExtractor($data_request,$request->headers->get("Authorization"))
     ,
     true);

     return $GMLService->json(array(
                   "success"=>true,
                   "body" =>$decodeJSON,
                   "request"=>$data_request),201);
});

$GMLService->get('/gml/warmup', function (Request $request) use ($GMLService) {
     mb_internal_encoding('UTF-8');

     $content = warmup();

     return $GMLService->json(array(
                   "success"=>true,
                   "body" => $content
     ),201);
});

$GMLService->get('/gml/data/crawlersearchengine/list', function (Request $request) use ($GMLService) {
     mb_internal_encoding('UTF-8');
     $data_request = array();

     foreach ( $request->query->keys() as $key){
        $data_request[$key] = $request->query->get($key);
     }

     $decodeJSON = json_decode(
        getListOfDataCrawlerSearchEngine($data_request,$request->headers->get("Authorization"))
     ,
     true);

     return $GMLService->json(array(
                   "success"=>true,
                   "body" =>$decodeJSON,
                   "request"=>$data_request),201);
});
$GMLService->post('/gml/data/crawlersearchengine', function (Request $request) use ($GMLService) {
     $data_request = json_decode(file_get_contents("php://input"),true);

     mb_internal_encoding('UTF-8');

     $decodeJSON = json_decode(
               executeCrawlerSearchEngine($data_request,$request->headers->get("Authorization"))
     ,
     true);

     return $GMLService->json(array(
                   "success"=>true,
                   "body" =>$decodeJSON,
                   "request"=>$data_request),201);
});
$GMLService->get('/gml/data/crawlerscrapingengine/list', function (Request $request) use ($GMLService) {
     mb_internal_encoding('UTF-8');
     $data_request = array();

     foreach ( $request->query->keys() as $key){
        $data_request[$key] = $request->query->get($key);
     }

     $decodeJSON = json_decode(
        getListOfDataCrawlerScrapingEngine($data_request,$request->headers->get("Authorization"))
     ,
     true);

     return $GMLService->json(array(
                   "success"=>true,
                   "body" =>$decodeJSON,
                   "request"=>$data_request),201);
});

$GMLService->post('/gml/data/crawlerscrapingengine', function (Request $request) use ($GMLService) {
     $data_request = json_decode(file_get_contents("php://input"),true);

     mb_internal_encoding('UTF-8');

     $decodeJSON = json_decode(
               executeCrawlerScrapingEngine($data_request,$request->headers->get("Authorization"))
     ,
     true);

     return $GMLService->json(array(
                   "success"=>true,
                   "body" =>$decodeJSON,
                   "request"=>$data_request),201);
});

$GMLService->get('/gml/data/crawlerengine/list', function (Request $request) use ($GMLService) {
     mb_internal_encoding('UTF-8');
     $data_request = array();

     foreach ( $request->query->keys() as $key){
        $data_request[$key] = $request->query->get($key);
     }

     $decodeJSON = json_decode(
        getListOfDataCrawlerEngine($data_request,$request->headers->get("Authorization"))
     ,
     true);

     return $GMLService->json(array(
                   "success"=>true,
                   "body" =>$decodeJSON,
                   "request"=>$data_request),201);
});

$GMLService->post('/gml/data/crawlerengine', function (Request $request) use ($GMLService) {
     $data_request = json_decode(file_get_contents("php://input"),true);

     mb_internal_encoding('UTF-8');

     $decodeJSON = json_decode(
               executeCrawlerEngine($data_request,$request->headers->get("Authorization"))
     ,
     true);

     return $GMLService->json(array(
                   "success"=>true,
                   "body" =>$decodeJSON,
                   "request"=>$data_request),201);
});

$GMLService->get('/gml/data/htmlconverterengine/list', function (Request $request) use ($GMLService) {
     mb_internal_encoding('UTF-8');
     $data_request = array();

     foreach ( $request->query->keys() as $key){
        $data_request[$key] = $request->query->get($key);
     }

     $decodeJSON = json_decode(
        getListOfHtmlConverterEngine($data_request,$request->headers->get("Authorization"))
     ,
     true);

     return $GMLService->json(array(
                   "success"=>true,
                   "body" =>$decodeJSON,
                   "request"=>$data_request),201);
});

$GMLService->post('/gml/data/htmlconverterengine', function (Request $request) use ($GMLService) {
     $data_request = json_decode(file_get_contents("php://input"),true);

     mb_internal_encoding('UTF-8');

     $decodeJSON = json_decode(
               executeHtmlConverterEngine($data_request,$request->headers->get("Authorization"))
     ,
     true);

     return $GMLService->json(array(
                   "success"=>true,
                   "body" =>$decodeJSON,
                   "request"=>$data_request),201);
});

$GMLService->run();
?>