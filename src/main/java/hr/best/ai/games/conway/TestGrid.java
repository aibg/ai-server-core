package hr.best.ai.games.conway;

import hr.best.ai.games.conway.gamestate.Cell;
import hr.best.ai.games.conway.gamestate.Cells;
import hr.best.ai.games.conway.gamestate.ConwayGameState;
import hr.best.ai.games.conway.gamestate.ConwayGameStateConstants;
import hr.best.ai.games.conway.gamestate.Rulesets;
import hr.best.ai.gl.Action;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hr.best.ai.server.ConfigUtilities;

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
        JsonParser parser = new JsonParser();
        JsonObject config;
        if (args.length == 0) {
            System.out.println("Falling back to default game configuration.");
            config = parser.parse(new InputStreamReader(RunGame.class.getClassLoader().getResourceAsStream("defaultConfig.json"), StandardCharsets.UTF_8)).getAsJsonObject();
        } else {
            System.out.println("Using " + args[0] + " configuration file");
            config = parser.parse(new FileReader(args[0])).getAsJsonObject();
        }

		ConwayGameState cgs = (ConwayGameState) ConfigUtilities.genInitState(config);
		if (config.get("visualization").getAsBoolean())
			addVisualization(cgs);

	}

	private static void addVisualization(ConwayGameState cgs) {
		

		SwingUtilities.invokeLater(() -> {
			new TestGrid(cgs);
		});
	}
}
