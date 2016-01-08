package hr.best.ai.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import hr.best.ai.games.conway.gamestate.CellType;
import hr.best.ai.games.conway.gamestate.ConwayGameState;
import hr.best.ai.games.conway.RunGame;
import hr.best.ai.games.conway.players.DoNothingPlayerDemo;
import hr.best.ai.gl.AbstractPlayer;
import hr.best.ai.gl.State;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lpp on 12/7/15.
 */
public class ConfigUtilities {
    public static ServerSocket socket = null;

    private static AbstractPlayer instantiatePlayerFromConfig(JsonObject playerConfiguration, int port) throws Exception {
        String type = playerConfiguration.get("type").getAsString();
        String name = playerConfiguration.get("name") == null ? "Unknown player" : playerConfiguration.get("name").getAsString();

        AbstractPlayer player;
        switch (type) {
            case "dummy":
                player = new DoNothingPlayerDemo(name);
                break;
            case "tcp":
                socket = socket != null ? socket : new ServerSocket(port, 50, null);
                player = new SocketIOPlayer(socket.accept(), name);
                break;
            case "process":
                ArrayList<String> command = new ArrayList<>();
                for (JsonElement e : playerConfiguration.getAsJsonArray("command"))
                    command.add(e.getAsString());
                if (playerConfiguration.has("workingDirectory")) {
                    player = new ProcessIOPlayer(command, Paths.get(playerConfiguration.get("workingDirectory")
                            .getAsString()), name);
                } else {
                    player = new ProcessIOPlayer(command, name);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown player type. Got: " + type);
        }

        if (playerConfiguration.has("timer")) {
            JsonObject timeBucketConfig = playerConfiguration.get("timer").getAsJsonObject();
            long timePerTurn = timeBucketConfig.get("maxLength").getAsInt();
            player = new TimeBucketPlayer(player, timePerTurn, 5 * timePerTurn);
        }
        return player;
    }

    public static State genInitState(JsonObject config) {
        final JsonObject gameConfig = config.getAsJsonObject("game");
        final JsonArray players = config.getAsJsonArray("players");

        ConwayGameState.Builder builder = ConwayGameState.newBuilder
                (gameConfig.get("rows").getAsInt()
                        , gameConfig.get("cols").getAsInt()
                ).setCellGainPerTurn(gameConfig.get("cellGainPerTurn").getAsInt())
                .setMaxCellCapacity(gameConfig.get("maxCellCapacity").getAsInt())
                .setMaxColonisationDistance(gameConfig.get("maxColonisationDistance").getAsInt())
                .setMaxGameIterations(gameConfig.get("maxGameIterations").getAsInt())
                .setStartingCells(gameConfig.get("startingCells").getAsInt())
                .setRuleset(gameConfig.get("ruleset").getAsString());

        players.get(0).getAsJsonObject().getAsJsonArray("startingCells").forEach((JsonElement e) -> {
            final JsonArray a = e.getAsJsonArray();
            builder.setCell(a.get(0).getAsInt(), a.get(1).getAsInt(), CellType.P1);
        });

        players.get(1).getAsJsonObject().getAsJsonArray("startingCells").forEach((JsonElement e) -> {
            final JsonArray a = e.getAsJsonArray();
            builder.setCell(a.get(0).getAsInt(), a.get(1).getAsInt(), CellType.P2);
        });
        return builder.getState();
    }

    public static JsonObject configFromCMDArgs(String[] args) throws FileNotFoundException {
        final JsonParser parser = new JsonParser();

        if (args.length == 0) {
            System.out.println("Falling back to default game configuration.");
            return parser.parse(new InputStreamReader(RunGame.class.getClassLoader().getResourceAsStream("defaultConfig.json"), StandardCharsets.UTF_8)).getAsJsonObject();
        } else {
            System.out.println("Using " + args[0] + " configuration file");
            return parser.parse(new FileReader(args[0])).getAsJsonObject();
        }
    }

    public static List<AbstractPlayer> istantiateAllPlayersFromConfig(JsonArray playerConfigurations, int port)
            throws Exception {
        List<AbstractPlayer> sol = new ArrayList<>();

        for (JsonElement playerElement : playerConfigurations) {
            JsonObject playerConfiguration = playerElement.getAsJsonObject();
            AbstractPlayer player = instantiatePlayerFromConfig(playerConfiguration, port);
            sol.add(player);
        }
        return sol;
    }
}
