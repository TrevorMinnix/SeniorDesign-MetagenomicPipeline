<!DOCTYPE html>

<html lang="en">

<?php
	//error reporting
	error_reporting(E_ALL);
	ini_set('display_errors', TRUE);
	ini_set('display_startup_errors', TRUE);

	//get status
	include "mysqli_con.php";

	$query = htmlspecialchars($_GET["jobID"]);

	if($statusSet = $con->query("SELECT * FROM jobStatus WHERE jobID  = '{$query}'")){
		$row = $statusSet->fetch_assoc();
		$statusSet->free();
	}
?>

<head>

<meta charset="UTF-8">
<meta name="viewport" content="width = device-width, initial-scale = 1">
<title>Metagenomic Assembly and Evaluation Pipeline-Results</title>
<link rel="stylesheet" type="text/css" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css"/>
<script type="text/javascript">
function openCity(evt, cityName) {
    // Declare all variables
    var i, tabcontent, tablinks;

    // Get all elements with class="tabcontent" and hide them
    tabcontent = document.getElementsByClassName("tabcontent");
    for (i = 0; i < tabcontent.length; i++) {
        tabcontent[i].style.display = 'none';
    }

    // Get all elements with class="tablinks" and remove the class "active"
    tablinks = document.getElementsByClassName("tablinks");
    for (i = 0; i < tablinks.length; i++) {
        tablinks[i].className = tablinks[i].className.replace(" active", "");
    }

    // Show the current tab, and add an "active" class to the link that opened the tab
    document.getElementById(cityName).style.display = 'block';
    evt.currentTarget.class += " active";
}

function getQueryStrings() { 
	 
		var path = window.location.href;
		var frags = path.split('=');
		
		var fragstwo = frags[1];
		return fragstwo; 
	}
	
function makeSrc(){
	var prefix = "../Jobs/";
	var jid = getQueryStrings();
	var idbaSrc1 = prefix  + jid + "/idba/CumulPlot.jpg";
	document.getElementById("idbacp").src = idbaSrc1;
	
	var idbaSrc2 = prefix  + jid + "/idba/Nx_Plot.jpg";
	document.getElementById("idbanx").src = idbaSrc2;
	
	var idbaSrc3 = prefix  + jid + "/idba/table.jpg";
	document.getElementById("idbast").src = idbaSrc3;

	var idbaSrc4 = prefix  + jid + "/idba/GCPlot.jpg";
	document.getElementById("idbagp").src = idbaSrc4;
	
	
	var mhSrc1 = prefix  + jid + "/megahit/CumulPlot.jpg";
	document.getElementById("mhcp").src = mhSrc1;
	
	var mhSrc2 = prefix  + jid + "/megahit/Nx_Plot.jpg";
	document.getElementById("mhnx").src = mhSrc2;
	
	var mhSrc3 = prefix  + jid + "/megahit/table.jpg";
	document.getElementById("mhst").src = mhSrc3;

	var mhSrc4 = prefix  + jid + "/megahit/GCPlot.jpg";
	document.getElementById("mhgp").src = mhSrc4;
	
	
	var msSrc1 = prefix  + jid + "/metaspades/CumulPlot.jpg";
	document.getElementById("mscp").src = msSrc1;
	
	var msSrc2 = prefix  + jid + "/metaspades/Nx_Plot.jpg";
	document.getElementById("msnx").src = msSrc2;
	
	var msSrc3 = prefix  + jid + "/metaspades/table.jpg";
	document.getElementById("msst").src = msSrc3;

	var msSrc4 = prefix  + jid + "/metaspades/GCPlot.jpg";
	document.getElementById("msgp").src = msSrc4;
	
	
	var idbaaf = prefix + jid + "/idba/contig.fa";
	document.getElementById("idbahref").href = idbaaf;
	
	var mhaf = prefix + jid + "/megahit/final.contigs.fa";
	document.getElementById("mhhref").href = mhaf;
	
	var msaf = prefix + jid + "/metaspades/contigs.fasta";
	document.getElementById("mshref").href = msaf;
}

</script>
<style>
h1{
color: #3419a0;
font-family: MS Georgia;
}
.page-header{
background-color: #42f4a1;
text-align: center;
width: 94%;
border-radius: 12px;
box-shadow: 0 0 10px 0px;
}

h4{
padding-top: 100px;
color: #3419a0;
}

ul{
list-style: none;
}

.tab{
float: left;
}


</style>
</head>

<body onload="makeSrc()">
<div class="page-header">
<h1>Metagenomic Assembly and Evaluation Pipeline - Results</h1>
</div>

<ul class="nav nav-pills">
  <?php if($row['idba'] == 1) : ?>
    <li id="tab"><a data-toggle="pill" href="javascript:void(0)" class="tablinks" onclick="openCity(event, 'as1')">IDBA</a></li>
  <?php endif; ?>
  <?php if($row['megahit'] == 1) : ?>
    <li id="tab"><a data-toggle="pill" href="javascript:void(0)" class="tablinks" onclick="openCity(event, 'as2')">MEGAHIT</a></li>
  <?php endif; ?>
  <?php if($row['metaspades'] == 1) : ?>
    <li id="tab"><a data-toggle="pill" href="javascript:void(0)" class="tablinks" onclick="openCity(event, 'as3')">MetaSPAdes</a></li>
  <?php endif; ?>
  <?php if($row['megahit'] + $row['idba'] + $row['metaspades'] > 1) : ?>
    <li id="tab"><a data-toggle="pill" href="javascript:void(0)" class="tablinks" onclick="openCity(event, 'asc')">Assembler Comparison</a></li>
  <?php endif; ?>

</ul>



<!--<div id="as1" class="tabcontent" <?php if($row['idba'] != 1): ?> style="display: none" <?php endif; ?>>-->
<div id="as1" class="tabcontent" style="display: none">
  <h3>IDBA</h3>
  <hr style="height:30px; color: black>
  <a id="idbahref" href="#" download><h3>Download Assembly</h3></a>
  <ul>
	<li>
		<h3>Cumulative Plot</h3>
		<div>
			<img src="CumulPlot.jpg" alt="No visualizations generated." id="idbacp">
		</div>
	</li>
	<br/>
	<li>
		<h3>Nx</h3>
		<div>
			<img src="Nx_Plot.jpg" alt="No visualizations generated." id="idbanx">
		</div>
	</li>
	<br/>
	<li>
		<h3>Statistics Table</h3>
		<div>
			<img src="table.jpg" alt="No visualizations generated." id="idbast">
		</div> 
	</li>
	<br/>
	<li>
		<h3>GC Plot</h3>
		<div>
			<img src="GCPlot.jpg" alt="No visualizations generated." id="idbagp">
		</div> 
	</li>
  </ul>
</div>

<div id="as2" class="tabcontent" style="display: none">
  <h3>MEGAHIT</h3>
  <hr style="height:30px; color: black;">
  <a id="mhhref" href="#" download>Download assembled file.</a>
  <ul>
	<li>
		<h3>Cumulative Plot</h3>
		<div>
			<img src="CumulPlot.jpg" alt="No visualizations generated." id="mhcp">
		</div>
	</li>
	<br/>
	<li>
		<h3>Nx</h3>
		<div>
			<img src="Nx_Plot.jpg" alt="No visualizations generated." id="mhnx">
		</div>
	</li>
	<br/>
	<li>
		<h3>Statistics Table</h3>
		<div>
			<img src="table.jpg" alt="No visualizations generated." id="mhst">
		</div> 
	</li>
	<br/>
	<li>
		<h3>GC Plot</h3>
		<div>
			<img src="GCPlot.jpg" alt="No visualizations generated." id="mhgp">
		</div> 
	</li>
  </ul>
</div>

<div id="as3" class="tabcontent" style="display: none">
  <h3>MetaSPAdes</h3>
  <hr style="height:30px; color: black;">
  <a id="mshref" href="" download>Download assembled file.</a>
  <ul>
	<li>
		<h3>Cumulative Plot</h3>
		<div>
			<img src="CumulPlot.jpg" alt="No visualizations generated." id="mscp">
		</div>
	</li>
	<br/>
	<li>
		<h3>Nx</h3>
		<div>
			<img src="Nx_Plot.jpg" alt="No visualizations generated." id="msnx">
		</div>
	</li>
	<br/>
	<li>
		<h3>Statistics Table</h3>
		<div>
			<img src="table.jpg" alt="No visualizations generated." id="msst">
		</div> 
	</li>
	<br/>
	<li>
		<h3>GC Plot</h3>
		<div>
			<img src="GCPlot.jpg" alt="No visualizations generated." id="msgp">
		</div> 
	</li>
  </ul>
</div>

<div id="asc" class="tabcontent" style="display: none">
  <h3>Assembler Comparison</h3>
  <hr style="height:30px; color: black;">
</div>

</body>

</html>
