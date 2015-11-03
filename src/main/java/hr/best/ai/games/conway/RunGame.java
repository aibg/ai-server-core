package hr.best.ai.games.conway;

import hr.best.ai.games.GameContextFactory;
import hr.best.ai.games.conway.players.DoNothingPlayerDemo;
import hr.best.ai.games.conway.visualization.GameBarPanel;
import hr.best.ai.games.conway.visualization.GameGridPanel;
import hr.best.ai.games.conway.visualization.PlayerInfoPanel;
import hr.best.ai.gl.GameContext;
import hr.best.ai.server.ProcessIOPlayer;
import hr.best.ai.server.SocketIOPlayer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
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
		Color gridColor = new Color(200, 200, 200);
		
		int barHeight = 30;
		String p1Logo="src/main/resources/BEST_ZG_mali.png";
		String p2Logo="src/main/resources/Untitled-3.png";
		
		GameBarPanel bar = new GameBarPanel(p1color, p2color);
		GameGridPanel grid = new GameGridPanel(p1Logo,p2Logo,p1color,p2color,gridColor);

		SwingUtilities.invokeAndWait(() -> {

			JFrame f = new JFrame("DemoConway");
			f.setSize(new Dimension(800,600));
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
				
				background.add(new PlayerInfoPanel(1));
				background.add(grid);
				background.add(new PlayerInfoPanel(2));
				
				
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
                    .setFromEmpty(GameContextFactory.Ruleset1::fromEmpty)
                    .setFromOccupied(GameContextFactory.Ruleset1::fromOccupied);

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
        final JsonParser parser = new JsonParser();
        final JsonObject config = parser.parse(new InputStreamReader(RunGame.class.getClassLoader().getResourceAsStream("ai.json"), StandardCharsets.UTF_8)).getAsJsonObject();

        try (GameContext gc = initialize(config)) {
            if (config.get("visualization").getAsBoolean()) {
                RunGame.addVisualization(gc);
            }
            gc.play();
        }
    }
}
