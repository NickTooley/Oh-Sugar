<?php
$title="Full Database.";
//include 'header.html.php';
include 'connect.inc.php';
session_start();?>

<?php
  $rest_json = file_get_contents("php://input");
  $_POST = json_decode($rest_json, true);
  
  if (isset($_POST['foodInsert'])){
    $date = date('Y-m-d H:i:s');


    $insertFood = $pdo->prepare("INSERT INTO Foods (foodName, sugar, barcode, category, dateAdded) VALUES(:foodname, :sug, :barc, :categ, :date)");
	
	$insertFood->bindParam(':foodname',$foodName);
    $insertFood->bindParam(':sug',$sugar);
    $insertFood->bindParam(':barc', $barcode);
    $insertFood->bindParam(':categ', $category);
    $insertFood->bindParam(':date', $date);

	
	
	$foods = $_POST['foods'];
	
	print_r($foods);
	
	for($i = 0; $i < count($foods); $i++){
		echo("\n");
		print_r($foods[$i]['name']);
		echo("\n");
		print_r($foods[$i]['sugar']);
		echo("\n");
		print_r($foods[$i]['barcode']);
		echo("\n");
		print_r($foods[$i]['category']);
		echo("\n");
		
		$foodName = $foods[$i]['name'];
		$sugar = $foods[$i]['sugar'];
		$barcode = $foods[$i]['barcode'];
		$category = $foods[$i]['category'];
		
		$insertFood->execute();
		echo("Food INSERTED\n \n");
	}
  }

    
  else{
		echo "didnt make it here \n \n";
		print_r($_POST);

  }





?>
