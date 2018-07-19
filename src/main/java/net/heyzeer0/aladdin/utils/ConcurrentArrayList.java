package net.heyzeer0.aladdin.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by HeyZeer0 on 17/06/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class ConcurrentArrayList<T> {

    private final Lock readLock;
    private final Lock writeLock;
    private final List<T> list = new ArrayList<>();

    {
        ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
        readLock = rwLock.readLock();
        writeLock = rwLock.writeLock();
    }

    public void add(T e) {
        writeLock.lock();
        try {
            list.add(e);
        } finally {
            writeLock.unlock();
        }
    }

    public T get(int index) {
        readLock.lock();
        try {
            return list.get(index);
        } finally {
            readLock.unlock();
        }
    }

    public void remove(int index) {
        writeLock.lock();
        try{
            list.remove(index);
        } finally {
            writeLock.unlock();
        }
    }

    public int size() {
        return list.size();
    }

    public Iterator<T> iterator() {
        readLock.lock();
        try {
            return new ArrayList<T>(list).iterator();
        } finally {
            readLock.unlock();
        }
    }

}