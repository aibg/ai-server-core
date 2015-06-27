package hr.best.ai.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;

/**
 * Created by lpp
 */
public class ConnectionHandler implements Runnable, AutoCloseable {
    private final Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    final static Logger logger = Logger.getLogger(Server.class);

    public ConnectionHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            this.writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
            final JsonParser parser = new JsonParser();
            JsonObject init = parser.parse(reader.readLine()).getAsJsonObject();

            logger.debug("Client " + socket.getInetAddress());
            logger.debug(init);

            //TODO: Refaktorirati, moze i bolje al ok je za prvu ruku!
            switch (init.get("type").getAsString()) {
                case "client":
                    new ClientThread(init, reader, writer, socket.getInetAddress().toString()).run();
                    break;

                case "admin":
                    new AdminThread(init, reader, writer, socket.getInetAddress().toString()).run();
                    break;
                default:
                    throw new IllegalArgumentException("Unknown identification");
            }
        } catch (IOException e) {
            logger.error(e);
            writer.println("error happened\n" + e.toString());
        } finally {
            try {
                close();
            } catch (Exception ignorable) {
            }
        }
    }

    @Override
    public void close() throws Exception {
        reader.close();
        writer.close();
        socket.close();
    }
}
