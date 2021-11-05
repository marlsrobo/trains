DATE: October 5, 2021  
TO:  Developers in Codemanistan  
FROM: Laura Calderon and Marley Robinson, Northeastern University  
RE: Design for Trains Map Visualization  

In this memo, we will be outlining the design we would like to be implemented
for the visualization of a Map for a game of Trains to be built in Java.

The program should consume a Map (comprised of cities and connections) which contains relevant data to be displayed, and two
integers, height and width, to represent the GUI window size.

To build the visualization, first create a window of dimensions height x width with a black background.

The next step will be to draw all the cities on the Map. To do so,
for each City in the Set of City returned by calling the method .getCities() on the Map, draw a red dot (circle).
To get the coordinates of where to place the dot on the background, use the methods .getX() and .getY() on each City,
multiply the x value by the width, and the y value by the height, and then divide these values by 100. Then, use the 
method .getName() on each City which will return the name of the City as a String, and draw this in white text directly
on top of the red dot. Ensure that the dots and text are of reasonable size; allow both elements to be visible, but not
minuscule or taking up the entire window. Also, ensure that that city name is proportional to the size of the dot. All 
dots should be the same size, as well as the city names.

Finally, the connections must be drawn on the background which will be represented by thin, colored line segments.
To get the relevant data for each Connection, call the .getConnections() method on the Map which will return a Set of
Connection. To get the endpoints for each Connection for where to draw the line on the background, call the .getCities() method on the
Connection which will return an ArrayList of City of length 2. For each City, once again get their coordinates by using
the methodology mentioned above when drawing a City. Then, to reflect the length of the Connection, represent it
by having the line have a corresponding number of dashes (segments) of equal length comprising the line. To get the 
length of the Connection (represented by the number of dashes), call the .getLength() method on the Connection.
For instance, if the length of a Connection is 3, then the line will be composed of 3 line segments of equal length (- - -).
The last step to draw a Connection will be to ensure that the line for the Connection is the correct color. To get the color,
call the .getColor() method on the Connection which returns a Java.awt.Color.

In the end, the visualization will be made up of red dots with white text on top of them (representing cities) 
and colored, dashed lines (representing connections) that connect however many dots that the Map specifies. 
All of these elements should be placed on a black background of the specified height and width.
