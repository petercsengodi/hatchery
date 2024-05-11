package hu.csega.common.juimq;

public abstract class AbstractMessageQueueComponent implements MessageQueueComponent {

    protected AbstractMessageQueueComponent(MessageQueueHandler parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    private final MessageQueueHandler parent;
    private final String name;

    public String getName() {
        return name;
    }

    protected void sendMessage(QueueMessage message) {
        if(!parent.active) {
            parent.start();
        }

        parent.putMessage(message);
    }

}
