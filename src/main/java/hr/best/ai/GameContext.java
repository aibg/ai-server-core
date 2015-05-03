package hr.best.ai;

import com.google.gson.JsonObject;
import hr.best.ai.gl.Action;
import hr.best.ai.server.ClientThread;

import java.util.ArrayList;
import java.util.List;

/**
 * Main class which surrounds whole play of one game.
 */
public class GameContext {

    private void resert() {
        clients = new ArrayList<>();
    }


    /**
     * Services for players
     */

    private List<ClientThread> clients;
    private Action[] actions;

    public synchronized int registerPlayer(ClientThread client) {
        clients.add(client);
        return clients.size() - 1;
    }

    /**
     * WARNING: This returns a view...
     */
    public synchronized List<ClientThread> getPlayers() {
        return clients;
    }

    public synchronized void signalError(int playerID, Exception e) {
    }

    public synchronized void commitAction(int playerID, Action action) {
    }


    /**
     * Services for control thread.
     */
    public synchronized void startGame() {
    }

    ;

    public synchronized void pauseGame() {
    }

    ;

    public synchronized void stopGame() {
    }

    ;

    synchronized JsonObject status() {
    }

    ;
}
