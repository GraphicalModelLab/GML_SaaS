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

$initialization="";
if(isset($_GET["userid"]) && isset($_GET["token"])){
    $initialization =
        'localStorage.token="'.$_GET["token"].'";'
       .'localStorage.userid="'.$_GET["userid"].'";';
}

if(isset($_GET["companyid"])){
    $initialization = $initialization.'var companyid="'.$_GET["companyid"].'";';
}

$initialization = $initialization.'var role="'.$_GET["role"].'";';

echo '
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
	<meta name="description" content=""/>
	<meta name="keywords" content="Graphical Model, Machine Learning, AI"/>
	<meta name="DC.Creator" content="伊藤真央, Mao Ito"/>
	<meta name="DC.Language" content="ja"/>
	<meta name="DC.Title" content="Graphical Model Lab"/>
	<title>Graphical Model Lab</title>
</head>
<body>
<div id="app">
</div>
<script type="text/javascript">
// This is to avoid the issue of Facebook Federated Login where "#_=_" is added to the url.
if (window.location.hash && window.location.hash == "#_=_") {
        window.location.hash = "";
}
'
.$initialization.
'
</script>
<script src="./../dist/bundleCompanyDev.js"></script>
</body>
</html>
';
?>