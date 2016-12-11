package hr.best.ai.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import hr.best.ai.exceptions.ClientDisconnectException;
import hr.best.ai.exceptions.InvalidActionException;
import hr.best.ai.gl.AbstractPlayer;

import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Abstract player which communicates with clients via 
 * input and output stream supplied in constructor.
 */
public abstract class IOPlayer extends AbstractPlayer{

    final static Logger logger = Logger.getLogger(IOPlayer.class);
   
    private final BufferedReader reader;
    private final PrintWriter writer;
    
    private final JsonParser parser = new JsonParser();

    public IOPlayer(InputStream in, OutputStream out, String name) throws IOException{
        super(name);
        this.reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        this.writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8), true);
    }

    @Override
    public void sendError(JsonObject message) {
        logger.error("Player " + getName() + " error happened: " +message.toString());
        writer.println(message.toString());
    }

    /**
     * Sends state to output stream and reads player action from input.
     */
    @Override
    public JsonObject signalNewState(JsonObject state) throws IOException, InvalidActionException {
        logger.debug(String.format("%s sent: %s", this.getName(),
                logger.isTraceEnabled() ? state.toString() : "[Set logging level to TRACE for full state]"));
        long t = System.currentTimeMillis();
        writer.println(state.toString());
        String line = reader.readLine();
        if (line == null) {
            throw new ClientDisconnectException(this.getName() + " has disconnected. Unexpected end of stream");
        }
        logger.debug(String.format(
                "%s response received [t:%3dms] :%s"
                , this.getName()
                , System.currentTimeMillis() - t
                , logger.isTraceEnabled() ? line : "[Set logging level to TRACE for full line received]"));
        try {
            return parser.parse(line).getAsJsonObject();
        } catch (IllegalStateException ex) {
            throw new InvalidActionException(ex);
        }
    }

    @Override
    public void signalFinal(JsonObject state) throws Exception {
        writer.println(state.toString());
        close();
    }

    @Override
    public void close() throws Exception {
        reader.close();
        writer.close();
    }
}
