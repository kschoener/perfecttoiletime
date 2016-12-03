# perfecttoiletime
Group Members:
Kyle Schoener
Mark Abidargham
Steven Wilser
Edward Selig
Congying Wang

This is our group project for CSE 442 at the University at Buffalo.
We are building a bathroom tracking and rating system.
This system will also allow us to contact maintenance if there is anything wrong/missing with the bathroom.

HOW TO CLONE REPOSITORY:

  Using terminal:
    > git clone https://github.com/kschoener/perfecttoiletime

  Using github.com:
    - Make sure you are located in the master branch (this is loaded by default).
    - Click the green "Clone or download" button on the right side of the webpage.
    - Click "Download ZIP".
    - Unzip the archive with your favorite extracting application or use the unzip command in terminal.

HOW TO TEST OR MODIFIY THE APP WITH ANDROID STUDIO:

  First, import the Perfect Toilet Time App into Android Studio:
    - If you are using a fresh install of Android Studio, have not worked on any previous projects, or have closed your current project, click "Open an existing Android Studio Project" when Android Studio is finished loading.
    - Locate the directory you cloned the repository into, click perfecttoiletime, then click the "OK".
    - If you have worked with Android Studio before and have not closed your current project, click "File" in the toolbar then click "Close Project" and follow the above step, or click "File" in the toolbar then "New" and finally "Import Project..." and follow the above step.
  
  To run the app:
    - Click "Run" in the toolbar, then click "Run 'app'".
    - Click your connected device (supported Android device) or Android emulated device you have set up, then click "OK".
    - Proceed to use and test the app's functionality.


The goal for this app is firstly to bring a better experience for the everyday must-have "restroom" time. With preference settings and location choosing, people can have a more peaceful time and make it easier when they are not at their home. Now we are mainly focusing on building this app for students and faculty at UB. We noticed the usage for all the restrooms are unbalanced which means some of them are crowded but some are rarely used by people. This app can balance usage of the restroom, and also achieve the goal of making toilet time better, hence our name, PerfectToiletTime.

# Backend Documentation
MySQL was used for the database.

"GetAllLocations.php" returns all the data in the "Users1" table.

"getLocation.php" uses the longitude, latitude, and distance input passed in through GET requests and returns all bathrooms within the radius of the distance from the coordinates.

"getRatings.php" returns all of the ratings for the bathroom id input and also outputs the average ratings of that bathroom.

"insertBathroom.php" has inputs for longitude, latitude, name, description, and floor of a bathroom.  It inserts this data into the "Users1" table.  If a bathroom is within 30 feet of another bathroom it will not insert the bathroom to avoid duplicates.

"insertRatingsAndComments.php" has inputs for id, busy, clean, wifi, and comments and inserts this data into the "Ratings" table.

"isUserEntered.php" takes an id as input and outputs the data for the user with that ID.  If that user is not in the database it returns false.

Description of "Ratings" table  
+-------------+--------------+------+-----+---------+----------------+  
| Field       | Type         | Null | Key | Default | Extra          |  
+-------------+--------------+------+-----+---------+----------------+  
| id          | int(11)      | NO   | PRI | NULL    | auto_increment |  
| Wifi        | varchar(255) | YES  |     | NULL    |                |  
| Busy        | varchar(255) | YES  |     | NULL    |                |  
| Cleanliness | varchar(255) | YES  |     | NULL    |                |  
| Comment     | varchar(255) | YES  |     | NULL    |                |  
| Bathroom_id | int(11)      | YES  |     | NULL    |                |  
+-------------+--------------+------+-----+---------+----------------+  
  
Description of "UserData" table  
+-------------+--------------+------+-----+---------+-------+  
| Field       | Type         | Null | Key | Default | Extra |  
+-------------+--------------+------+-----+---------+-------+  
| id          | varchar(255) | NO   | PRI | NULL    |       |  
| Wifi        | varchar(255) | YES  |     | NULL    |       |  
| Busy        | varchar(255) | YES  |     | NULL    |       |  
| Cleanliness | varchar(255) | YES  |     | NULL    |       |  
+-------------+--------------+------+-----+---------+-------+  
  
Description of "Users1" table  
+-------------+--------------+------+-----+---------+----------------+  
| Field       | Type         | Null | Key | Default | Extra          |  
+-------------+--------------+------+-----+---------+----------------+  
| id          | int(11)      | NO   | PRI | NULL    | auto_increment |  
| Longitude   | double       | YES  |     | NULL    |                |  
| Latitude    | double       | YES  |     | NULL    |                |  
| name        | varchar(255) | YES  |     | NULL    |                |  
| description | varchar(255) | YES  |     | NULL    |                |  
| floor       | int(11)      | YES  |     | NULL    |                |  
+-------------+--------------+------+-----+---------+----------------+  
