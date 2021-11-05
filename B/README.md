# README #
**What is xhead?**  
*"A program that re-directs a command-line-specified number of lines from STDIN to STDOUT" (taken from CS4500 course website)*
## TAHBPL:  *B — The Very Basics* ##

### Directory structure ###
├── B  
├-----├───Other  
├-----├-------├───Xhead.java  
├-----├-------├───Xhead.class  
├-----├-------├───xheadTest.java  
├-----├───README.md  
├-----├───xhead 

### Folder/File Details ###  
**B** (main folder)
* xhead -- a shell script that runs the Java program.

**Other** (auxiliary files folder)
* Xhead.java -- a Java file that contains the main method. 
* Xhead.class -- a compiled .class file. 
* xheadTest.java -- a Java class file that contains unit tests for the Xhead class/methods.

### How to run ###  
You run the program by navigating to the B directory and running 
>./xhead arg

where arg is a natural number prefixed with a dash (e.g. "-1", "-52", or "-100") 
denoting the *number-of-lines* to redirect to STOUT. 

You can directly type input to STDIN while the program is running, and it will 
output to STOUT until you've run out of *number-of-lines*.

Additionally, you may want to use the unix pipe command to redirect STDIN input to 
xhead. For example, the output of this command would be the first 11 lines of the 
result of the linux command "ls -l /":

> ls -l / | ./xhead -11

If the output of the command being piped contains fewer lines than 
the specified *number-of-lines* in the argument, then the resulting lines will 
be completely redirected to STOUT.
