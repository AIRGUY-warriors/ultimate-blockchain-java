package ds1.util;

import ds1.TransactionWithFee;

public class PriorityQueue {
    private MaxHeapArray maxHeap;

    public PriorityQueue(int capacity) {
        maxHeap = new MaxHeapArray(capacity);
    }

    public void enqueue(TransactionWithFee value) {
        maxHeap.insert(value);//O(log n),where n is the size of the heap
    }

    public TransactionWithFee dequeue() {
        return maxHeap.extractMax();//O(log n),where n is the size of the heap
    }

    public TransactionWithFee next() {
        return maxHeap.getMax();
    }

    public int size() {
        return maxHeap.size();
    }

    public TransactionWithFee[] toArray() {

        // First we need to copy the heap to avoid modifying it
        MaxHeapArray tempHeap = new MaxHeapArray(maxHeap.size());
        TransactionWithFee[] originalHeap = maxHeap.getHeap();
        for (int i = 0; i < maxHeap.size(); i++) {//O(n)
            tempHeap.insert(originalHeap[i]);//O(log n)
        }//this loop is O(logn*n)
        // Now we can extract elements from the tempHeap to get them in order
        TransactionWithFee[] sortedTransactions = new TransactionWithFee[maxHeap.size()];
        for (int i = 0; i < sortedTransactions.length; i++) {//O(n)
            sortedTransactions[i] = tempHeap.extractMax();//O(log n)
        }//this loop is O(logn*n)
        return sortedTransactions;
    }//this method is O(logn*n),not O(n)
}