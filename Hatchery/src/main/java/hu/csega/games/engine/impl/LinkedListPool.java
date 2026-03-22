package hu.csega.games.engine.impl;

import java.lang.reflect.InvocationTargetException;

public class LinkedListPool<T> {

    public LinkedListPool(Class<T> usedClass, int initialNumber, Integer maxNumber) {
        this.usedClass = usedClass;
        if(initialNumber > 0) {
            for(int i = 0; i < initialNumber; i++)
                release(create());
        }
        this.maxNumber = maxNumber;
    }

    public int getCounter() {
        return counter;
    }

    public synchronized T allocate() {
        LinkedListPoolItem<T> item;
        T result;

        if(releasedBucket != null) {
            item = releasedBucket;
            result = item.value;
            releasedBucket = releasedBucket.next;
        } else {
            item = new LinkedListPoolItem<>();
            result = create();
        }

        item.value = null;
        item.next = allocatedBucket;
        allocatedBucket = item;
        return result;
    }

    public synchronized void release(T object) {
        LinkedListPoolItem<T> item;

        if(allocatedBucket != null) {
            item = allocatedBucket;
            allocatedBucket = allocatedBucket.next;
        } else {
            item = new LinkedListPoolItem<>();
        }

        item.value = object;
        item.next = releasedBucket;
        releasedBucket = item;
    }

    public T create() {
        try {
            counter++;
            if(maxNumber != null && counter > maxNumber)
                throw new RuntimeException("LinkedListPool with " + usedClass.getName() + " exceeded the maximum number of " + maxNumber);
            return usedClass.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            throw new RuntimeException("Could not call default constructor of " + usedClass.getName(), ex);
        }
    }

    private LinkedListPoolItem<T> allocatedBucket;
    private LinkedListPoolItem<T> releasedBucket;
    private Class<T> usedClass;
    private Integer maxNumber;
    private int counter;
}
