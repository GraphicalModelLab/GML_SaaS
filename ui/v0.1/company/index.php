<?php

$initialization="";
if(isset($_GET["userid"]) && isset($_GET["token"])){
    $initialization =
        'localStorage.token="'.$_GET["token"].'";'
       .'localStorage.userid="'.$_GET["userid"].'";';
}

if(isset($_GET["companyid"])){
    $initialization = $initialization.'var companyid="'.$_GET["companyid"].'";';
}

if(isset($_GET["role"])){
    $initialization = $initialization.'var role="'.$_GET["role"].'"; var autocompleteLanguage=[]; var autocompleteServeros=[]; var autocompleteOs = []; var autocompleteFrameworkmiddleware = []; var autocompleteDatabase = []; var autocompleteNetwork = []; var autocompleteDesignerSoftware=[]; var autocompleteContractForm = [];';
}else{
    $initialization = $initialization.'var role=""; var autocompleteLanguage=[]; var autocompleteServeros=[]; var autocompleteOs = []; var autocompleteFrameworkmiddleware = []; var autocompleteDatabase = []; var autocompleteNetwork = [];  var autocompleteDesignerSoftware=[]; var autocompleteContractForm = [];';
}

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
'
.$initialization.
'
</script>
<script src="./../dist/bundleCompanyDev.js"></script>
</body>
</html>
';
?>