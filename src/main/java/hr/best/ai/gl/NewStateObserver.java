package hr.best.ai.gl;

import com.google.gson.JsonObject;

/**
 * Observer signaled on every new state.
 */
public interface NewStateObserver extends AutoCloseable {
	
	/**
	 * Signals a new state is computed.
	 * 
	 * @param state
	 *            new state sent to observer
	 */
    public void signalNewState(State state);
    
    /**
     * Sends error message
     * 
     * @param message
     *            massage as a json object
     */
    public void signalError(JsonObject message);
    
    /**
     * @return observer name
     */
    public String getName();
}
