<?php
	include('database.php');
	if(!$con)
	{
		die("Database server connection failed.");	
	}
	else
	{
		$records1 = array();
		$query = "SELECT * FROM Users1";
		//$query = "SELECT * FROM Bump WHERE bumped_by_id = '$id'";
		$resultset = mysqli_query($con, $query);
			$records = array();
			//Loop through all our records and add them to our array
			while($r = mysqli_fetch_assoc($resultset))
			{
  				$records[] = $r;
			}
			echo json_encode($records);
			//Output the data as JSON
			//Check to see if we could select the database
			//Output the data as JSON
			
	}
?>