package hu.csega.common.juimq;

public interface MessageQueueComponent {

    void process(QueueMessage message) throws InterruptedException;

}
