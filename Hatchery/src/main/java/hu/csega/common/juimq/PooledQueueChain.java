package hu.csega.common.juimq;

public class PooledQueueChain<T> {

    public void add(T content) {
        QueueChainElement<T> chainElement;
        if(firstUnused != null) {
            chainElement = firstUnused;
            firstUnused = chainElement.next;
        } else {
            chainElement = new QueueChainElement<>();
        }

        chainElement.content = content;
        chainElement.next = null;
        if(lastUsed == null) {
            chainElement.previous = null;
            firstUsed = lastUsed = chainElement;
        } else {
            lastUsed.next = chainElement;
            chainElement.previous = lastUsed;
        }
    }

    public T get() {
        if(firstUsed == null) {
            return null;
        }

        QueueChainElement<T> tmp = firstUsed;
        T ret = tmp.content;
        firstUsed = tmp.next;
        if(firstUsed == null) {
            lastUsed = null;
        }

        tmp.content = null;
        tmp.previous = null;
        tmp.next = firstUnused;
        firstUnused = tmp;

        return ret;
    }

    public void clear() {
        while(firstUsed != null) {
            firstUsed.content = null;
            firstUsed.next = firstUnused;
            firstUsed.previous = null;
            firstUnused = firstUsed;
            firstUsed = firstUsed.next;
        }

        lastUsed = null;
    }

    private QueueChainElement<T> firstUsed;
    private QueueChainElement<T> lastUsed;
    private QueueChainElement<T> firstUnused;
}
