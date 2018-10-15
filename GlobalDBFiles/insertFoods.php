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

	$selectFood = $pdo->prepare("SELECT * FROM Foods WHERE foodName = :foodname");

    $insertFood = $pdo->prepare("INSERT INTO Foods (foodName, sugarPerServe, sugarPer100, barcode, category, dateAdded) 
									VALUES(:foodname, :sug1, :sug2, :barc, :categ, :date)
									ON DUPLICATE KEY UPDATE sugarPerServe = :sug1, sugarPer100 = :sug2, category = :categ, dateAdded = :date");
	
	$selectFood->bindParam(':foodname',$foodName);
	$insertFood->bindParam(':foodname',$foodName);
    $insertFood->bindParam(':sug1',$sugar1);
    $insertFood->bindParam(':sug2',$sugar2);
    $insertFood->bindParam(':barc', $barcode);
    $insertFood->bindParam(':categ', $category);
    $insertFood->bindParam(':date', $date);

	
	
	$foods = $_POST['foods'];
	
	print_r($foods);
	
	for($i = 0; $i < count($foods); $i++){
		echo("\n");
		print_r($foods[$i]['name']);
		echo("\n");
		print_r($foods[$i]['sugar1']);
		echo("\n");
		print_r($foods[$i]['barcode']);
		echo("\n");
		print_r($foods[$i]['category']);
		echo("\n");
		
		$foodName = $foods[$i]['name'];
		$sugar1 = $foods[$i]['sugar1'];
		$sugar2 = $foods[$i]['sugar2'];
		$barcode = $foods[$i]['barcode'];
		$category = $foods[$i]['category'];

		if($sugar1 == 101 || $sugar2 == 101){
		}else{

			$selectFood->execute();

			if($selectFood->rowCount() > 0){

				$rows = $selectFood->fetch();

				$existingData = array(
					"name" => $rows['foodName'],
					"sugar1" => $rows['sugarPerServe'],
					"sugar2" => $rows['sugarPer100'],
					"barcode" => $rows['barcode'],
					"category" => $rows['category'],
				);

				$newData = array(
					"name" => $foodName,
					"sugar1" => $sugar1,
					"sugar2" => $sugar2,
					"barcode" => $barcode,
					"category" => $category,
				);

				print_r($existingData);
				print_r($foods[$i]);
				if($existingData != $foods[$i]){
					$insertFood->execute();
				}

			}else{

				$insertFood->execute();

			}
			echo("Food INSERTED\n \n");
		}
	}
	
	/**
	$foodjson = $_POST['foods'];
	$foodarr = json_decode($foodjson, true);
	**/
	
	
	/**
	echo $foodarr;
	**/
	
	
	/**
	for($foodarr as $key => $value){
		$foodname = $_POST['name'];
		$sugar = $_POST['sugar'];
		$barcode = $_POST['barcode'];
		$category = $_POST['category'];
	}
    **/
  }

    
  else{
		echo "didnt make it here \n \n";
		print_r($_POST);

  }





?>
