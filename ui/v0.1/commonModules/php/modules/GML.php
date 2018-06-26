<?php
require_once __DIR__.'/../../../commonLibs/silex/vendor/autoload.php';

use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

$GMLService = new Silex\Application();

function training($data)
{
    $curl = curl_init();

    curl_setopt($curl, CURLOPT_URL, "http://localhost:9098/gml/".$data["companyid"]."/training");
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
                   "body" =>$data_request,
                   "request"=>$data_request),201);
});

$GMLService->run();
?>