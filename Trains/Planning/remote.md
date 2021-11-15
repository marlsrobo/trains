# Remote Collaboration Protocol

![Remote Collaboration Protocol](remote-collaboration-protocol.jpeg)

## JSON Formats
The JSON messages passed between the client and server, and client and ProxyPlayer are sent using the following formats.
### Player Name as JSON
```
A Player Name is a non-empty String of maximally 50 lower- or upper-case alphabetical chars ([A-Za-z]).  
```
### Begin Tournament as JSON
```
A Begin Tournament is a Boolean.
```
### Map as JSON
```
Map is an object:
{ "width"        : Width,
  "height"       : Height,
  "cities"       : [City,..,City],
  "connections"  : Connections }

Width and Height are natural numbers in [10, 800] and [10, 800]

A City is [Name, [X, Y]] where X is a natural number 
in [0,W] and Y is a natural number in [0,H] with W and H as specified
in the map object's "width" and "height" fields, respectively.

A Connection Connections is an object whose domain elements are Names
and whose range elements are Targets.

A Name is a String that satisfies the regular expression "[a-zA-Z0-9\\ \\.\\,]+"
 and has at most 25 ASCII characters.

A Target is an object whose domain elements are Names 
and whose range elements are Segments.

A Segment is an object whose domain elements are Colors
and whose range elements are Lengths.

A Color is one of ("red", "blue", "green", "white").

A Length is one of (3, 4, 5).
```
### Setup as JSON
```
Setup is an object:
{ "map"   : Map,
  "rails" : Natural,
  "cards" : Card* }

Map is defined above.

A Card* is an object whose domain elements are Colors
and whose range elements are natural numbers.

A Natural is a natural number.

Color is defined above.
```
### Destinations as JSON
```
[Destination,...,Destination]

A Destination is [Name, Name]

Name is defined above.
```
### Player State as JSON
```
Player State is an object:
{ "this"     : ThisPlayer, 
  "acquired" : [Player, ..., Player] }

A ThisPlayer is an object:
{ "destination1" : Destination,
  "destination2" : Destination,
  "rails"        : Natural,
  "cards"        : Card*,
  "acquired"     : Player }

Destination is defined above.

Natural is defined above.

Card* is defined above.

A Player is an Array:
[Acquired,..,Acquired]

An Acquired is [Name, Name, Color, Length]

Name is defined above.

Color is defined above.

Length is defined above.
```
### More cards as JSON
```
More Cards is the String "more_cards"
```
### Cards as JSON
```
A Cards is a Card*.
```
### Connection as JSON
```
A Connection is an Acquired.
```
### Game Result as JSON
```
A Game result is a Boolean.
```
### Tournament Result as JSON
```
A Tournament Result is a Boolean.
```

## Explanation
The new components in this diagram are the ProxyPlayer, the Client, and the Server.  

The Client is a customer of our company, and has implemented a player that they would like to 

The Server

The ProxyPlayer is responsible for sending and re