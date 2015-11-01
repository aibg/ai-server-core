package hr.best.ai.games.conway.visualization;

import hr.best.ai.games.conway.Cell;
import hr.best.ai.games.conway.Cells;
import hr.best.ai.games.conway.ConwayGameState;
import hr.best.ai.games.conway.ConwayGameStateConstants;
import hr.best.ai.gl.NewStateObserver;
import hr.best.ai.gl.State;
import org.apache.log4j.Logger;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class GameGrid extends JPanel implements NewStateObserver {

    final static Logger logger = Logger.getLogger(GameGrid.class);
	private volatile ConwayGameState state;

	private BufferedImage P1_logo;
	private BufferedImage P2_logo;

	public GameGrid() throws IOException{
			P1_logo = ImageIO.read(new File("src/main/resources/BEST_ZG_mali.png"));
			P2_logo = ImageIO.read(new File("src/main/resources/EBEC.png"));

	}

	@Override
	public void close() throws Exception {
	}

	@Override
	public void signalNewState(State state) {
		this.state = (ConwayGameState) state;
		// TODO Invoke later, provjerit dal moram bit tako repaint
		this.repaint(0);
	}

	@Override
	public void signalCompleted(String message) {
	}

	@Override
	protected void paintComponent(Graphics g) {
        long t = System.currentTimeMillis();
		super.paintComponent(g);
		if (state == null) {
			return;
		}
		// TODO
		int blockSize = 8;

		// clear canvas and paint background image
		g.clearRect(0, 0, blockSize * (state.getCols() + 1), blockSize * (state.getRows() + 1));

		drawCurrentActions(g, state.getPlayer1Actions(), Color.red, blockSize);
		drawCurrentActions(g, state.getPlayer2Actions(), Color.blue, blockSize);

		for (int i = 0; i < state.getRows(); i++) {
			for (int j = 0; j < state.getCols(); j++) {
				g.drawRect(blockSize * j, blockSize * i, blockSize, blockSize);
                switch (state.getCell(i, j)) {
                    case ConwayGameStateConstants.PLAYER1_CELL:
                        g.drawImage(P1_logo, blockSize * j, blockSize * i, blockSize, blockSize, this);
                        break;
                    case ConwayGameStateConstants.PLAYER2_CELL:
                        g.drawImage(P2_logo, blockSize * j, blockSize * i, blockSize, blockSize, this);
                        break;
                    case ConwayGameStateConstants.DEAD_CELL:
                        continue;
                }
			}
		}
        logger.debug(String.format("Repaint finished: %3dms", System.currentTimeMillis() - t));
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
