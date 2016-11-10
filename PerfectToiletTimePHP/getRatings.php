<?php
	include('database.php');
	if(!$con)
	{
		die("Database server connection failed.");	
	}
	else
	{
	if(isset($_GET['bathroomID'])){
		$id = $_GET['bathroomID'];
	}
	
		$records1 = array();
		$query1 = "SELECT * FROM Users1 WHERE id='$id'";
		//$query = "SELECT * FROM Bump WHERE bumped_by_id = '$id'";
		$resultset1 = mysqli_query($con, $query1);
		$r1 = mysqli_fetch_assoc($resultset1);
		$query = "SELECT * FROM Ratings WHERE Bathroom_id='$id'";
		//$query = "SELECT * FROM Bump WHERE bumped_by_id = '$id'";
		$resultset = mysqli_query($con, $query);
			$records = array();
			$averageWifi = 0;
			$averageClean = 0;
			$averageBusy = 0;
			$counter = 0;
			//Loop through all our records and add them to our array
			while($r = mysqli_fetch_assoc($resultset))
			{
				$averageWifi = $averageWifi + $r['Wifi'];
				$averageClean = $averageClean + $r['Cleanliness'];
				$averageBusy = $averageBusy + $r['Busy'];
				$counter++;
				$records[] = $r;		
			}
			$records['average']['Wifi'] = $averageWifi / $counter;
			$records['average']['Clean'] = $averageClean / $counter;
			$records['average']['Busy'] = $averageBusy / $counter;
			$records['info'] = $r1;
			echo json_encode($records);
			//Output the data as JSON
			//Check to see if we could select the database
			//Output the data as JSON
			
	}
?>