package hr.best.ai.gl;

public class Bucket implements IBucket {

	private final long maxSize;
	private volatile long currentSize = 0;

	public Bucket(long maxSize) {
		this.maxSize = maxSize;
	}

	@Override
	public void fill() {
		currentSize = maxSize;
	}

	@Override
	public void take(long amount) throws RuntimeException {
		if (currentSize - amount >= 0) {
			currentSize -= amount;
		} else {
			throw new RuntimeException();
		}
	}
}
