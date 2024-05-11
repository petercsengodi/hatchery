package hu.csega.common.juimq;

public abstract class AbstractQueueMessage implements QueueMessage {

    private final String name;
    private final AbstractMessageQueueComponent receiver;
    private final AbstractMessageQueueComponent sender;

    protected AbstractQueueMessage(String name, AbstractMessageQueueComponent receiver, AbstractMessageQueueComponent sender) {
        this.name = name;
        this.sender = sender;
        this.receiver = receiver;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public AbstractMessageQueueComponent getReceiver() {
        return receiver;
    }

    @Override
    public AbstractMessageQueueComponent getSender() {
        return sender;
    }
}
