package hr.best.ai.gl.bucket;

public interface IBucket {

	/**
	 * Fills the bucket.
	 */
	public void fill();

	/**
	 * Takes amount of time from the bucket.
	 * 
	 * @param amount
	 * @return true if there's enough time in bucket to take
	 */
	public void take(long amount) throws RuntimeException;
}
