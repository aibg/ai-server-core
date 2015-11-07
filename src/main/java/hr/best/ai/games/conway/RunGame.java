package hr.best.ai.games.conway;


import hr.best.ai.games.conway.players.DoNothingPlayerDemo;
import hr.best.ai.games.conway.visualization.GameBarPanel;
import hr.best.ai.games.conway.visualization.GameGridPanel;
import hr.best.ai.games.conway.visualization.PlayerInfoPanel;
import hr.best.ai.gl.AbstractPlayer;
import hr.best.ai.gl.GameContext;
import hr.best.ai.gl.bucket.SimpleBucket;
import hr.best.ai.server.ProcessIOPlayer;
import hr.best.ai.server.SocketIOPlayer;
import hr.best.ai.server.TimeBucketPlayer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kitfox.svg.app.beans.SVGPanel;


/**
 * Created by lpp on 10/31/15.
 */
public class RunGame {

	public static void addVisualization(GameContext gc) throws Exception {
		
		Color p1color = Color.red;
		Color p2color = Color.blue;
		Color gridColor = new Color(200, 200, 200, 200);
		
		int barHeight = 30;
		String p1Logo="/BEST_ZG_mali.png";
		String p2Logo="/Untitled-3.png";
		
		GameBarPanel bar = new GameBarPanel(p1color, p2color);
		GameGridPanel grid = new GameGridPanel(p1Logo,p2Logo,p1color,p2color,gridColor);
		PlayerInfoPanel p1info=new PlayerInfoPanel(ConwayGameStateConstants.PLAYER1_CELL,p1color);
		PlayerInfoPanel p2info=new PlayerInfoPanel(ConwayGameStateConstants.PLAYER2_CELL,p2color);
		
		SwingUtilities.invokeAndWait(() -> {

			JFrame f = new JFrame("Conway");
			f.setSize(new Dimension(1200,800));
			//f.setExtendedState(JFrame.MAXIMIZED_BOTH);
			// bar setup
				bar.setPreferredSize(new Dimension(0, barHeight));
				f.getContentPane().add(bar, BorderLayout.NORTH);

				// background setup
				SVGPanel background = new SVGPanel();
				background.setLayout(new BoxLayout(background, BoxLayout.LINE_AXIS));
				background.setScaleToFit(true);
				background.setAntiAlias(true);
				try {
					background.setSvgResourcePath("/background.svg");
				} catch (Exception e) {
					e.printStackTrace();
				}
				f.getContentPane().add(background, BorderLayout.CENTER);
				
				background.add(p1info);
				background.add(grid);
				background.add(p2info);
				
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setVisible(true);
			});

		gc.addObserver(bar);
		gc.addObserver(grid);
		gc.addObserver(p1info);
		gc.addObserver(p2info);
	}


    private static ServerSocket socket = null;

    private static AbstractPlayer createPlayer(JsonObject playerConfiguration, int port) throws Exception {
        String type = playerConfiguration.get("type").getAsString();
        String name = playerConfiguration.get("name") == null ? "Unknown player" : playerConfiguration.get("name").getAsString();
        switch (type) {
            case "dummy":
                return new DoNothingPlayerDemo(name);
            case "tcp":
                socket = socket != null ? socket : new ServerSocket(port, 50, null);
                return new SocketIOPlayer(socket.accept(), name);
            case "process":
                ArrayList<String> command = new ArrayList<>();
                for (JsonElement e : playerConfiguration.getAsJsonArray("command"))
                    command.add(e.getAsString());
                if (playerConfiguration.has("workingDirectory")) {
                    return new ProcessIOPlayer(command, Paths.get(playerConfiguration.get("workingDirectory")
                            .getAsString()), name);
                } else {
                    return new ProcessIOPlayer(command, name);
                }
            default:
                throw new IllegalArgumentException("Unknown player type. Got: " + type);
        }
    }

    private static AbstractPlayer getTimeBucketedPlayer(AbstractPlayer player, JsonObject timeBucketConfig) {
        return new TimeBucketPlayer(player, new SimpleBucket(
                timeBucketConfig.get("maxLength").getAsInt()
        ));
    }

    private static GameContext initialize(JsonObject config) throws Exception{
        try {
            final JsonObject gameConfig = config.getAsJsonObject("game");
            final JsonArray players = config.getAsJsonArray("players");
            final int port = config.get("port").getAsInt();

            ConwayGameStateBuilder builder = ConwayGameStateBuilder.newConwayGameStateBuilder
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
                builder.setCell(a.get(0).getAsInt(), a.get(1).getAsInt(), ConwayGameStateConstants.PLAYER1_CELL);
            });

            players.get(1).getAsJsonObject().getAsJsonArray("startingCells").forEach((JsonElement e) -> {
                final JsonArray a = e.getAsJsonArray();
                builder.setCell(a.get(0).getAsInt(), a.get(1).getAsInt(), ConwayGameStateConstants.PLAYER2_CELL);
            });
            GameContext gc = new GameContext(builder.getState(), 2);

            for (JsonElement playerElement : players) {
                JsonObject playerConfiguration = playerElement.getAsJsonObject();
                AbstractPlayer player = createPlayer(playerConfiguration, port);

                if (playerConfiguration.has("timer"))
                    gc.addPlayer(getTimeBucketedPlayer(
                            player
                            , playerConfiguration.get("timer").getAsJsonObject())
                    );
                else
                    gc.addPlayer(player);
            }
            return gc;
        } catch (Exception ex) {
            if (socket != null) {
                socket.close();
                socket = null;
            }
            throw ex;
        }
    }

    public static void main(String[] args) throws Exception {
        Rulesets.getInstance(); // loading the class static part into JVM
        final JsonParser parser = new JsonParser();
        JsonObject config;

        if (args.length == 0) {
            System.out.println("Falling back to default game configuration.");
            config = parser.parse(new InputStreamReader(RunGame.class.getClassLoader().getResourceAsStream("defaultConfig.json"), StandardCharsets.UTF_8)).getAsJsonObject();
        } else {
            System.out.println("Using " + args[0] + " configuration file");
            config = parser.parse(new FileReader(args[0])).getAsJsonObject();
        }

        try (GameContext gc = initialize(config)) {
            if (config.get("visualization").getAsBoolean()) {
                RunGame.addVisualization(gc);
            }
            gc.play();
        }
    }
}
