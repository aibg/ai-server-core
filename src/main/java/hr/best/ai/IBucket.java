package hr.best.ai;

public interface IBucket {

    /**
     * Signals ticking.
     */
    public void tick();

    /**
     * Signals end of ticking and add gain to internal clock.
     *
     * @return true if tok is called in time.
     */
    public boolean tok();

    /**
     * Checks does this bucket holds enough time before the tok. Tick must be called first.
     *
     * @return true if there's more time.
     */
    public boolean ok();

    /**
     *
     * @return how many miliseconds the players has left
     */
    public long getMills();
}
