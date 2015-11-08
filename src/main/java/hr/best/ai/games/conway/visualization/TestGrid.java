package hr.best.ai.games.conway.visualization;

import hr.best.ai.games.conway.Cell;
import hr.best.ai.games.conway.Cells;
import hr.best.ai.games.conway.ConwayGameState;
import hr.best.ai.games.conway.ConwayGameStateBuilder;
import hr.best.ai.games.conway.ConwayGameStateConstants;
import hr.best.ai.games.conway.Rulesets;
import hr.best.ai.games.conway.RunGame;
import hr.best.ai.gl.Action;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@SuppressWarnings("serial")
public class TestGrid extends JPanel {

	private final Color p1color = new Color(160,0,0);
	private final Color p2color = new Color(0,0,160);
	private final Color gridColor = Color.black;

	private int blockSize;

	private ConwayGameState state;
	private Cells p1actions=new Cells();
	private Cells p2actions=new Cells();

	public TestGrid(ConwayGameState cgs) {
		this.state = cgs;
		
		JFrame f = new JFrame("Conway");
		
		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		f.getContentPane().add(this);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);	

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					p1actions.add(toCell(e.getPoint()));
				} else if (e.getButton() == MouseEvent.BUTTON3) {
					p2actions.add(toCell(e.getPoint()));
				}
				repaint();
			}

			private Cell toCell(Point point) {
				return new Cell(point.y / blockSize, point.x / blockSize);
			}
		});
		f.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					List<Action> actions = new ArrayList<Action>();
					actions.add(p1actions);
					actions.add(p2actions);

					state = (ConwayGameState) state.nextState(actions);
					p1actions.clear();
					p2actions.clear();
					repaint();
				}
			}
		});

	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		double blockWidth = (double) getParent().getBounds().width
				/ this.state.getCols();
		double blockHeight = (double) getParent().getBounds().height
				/ this.state.getRows();

		blockSize = Math.toIntExact(Math.round(Math.floor(Math.min(blockWidth,
				blockHeight))));

		int width = blockSize * this.state.getCols() + 1;
		int height = blockSize * this.state.getRows() + 1;
		Dimension newSize = new Dimension(width, height);
		setPreferredSize(newSize);

		// draw grid

		g.setColor(gridColor);
		// horizontal lines
		for (int i = 0; i <= state.getRows(); i++) {
			g.drawLine(0, i * blockSize, state.getCols() * blockSize, i
					* blockSize);
		}
		// vertical lines
		for (int i = 0; i <= state.getCols(); i++) {
			g.drawLine(i * blockSize, 0, i * blockSize, state.getCols()
					* blockSize);
		}

		drawCurrentActions(g, p1actions, p1color.brighter().brighter().brighter());
		drawCurrentActions(g, p2actions, p2color.brighter().brighter().brighter());
		// draw live cells
		for (int i = 0; i < state.getRows(); i++) {
			for (int j = 0; j < state.getCols(); j++) {
				switch (state.getCell(i, j)) {
				case ConwayGameStateConstants.PLAYER1_CELL:
					g.setColor(p1color);
					break;
				case ConwayGameStateConstants.PLAYER2_CELL:
					g.setColor(p2color);
					break;
				case ConwayGameStateConstants.DEAD_CELL:
					continue;
				}
				g.fillRect(blockSize * j, blockSize * i, blockSize, blockSize);

			}
		}

	}

	private void drawCurrentActions(Graphics g, Cells actions, Color color) {

		g.setColor(color);
		for (int i = 0; i < actions.size(); i++) {
			Cell c = actions.get(i);
			g.fillRect(blockSize * c.getCol(), blockSize * c.getRow(),
					blockSize, blockSize);
		}
		g.setColor(gridColor);
	}

	public static void main(String[] args) throws Exception {

		Rulesets.getInstance(); // loading the class static part into JVM
		JsonObject config = new JsonParser().parse(
				new InputStreamReader(RunGame.class.getClassLoader()
						.getResourceAsStream("defaultConfig.json"),
						StandardCharsets.UTF_8)).getAsJsonObject();

		ConwayGameState cgs = initialize(config);
		if (config.get("visualization").getAsBoolean())
			addVisualization(cgs);

	}

	private static void addVisualization(ConwayGameState cgs) {
		

		SwingUtilities.invokeLater(() -> {
			new TestGrid(cgs);
		});
	}

	private static ConwayGameState initialize(JsonObject config) {

		final JsonObject gameConfig = config.getAsJsonObject("game");
		final JsonArray players = config.getAsJsonArray("players");
		final int port = config.get("port").getAsInt();

		ConwayGameStateBuilder builder = ConwayGameStateBuilder
				.newConwayGameStateBuilder(gameConfig.get("rows").getAsInt(),
						gameConfig.get("cols").getAsInt())
				.setCellGainPerTurn(
						gameConfig.get("cellGainPerTurn").getAsInt())
				.setMaxCellCapacity(
						gameConfig.get("maxCellCapacity").getAsInt())
				.setMaxColonisationDistance(
						gameConfig.get("maxColonisationDistance").getAsInt())
				.setMaxGameIterations(
						gameConfig.get("maxGameIterations").getAsInt())
				.setStartingCells(gameConfig.get("startingCells").getAsInt())
				.setRuleset(gameConfig.get("ruleset").getAsString());

		players.get(0)
				.getAsJsonObject()
				.getAsJsonArray("startingCells")
				.forEach(
						(JsonElement e) -> {
							final JsonArray a = e.getAsJsonArray();
							builder.setCell(a.get(0).getAsInt(), a.get(1)
									.getAsInt(),
									ConwayGameStateConstants.PLAYER1_CELL);
						});

		players.get(1)
				.getAsJsonObject()
				.getAsJsonArray("startingCells")
				.forEach(
						(JsonElement e) -> {
							final JsonArray a = e.getAsJsonArray();
							builder.setCell(a.get(0).getAsInt(), a.get(1)
									.getAsInt(),
									ConwayGameStateConstants.PLAYER2_CELL);
						});

		return builder.getState();
	}
}
