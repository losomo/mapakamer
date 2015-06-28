<?php
  #$jsonurl = "http://open.mapquestapi.com/api/xapi/0.6/".$_SERVER['QUERY_STRING'];
  $jsonurl = "http://www.overpass-api.de/api/xapi_meta?".$_SERVER['QUERY_STRING'];

  $ch = curl_init();
  curl_setopt($ch, CURLOPT_URL, $jsonurl);
  curl_setopt($ch, CURLOPT_RETURNTRANSFER, true );
  $result = curl_exec($ch);
  if( !$result ) {
   die('Error: "' . curl_error($ch) . '" - Code: ' . curl_errno($ch));
  }
  curl_close($ch);
  #echo "Sent to " . $jsonurl . ", got " . $result;
  echo $result;
?>
