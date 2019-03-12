package main.java;

import org.apache.commons.lang.NotImplementedException;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RequestQueue implements BlockingQueue {

    private final Lock lock = new ReentrantLock();
    private Queue<Object> queue = new ArrayDeque<>();
    private int size;

    // Unsure about this, need to understand idea better.
    public void waitForElevator() {
        lock.lock();
    }

    @Override
    public boolean add(Object o) {
        this.size++;
        return queue.add(o);
    }

    @Override
    public boolean offer(Object o) {
        return queue.offer(o);
    }

    @Override
    public Object remove() {
        if (size > 0) {
            size--;
            return queue.remove();
        }
        return null;
    }

    @Override
    public Object poll() {
        return queue.poll();
    }

    @Override
    public Object element() {
        return queue.element();
    }

    @Override
    public Object peek() {
        return queue.peek();
    }

    @Override
    public void put(Object o) {
        throw new NotImplementedException();
    }

    @Override
    public boolean offer(Object o, long timeout, TimeUnit unit) {
        throw new NotImplementedException();
    }

    @Override
    public Object take() {
        throw new NotImplementedException();
    }

    @Override
    public Object poll(long timeout, TimeUnit unit) {
        throw new NotImplementedException();
    }

    @Override
    public int remainingCapacity() {
        throw new NotImplementedException();
    }

    @Override
    public boolean remove(Object o) {
        throw new NotImplementedException();
    }

    @Override
    public boolean addAll(Collection c) {
        throw new NotImplementedException();
    }

    @Override
    public void clear() {
        while(size() > 0) {
            this.remove();
        }
    }

    @Override
    public boolean retainAll(Collection c) {
        throw new NotImplementedException();
    }

    @Override
    public boolean removeAll(Collection c) {
        throw new NotImplementedException();
    }

    @Override
    public boolean containsAll(Collection c) {
        throw new NotImplementedException();

    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        throw new NotImplementedException();
    }

    @Override
    public Iterator iterator() {
        throw new NotImplementedException();
    }

    @Override
    public Object[] toArray() {
        throw new NotImplementedException();
    }

    @Override
    public Object[] toArray(Object[] a) {
        throw new NotImplementedException();
    }

    @Override
    public int drainTo(Collection c) {
        throw new NotImplementedException();
    }

    @Override
    public int drainTo(Collection c, int maxElements) {
        throw new NotImplementedException();
    }
}
