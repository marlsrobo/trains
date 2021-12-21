# Milestone 10 - Final Integration - Xclients and Xserver  

How to run:

In one command line window, either run `xserver port#` or `xserver port# IPAddress` where port# is the port you would like to connect to, and IPAddress is the IP address you would like to use. If the IPAddress is not provided, the default is local host (127.0.0.1). You must feed JSON as input to the executable, and can be done so by adding `< Tests/json` where `json` is one of the example JSON inputs within the Tests folder.

Then, within 80 seconds of running xserver, in a separate command line window run `xclients port#` or `xclients port# IPAddress` where port# and IPAddress are the same that were used in the xserver command, and also providing the same JSON input as xserver.

The JSON file containing three JSON values should be formatted as the following (in order):  

- a `Map` with at most 20 cities and 40 connections
- an array between 2 and 8 `PlayerInstance`s (inclusive) where the PlayerNames are pairwise distinct
- an array of 250 `Color`s (representing cards)

A `Map` represents the logical and visual layout of a game map and is represented as:  
{ "width"       : `Width`,
  "height"      : `Height`,
  "cities"      : [`City`, ..., `City`],
  "connections" : `Connections` }
  

