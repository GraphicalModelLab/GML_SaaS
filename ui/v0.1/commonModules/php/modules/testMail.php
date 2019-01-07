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

    $mailto = "shimane_mao@yahoo.co.jp";
    $config = parse_ini_file(__DIR__."/../config/MailApp.ini");
    $from  = $config["mailfrom"];
    $replyTo = $from;

    $headers = "From: " . $from . " \r\n";
    $headers .= "Reply-To: ". $replyTo . " \r\n";
    $headers .= "MIME-Version: 1.0\r\n";
    $headers .= "Content-Type: text/html; charset=iso-2022-jp\r\n";


    $html_content = '<h>test</h>';

    $subject = "test";
    if(mb_send_mail($mailto, $subject, $html_content, $headers)){
        echo 'success to send an e-mail';

    }else{
        echo 'fail to send an e-mail';
    }
?>