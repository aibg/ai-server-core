package hr.best.ai.games.conway.visualization;

import hr.best.ai.games.conway.Cell;
import hr.best.ai.games.conway.GameState;
import hr.best.ai.gl.NewStateObserver;
import hr.best.ai.gl.State;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class GameGrid extends JPanel implements NewStateObserver {

	private GameState state;

	// images
	private BufferedImage logo1;
	private BufferedImage logo10;

	public GameGrid() {

		// TODO GameGrid.class.getResource(name);

		try {
			logo1 = ImageIO.read(new File("resources/BEST_ZG_mali.png"));
			logo10 = ImageIO.read(new File("resources/EBEC.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public void signalNewState(State state) {
		this.state = (GameState) state;
		this.repaint();
	}

	@Override
	public void signalCompleted(String message) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		// TODO
		int blockSize = 32;

		int[][] field = state.getField();

		int height = field.length;
		int width = field[0].length;

		// clear canvas and paint background image
		g.clearRect(0, 0, blockSize * (width + 1), blockSize * (height + 1));

		drawCurrentActions(g, state.getPlayer1Actions(), Color.red, blockSize);
		drawCurrentActions(g, state.getPlayer2Actions(), Color.blue, blockSize);

		// draw images for live cells
		Image img = null;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				g.drawRect(blockSize * j, blockSize * i, blockSize, blockSize);
				if (field[i][j] == 1) {
					img = logo1;
				} else if (field[i][j] == 10) {
					img = logo10;
				} else
					continue;
				g.drawImage(img, blockSize * j, blockSize * i, this);

			}
		}
	}

	/**
	 * TODO
	 * 
	 * @param g
	 * @param actions
	 * @param color
	 * @param blockSize
	 */
	private void drawCurrentActions(Graphics g, List<Cell> actions,
			Color color, int blockSize) {
		g.setColor(Color.red);
		for (int i = 0; i < actions.size(); i++) {
			Cell c = actions.get(i);
			g.fillRect(blockSize * c.getCol(), blockSize * c.getRow(),
					blockSize, blockSize);
		}
		g.setColor(Color.black);
	}

}
