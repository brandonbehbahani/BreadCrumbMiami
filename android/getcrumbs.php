<?php
require "connect.php";
$id = $_GET["id"];

$getProducts = "select crumbLatitude, crumbLongitude, userName, crumbTitle, crumbContent, color from crumbs where crumbSector = '$id' ORDER BY id ASC;";
$result = mysqli_query($conn, $getProducts);

while($row[] = $result->fetch_assoc()){
	$tem = $row;
	$json = json_encode($tem);
}

echo $json;
$conn->close();

?>
