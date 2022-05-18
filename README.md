# SmartPhone-Sensor-Data-Collection-App
We can collect raw data of the sensors available in our smartphone by using this app.
This app labels the data into user's position(Indoor, Semi-indoor and Outdoor).
It creates a separate CSV file for each sensors, which you can retrieve from /documents/IOPS_1.

Version v1.0.0
*Basic version

Version v1.0.1
*The calling of sensor event listioner is pushed from onresume() function to oncreate()
*The unregistering of event listener is pushed to ondestroy() so that data can be collected while using another app

Version v1.0.2
*The App was throwing nullPointerException in version v1.0.1 when the CSVwriter was wirting the data in file in onSensorChanged() function. This glitch was rectified and solved using try and catch metthod.
*The App was crashing upon destruction of the SensorActivity page. Function onBackPressed() was added to solve this.
*Main Activity code is cleaned by adding functions.
*In SensorActivity.java sensor event timestamp was added to the files.
*Input field for sensor frequency added in Main Activity, the same has been caliberated with SensorActivity.
*Save and Exit button added in the SensorActivity page.