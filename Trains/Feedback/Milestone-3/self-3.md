## Self-Evaluation Form for Milestone 3

Indicate below each bullet which file/unit takes care of each task:

1. explain how your main visualization method/function 

   - manages the timed tear down of the visualization window

It does not have a timed tear down, instead we chose to let the user close the window by setting the exit on close option on the JFrame containing the visualization. This option will cause our visualization program to exit when the user closes the visualization.   
https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Common/map_view/MapFrame.java#:~:text=frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)%3B   

   - calls out to the drawing the graph itself 

The Map data is given to a JPanel, which then calls the drawing of the graph from its paintComponent() override  
https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Common/map_view/MapPanel.java#:~:text=this.mapRenderer.render((Graphics2D)%20g%2C%20map)%3B  
The actual drawing occurs in MapRenderer.render(), which takes in a graphics and draws the map data onto it  
https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Common/map_view/MapRenderer.java#:~:text=public%20void%20render(Graphics2D%20g%2C%20ITrainMap%20map)%20%7B  


2. point to the functionality for adding cities to the visualized map

- This optional task was not realized

3. point to the functionality for adding connections to the visualized map

- This optional task was not realized

The ideal feedback for each of these three points is a GitHub
perma-link to the range of lines in a specific file or a collection of
files.

A lesser alternative is to specify paths to files and, if files are
longer than a laptop screen, positions within files are appropriate
responses.

You may wish to add a sentence that explains how you think the
specified code snippets answer the request.

If you did *not* realize these pieces of functionality, say so.

