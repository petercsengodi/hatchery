package hu.csega.common.juimq;

public interface MessageReceiver {

    void process(QueueMessage message) throws InterruptedException;

}
