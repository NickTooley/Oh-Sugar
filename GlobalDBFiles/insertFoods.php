<?php
$title="Full Database.";
//include 'header.html.php';
include 'connect.inc.php';
session_start();?>

<?php
  
  if (isset($_POST['leaveSubmit'])){
    $date = date('Y-m-d H:i:s');


    $insertFood = $pdo->prepare("INSERT INTO Foods (foodName, sugar, barcode, category, dateAdded) VALUES(:foodname, :sug, :barc, :categ, :date)");
	
	$insertFood->bindParam(':foodname',$foodName);
    $insertFood->bindParam(':sug',$sugar);
    $insertFood->bindParam(':barc', $barcode);
    $insertFood->bindParam(':categ', $category);
    $insertFood->bindParam(':date', $date);

	
	$foodjson = $_POST['foods'];
	$foodarr = json_decode($foodjson, true);
	
	for($i = 0; $i < $count($foodarr); $i++){
		$foodname = $_POST['name'];
		$sugar = $_POST['sugar'];
		$barcode = $_POST['barcode'];
		$category = $_POST['category'];
	}
    

    
  else{

      include 'leaveRequestForm.html.php';

  }





?>
