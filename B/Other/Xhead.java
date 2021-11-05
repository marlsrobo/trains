import java.util.Scanner;

/**
 * Re-directs specified number of lines from STDIN to STDOUT.
 */
public class Xhead {

    // printLines: int -> void
    // Prints every line in STDIN up to the max number of lines specified or until no lines are left in STDIN.
    private static void printLines(int numLines) {
        if(numLines < 0) {
            System.out.println("error");  // the program prints the word "error" to STDOUT for negative numLines.
            return;
        }

        Scanner scanner = new Scanner(System.in);
         
        // We can use Scanner here to go through each line in STDIN and print them up to our max!
        for (int i = 0; i < numLines; i++) {    // stop at our max numLines
            if (scanner.hasNextLine()){         // check if there is a new line to print
                System.out.println(scanner.nextLine());     // print every line until numLines
            }
            else {
                return;
            }
        }
    }

    // Takes input stream from STDIN and prints out the specified number of lines given in the command line arguments.
    public static void main(String[] args) {
        try {
            if(args[0].charAt(0) == '-') {      // Check that first argument is prefixed with a dash ("-")
                int maxLines = Integer.parseInt(args[0].substring(1));  // Parse as an int
                printLines(maxLines);
            }
            else {
                System.out.println("error");    // the program prints the word "error" to STDOUT on malformed args.
            }
        } catch (Exception e) {
            System.out.println("error");        // the program prints the word "error" to STDOUT on malformed/missing args.
        }
    }
}
