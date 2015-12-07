package hr.best.ai.games.conway;


import hr.best.ai.games.conway.players.DoNothingPlayerDemo;
import hr.best.ai.games.conway.visualization.GameBarPanel;
import hr.best.ai.games.conway.visualization.GameGridPanel;
import hr.best.ai.games.conway.visualization.PlayerInfoPanel;
import hr.best.ai.gl.AbstractPlayer;
import hr.best.ai.gl.GameContext;
import hr.best.ai.gl.State;
import hr.best.ai.server.ProcessIOPlayer;
import hr.best.ai.server.SocketIOPlayer;
import hr.best.ai.server.TimeBucketPlayer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.log4j.Logger;


/**
 * Created by lpp on 10/31/15.
 */
public class RunGame {

	private static ConwayGameState initialState;
	private static String p1name;
	private static String p2name;
    final static Logger logger = Logger.getLogger(RunGame.class);

    public static void addVisualization(GameContext gc) throws Exception {

		Color p1color = Color.white;
		Color p2color = new Color(248,156,16);
		Color gridColor = new Color(200, 200, 200, 200);
		int barHeight = 30;

		SwingUtilities.invokeAndWait(() -> {
			JFrame frame = new JFrame("Conway");
			frame.setSize(new Dimension(1280,800));
			frame.setVisible(true);

			//TODO the following -1 hack must be changed
			Dimension gridSize=new Dimension(frame.getWidth()-frame.getInsets().left-frame.getInsets().right,frame.getHeight()-frame.getInsets().top-frame.getInsets().bottom-barHeight-1);

			GameGridPanel grid = new GameGridPanel(initialState, p1color, p2color, gridColor, gridSize);
			PlayerInfoPanel p1info=new PlayerInfoPanel(ConwayGameStateConstants.PLAYER1_CELL,p1color,p1name);
			PlayerInfoPanel p2info=new PlayerInfoPanel(ConwayGameStateConstants.PLAYER2_CELL,p2color,p2name);

			// bar setup
			GameBarPanel bar = new GameBarPanel(initialState,p1color, p2color);
			bar.setPreferredSize(new Dimension(0, barHeight));
			frame.getContentPane().add(bar, BorderLayout.NORTH);

			// background setup
			JPanel background=null;
			try {
				Image back=ImageIO.read(RunGame.class.getResource("/pozadina-crna-elektronika.png"));
			    background=new JPanel(){
					@Override
					protected void paintComponent(Graphics g) {
						super.paintComponent(g);
						Color current=g.getColor();
						g.setColor(Color.black);
						g.fillRect(0, 0, getWidth(), getHeight());
						g.setColor(current);
						g.drawImage(back, 0, 0, getWidth(), getHeight(), this);

					}
				};
			} catch (Exception e) {
				e.printStackTrace();
                logger.error(e);
			}
                background.setLayout(new BoxLayout(background, BoxLayout.LINE_AXIS));

				frame.getContentPane().add(background, BorderLayout.CENTER);

				background.add(p1info);
				background.add(grid);
				background.add(p2info);

				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				frame.setVisible(true);

				gc.addObserver(bar);
				gc.addObserver(grid);
				gc.addObserver(p1info);
				gc.addObserver(p2info);
			});

	}

    private static ServerSocket socket = null;

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
        p1name=players.get(0).getAsJsonObject().get("name").getAsString();

        players.get(1).getAsJsonObject().getAsJsonArray("startingCells").forEach((JsonElement e) -> {
            final JsonArray a = e.getAsJsonArray();
            builder.setCell(a.get(0).getAsInt(), a.get(1).getAsInt(), ConwayGameStateConstants.PLAYER2_CELL);
        });
        p2name=players.get(1).getAsJsonObject().get("name").getAsString();

        return builder.getState();
    }

    public static GameContext initializeGC(JsonObject config) throws Exception{
        try {
            final JsonArray players = config.getAsJsonArray("players");
            final int port = config.get("port").getAsInt();

            initialState = (ConwayGameState) genInitState(config);
            GameContext gc = new GameContext(initialState, 2);

            for (JsonElement playerElement : players) {
                JsonObject playerConfiguration = playerElement.getAsJsonObject();
                AbstractPlayer player = instantiatePlayerFromConfig(playerConfiguration, port);
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

        try (GameContext gc = initializeGC(config)) {
            if (config.get("visualization").getAsBoolean()) {
                RunGame.addVisualization(gc);
                Thread.sleep(2000);
            }
            gc.play();
        }
    }
}
