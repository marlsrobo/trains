# README #
## TAHBPL:  *D — GUI* ##

**What is xgui?**  
"[...] a program that reads a JSON specification for a small graph from STDIN and pops up a window with a drawing of this graph for 3s. It then shuts down without printing anything to STDOUT.

The graph is specified as a single JSON Graph object as follows:
    { "nodes" : [Posn, ..., Posn],
      "size"  : SIZE }
    
    SIZE is an integer between 100 and 800, and
    Posn is a JSON array of two integers between 0 and SIZE (inclusive).
    
    INTERPRETATION A Posn denotes a point in a SIZE x SIZE grid.
Your program renders each Posn as a small black dot on a SIZE by SIZE canvas of orange color and then draws red lines from every specified Posn to every other, distinct Posn." 

(taken from CS4500 course website)


### Directory structure ###
├── D  
├-----├───Other  
├-----├-------├───src/sample  
├-----├-------├------├───XguiSwing.java  
├-----├-------├------├───Xgui.java  
├-----├-------├------├───Main.java  
├-----├-------├───lib   
├-----├-------├───input.json  
├-----├-------├───input2.json  
├-----├───README.md  
├-----├───xgui  

### Folder/File Details ###  
**D** (main folder)  
* xgui -- a shell script that runs the main Java program.  

**Other** (auxiliary files folder)  
*src/sample*  
  * Xgui.java -- a Java file that contains the code using JavaFx library 
  * Main.java -- a Java file with the main method that runs the Xgui code
  * XguiSwing.java -- a Java file with the reimplemented code using Java Swing (**this is what we are running our program with**) 

*lib*  
(contains all the relevant libraries .jar files)  
  * gson-2.8.8-sources  
  * gson-2.8.8

*input.json and input2.json*  
  * two .json files with a valid, wellformed json we used to test our application

### How to run ###  
You run the program by navigating to the D directory and running 
> ./xgui

However, you may want to pipe in a well-formed, valid json value:

> echo "{ "nodes" : [Posn, ..., Posn], "size"  : SIZE }" | ./xgui

Or you may want to give premade .json files directly to the program. In which case, you would run it like this:  

> ./xgui < input_file.json 

Note: you should mark the xjson shell script as executable before running again if there are any changes made to Xjson.java.  

> chmod +x xgui

### Considerations and other information ### 
As the website describes, "The word 'well-formed' means that each element of the sequence satisfies the JSON grammar." Thus, there is actually no error handling that happens for a malformed JSON, the library would throw whatever malformed error it desires. 

Also, we did not need to check the SIZE or Posn data types to ensure that they were within the constraints specified due to the interpretation that we would always be given well-formed JSON Graphs.

The two main libraries that were used for this assignment were GSON and Java Swing. GSON was used in the same manner as assignment C to parse JSON data and convert it into useful objects. 

Swing was used for all of the GUI aspects of our code which included creating a window/canvas, customizing and adding shapes to the canvas, and delaying the closure of the window by 3 seconds. However, in the begnning we chose JavaFx. Unfortunately, we had issues running it with the Linux machines, so we reimplemented everything using Swing instead. 

JavaFx appeared to us to be much more straightforward and the UI components had a more modern feel to them, but ultimately Swing was more reliable.
