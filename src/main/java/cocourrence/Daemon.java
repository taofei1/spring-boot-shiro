package cocourrence;
public class Daemon {
    public static void main(String[] args) {
        Thread thread = new Thread(new DaemonRunner(), "DaemonRunner");
        Thread thread2 = new Thread(new DaemonRunner(), "DaemonRunner2");
        thread.setDaemon(true);
        thread.start();
      //  thread2.start();
    }
    static class DaemonRunner implements Runnable {
        @Override
        public void run() {
            try {
                SleepUtils.second(10);
            } finally {
                System.out.println("DaemonThread finally run.");
            }
        }
    }
}