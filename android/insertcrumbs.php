<?php
require "connect.php";

$pSector = $_POST["sector"];
$pLatitude = $_POST["latitude"];
$pLongitude = $_POST["longitude"];
$pUsername = $_POST["name"];
$pTitle = $_POST["title"];
$pContent = $_POST["content"];
$pColor = $_POST["color"];
$mysqli_qry = "insert into crumbs (crumbSector, crumbLatitude, crumbLongitude,
userName, crumbTitle, crumbContent, color) values ('$pSector', '$pLatitude', 
'$pLongitude', '$pUsername', '$pTitle', '$pContent', '$pColor');";

if($conn->query($mysqli_qry) == TRUE){
	echo "Insert Successful";
}
else {
	echo "Error " . mysqli_error($mysqli_qry);
}

$conn->close();
?>
