package utils;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Utils {

    public static void main(String[] args) {
        Callable<Integer> c = () -> {
            //throw new IllegalArgumentException();
            Thread.sleep(1000000);
            return 3;
        };

        Optional<Integer> ret = callFunctionWithTimeout(c, 3000);

        ret.ifPresent(System.out::println);
        if (ret.isEmpty()) {
            System.out.println("no output");
        }
    }

    public static <T> Optional<T> callFunctionWithTimeout(Callable<T> function, long timeoutMillis) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<T> future = executor.submit(function);

        T returnValue = null;
        try {
             returnValue = future.get(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            future.cancel(true);
        }

        executor.shutdownNow();
        return Optional.ofNullable(returnValue);
    }
}
