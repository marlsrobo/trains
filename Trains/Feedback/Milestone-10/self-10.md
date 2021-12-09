## Self-Evaluation Form for Milestone 10

Indicate below each bullet which file/unit takes care of each task.

The `remote proxy patterns` and `server-client` implementation calls for several
different design-implementation tasks. Point to each of the following: 

1. the implementation of the `remote-proxy-player`

	With one sentence explain how it satisfies the player interface. 

https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/db2bdb60006cf37db9a02a84adc4d2b0c45e49d0/Trains/Remote/remote/ProxyPlayer.java#L27  
ProxyPlayer implements the IPlayer interface, which contains all of the methods that both the tournament manager and referee call on players.


2. the unit tests for the `remote-proxy-player`   
https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/db2bdb60006cf37db9a02a84adc4d2b0c45e49d0/Trains/Other/UnitTests/UnitTestClasses/TestProxyPlayer.java#L42  


3. the `server` and especially the following two pieces of factored-out
   functionality:   
   
The server is at:  
https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/db2bdb60006cf37db9a02a84adc4d2b0c45e49d0/Trains/Remote/remote/Server.java#L33

   - signing up enough players in at most two rounds of waiting  
One round of waiting is factored out here:  

https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/db2bdb60006cf37db9a02a84adc4d2b0c45e49d0/Trains/Remote/remote/Server.java#L218-L249  

But we did not factor out both waiting periods together, they are performed here:  

https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/db2bdb60006cf37db9a02a84adc4d2b0c45e49d0/Trains/Remote/remote/Server.java#L158-L162  

   - signing up a single player (connect, check name, create proxy)  

Signing up one player is performed in a seperate thread that is running this clientHandler.  
https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/db2bdb60006cf37db9a02a84adc4d2b0c45e49d0/Trains/Remote/remote/Server.java#L251-L319


4. the `remote-proxy-manager-referee`  
https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/db2bdb60006cf37db9a02a84adc4d2b0c45e49d0/Trains/Remote/remote/ProxyServer.java#L28

	With one sentence, explain how it deals with all calls from the manager and referee on the server side.   

ProxyServer will receive all calls from the manager and referee as JSON messages over the socket or input stream that it is given at construction. It then parses the JSON for the method name and arguments, and calls it on the underlying IPlayer.




The ideal feedback for each of these three points is a GitHub
perma-link to the range of lines in a specific file or a collection of
files.

A lesser alternative is to specify paths to files and, if files are
longer than a laptop screen, positions within files are appropriate
responses.

You may wish to add a sentence that explains how you think the
specified code snippets answer the request.

If you did *not* realize these pieces of functionality, say so.

