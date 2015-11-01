package hr.best.ai.games.conway;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hr.best.ai.games.GameContextFactory;
import hr.best.ai.games.conway.players.DoNothingPlayerDemo;
import hr.best.ai.games.conway.visualization.GameBar;
import hr.best.ai.games.conway.visualization.GameGrid;
import hr.best.ai.gl.GameContext;
import hr.best.ai.gl.State;
import hr.best.ai.server.ProcessIOPlayer;
import hr.best.ai.server.SocketIOPlayer;

import javax.swing.*;
import java.awt.*;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by lpp on 10/31/15.
 */
public class RunGame {
    public static void addVizualization(GameContext gc) throws Exception{
        GameBar bar = new GameBar();
        GameGrid grid = new GameGrid();

        SwingUtilities.invokeAndWait(() -> {
            JFrame f = new JFrame("DemoConway");

            f.setExtendedState(JFrame.MAXIMIZED_BOTH);
            f.getContentPane().add(bar, BorderLayout.NORTH);
            f.getContentPane().add(grid, BorderLayout.CENTER);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            f.setVisible(true);
        });

        gc.addObserver(bar);
        gc.addObserver(grid);
    }

    private static GameContext initialize(JsonObject config) throws Exception{
        ServerSocket socket = null;
        try {
            final JsonObject gameConfig = config.getAsJsonObject("game");
            final JsonArray players = config.getAsJsonArray("players");

            ConwayGameStateBuilder builder = ConwayGameStateBuilder.newConwayGameStateBuilder
                    (gameConfig.get("rows").getAsInt()
                            , gameConfig.get("cols").getAsInt()
                    ).setCellGainPerTurn(gameConfig.get("cellGainPerTurn").getAsInt())
                    .setMaxCellCapacity(gameConfig.get("maxCellCapacity").getAsInt())
                    .setMaxColonisationDistance(gameConfig.get("maxColonisationDistance").getAsInt())
                    .setMaxGameIterations(gameConfig.get("maxGameIterations").getAsInt())
                    .setRuleset(gameConfig.get("ruleset").getAsString());

            players.get(0).getAsJsonObject().getAsJsonArray("startingCells").forEach((JsonElement e) -> {
                final JsonArray a = e.getAsJsonArray();
                builder.setCell(a.get(0).getAsInt(), a.get(1).getAsInt(), ConwayGameStateConstants.PLAYER1_CELL);
            });

            players.get(1).getAsJsonObject().getAsJsonArray("startingCells").forEach((JsonElement e) -> {
                final JsonArray a = e.getAsJsonArray();
                builder.setCell(a.get(0).getAsInt(), a.get(1).getAsInt(), ConwayGameStateConstants.PLAYER2_CELL);
            });
            GameContext gc = new GameContext(builder.getState(), 2);

            for (JsonElement playerElement : players) {
                JsonObject player = playerElement.getAsJsonObject();
                String type = player.get("type").getAsString();
                String name = player.get("name") == null ? null : player.get("name").getAsString();
                switch (type) {
                    case "dummy":
                        gc.addPlayer(new DoNothingPlayerDemo());
                        break;
                    case "tcp":
                        socket = socket != null ? socket : new ServerSocket(config.get("port").getAsInt(), 50, null);
                        gc.addPlayer(name != null ? new SocketIOPlayer(socket.accept(), name) : new SocketIOPlayer
                                (socket.accept()));
                        break;
                    case "process":
                        ArrayList<String> command = new ArrayList<>();
                        for (JsonElement e : player.getAsJsonArray("command"))
                            command.add(e.getAsString());
                        gc.addPlayer(name != null ? new ProcessIOPlayer(command, name) : new ProcessIOPlayer(command));
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown player type. Got: " + type);
                }
            }
            return gc;
        } catch (Exception ex) {
            if (socket != null) {
                socket.close();
            }
            throw ex;
        }
    }

    public static void main(String[] args) throws Exception {
        Rulesets.getInstance(); // loading the class static part into JVM
        final JsonParser parser = new JsonParser();
        final JsonObject config = parser.parse(new InputStreamReader(RunGame.class.getClassLoader().getResourceAsStream("ai.json"), StandardCharsets.UTF_8)).getAsJsonObject();

        try (GameContext gc = initialize(config)) {
            if (config.get("visualization").getAsBoolean()) {
                RunGame.addVizualization(gc);
            }
            gc.play();
        }
    }
}
