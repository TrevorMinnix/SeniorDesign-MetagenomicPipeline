<?php
$con = mysqli_connect("localhost", "root", "student", "pipeline");

if(!$con){
  echo "Error: Unable to connect to MySQL." . PHP_EOL;
  echo "Debugging errno: " . mysqli_connect_errno() . PHP_EOL;
  echo "Debugging error: " . mysqli_connect_error() . PHP_EOL;
  exit;
}

//echo "Success!" . PHP_EOL;
//echo "Host information: " . mysqli_get_host_info($con) . PHP_EOL;
?>
