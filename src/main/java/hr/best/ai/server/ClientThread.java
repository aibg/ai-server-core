package hr.best.ai.server;

import com.google.gson.JsonParser;
import hr.best.ai.exceptions.InvalidActionException;
import hr.best.ai.gl.Action;
import hr.best.ai.gl.IPlayer;
import hr.best.ai.gl.State;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Created by lpp on 9/23/15.
 */
public class ClientThread implements IPlayer{

    final static Logger logger = Logger.getLogger(ClientThread.class);
    private final Socket socket;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private final JsonParser parser = new JsonParser();

    public ClientThread(Socket socket) throws IOException{
        this.socket = socket;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        this.writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
    }

    @Override
    public void sendError(String message) {
        logger.error(message);
        writer.println(message);
    }

    @Override
    public Action signalNewState(State state) throws IOException, InvalidActionException {
        logger.debug("Client[" + socket.getInetAddress() + "] State: " + state.toJSONObject().toString());
        writer.println(state.toJSONObject().toString());
        String line = reader.readLine();
        try {
            return state.parseAction(parser.parse(line).getAsJsonObject());
        } catch (IllegalStateException ex) {
            throw new InvalidActionException(ex);
        }
    }

    @Override
    public void signalCompleted(String message) {
        logger.debug("Client[" + socket.getInetAddress() + "] has signal Completed. Message " + message);
        writer.println(message);
    }

    @Override
    public String getName() {
        return socket.toString();
    }

    @Override
    public void close() throws Exception {
        reader.close();
        writer.close();
        socket.close();
    }
}
