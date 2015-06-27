package hr.best.ai.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hr.best.ai.exceptions.InvalidActionException;
import hr.best.ai.gl.Action;
import hr.best.ai.gl.GameContext;
import hr.best.ai.gl.State;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * TODO: Dovrsiti i testirati
 */
public class ClientThread implements Runnable, IClient, AutoCloseable {

    final static Logger logger = Logger.getLogger(Server.class);
    private final GameContext game;
    private final String name;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private final JsonParser parser = new JsonParser();

    public ClientThread(JsonObject init, BufferedReader reader, PrintWriter writer, String name) {
        this.reader = reader;
        this.writer = writer;
        this.name = name;

        // TODO: add some checking for sanity and invalid input
        int gid = init.get("gid").getAsInt();
        game = GameManager.INSTANCE.getGameContext(gid);
    }

    public ClientThread(JsonObject init, BufferedReader reader, PrintWriter writer) {
        this(init, reader, writer, init.get("name").getAsString());
    }

    @Override
    public void sendError(String message) {
        logger.info("Error sent to client " + name + " " + message);
        writer.println(message);
        try {
            close();
        } catch (Exception ignorable) {
        }
    }

    @Override
    public void signalNewState(State state) {
        logger.debug("Sending new state to " + name + " " + state.toJSONObject().toString());
        writer.println(state.toJSONObject());
    }

    @Override
    public void signalCompleted(String message) {
        logger.debug("Signaling completed " + name + " " + message);
        writer.println(message);
        try {
            close();
        } catch (Exception ignorable) {
        }
    }

    @Override
    public void run() {
        final int id = game.registerPlayer(this);
        while (true) {
            try {
                JsonObject pck = parser.parse(reader.readLine()).getAsJsonObject();
                Action a = game.parseAction(pck);
                game.commitAction(id, a);

            } catch (IOException e) {
                logger.error(e);
                sendError(e.toString());
            } catch (InvalidActionException e) {
                logger.error("Player " + name + "sent invalid action." + e.toString());
                sendError("Invalid action!"); // TODO: make nice JSON error message for brevity
            }
        }
    }

    @Override
    public void close() throws Exception {
        reader.close();
        writer.close();
    }
}