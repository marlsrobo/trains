## MAP DESIGN  

### Map  
#### Attributes:
- Cities: a dictionary of city names (String) to City representing all of the cities within a Map
- Connections: a set of Connection representing the direct rail connections between 2 cities on the Map


### Connection: a direct connection between 2 cities on the Map

#### Attributes:
- Color: the ConnectionColor of the Connection
- Length: the length of the Connection (one of 3, 4, or 5)
- Cities: the pair of cities on either end of this Connection (set of length 2)

### City: a city (point) on the Map

#### Attributes:
- Name (String): the name of the City
- X (integer): the relative x-coordinate of this City on the Map (between 0 and 100 inclusive)
- Y (integer): the relative y-coordinate of this City on the Map (between 0 and 100 inclusive)

### ConnectionColor (enum): the possible color of a Connection
- Red: the color red
- Blue: the color blue
- Green: the color green
- White: the color white
