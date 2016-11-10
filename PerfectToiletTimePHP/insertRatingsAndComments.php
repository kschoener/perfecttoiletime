<?php
	include('database.php');
	if(!$con)
	{
		die("Database server connection failed.");	
	}
	else
	{
	if(isset($_GET['id'])){
		$id = $_GET['id'];
	}
	if(isset($_GET['busy'])){
		$busy = $_GET['busy'];
	}
	if(isset($_GET['clean'])){
		$clean = $_GET['clean'];
	}
	if(isset($_GET['wifi'])){
		$wifi = $_GET['wifi'];
	}
	if(isset($_GET['comments'])){
		$comments = $_GET['comments'];
	}
	
		$records1 = array();
		$query = "INSERT into Ratings (Bathroom_id, Busy, Cleanliness, Wifi,Comment) values ('$id','$busy','$clean','$wifi','$comments')";
		//$query = "SELECT * FROM Bump WHERE bumped_by_id = '$id'";
		$resultset = mysqli_query($con, $query);
			echo $resultset;
			//Output the data as JSON
			//Check to see if we could select the database
			//Output the data as JSON
			
	}
?>