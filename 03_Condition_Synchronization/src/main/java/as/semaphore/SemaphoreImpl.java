package as.semaphore;

import java.util.ArrayDeque;
import java.util.Deque;

public final class SemaphoreImpl implements Semaphore {
    private int value;
    private final Object lock = new Object();
    private Deque<Long> waitList;

    public SemaphoreImpl(int initial) {
        if (initial < 0) throw new IllegalArgumentException();
        value = initial;
        waitList = new ArrayDeque<>();
    }

    @Override
    public int available() {
        synchronized (lock) {
            return value;
        }
    }

    @Override
    public void acquire() {
        if (!waitList.isEmpty() ) {
            synchronized (lock) {
                while (available() == 0) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                    }
                }
                waitList.removeFirst();
                System.out.println("Removed"+Thread.currentThread().getId());
                value--;
                lock.notifyAll();
            }
        } else {
            synchronized (lock) {
                while (available() == 0) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                    }
                }
                waitList.addLast(Thread.currentThread().getId());
                System.out.println("Added"+Thread.currentThread().getId());
                value--;
                lock.notifyAll();
            }
        }
    }

    @Override
    public void release() {
        synchronized (lock) {
            value++;
            lock.notifyAll();
        }
    }
}
