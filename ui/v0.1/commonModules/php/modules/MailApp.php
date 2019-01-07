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

$emailApp = new Silex\Application();

function senGridMail($APIKey,$to,$from,$subject,$replyto,$content){
    $js = array(
      'sub' => array(':name' => array('Elmer')),
    );
    $url = 'https://api.sendgrid.com/';

    $params = array(
        'to'        => $to,
        'toname'    => "to Shimane",
        'from'      => $from,
        'fromname'  => "Graphical ModelLab",
        'subject'   => $subject,
        'html'      => $content,
        'x-smtpapi' => json_encode($js),
      );

    if($replyto != null) $params["replyto"] = $replyto;

    $request =  $url.'api/mail.send.json';

    // Generate curl request
    $session = curl_init($request);
    // Tell PHP not to use SSLv3 (instead opting for TLS)
    //curl_setopt($session, CURLOPT_SSLVERSION, CURL_SSLVERSION_TLSv1_2);
    curl_setopt($session, CURLOPT_HTTPHEADER, array('Authorization: Bearer ' . $APIKey));
    // Tell curl to use HTTP POST
    curl_setopt ($session, CURLOPT_POST, true);
    // Tell curl that this is the body of the POST
    curl_setopt ($session, CURLOPT_POSTFIELDS, $params);
    // Tell curl not to return headers, but do return the response
    curl_setopt($session, CURLOPT_HEADER, false);
    curl_setopt($session, CURLOPT_RETURNTRANSFER, true);

    // obtain response
    $response = curl_exec($session);
    curl_close($session);

    // print everything out
    return $response;
}
$emailApp->post('/sendMail', function (Request $request) use ($emailApp) {
    $data_request = json_decode(file_get_contents("php://input"),true);

    $config = parse_ini_file(__DIR__."/../config/MailApp.ini");

    mb_internal_encoding('UTF-8');

    $mailto = $data_request["mailto"];
    $subject = $data_request['subject'];

    $from  = $config["mailfrom"];
    $replyTo = $from;

    $headers = "From: " . $from . " \r\n";
    $headers .= "Reply-To: ". $replyTo . " \r\n";
    $headers .= "MIME-Version: 1.0\r\n";
    $headers .= "Content-Type: text/html; charset=iso-2022-jp\r\n";


    $url = $config["registrationCallBackDomain"]."/commonModules/php/modules/Auth.php/auth/validation?validationCode=".$data_request["codeValue"]."&email=".$data_request["email"]."&companyid=".$data_request["companyid"]."&type=".$data_request["type"];
    $html_content = '<table>'.
                        '<tr> <td align="center" style="text-decoration: underline;">Graphical Model Lab</td></tr>'.
                        '<tr> <td> <br>Thank you for the registration ! Please click the below URL to complete the registration:<br><br> <a href="'.$url.'">Complete Registration</a><br><br></td></tr>'.
                        '<tr> <td align="center" bgcolor="#DCDCDC" style="Font-family: Arial, sans-serif;"> Graphical Model Lab</td> </tr>'.
                    '</table>';


    $result = mb_send_mail($mailto, $subject, $html_content, $headers);

    return $emailApp->json(array(
          "success"=>true,
          "mailto"=>$mailto,
          "subject"=>$subject,
          "content"=>$html_content,
          "from" => $from),201);
});

$emailApp->post('/sendRegistrationMailBySendGrid', function (Request $request) use ($emailApp) {
     $data_request = json_decode(file_get_contents("php://input"),true);

     $config = parse_ini_file(__DIR__."/../config/MailApp.ini");

    $url = $config["registrationCallBackDomain"]."/commonModules/php/modules/Auth.php/auth/validation?validationCode=".$data_request["codeValue"]."&email=".$data_request["email"]."&companyid=".$data_request["companyid"];
    $html_content = '<table>'.
                        '<tr> <td align="center" style="text-decoration: underline;">Graphical Model Lab</td></tr>'.
                        '<tr> <td> <br>Thank you for the registration ! Please click the below URL to complete the registration:<br><br> <a href="'.$url.'">Complete Registration</a><br><br></td></tr>'.
                        '<tr> <td align="center" bgcolor="#DCDCDC" style="Font-family: Arial, sans-serif;"> Graphical Model Lab</td> </tr>'.
                    '</table>';

     return $emailApp->json(
        array(
            "success" => true,
            "sendgridResponse" => json_decode(senGridMail($config["sendGridAPIKey"], $data_request["mailto"], $config["mailfrom"], $data_request['subject'], null,$html_content),true)
        ),201);
});

$emailApp->run();
?>