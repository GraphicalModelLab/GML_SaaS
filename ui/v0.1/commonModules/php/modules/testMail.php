<?php

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