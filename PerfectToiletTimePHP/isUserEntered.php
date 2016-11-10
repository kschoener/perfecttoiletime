<?php
	include('database.php');
	if(!$con)
	{
		die("Database server connection failed.");	
	}
	else
	{
	if(isset($_GET['userID'])){
		$id = $_GET['userID'];
	}
	
		$records1 = array();
		$query = "SELECT * FROM UserData WHERE id='$id'";
		//$query = "SELECT * FROM Bump WHERE bumped_by_id = '$id'";
		$resultset = mysqli_query($con, $query);
			$records = array();
			//Loop through all our records and add them to our array
			while($r = mysqli_fetch_assoc($resultset))
			{
				$records[] = $r;		
			}
			if(count($records)==0)
				echo "false";
			else
				echo json_encode($records);
			//Output the data as JSON
			//Check to see if we could select the database
			//Output the data as JSON
			
	}
?>