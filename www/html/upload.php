
<?php
include "mysqli_con.php";
 
//get uuid
$jobID = str_replace(".", "-", uniqid("", true));

//get check box values
$idbaCheck = (isset($_POST['idbaCheck'])) ? 1 : 0;
$megahitCheck = (isset($_POST['megahitCheck'])) ? 1 : 0;
$metaspadesCheck = (isset($_POST['metaspadesCheck'])) ? 1 : 0;

//get radio button value
$pairedEnd = 0;
if($_POST['end'] == "paired-end"){
	$pairedEnd = 1;
}

//file paths
$basePath = "/home/student/SeniorDesign-MetagenomicPipeline/www/Jobs/" . $jobID . "/";
$trimmedSEPath = $basePath . "trimmedSE.fq";
$trimmedFPPath = $basePath . "trimmedFP.fq";
$trimmedFUPath = $basePath . "trimmedFU.fq";
$trimmedRPPath = $basePath . "trimmedRP.fq";
$trimmedRUPath = $basePath . "trimmedRU.fq";
$trimmedCPath = $basePath . "trimmedC.fa";
$idbaAssembly = $basePath . "idba/";
$idbaStat = $basePath . "idbaStat.txt";
$idbaVisual = $idbaAssembly;
$megahitAssembly = $basePath . "megahit/";
$megahitStat = $basePath . "megahit/";
$megahitVisual = $megahitAssembly;
$metaspadesAssembly = $basePath . "metaspades/";
$metaspadesStat = $basePath . "metaspadesStat.txt";
$metaspadesVisual = $metaspadesAssembly;


//sql query
//single end
if($pairedEnd == 0){
	$inputForward = $basePath . "inputSE.fq";

	$query = "INSERT INTO job (jobID, email, inputForward, idba, megahit, metaspades, pairedEnd, jobStatus, trimmedSE) VALUES ('{$jobID}', '{$_POST['email']}', '{$inputForward}', '{$idbaCheck}', '{$megahitCheck}', '{$metaspadesCheck}', '{$pairedEnd}', '0', '{$trimmedSEPath}')";
} 
//paired end
else{
	$inputForward = $basePath . "inputForward.fq";
	$inputReverse= $basePath . "inputReverse.fq";

	$query = "INSERT INTO job (jobID, email, inputForward, inputReverse, idba, megahit, metaspades, pairedEnd, jobStatus, trimmedForwardPaired, trimmedForwardUnpaired, trimmedReversePaired, trimmedReverseUnpaired, trimmedCombined) VALUES ('{$jobID}', '{$_POST['email']}', '{$inputForward}', '{$inputReverse}', '{$idbaCheck}', '{$megahitCheck}', '{$metaspadesCheck}', '{$pairedEnd}', '0', '{$trimmedFPPath}', '{$trimmedFUPath}', '{$trimmedRPPath}', '{$trimmedRUPath}', '{$trimmedCPath}')";
}


echo "Database insert: " . $query . "\n";

$con->query($query);

//assembler table queries
//idba
if($idbaCheck == 1){
	$query = "INSERT INTO idba (jobID, assembly, stat, visual) VALUES ('{$jobID}', '{$idbaAssembly}', '{$idbaStat}', '{$idbaVisual}')";
	$con->query($query);
}
//megahit
if($megahitCheck == 1){
	$query = "INSERT INTO megahit (jobID, assembly, stat, visual) VALUES ('{$jobID}', '{$megahitAssembly}', '{$megahitStat}', '{$megahitVisual}')";
	$con->query($query);
}
//metaspades
if($metaspadesCheck == 1){
	$query = "INSERT INTO metaspades (jobID, assembly, stat, visual) VALUES ('{$jobID}', '{$metaspadesAssembly}', '{$metaspadesStat}', '{$metaspadesVisual}')";
	$con->query($query);
}

mkdir("/home/student/SeniorDesign-MetagenomicPipeline/www/Jobs/" . $jobID . "/");

mkdir($idbaAssembly);
mkdir($megahitAssembly);
mkdir($metaspadesAssembly);

$uploadOk = 1;

if($pairedEnd == 0){
	$target_file = $basePath . basename($_FILES["my_file"]["name"]); 

	if (file_exists($target_file)) {
   		 echo "Sorry, file already exists.\n";
   		 $uploadOk = 0;
	}

	if(($idbaCheck == 1 && pairedEnd == 0) || ($metaspadesCheck == 1 && pairedEnd == 0)){
		echo "Sorry, only MEGAHIT accepts single-end data.";
		$uploadOk = 0;
	}

	// Check if $uploadOk is set to 0 by an error
	if ($uploadOk == 0) {
    		echo "Sorry, your file was not uploaded.\n";
	// if everything is ok, try to upload file
	} else {
   		 if (move_uploaded_file($_FILES["my_file"]["tmp_name"], $target_file)) {
       			 echo "The file ". basename( $_FILES["my_file"]["name"]). " has been uploaded.\n";
			 	$con->query("UPDATE job SET jobStatus = '1' WHERE jobID = '{$jobID}'");
			 	//send email to results page

	            $message = "The results for your metagenomic assembly pipeline job can be found at 10.171.204.144/html/results.html?jobID={$jobID}.";
	            $mailCommand = "python '/home/student/SeniorDesign-MetagenomicPipeline/www/html/sendmail.py' '{$_POST['email']}' 'Metagenomic Pipeline Results' '{$message}'";
	            $mailingOutput = shell_exec($mailCommand);
	            echo "Mail command: " . $mailCommand . "\n";
	            echo "Mailing output: " . $mailingOutput . "\n";
   		 } else {
       			 echo "Sorry, there was an error uploading your file.\n";
    		   }
	  }

	$new_dir = $basePath . "/" . "inputSE.fq";

	$fileHand = fopen($target_file, 'r');
	fclose($fileHand);
	rename($target_file, $new_dir);
}

else{

	$f_target_file = $target_dir . basename($_FILES["fmy_file"]["name"]);
	$r_target_file = $target_dir . basename($_FILES["rmy_file"]["name"]);

	if(file_exists($f_target_file) || file_exists($r_target_file)){
		echo "Sorry, 1 or more files already exist.\n";
		$uploadOk = 0;
	}
	
	if($uploadOk == 0){
		echo "Sorry, your files were not uploaded.\n";
	}

	else{
		if(move_uploaded_file($_FILES["fmy_file"]["tmp_name"], $f_target_file) && move_uploaded_file($_FILES["rmy_file"]["tmp_name"], $r_target_file)){
			echo "The files ". basename($_FILES["fmy_file"]["name"]). "and ". basename($_FILES["rmy_file"]["name"]). "have been uploaded.\n";

            //update job status
            $con->query("UPDATE job SET jobStatus = '1' WHERE jobID = '{$jobID}'");

            //send email to results page
            $message = "The results for your metagenomic assembly pipeline job can be found at 10.171.204.144/html/results.html?jobID=" . $jobID . ".";
            $mailCommand = "/home/student/SeniorDesign-MetagenomicPipeline/www/html/sendmail.py '{$email}' 'Metagenomic Pipeline Results' '{$message}'";
            $mailingOutput = shell_exec($mailCommand);
	    echo "Mail command: " . $mailCommand . "\n";
            echo "Mailing output: " . $mailingOutput . "\n";
		}

	      else{
			echo "Sorry, there was an error uploading 1 or more files.\n";
	     }
	}

	$f_new_dir = $target_dir . "/" . "inputForward.fq";
	$r_new_dir = $target_dir . "/" . "inputReverse.fq";

	$f_file_hand = fopen($f_target_file, 'r');
	fclose($f_file_hand);
	rename($f_target_file, $f_new_dir);

	$r_file_hand = fopen(r_target_file, 'r');
	fclose($r_file_hand);
	rename($r_target_file, $r_new_dir);
}




?>
