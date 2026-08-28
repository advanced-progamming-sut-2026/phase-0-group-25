package com.test1.PlantsVsZombies.src.Network.Server;

/**
 * A small, hand-rolled readers-writer lock built directly on
 * synchronized/wait/notify (deliberately not java.util.concurrent.locks,
 * to make the coordination explicit).
 *
 * Rules enforced:
 *  - Any number of readers may hold the lock at the same time (e.g.
 *    several clients fetching the leaderboard, or checking login
 *    credentials, concurrently).
 *  - A writer (register, save progress, change username, password
 *    reset, ...) needs EXCLUSIVE access: no readers and no other writer
 *    may be active while it holds the lock.
 *  - Once a writer is waiting, new readers queue up behind it instead of
 *    continuing to cut in line -- otherwise a steady stream of readers
 *    could starve a writer indefinitely.
 *
 * Every thread that calls lockRead()/lockWrite() MUST release it with
 * unlockRead()/unlockWrite() in a finally block.
 */
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
            // Wake everyone up (a waiting writer, or other waiting
            // readers if there was no writer at all); whoever can
            // actually proceed will, the rest just re-check and wait again.
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
