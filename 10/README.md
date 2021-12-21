# Milestone 10 - Final Integration - Xclients and Xserver  

### How to Run

In one command line window, either run `xserver port#` or `xserver port# IPAddress` where port# is the port you would like to connect to, and IPAddress is the IP address you would like to use. If the IPAddress is not provided, the default is local host (127.0.0.1). You must feed JSON as input to the executable, and can be done so by adding `< Tests/json` where `json` is one of the example JSON inputs within the Tests folder.

Then, within 80 seconds of running xserver, in a separate command line window run `xclients port#` or `xclients port# IPAddress` where port# and IPAddress are the same that were used in the xserver command, and also providing the same JSON input as xserver.

### JSON Input

The JSON file containing three JSON values should be formatted as the following (in order):  

- a `Map` with at most 20 cities and 40 connections
- an array between 2 and 8 `PlayerInstance`s (inclusive) where the PlayerNames are pairwise distinct
- an array of 250 `Color`s (representing cards)

A `Map` represents the logical and visual layout of a game map and is formatted as:  
```
{ "width"       : Width,  
  "height"      : Height,    
  "cities"      : [City, ..., City],    
  "connections" : Connections }
```
 
`Width` and `Height` are both natural numbers in the range [10, 800] and denote the number of pixels for a `Map`.  

A `City` represents a city's name and location on the map and is formatted as `[Name, [X, Y]]` where X is a natural number in [0, W] and Y is a natural number in [0, H] where W is specified by the "width" field in the `Map` and H is specified by the "height" field in the `Map`.  

CONSTRAINT: No two cities may have the same name or occupy the same spot.  

A `Name` is a string satisfying the natural expression "[a-zA-Z0-9\\ \\.\\,]+" and has at most 25 ASCII characters.  

A `Connections` specifies from where connections originate and end, and the colors and lengths of the connections. It is formatted as:
```
{ Name    : Target,
  ...,
  Name    : Target}
```

CONSTRAINT: Every `Name` must be a member of the `Name`s in the "cities" field and the domain name must be lexicographically before the range's name.

A `Target` specifies where a connection goes and their characteristics. It is formatted as:  

```
{ Name    : Segment,
  ...,
  Name    : Segment}
```  

A `Segment` specifies the length and color of a connection. It is formatted as:

```
{ Color   : Length,
  ...,
  Color   : Length}
```

CONSTRAINT: The colors must be pairwise distinct.

A `Color` is one of "red", "blue", "green", "white" and represents one of the possible colors a connection can be.  

A `Length` is one of 3, 4, or 5 and represents one of the possible lengths of a connection.  

MAP EXAMPLE:

```
{ "width"       : 200,
  "height"      : 350,
  "cities"      : [["Boston", [190, 75]], ["NYC", [160, 90]], ["Los Angeles", [10, 300]], ["Phoenix", [75, 275]]],
  "connections" : {"Boston" : {"NYC" : {"blue" : 3,
                                        "red"  : 5},
                               "Phoenix" : {"green" : 4}},
                   "Los Angeles" : {"Phoenix" : {"white" : 4}}}
}
```

INTERPRETATION: The Map has a width of 200 pixels, and height of 350 pixels. The Map has 4 cities: Boston, NYC, Los Angeles, and Phoenix which have various locations on the Map. Between Boston and NYC, there are 2 direct connections: one that is blue and of length 3, while the other is red and of length 5. There is also a direct connection between Boston and Phoenix that is green of length 4. Finally, there is a direct connection between Los Angeles and Phoenix that is white of length 4.
