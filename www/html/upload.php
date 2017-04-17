
<?php
include "mysqli_con.php";
 
//get uuid
$jobID = str_replace(".", "-", uniqid("", true));

//get check box values
$idbaCheck = $_POST['idbaCheck'] ? 1 : 0;
$megahitCheck = $_POST['megahitCheck'] ? 1 : 0;
$metaspadesCheck = $_POST['metaspadesCheck'] ? 1 : 0;

if(isset($_POST['idba'])){
	$idbaCheck = 1;
}else{
	$idbaCheck = 0;
}

if(isset($_POST['megahit'])){
	$megahitCheck = 1;
}else{
	$megahitCheck = 0;
}

if(isset($_POST['metaspades'])){
	$metaspadesCheck = 1;
}else{
	$metaspadesCheck = 0;
}
//get radio button value
$pairedEnd = 0;
if(isset($_POST['end'])){
	echo "Paired end radio set";
}else{
	echo "Radio button not working";
}
if($_POST['end'] == "paired-end"){
	$pairedEnd = 1;
}

//sql query
//single end
if($pairedEnd == 0){
	$query = "INSERT INTO job (jobID, email, inputForward, idba, megahit, metaspades, pairedEnd, jobStatus) VALUES ('{$jobID}', '{$_POST['email']}', '{$_FILES['my_file']['name']}', '{$idbaCheck}', '{$megahitCheck}', '{$metaspadesCheck}', '{$pairedEnd}', '0')";
} 
//paired end
else{
	$query = "INSERT INTO job (jobID, email, inputForward, inputReverse, idba, megahit, metaspades, pairedEnd, jobStatus) VALUES ('{$jobID}', '{$_POST['email']}', '{$_FILES['fmy_file']['name']}', '{$_FILES['rmy_file']['name']}', '{$idbaCheck}', '{$megahitCheck}', '{$metaspadesCheck}', '{$pairedEnd}', '0')";
}

echo $query;

$con->query($query);

mkdir("/home/student/SeniorDesign-MetagenomicPipeline/Jobs/" . $jobID . "/");
mkdir("home/student/SeniorDesign-MetagenomicPipeline/Jobs/" . $jobID . "/" . "IDBA". "/");
mkdir("home/student/SeniorDesign-MetagenomicPipeline/Jobs/" . $jobID . "/" . "MEGAHIT". "/");
mkdir("home/student/SeniorDesign-MetagenomicPipeline/Jobs/" . $jobID . "/" . "MetaSPAdes". "/");
$target_dir = "/home/student/SeniorDesign-MetagenomicPipeline/Jobs/" . $jobID . "/";

//mkdir($target_dir . "IDBA". "/");
//mkdir($target_dir . "MEGAHIT". "/");
//mkdir($target_dir . "MetaSPAdes". "/");

$uploadOk = 1;

if($pairedEnd == 0){
	$target_file = $target_dir . basename($_FILES["my_file"]["name"]); 

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
			 $con->query("UPDATE job SET jobStatus = '1' WHERE jobID = '{$jobID}'");
   		 } else {
       			 echo "Sorry, there was an error uploading your file.";
    		   }
	  }

	$new_dir = $target_dir . "/" . "input_SE.fq";

	$fileHand = fopen($target_file, 'r');
	fclose($fileHand);
	rename($target_file, $new_dir);
}

else{

	$f_target_file = $target_dir . basename($_FILES["fmy_file"]["name"]);
	$r_target_file = $target_dir . basename($_FILES["rmy_file"]["name"]);

	if(file_exists($f_target_file) || file_exists($r_target_file)){
		echo "Sorry, 1 or more files already exist.";
		$uploadOk = 0;
	}
	
	if($uploadOk == 0){
		echo "Sorry, your files were not uploaded.";
	}

	else{
		if(move_uploaded_file($_FILES["fmy_file"]["tmp_name"], $f_target_file) && move_uploaded_file($_FILES["rmy_file"]["tmp_name"], $r_target_file)){
			echo "The files ". basename($_FILES["fmy_file"]["name"]). "and ". basename($_FILES["rmy_file"]["name"]). "have been uploaded.";
            //update job status
            $con->query("UPDATE job SET jobStatus = '1' WHERE jobID = '{$jobID}'");
		}

	      else{
			echo "Sorry, there was an error uploading 1 or more files.";
	     }
	}

	$f_new_dir = $target_dir . "/" . "input_forward.fq";
	$r_new_dir = $target_dir . "/" . "input_reverse.fq";

	$f_file_hand = fopen($f_target_file, 'r');
	fclose($f_file_hand);
	rename($f_target_file, $f_new_dir);

	$r_file_hand = fopen(r_target_file, 'r');
	fclose($r_file_hand);
	rename($r_target_file, $r_new_dir);
}




$signal = TRUE;



while($signal){

	
	$sql_current = "SELECT TOP 1 jobID FROM job";
	
	$current = $con->query($sql_current);
	
	

	if(!$current){
		
		$signal = FALSE;
		
		break;
	
	}

	

	$sql_status = "SELECT TOP 1 jobStatus FROM job";
	
	$status = $con->query($sql_status);

	
	$con->close();
	

	if($status == 3){
		
		$result_dir = "home/student/SeniorDesign-MetagenomicPipeline/Jobs/" . $current . "/";
		
		$result_file = $result_dir . "result.pdf";
		
		echo "success!!!!";
	
	} 

}



?>
