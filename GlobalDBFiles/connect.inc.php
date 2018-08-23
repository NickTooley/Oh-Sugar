<?php
	$host = "mariadb.ict.op.ac.nz";
	$userMS = "toolnj1";
	$passwordMS = "toolnj1";
	$database = "toolnj1_ohsugar";

	//Establish connection to the database
	try{
		$pdo = new PDO("mysql:host=$host; dbname=$database", $userMS, $passwordMS);
		$pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
		$pdo->exec('SET NAMES "utf8"');


	}
	catch(PDOException $e){
		$error = 'Connection to database failed';
		include 'error.html.php';
		exit();

	}

?>
