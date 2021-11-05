# README #
**What is xjson?**  
*"a program that consumes a series of well-formed JSON from STDIN and delivers an equally long series of JSON to STDOUT. [...] For each element, xjson applies a “reverse” operation before it outputs them again" (taken from CS4500 course website)*
## TAHBPL:  *C — JSON* ##

### Directory structure ###
├── C  
├-----├───Other  
├-----├-------├───src  
├-----├-------├------├───Xjson.java  
├-----├-------├------├───Xjson.class  
├-----├-------├───lib  
├-----├-------├───UnitTests  
├-----├-------├------├───xjsonTests.java  
├-----├───Tests  
├-----├-------├───1-in.json  
├-----├-------├───1-out.json  
├-----├-------├───2-in.json  
├-----├-------├───2-out.json  
├-----├-------├───3-in.json  
├-----├-------├───3-out.json  
├-----├───README.md  
├-----├───xjson  

### Folder/File Details ###  
**C** (main folder)  
* xjson -- a shell script that runs the main Java program.  

**Other** (auxiliary files folder)  
*src*  
  * Xjson.java -- a Java file that contains the main method.   
  * Xjson.class -- a compiled .class file.   

*lib*  
(contains all the libraries .jar files)  
  * gson-2.8.8-sources  
  * gson-2.8.8  
  there are the two main ones!  

*UnitTests*  
  * xjsonTests.java -- a Java file that contains all the junit tests for our Xjson java program  

### How to run ###  
You run the program by navigating to the C directory and running 
> ./xjson

You can directly type input to STDIN while the program is running and produce an EOF character for the program to know when to stop
taking input in (this ensures there is no "crash" if a wellformed JSON is in the middle of its creation). It will redirect the output
to STOUT.  

Additionally, you may want to give premade .json files directly to the program. In which case, you would run it like this:  

> ./xjson < input_file.json 

And you could also specify an output file which populates with the resulting reversed JSON. A new file will be created if it doesn't already exist.  

> ./xjson < input_file.json > output_file.json

Note: you should mark the xjson shell script as executable before running again if there are any changes made to Xjson.java.  

> chmod +x xjson

### Considerations and other information ### 
As the website describes, "The word 'well-formed' means that each element of the sequence satisfies the JSON grammar." Thus, there is actually no error handling that happens for a malformed JSON, the library would throw whatever malformed error it desires. 

The library we chose to complete this assignment, GSON, comes with a lot of features. Mainly, it allowed us to take in json and transform it into a JSONElement, which is one of:
* JSONObject
* JSONArray
* JSONNull
* JSONPrimitive (which could be a JSONNumber, a JSONBoolean or a JSONString)

One thing we thought about was how we would reverse numbers. We use doubles in our solution, but there is a bit to think about here. We shouldn't necessarily be assuming that's what we'd do to move forward. But for a JSONNumber we have to specify what type we want to use (int, double, float, etc.) so we can multiply it by -1 in our solution. I guess we're still thinking about how this might fail in the future and considering other options. 
