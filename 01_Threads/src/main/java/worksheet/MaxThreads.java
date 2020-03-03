package worksheet;

public class MaxThreads {
    public static void main(String[] args) throws InterruptedException{
        int i = 0;
        while(true) {
            new Thread(new MaxRunnable()).start();
            System.out.println(++i);
        }
    }

    static class MaxRunnable implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException e) {

            }
        }
    }
}