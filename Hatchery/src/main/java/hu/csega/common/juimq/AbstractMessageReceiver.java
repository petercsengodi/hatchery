package hu.csega.common.juimq;

public abstract class AbstractMessageReceiver implements MessageReceiver {

    protected AbstractMessageReceiver(MessageQueue parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    private final MessageQueue parent;
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
