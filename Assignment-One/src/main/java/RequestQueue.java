package main.java;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class RequestQueue implements BlockingQueue {

    private Queue<Object> queue = new ArrayDeque<>();
    private int size;

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

    }

    @Override
    public boolean offer(Object o, long timeout, TimeUnit unit) {
        return false;
    }

    @Override
    public Object take() {
        return null;
    }

    @Override
    public Object poll(long timeout, TimeUnit unit) {
        return null;
    }

    @Override
    public int remainingCapacity() {
        return 0;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean addAll(Collection c) {
        return false;
    }

    @Override
    public void clear() {
        while(size() > 0) {
            this.remove();
        }
    }

    @Override
    public boolean retainAll(Collection c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection c) {
        return false;
    }

    @Override
    public boolean containsAll(Collection c) {
        return false;
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
        return false;
    }

    @Override
    public Iterator iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return null;
    }

    @Override
    public Object[] toArray(Object[] a) {
        return null;
    }

    @Override
    public int drainTo(Collection c) {
        return 0;
    }

    @Override
    public int drainTo(Collection c, int maxElements) {
        return 0;
    }
}
