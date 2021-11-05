# README #
## TAHBPL:  *E — TCP* ##

**What is xtcp?**  
"[...] a program that allows a single TCP client to connect. It then consumes a series of JSON values from the input side of this TCP connection and delivers JSON to the output side of a TCP connection. Once the connection is closed, the server shuts down."

(taken from CS4500 course website)


### Directory structure ###
├── E  
├-----├───Other  
├-----├-------├───src  
├-----├-------├------├───Xtcp.java  
├-----├-------├------├───Xjson.java  
├-----├-------├───lib  
├-----├-------├------├───gson-2.8.8-sources.jar  
├-----├-------├------├───gson-2.8.8.jar  
├-----├───Tests  
├-----├-------├───1-in.json  
├-----├-------├───1-out.json  
├-----├-------├───2-in.json  
├-----├-------├───2-out.json  
├-----├-------├───3-in.json  
├-----├-------├───3-out.json  
├-----├───README.md  
├-----├───xtcp  

### Folder/File Details ###  
**E** (main folder)  
* xtcp -- a shell script that runs the main Java program.  

**Other** (auxiliary files folder)  
*src*  
  * Xtcp.java -- a Java file that handles the TCP connection
  * Xjson.java -- a Java file that handles the JSON operations

*lib*  
(contains all the relevant libraries .jar files)  
  * gson-2.8.8-sources  
  * gson-2.8.8

**Tests** (test files folder) 
  * 3 tests for the JSON input/output

### How to run ###  
You run the program by first navigating to the E directory and running either
> ./xtcp  
> ./xtcp portNumber

to start the server connection. If the executable is not given a port number as an argument, then the default port number of 45678 is used.

Then, on a separate computer which will act as the client, within 3 seconds you must run
> cat jsonFile | nc IP portNumber

which will pipe the contents of the JSON file into nc which will then send those contents to the server if the port number matches that of the server.
If the server does not receive a connection from a client within 3 seconds, the server shuts down and outputs an informative message.

Note: you should mark the xjson shell script as executable before running again if there are any changes made to Xtcp.java.  

> chmod +x xtcp

### Considerations and other information ### 
Due to the nature of these exploratory assignments, we are assuming that we receive all valid inputs.

This means that we assume the JSON input is valid JSON and follows the criteria set forth in assignment C.

Also, we assume that for the TCP connection we are given a port number between 2048 and 65535.

For libraries for the TCP connection, we used java.net and java.io which are built in libraries for Java. We did not consider any other libraries because these seemed to work perfectly and it was convenient to not have to import any external libraries.
