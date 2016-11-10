<?php
	include('database.php');
	if(!$con)
	{
		die("Database server connection failed.");	
	}
	else
	{
	if(isset($_GET['Longitude'])){
		$longitude = $_GET['Longitude'];
	}
	if(isset($_GET['Latitude'])){
		$latitude = $_GET['Latitude'];
	}
	if(isset($_GET['Distance'])){
		$distance = $_GET['Distance'];
	}
		$records1 = array();
		$query = "SELECT * FROM Users1";
		//$query = "SELECT * FROM Bump WHERE bumped_by_id = '$id'";
		$resultset = mysqli_query($con, $query);
			$records = array();
			//Loop through all our records and add them to our array
			while($r = mysqli_fetch_assoc($resultset))
			{
				$tlongitude = $r['Longitude'];
				$tlatitude = $r['Latitude'];
				$dis = haversineGreatCircleDistance(
  $latitude, $longitude, $tlatitude, $tlongitude,3959);	
  				if($dis < $distance){
  					$records[] = $r;
  				}	
			}
			echo json_encode($records);
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