package hu.csega.common.juimq;

public class MessageQueueHandler {

    private static final int NUMBER_OF_THREADS = 20;

    private final PooledQueue<QueueMessage> messages = new PooledQueue<>();

    private Thread[] threads = null;
    boolean active = false;

    public synchronized void putMessage(QueueMessage message) {
        messages.add(message);
        this.notify();
    }

    synchronized QueueMessage nextMessage() {
        return messages.get();
    }

    public synchronized void start() {
        if(active)
            return;

        this.active = true;

        threads = new Thread[NUMBER_OF_THREADS];
        for(int i = 0; i < NUMBER_OF_THREADS; i++) {
            String id = String.valueOf(i);
            while(id.length() < 3) {
                id = '0' + id;
            }

            Thread thread = new Thread(new ThreadWorker(id, this));
            thread.start();
            threads[i] = thread;
        }
    }

    public synchronized void stop() {
        if(!active)
            return;

        this.active = false;
        this.notifyAll();

        if(threads != null) {
            for(Thread thread : threads) {
                if(thread != null) {
                    try {
                        thread.join();
                    } catch(InterruptedException ex) {
                        return;
                    }
                }
            }

            threads = null;
        }
    }

}
