import java.net.*;
import java.io.*;

/***
 * Initializes server and accepts input from a client.
 * Redirects the input to STDIN and calls Xjson for processing.
 */
public class Xtcp {
  private ServerSocket server;
  private static int port = 45678;
  private final static int TIMEOUT = 3000;

  /***
   * Constructor that starts the server and redirects client input.
   *
   * @param port the port number for setting up the ServerSocket.
   */
  private Xtcp(int port) {
    try {
      Socket client;
      try {
        // Start the server
        server = new ServerSocket(port);
        // Set a timeout after 3 seconds (if there's no connection)
        server.setSoTimeout(TIMEOUT);
        // Connect to client
        client = server.accept();
      }
      catch (InterruptedIOException e) {
        System.out.println("xtcp: timed out after 3 seconds.");
        // Close the server if no connection made within 3 seconds
        if (server != null) {
          server.close();
        }
        return;
      }

      // Set STDIN to be the input stream from the client
      // and set STDOUT to be the output stream of the client
      InputStream in = client.getInputStream();
      PrintStream out = new PrintStream(client.getOutputStream());
      System.setIn(in);
      System.setOut(out);

      // perform the json operation which will print to STDOUT
      Xjson.main(null);

    }
    catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }

  /***
   * Creates a Xtcp server with correct port number
   *
   * @param args port number (if any)
   */
  public static void main(String[] args) {
    // Set the port number to the first argument, if there is one.
    if (args.length == 1) {
      port = Integer.parseInt(args[0]);
    }
    Xtcp server = new Xtcp(port);
  }
}
