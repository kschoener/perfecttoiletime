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
	if(isset($_GET['floor'])){
		$floor = $_GET['floor'];
	}
		
		$records1 = array();
		$query = "SELECT Longitude, Latitude, floor FROM Users1 WHERE true";
		//$query = "SELECT * FROM Bump WHERE bumped_by_id = '$id'";
		$resultset = mysqli_query($con, $query);
			$records = array();
			//Loop through all our records and add them to our array
			while($r = mysqli_fetch_assoc($resultset))
			{
				$tlongitude = $r['Longitude'];
				$tlatitude = $r['Latitude'];
				$tfloor = $r['floor'];
				$dis = haversineGreatCircleDistance(
  $latitude, $longitude, $tlatitude, $tlongitude,20902231);	
  				if($dis < 30 && $tfloor == $floor){
  					echo "already in database";
  					exit();
  				}	
			}
		
		
		
		
		$query = "INSERT into Users1 (Longitude, Latitude, name, description,floor) values ('$longitude','$latitude','$name','$description','$floor')";
		//$query = "SELECT * FROM Bump WHERE bumped_by_id = '$id'";
		$resultset = mysqli_query($con, $query);
			echo $resultset;
			//Output the data as JSON
			//Check to see if we could select the database
			//Output the data as JSON
			
	}
	function haversineGreatCircleDistance(
  $latitudeFrom, $longitudeFrom, $latitudeTo, $longitudeTo, $earthRadius = 3959)
{
  // convert from degrees to radians
  $latFrom = deg2rad($latitudeFrom);
  $lonFrom = deg2rad($longitudeFrom);
  $latTo = deg2rad($latitudeTo);
  $lonTo = deg2rad($longitudeTo);

  $latDelta = $latTo - $latFrom;
  $lonDelta = $lonTo - $lonFrom;

  $angle = 2 * asin(sqrt(pow(sin($latDelta / 2), 2) +
    cos($latFrom) * cos($latTo) * pow(sin($lonDelta / 2), 2)));
  return $angle * $earthRadius;
}
?>