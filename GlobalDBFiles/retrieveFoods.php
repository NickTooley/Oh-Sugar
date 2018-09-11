<?php
$title="Full Database.";
//include 'header.html.php';
include 'connect.inc.php';
session_start();

?>

<?php

if(isset($_GET['date'])){

	$currDate = $date = date('Y-m-d H:i:s');
	
	$lastSync = $_GET['date'];
	
	$getFoods = $pdo->prepare("SELECT * FROM Foods WHERE dateAdded  >= :date");

	$getFoods->bindParam(':date', $lastSync);

	$getFoods->execute();

	foreach($getFoods as $row) {
		echo("<div class='food'>");
		echo("<div class='name'>$row[foodName]</div>");
		echo("<div class='sugar'>$row[sugarPerServe]</div>");
		echo("<div class='sugar100'>$row[sugarPer100]</div>");
		echo("<div class='barcode'>$row[barcode]</div>");
		echo("<div class='category'>$row[category]</div>");
		echo("</div>");
	}
	echo("<div id='date'>$currDate</div>");
}


?>

