package net.heyzeer0.aladdin.utils;

import java.util.Iterator;

public class Cache<K, V> {

    ConcurrentArrayList<Entry<K, V>> entries = new ConcurrentArrayList<>();

    int max_size;

    public Cache(int max_size) {
        this.max_size = max_size;
    }

    public int getSize() {
        return entries.size();
    }

    public V getValue(K key) {
        if(entries.size() > 0) {
            Iterator<Entry<K, V>> i = entries.iterator();
            while(i.hasNext()) {
                Entry<K, V> entry = i.next();
                if(entry.getKey() == key) {
                    return entry.getValue();
                }
            }
        }

        return null;
    }

    public void add(K key, V value) {
        if(entries.size() + 1 > max_size) entries.remove(entries.size() - 1);

        if(entries.size() > 0) {
            Iterator<Entry<K, V>> i = entries.iterator();
            while(i.hasNext()) {
                Entry<K, V> entry = i.next();
                if(entry.getKey() == key) {
                    return;
                }
            }
        }

        entries.add(new Entry<>(key, value));
    }


    static class Entry<K, V> {

        K key; V value;

        public Entry(K key, V value) {
            this.key = key; this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

    }


}
