package hu.csega.common.juimq;

public interface QueueMessage {

    String getName();
    AbstractMessageReceiver getReceiver();
    AbstractMessageReceiver getSender();

}
