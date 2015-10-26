package hr.best.ai.gl.bucket;

import hr.best.ai.exceptions.TimeLimitException;

public class SimpleBucket implements IBucket {

	private final long maxSize;
	private volatile long currentSize = 0;

	public SimpleBucket(long maxSize) {
		this.maxSize = maxSize;
	}

	@Override
	public void fill() {
		currentSize = maxSize;
	}

	@Override
	public void take(long amount) {
		currentSize -= amount;
        if (currentSize < 0)
			throw new TimeLimitException();
	}
}
