package utils;

import java.util.Optional;
import java.util.concurrent.Callable;

public class Utils {

    /**
     * Call a function, and return its return value. If the method throws an exception or returns
     * null, this method will return an empty optional.
     *
     * @param function The function to call.
     * @param <T>      The return type of the function.
     * @return The value that the function returned, or empty if it threw an exception or returned
     * null.
     */
    public static <T> Optional<T> callFunction(Callable<T> function) {
        try {
            return Optional.ofNullable(function.call());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
