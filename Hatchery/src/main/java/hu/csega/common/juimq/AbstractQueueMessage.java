package hu.csega.common.juimq;

public abstract class AbstractQueueMessage implements QueueMessage {

    private final String name;
    private final AbstractMessageReceiver receiver;
    private final AbstractMessageReceiver sender;

    protected AbstractQueueMessage(String name, AbstractMessageReceiver receiver, AbstractMessageReceiver sender) {
        this.name = name;
        this.sender = sender;
        this.receiver = receiver;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public AbstractMessageReceiver getReceiver() {
        return receiver;
    }

    @Override
    public AbstractMessageReceiver getSender() {
        return sender;
    }
}
