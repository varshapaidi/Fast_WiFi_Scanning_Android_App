Fast_WiFi_Scanning_Android_App
==============================

There are over 15 WiFi channels on the 2.4 GHz and 5 GHz bands. WiFi APs are typically assigned one of these channels to operate on. 
When a client enters a new location, it has to scan all the 15+ channels for sometime, until it finds a suitable AP to associate with. 
This causes a delay before the client can connect, as well as drains energy. Additionally, in areas where APs are not available, 
or the password is not known to the user (e.g., store), the AP continuously wakes up, to scan for an AP with which it can connect. 
This is a waste of energy if the user has not moved from the previous location. 

Implemented a smart scanning scheme for android phones that would allow for faster AP-client connection. 
The app makes use of sensors on the phone to detect user activity / motion, surrounding Cell IDs 
and signal strength information and also keeps and uses the history of locations and channels on 
which APs were previously observed and connected to. 
