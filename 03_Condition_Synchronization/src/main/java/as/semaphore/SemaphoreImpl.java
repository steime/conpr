package as.semaphore;

public final class SemaphoreImpl implements Semaphore {
    private int value;
    private final Object lock = new Object();

    public SemaphoreImpl(int initial) {
        if (initial < 0) throw new IllegalArgumentException();
        value = initial;
    }

    @Override
    public int available() {
        synchronized (lock) {
            return value;
        }
    }

    @Override
    public void acquire() {
        synchronized (lock) {
            while (available() == 0) {
                try { lock.wait();}
                catch (InterruptedException e) {}
            }
            value--;
            lock.notifyAll();
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
