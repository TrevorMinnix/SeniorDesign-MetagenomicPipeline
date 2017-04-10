<?php
include "mysqli_con.php";
 
//get uuid
$jobID = uniqid("", true);
echo $jobID;

//get check box values
$idbaCheck = $_POST[idbaCheck] ? 1 : 0;
$megahitCheck = $_POST[megahitCheck] ? 1 : 0;
$metaspadesCheck = $_POST[metaspadesCheck] ? 1 : 0;

//get radio button value
$pairedEnd = 0;
if($_POST['end'] == "paired-end"){
	$pairedEnd = 1;
}

//sql query
//single end
if($pairedEnd == 0){
	$query = "INSERT INTO job (jobID, email, inputForward, idba, megahit, metaspades, pairedEnd, jobStatus) VALUES ('{$jobID}', '{$_POST['email']}', '{$_FILES['my_file']['name']}', '{$idbaCheck}', '{$megahitCheck}', '{$metaspadesCheck}', '{$pairedEnd}', '1')";
} 
//paired end
else{
	$query = "INSERT INTO job (jobID, email, inputForward, inputReverse, idba, megahit, metaspades, pairedEnd, jobStatus) VALUES ('{$jobID}', '{$_POST['email']}', '{$_FILES['fmy_file']['name']}', '{$_FILES['rmy_file']['name']}', '{$idbaCheck}', '{$megahitCheck}', '{$metaspadesCheck}', '{$pairedEnd}', '1')";
}

echo $query;

$con->query($query);

$con->close();

$chunk = isset($_REQUEST["chunk"]) ? intval($_REQUEST["chunk"]) : 0;
$chunks = isset($_REQUEST["chunks"]) ? intval($_REQUEST["chunks"]) : 0;

$target_dir ="/home/student/SeniorDesign-MetagenomicPipeline/Jobs/";
$target_file = $target_dir . basename($_FILES["my_file"]["name"]); 
$uploadOk = 1;

if (file_exists($target_file)) {
    echo "Sorry, file already exists.";
    $uploadOk = 0;
}
// Check if $uploadOk is set to 0 by an error
if ($uploadOk == 0) {
    echo "Sorry, your file was not uploaded.";
// if everything is ok, try to upload file
} else {
    if (move_uploaded_file($_FILES["my_file"]["tmp_name"], $target_file)) {
        echo "The file ". basename( $_FILES["my_file"]["name"]). " has been uploaded.";
    } else {
        echo "Sorry, there was an error uploading your file.";
    }
}

?>
