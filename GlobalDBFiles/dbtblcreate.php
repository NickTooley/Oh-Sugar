
<?php
$host = 'mariadb.ict.op.ac.nz';
$userMS = 'toolnj1';
$passwordMS = 'toolnj1';
$database = 'toolnj1_ohsugar';
// Tries to estabilish a connection to the database:
try
{
    $pdoObject = new PDO("mysql:host=$host;dbname=$database", $userMS, $passwordMS);
    $pdoObject->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    $pdoObject->exec('SET NAMES "utf8"');
}
catch (PDOException $e)
{
    $error = 'Connection to database failed';
    include 'Error.php';
    exit();
}

try
    {
        $dropQuery ="SET FOREIGN_KEY_CHECKS = 0";
        $pdoObject->exec($dropQuery);
        
        $dropQuery ="DROP TABLE IF EXISTS Foods";
        $pdoObject->exec($dropQuery);

        $dropQuery ="SET FOREIGN_KEY_CHECKS = 1";
        $pdoObject->exec($dropQuery);
    }
    catch (PDOException $e)
    {
        $error = 'Error when deleting table(s)';
        include 'Error.php';
        exit();
    }
	
	
	 try
    {
        $createQuery ="CREATE TABLE Foods
        (
            foodID         	INT(20)       NOT NULL    AUTO_INCREMENT,
            foodName       	VARCHAR(20)   NOT NULL,
			sugar		   	FLOAT(5,2)	  NOT NULL,
			barcode			VARCHAR(13),
			category		VARCHAR(30),
			dateAdded		DATETIME,
            
            PRIMARY KEY(foodID)
        )";
        $pdoObject->exec($createQuery);
    }
    catch (PDOException $e)
    {
        $error = 'Creating table Foods failed';
        include 'Error.php';
        exit();
    }

?>

<body>
    TABLE CREATED SUCCESSFULLY!
</body>
</html>
