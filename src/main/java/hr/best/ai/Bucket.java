package hr.best.ai;

public class Bucket implements IBucket {

    private volatile long ticks = 0;
    private volatile long currentSize = 0;

    private final long gain;
    private final long maxSize;

    public Bucket(long initSize, long gain, long maxSize) {
        currentSize = initSize;
        this.gain = gain;
        this.maxSize = maxSize;
    }

    @Override
    public synchronized void tick() {
        ticks = System.currentTimeMillis();
    }

    @Override
    public synchronized boolean tok() {
        if (!ok())
            return false;

        long dt = java.lang.System.currentTimeMillis() - ticks;
        currentSize -= dt;

        ticks += 100000; /// TODO: Correct this hack...I'm hungry. It should be something enormous.

        if (currentSize < 0) {
            return false;
        } else {
            currentSize += gain;
            currentSize = Math.min(currentSize, maxSize);
            return true;
        }

    }

    @Override
    public synchronized boolean ok() {
        return currentSize - (java.lang.System.currentTimeMillis() - ticks) > 0;
    }
}
