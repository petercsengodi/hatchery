package hu.csega.common.juimq;

public interface QueueMessage {

    String getName();
    AbstractMessageQueueComponent getReceiver();
    AbstractMessageQueueComponent getSender();

}
