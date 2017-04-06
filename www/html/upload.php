<?php
$target_dir ="/home/student/SeniorDesign-MetagenomicPipeline/FASTQ/Jobs/";
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
    mkdir($target_file, 0777, true);
    if (move_uploaded_file($_FILES["my_file"]["tmp_name"], $target_file)) {
        echo "The file ". basename( $_FILES["my_file"]["name"]). " has been uploaded.";
    } else {
        echo "Sorry, there was an error uploading your file.";
    }
}

?>
=======
if(!file_exists($target_dir."/".$target_file)){
	mkdir($target_dir."/".$target_file, 0777, true);
}
?>
