<?php
	include('database.php');
	if(!$con)
	{
		die("Database server connection failed.");	
	}
	else
	{
	if(isset($_GET['longitude'])){
		$longitude = $_GET['longitude'];
	}
	if(isset($_GET['latitude'])){
		$latitude = $_GET['latitude'];
	}
	if(isset($_GET['name'])){
		$name = $_GET['name'];
	}
	if(isset($_GET['description'])){
		$description = $_GET['description'];
	}
	
		$records1 = array();
		$query = "INSERT into Users1 (Longitude, Latitude, name, description) values ('$longitude','$latitude','$name','$description')";
		//$query = "SELECT * FROM Bump WHERE bumped_by_id = '$id'";
		$resultset = mysqli_query($con, $query);
			echo $resultset;
			//Output the data as JSON
			//Check to see if we could select the database
			//Output the data as JSON
			
	}
?>