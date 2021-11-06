import com.google.gson.*;
import java.util.Scanner;

/*
  Class for our xjson main method which will reverse the contents of the JSON data from STDIN
  and print to STDOUT
 */
public class Xjson {

    // Reverses the series of JSON from STDIN and outputs the reversed JSON to STDOUT
    public static void main(String[] args) {

        // TODO: should we be checking if args[] is empty since we just want STDIN?

        // getting STDIN input and converting to string
        Scanner scanner = new Scanner(System.in);
        StringBuilder jsonString = new StringBuilder();

        while (scanner.hasNext()) {
            jsonString.append(scanner.next());
        }

        // parse JSON string
        JsonStreamParser parsedJson = new JsonStreamParser(jsonString.toString());

        // while the parsed json has more json elements elements, reverse each one and print to STDOUT
        while (parsedJson.hasNext()) {
            JsonElement element = parsedJson.next();
            JsonElement reversedJson = reverseJSON(element);
            System.out.println(reversedJson);
        }

        scanner.close();
    }

    // Reverses and returns the JSONElement appropriately based on what subclass of JsonElement it is
    private static JsonElement reverseJSON(JsonElement json) {

        JsonElement buildReverse = new JsonObject();

        // Reverse each field in the JsonObject
        if (json.isJsonObject()) {
            JsonObject object = json.getAsJsonObject();
            for (String k : object.keySet()) {
                buildReverse.getAsJsonObject().add(k, reverseJSON(object.get(k)));
            }
            return buildReverse;
        }
        // Reverse the order of the JsonArray and the contents of each slot
        else if (json.isJsonArray()) {
            JsonArray array = json.getAsJsonArray();
            JsonArray reversedArray = new JsonArray();
            for (int i = array.size() - 1; i >= 0; i--) {
                reversedArray.add(reverseJSON(array.get(i)));
            }
            return reversedArray;
        }
        // check what kind of primitive the JSONPrimitive is (Number, Boolean, String)
        else if (json.isJsonPrimitive()) {
            JsonPrimitive primitive = json.getAsJsonPrimitive();
            // negate the Number
            if (primitive.isNumber()) {
                Number numberVal = primitive.getAsNumber();
                // Check the instance of the Number to see if its an int, double, float, etc.
                Number reversedVal;
                if (numberVal instanceof Integer) {
                    System.out.println("integer");
                    reversedVal = numberVal.intValue() * -1;
                } else if (numberVal instanceof Long) {
                    System.out.println("long");
                    reversedVal = numberVal.longValue() * -1;
                } else if (numberVal instanceof Double) {
                    System.out.println("double");
                    reversedVal = numberVal.doubleValue() * -1;
                } else {
                    System.out.println("float");
                    reversedVal = numberVal.floatValue() * -1;
                }
                return new JsonPrimitive(reversedVal);
            }
            // "not" the Boolean
            else if (primitive.isBoolean()) {
                Boolean reversedVal = !primitive.getAsBoolean();
                return new JsonPrimitive(reversedVal);
            }
            // reverse the characters in the String
            else if (primitive.isString()) {
                String originalVal = primitive.getAsString();
                String reversedVal = new StringBuilder(originalVal).reverse().toString();
                return new JsonPrimitive(reversedVal);
            }
        }
        // return JsonNull
        else if (json.isJsonNull()) {
            return json;
        }
        return json;
    }
}