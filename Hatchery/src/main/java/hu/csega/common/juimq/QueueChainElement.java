package hu.csega.common.juimq;

class QueueChainElement<T> {
    QueueChainElement<T> previous;
    QueueChainElement<T> next;
    T content;
}
