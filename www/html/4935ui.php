<?php 
	session_start();
	$_SESSION['post'] = $_POST;
?>
<!DOCTYPE html>

 <html lang="en">

 <head> 

	<meta charset="UTF-8">
	<meta name="viewport" content="width = device-width, initial-scale = 1">

	<title>Metagenomic Assembly Evaluator Pipeline</title>
		
		<script type="text/javascript">
			var allowedExtensions = [".fastq", ".fq", ".jpg"];    
				function verify(inFile) {
					var fileArray = inFile.getElementsByTagName("input");
					
					for (var i = 0; i < fileArray.length; i++) {
						var fileString = fileArray[i];
						
						if (fileString.type == "file") {
							var fileName = fileString.value;
							
							if (fileName.length > 0) {
								var extension_valid = false;
								
								for (var j = 0; j < allowedExtensions.length; j++) {
									var extension = allowedExtensions[j];
									
									if (fileName.substr(fileName.length - extension.length, extension.length).toLowerCase() == extension.toLowerCase()) {
										extension_valid = true;
										break;
									}
								}
                
								if (!extension_valid) {
									alert("Sorry, " + fileName + " is an invalid file extension, allowed extensions are: " + allowedExtensions.join(", "));
									return false;
								}
							}
						}
					}
  
					return true;
				}
				
				var estring = document.getElementById('email').value;
				var ch1 = document.getElementById('ca1');
				var ch2 = document.getElementById('ca2');
				var ch3 = document.getElementById('ca3');

				function  checkReady(){
					if(estring.length==0){
						alert('Please enter an eMail address in the eMail Address field.');
						return;
					}
					
					if(!ch1.checked && !ch2.checked && !ch3.checked){
						alert('Please select at least 1 assembler.');
						return;
					}
					
				}
				
		</script>

		<link rel="stylesheet" type="text/css" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css"/>

		
		<style>

			h1{
				color: #3419a0;
				font-family: MS Georgia;
			}

			h3{
				padding-top: 100px;
				color: #3419a0;
			}

			ul{
				list-style: none;
			}

			li{
				float: left;
			}

			.jumbotron{
				background-color: #2E2D88;
				height: 100px;
				width: 250px;
			}

			.page-header{
				background-color: #42f4a1;
				text-align: center;
				width: 94%;
				border-radius: 12px;
				box-shadow: 0 0 10px 0px;
			}

			p{
				color: #3419a0;
			}

			h4{
				color: #3419a0;
			}

		</style>

 </head>

 
 <body>
 
	<div class="page-header">
		<h1>Metagenomic Assembly and Evaluation Pipeline - Job Submission</h1>
	</div>

	<div class="page-header" style="height: 200px;">
		<h3>Assembler Selection</h3>
		<ul style="padding-left: 350px; padding-bottom:100;">
			<li>
				<ul>
					<li> 
						<h4>IDBA</h4>
					</li>

					<li style="padding-top: 11px;">
						<input type="checkbox" id="ca1" name="idbaCheck">
					</li>
				</ul>
			</li>

			<li style="padding-left: 40px;">
				<ul>
					<li> 
						<h4>MEGAHIT</h4>
					</li>

					<li style="padding-top: 11px;">
						<input type="checkbox" id="ca2" name="megahitCheck">
					</li>
				</ul>

			</li>

			<li style="padding-left: 40px;">
				<ul>
					<li> 
						<h4>MetaSPAdes</h4>
					</li>

					<li style="padding-top: 11px;">
						<input type="checkbox" id="ca3" name="metaspadesCheck">
					</li>
				</ul>
			</li>
		</ul>
	</div>

	<div>
		<ul style="padding-left: 400px; padding-bottom: 25px">
			<li style="padding-right: 150px;">
				<h3>Email Address</h3>
				<input type="text" name="email" id="email" placeholder="Email address field">
			</li>

			<li>
				<h3 style="padding-left:125px;">FASTQ Reads File</h3>
				<form action="upload.php" method="post" enctype="multipart/form-data" onsubmit="return verify(this);">
					<ul>
						 <li>
							<input type="radio" name="end" value="single-end">Single-end Data<br>
							<input type="file" name="my_file" id="my_file"/>
						 </li>
						 <li>
							<input type="radio" name="end" value="paired-end">Paired-end Data<br>
							<p>Forward:</p>
						    <input type="file" name="fmy_file" id="fmy_file"/>
							<p>Reverse:</p>
							<input type="file" name="rmy_file" id="rmy_file"/>
						 </li>
					</ul>
					<div style="padding-top: 50px; padding-right: 100px"> 
						<input type="submit" value="Submit Job" name="submitButton" style="height: 50px; width: 150px; border-radius: 10px; font-size: 18px;" onclick="checkReady();">
					</div>
				</form>
			</li>
		</ul>
	</div>

	/*<?php
		include "mysqli_con.php";

		$jobID = 'test';

		if(isset($_POST['submitButton'])){
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
				$query = "INSERT INTO job (jobID, email, inputForward, idba, megahit, metaspades, pairedEnd, jobStatus) VALUES ('{$test}', '{$_POST['email']}', '{$_POST['my_file']}', '{$idba}', '{$megahit}', '{$metaspades}', '{$pairedEnd}', '1')";
			} 
			//paired end
			else{
				$query = "INSERT INTO job (jobID, email, inputForward, inputReverse, idba, megahit, metaspades, pairedEnd, jobStatus) VALUES ('{$test}', '{$_POST['email']}', '{$_POST['fmy_file']}', '{$_POST['rmy_file']}', '{$idba}', '{$megahit}', '{$metaspades}', '{$pairedEnd}', '1')";
			}

			$con->query($query);
		}
	?>*/

 </body>

 </html>