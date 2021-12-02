package utils;

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
