package hr.best.ai.games.conway.visualization;

import hr.best.ai.games.conway.Cell;
import hr.best.ai.games.conway.Cells;
import hr.best.ai.games.conway.ConwayGameState;
import hr.best.ai.games.conway.ConwayGameStateConstants;
import hr.best.ai.gl.NewStateObserver;
import hr.best.ai.gl.State;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class GameGrid extends JPanel implements NewStateObserver {

	private volatile ConwayGameState state;

	// images
	private BufferedImage logo1;
	private BufferedImage logo10;

	public GameGrid() {

		// TODO GameGrid.class.getResource(name);

		try {
			logo1 = ImageIO
					.read(new File("src/main/resources/BEST_ZG_mali.png"));
			logo10 = ImageIO.read(new File("src/main/resources/EBEC.png"));
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
		this.state = (ConwayGameState) state;
		// TODO Invoke later, provjerit dal moram bit tako repaint
		this.repaint(0);
	}

	@Override
	public void signalCompleted(String message) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (state == null) {
			return;
		}
		// TODO
		int blockSize = 32;

		// clear canvas and paint background image
		g.clearRect(0, 0, blockSize * (state.getCols() + 1), blockSize * (state.getRows() + 1));

		drawCurrentActions(g, state.getPlayer1Actions(), Color.red, blockSize);
		drawCurrentActions(g, state.getPlayer2Actions(), Color.blue, blockSize);

		// draw images for live cells
		Image img = null;
		for (int i = 0; i < state.getRows(); i++) {
			for (int j = 0; j < state.getCols(); j++) {
				g.drawRect(blockSize * j, blockSize * i, blockSize, blockSize);
				if (state.getCell(i,j) == ConwayGameStateConstants.PLAYER1_CELL) {
					img = logo1;
				} else if (state.getCell(i,j) == ConwayGameStateConstants.PLAYER2_CELL) {
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
	private void drawCurrentActions(Graphics g, Cells actions, Color color,
			int blockSize) {
		if (actions == null)
			return;
		g.setColor(color);
		for (int i = 0; i < actions.size(); i++) {
			Cell c = actions.get(i);
			g.fillRect(blockSize * c.getCol(), blockSize * c.getRow(),
					blockSize, blockSize);
		}
		g.setColor(Color.black);
	}

}
