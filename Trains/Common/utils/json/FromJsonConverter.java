package utils.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import map.Destination;
import map.ITrainMap;
import utils.Constants;
import utils.UnorderedPair;

import java.util.HashSet;
import java.util.Set;

public class FromJsonConverter {

    public static Set<UnorderedPair<String>> fromJsonToUnvalidatedSetOfDestinations(JsonElement json) {
        JsonArray jsonAsArray = json.getAsJsonArray();
        Set<UnorderedPair<String>> destinationsNames = new HashSet<>();
        for (JsonElement jsonDestinationNames : jsonAsArray) {
            destinationsNames.add(fromJsonToUnvalidatedDestination(jsonDestinationNames));
        }
        return destinationsNames;
    }

    public static UnorderedPair<String> fromJsonToUnvalidatedDestination(JsonElement json) {
        JsonArray jsonAsArray = json.getAsJsonArray();
        if (jsonAsArray.size() != 2) {
            throw new IllegalArgumentException("Incorrect number of cities within a destination");
        }
        return new UnorderedPair<>(jsonAsArray.get(0).getAsString(), jsonAsArray.get(1).getAsString());
    }
}
