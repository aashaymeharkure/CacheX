package com.aics.backend.utils;

import com.aics.backend.model.CacheNode;

public class DoublyLinkedList {
    public CacheNode head;
    public CacheNode tail;
    public int size;

    public DoublyLinkedList() {
        head = new CacheNode("", "");
        tail = new CacheNode("", "");
        head.next = tail;
        tail.prev = head;
        size = 0;
    }

    public void addFirst(CacheNode node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
        size++;
    }

    public void remove(CacheNode node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
        size--;
    }

    public CacheNode removeLast() {
        if (size == 0) return null;
        CacheNode last = tail.prev;
        remove(last);
        return last;
    }
}