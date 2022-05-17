# SmartPhone-Sensor-Data-Collection-App
We can collect raw data of the sensors available in our smartphone by using this app.
This app labels the data into user's position(Indoor, Semi-indoor and Outdoor).
It creates a separate CSV file for each sensors, which you can retrieve from /documents/IOPS_1.

* Changes in this version:
* The calling of sensor event listioner is pushed from onresume() function to oncreate()
* The unregistering of event listener is pushed to ondestroy() so that data can be collected while using another app
