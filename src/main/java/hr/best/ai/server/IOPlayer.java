package hr.best.ai.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import hr.best.ai.exceptions.ClientDisconnectException;
import hr.best.ai.exceptions.InvalidActionException;
import hr.best.ai.gl.IPlayer;

import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Generic IPlayer operating on Input & output stream
 */
public abstract class IOPlayer implements IPlayer{

    final static Logger logger = Logger.getLogger(IOPlayer.class);
    private final BufferedReader reader;
    private final PrintWriter writer;
    private final JsonParser parser = new JsonParser();
    private final String name;

    public IOPlayer(InputStream in, OutputStream out, String name) throws IOException{
        this.name = name;
        this.reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        this.writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8), true);
    }

    @Override
    public void sendError(String message) {
        logger.error(message);
        writer.println(message);
    }

    @Override
    public JsonObject signalNewState(JsonObject state) throws IOException, InvalidActionException {
        logger.debug(String.format("%s sent: %s", this.getName(), state.toString()));
        long t = System.currentTimeMillis();
        writer.println(state.toString());
        String line = reader.readLine();
        if (line == null) {
            throw new ClientDisconnectException(this.getName() + " has disconnected. Unexpected end of stream");
        }
        logger.debug(String.format(
                "%s recieved[t:%3dms]: \"%s\""
                , this.getName()
                , System.currentTimeMillis() - t
                , line)
        );
        try {
            return parser.parse(line).getAsJsonObject();
        } catch (IllegalStateException ex) {
            throw new InvalidActionException(ex);
        }
    }

    @Override
    public void signalCompleted(String message) {
        logger.debug("Client[" + this.getName() + "] has signal Completed. Message " + message);
        writer.println(message);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void close() throws Exception {
        reader.close();
        writer.close();
    }
}
