## Self-Evaluation Form for TAHBPL/E

A fundamental guideline of Fundamentals I and II is "one task, one
function" or, more generally, separate distinct tasks into distinct
program units.

This assignment comes with three distinct, unrelated tasks.

Indicate below each bullet which file/unit takes care of each task:


1. dealing with command-line arguments  

- Xtcp.java (line 61-63): 
  https://github.ccs.neu.edu/CS4500-F21/bighorn/blob/2bafc06289a81507a408007a0ae89802adff36c5/E/Other/src/Xtcp.java#L61-L63

2. connecting the client on the specified port to the functionality  

- Xtcp.java (lines 27, and 40-43):
  https://github.ccs.neu.edu/CS4500-F21/bighorn/blob/0c9ec5ea86083a5cb98a1e3fddda3aba4b582639/E/Other/src/Xtcp.java#L40-L43
  
3. core functionality (either copied or imported from `C`)  

      a. Xjson.java core functionality (copied over from `C`: lines 11-32): https://github.ccs.neu.edu/CS4500-F21/bighorn/blob/0c9ec5ea86083a5cb98a1e3fddda3aba4b582639/E/Other/src/Xjson.java#L17-L29
  
      b. the call to Xjson's main method from Xtcp (line 46): https://github.ccs.neu.edu/CS4500-F21/bighorn/blob/0c9ec5ea86083a5cb98a1e3fddda3aba4b582639/E/Other/src/Xtcp.java#L46


The ideal feedback for each of these three points is a GitHub
perma-link to the range of lines in a specific file or a collection of
files.

A lesser alternative is to specify paths to files and, if files are
longer than a laptop screen, positions within files are appropriate
responses.

You may wish to add a sentence that explains how you think the
specified code snippets answer the request. If you did *not* factor
out these pieces of functionality into separate functions/methods, say
so.
