# The GXF IEC61850 Simulator
This software is a program to simulate devices which are capable of switching relays based on schedules (Sub Station Lighting Devices/SSLDs), which accepts [IEC61850 protocol messages](https://documentation.gxf.lfenergy.org/Protocols/IEC61850/index.html "GXF documentation about their implementation of the IEC61850 protocol"). It is developed for [LF Energy's GXF platform](https://documentation.gxf.lfenergy.org/index.html), to enable autonomous switching of relays based on 'switching moments' created out of pre-set schedules.

## How to configure the software

1. Download the source code from the GitHub repository to run within your IDE (it is recommended to use Eclipse)
2. Obtain a copy of the SWDevice-010805.icd file of the device that should get simulated [(GXF offers a webpage with one formatted, copy the XML-based text and paste it into a file with the .icd file extension. Keep in mind this .icd schematic has XSWC2's first schedule enabled, it is recommended to disable this by editing the value of XSWC2's Schedule 1's enabled status!)](https://documentation.gxf.lfenergy.org/Protocols/IEC61850/SWDevice-010805/SWDevice-010805.icd.html)
3. Create a PostgreSQL database to let the program set up its tables. Configure the settings of the PostgreSQL database in ```application.properties``` to have it set up with your database.

## How to use the software
Run the program with the following parameter: ```-m <file path to your .icd file>``` (you can also use ```-p``` to manually override the used port, which is mapped to 10102. Actual IEC61850 devices run on port 102 but this requires root permission on Linux).

When the program is starting, it scans the model file's BasicDataAttribute data to set up the simulated device as a data object (Device, 4 relays and 50 schedules for each relay). After that, it sets the CTLModel attribute of the to simulate device to 1, to allow switching relays on and off.

Then, the program compares the database data with that of the model. It will automatically create the required ```relay``` and ```schedule``` tables, with the right information. Relay statuses and enabled schedules will be inserted into the database if they are missing. After the intial setup, every time a change occurs, both the server's model, OOP classes and the database data will share the changes to allow persistent storage. 

Finally, the program will calculate Switching Moments by looking through enabled schedules. By looking at the days and times which those switching moments should occur, the Scheduler object will schedule tasks which will switch the targeted relay on and off at the calculated times.

While the server is running, you can use the following commands in terminal:

- `p` to print the entire simulated device model the server uses to accept IEC61850 messages for.
- `d` to print the OOP class of device, and its associated relays and enabled schedules
- `h` to show a help message stating the usable commands
- `q` to quit the server and the program

To use the program, send IEC61850 protocol-based messages to the localhost IP address along with the associated port (127.0.0.1:10102 by default). It is recommended to either use [SoapUI](https://www.soapui.org/) with GXF's SoapRequest templates ([example for switching relays](https://documentation.gxf.lfenergy.org/Protocols/IEC61850/SWDevice-010805/SetLight.html), see the GXF documentation for other SoapRequests) or use [IEC61850bean's GUI tool](https://www.beanit.com/iec-61850/) to view the model structure and write the attributes to the server. The EventDataListener will handle the written data messages to automatically update the model in the background while updating the created Device, Relay and Schedule objects before syncing up with the database.

With every data message, logging will iterate through the list of BasicDataAttribute objects of said message to update the model, objects and database with new values.
