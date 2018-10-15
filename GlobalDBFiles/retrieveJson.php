<?php
$title="Full Database.";
//include 'header.html.php';
include 'connect.inc.php';
session_start();

?>

<?php

if(isset($_GET['date'])){

    $currDate = ['date' => date('Y-m-d H:i:s')];

	$lastSync = $_GET['date'];
	$getFoods = $pdo->prepare("SELECT * FROM Foods WHERE dateAdded  >= :date");
	$getFoods->bindParam(':date', $lastSync);
	$getFoods->execute();
    $allFoods = array();

    foreach($getFoods as $row) {
        $food = ['name' => $row['foodName'], 'sugar' => $row['sugarPerServe'] , 'sugar100' => $row['sugarPer100'], 'barcode' => $row['barcode'], 'category' => $row['category']];
        
        $allFoods[] = $food;

    }

    $allFoods[] = $currDate;


    header('Content-Type: application/json');

    echo json_encode($allFoods);
}
    ?>