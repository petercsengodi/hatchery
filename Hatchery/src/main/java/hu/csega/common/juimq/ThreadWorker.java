package hu.csega.common.juimq;

public class ThreadWorker implements Runnable {

    private final String id;
    private final MessageQueueHandler parent;

    ThreadWorker(String id, MessageQueueHandler parent) {
        this.id = id;
        this.parent = parent;
    }

    @Override
    public void run() {
        try {
            System.out.println("Thread [ " + id + "] started.");
            while (parent.active) {
                QueueMessage message = parent.nextMessage();
                if(message != null) {
                    AbstractMessageQueueComponent receiver = message.getReceiver();
                    AbstractMessageQueueComponent sender = message.getSender();
                    String name = message.getName();
                    System.out.println("Message from " + nameOf(sender) + " to " + nameOf(receiver) + ": " + name);
                } else {
                    parent.wait(10000);
                }
            }

            System.out.println("Thread [ " + id + "] stopped.");
        } catch(InterruptedException ex) {
            System.out.println("Thread [ " + id + "] interrupted.");
        }
    }

    private String nameOf(AbstractMessageQueueComponent component) {
        if(component == null) {
            return "unknown";
        }

        String name = component.getName();
        if(name == null || name.isEmpty()) {
            return "unnamed";
        }

        return name;
    }
}
