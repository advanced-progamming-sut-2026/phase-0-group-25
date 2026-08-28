package com.test1.PlantsVsZombies.src.Network.Server;


public class ReadWriteLock {
    private int activeReaders = 0;
    private boolean writerActive = false;
    private int waitingWriters = 0;

    public synchronized void lockRead() throws InterruptedException {
        while (writerActive || waitingWriters > 0) {
            wait();
        }
        activeReaders++;
    }

    public synchronized void unlockRead() {
        activeReaders--;
        if (activeReaders == 0) {



            notifyAll();
        }
    }

    public synchronized void lockWrite() throws InterruptedException {
        waitingWriters++;
        try {
            while (writerActive || activeReaders > 0) {
                wait();
            }
        } finally {
            waitingWriters--;
        }
        writerActive = true;
    }

    public synchronized void unlockWrite() {
        writerActive = false;
        notifyAll();
    }
}
