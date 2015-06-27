package hr.best.ai.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hr.best.ai.games.GameContextFactory;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class AdminThread implements Runnable {
    @Override
    public void run() {
        while (true) {
            JsonObject command;
            try {
                command = parser.parse(reader.readLine()).getAsJsonObject();
            } catch (IOException e) {
                logger.error(e);
                try {
                    reader.close();
                    writer.close();
                } catch (Exception ignorable) {
                }
                return;
            }

            int gid;
            switch (command.get("type").getAsString()) {
                case "new":
                    gid = GameManager.INSTANCE.newGameContext(GameContextFactory.getSumGameInstance());
                    JsonObject out = new JsonObject();
                    out.addProperty("gid", gid);
                    writer.println(out);
                    logger.info("New game created. Gid:" + gid);
                    break;

                case "start":
                    gid = command.get("gid").getAsInt();
                    GameManager.INSTANCE.getGameContext(gid).startGame();
                    logger.info(name + "Starting game " + gid);
                    break;

                case "stop":
                    gid = command.get("gid").getAsInt();
                    GameManager.INSTANCE.getGameContext(gid).stopGame();
                    logger.info(name + "Stopping game " + gid);
                    break;
            }
        }
    }

    private final JsonObject init;
    private final String name;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private final JsonParser parser = new JsonParser();
    final static Logger logger = Logger.getLogger(Server.class);

    public AdminThread(JsonObject init, BufferedReader reader, PrintWriter writer, String name) {
        this.init = init;
        this.reader = reader;
        this.writer = writer;
        this.name = name;
    }

    public AdminThread(JsonObject init, BufferedReader reader, PrintWriter writer) {
        this(init, reader, writer, init.get("name").getAsString());
    }
}
