package hr.best.ai.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hr.best.ai.gl.State;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.PrintWriter;

/**
 * TODO: Dovrsiti.
 */
public class ClientThread implements Runnable, IClient {

    private final String name;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private final JsonParser parser = new JsonParser();
    final static Logger logger = Logger.getLogger(Server.class);

    public ClientThread(JsonObject init, BufferedReader reader, PrintWriter writer, String name) {
        this.reader = reader;
        this.writer = writer;
        this.name = name;
    }

    public ClientThread(JsonObject init, BufferedReader reader, PrintWriter writer) {
        this.reader = reader;
        this.writer = writer;
        this.name = init.get("name").getAsString();
    }

    @Override
    public void sendError(String message) {

    }

    @Override
    public void signalNewState(State state) {

    }

    @Override
    public void signalCompleted(String message) {

    }

    @Override
    public void run() {

    }
}