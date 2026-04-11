package com.aics.backend.utils;

import com.aics.backend.model.CacheNode;
import java.util.Comparator;

public class MinHeap {
    private CacheNode[] heap;
    private int size;
    private int capacity;

    public MinHeap(int capacity) {
        this.capacity = capacity;
        this.heap = new CacheNode[capacity];
        this.size = 0;
    }

    public void add(CacheNode node) {
        if (size == capacity) return;
        heap[size] = node;
        heapifyUp(size);
        size++;
    }

    public CacheNode extractMin() {
        if (size == 0) return null;
        CacheNode min = heap[0];
        heap[0] = heap[size - 1];
        size--;
        heapifyDown(0);
        return min;
    }

    public void update(CacheNode node) {
        for (int i = 0; i < size; i++) {
            if (heap[i] == node) {
                heapifyDown(i);
                heapifyUp(i);
                break;
            }
        }
    }

    public void remove(CacheNode node) {
        for (int i = 0; i < size; i++) {
            if (heap[i] == node) {
                heap[i] = heap[size - 1];
                size--;
                heapifyDown(i);
                heapifyUp(i);
                break;
            }
        }
    }

    private void heapifyUp(int i) {
        int parent = (i - 1) / 2;
        while (i > 0 && compare(heap[i], heap[parent]) < 0) {
            swap(i, parent);
            i = parent;
            parent = (i - 1) / 2;
        }
    }

    private void heapifyDown(int i) {
        int smallest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        if (left < size && compare(heap[left], heap[smallest]) < 0) {
            smallest = left;
        }
        if (right < size && compare(heap[right], heap[smallest]) < 0) {
            smallest = right;
        }
        if (smallest != i) {
            swap(i, smallest);
            heapifyDown(smallest);
        }
    }

    private int compare(CacheNode n1, CacheNode n2) {
        if (n1.frequency != n2.frequency) {
            return Integer.compare(n1.frequency, n2.frequency);
        }
        return Long.compare(n1.lastAccessTime, n2.lastAccessTime);
    }

    private void swap(int i, int j) {
        CacheNode temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }
}
