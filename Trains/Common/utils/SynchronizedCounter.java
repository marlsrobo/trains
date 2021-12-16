package utils;

/**
 * A monotonically increasing integer counter that is both synchronized and safe to use across
 * threads.
 *
 * Copied from the Synchronized Method Oracle Java Tutorial:
 *  https://docs.oracle.com/javase/tutorial/essential/concurrency/syncmeth.html
 */
public class SynchronizedCounter {

    private volatile int counter;

    public SynchronizedCounter() {
        this.counter = 0;
    }

    public synchronized void increment() {
        counter++;
    }

    public synchronized int get() {
        return counter;
    }
}
