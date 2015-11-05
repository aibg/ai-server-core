package hr.best.ai.games.conway.visualization;

import hr.best.ai.games.conway.Cell;
import hr.best.ai.games.conway.Cells;
import hr.best.ai.games.conway.ConwayGameState;
import hr.best.ai.games.conway.ConwayGameStateConstants;
import hr.best.ai.gl.NewStateObserver;
import hr.best.ai.gl.State;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GameGridPanel extends JPanel implements NewStateObserver {

	private volatile ConwayGameState state;
	private double blockSize;

	private BufferedImage P1_logo;
	private BufferedImage P2_logo;
	private Color player1Color;
	private Color player2Color;
	private Color gridColor;

	public GameGridPanel(String p1Logo, String p2Logo, Color player1Color,
			Color player2Color, Color gridColor) throws IOException {
		P1_logo = ImageIO.read(this.getClass().getResource(p1Logo));
		P2_logo = ImageIO.read(this.getClass().getResource(p2Logo));
		this.player1Color = player1Color;
		this.player2Color = player2Color;
		this.gridColor = gridColor;

		setVisible(false);
		setOpaque(false);
	}

	@Override
	public void signalNewState(State state) {
		this.state = (ConwayGameState) state;

		double blockWidth = (double) getParent().getBounds().width
				/ this.state.getCols();
		double blockHeight = ((double) getParent().getBounds().height)
				/ this.state.getRows();

		blockSize = Math.min(blockWidth, blockHeight);

		
		//this shouldn't be done on every new state but can't make it differently for now
		int width = (int) (blockSize * this.state.getCols() + 1);
		int height = (int) (blockSize * this.state.getRows());
		Dimension newSize = new Dimension(width, height);
		setMinimumSize(newSize);
		setPreferredSize(newSize);
		setMaximumSize(newSize);
		setVisible(true);
		validate();
		//--------------------------------------------------------------------------------
		
		
		this.repaint(0);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		// initially called before first state
		if (state == null)
			return;

		drawCurrentActions(g, state.getPlayer1Actions(), player1Color);
		drawCurrentActions(g, state.getPlayer2Actions(), player2Color);

		// draw grid

		// horizontal lines
		for (int i = 0; i <= state.getRows(); i++) {
			g.drawLine(0, (int) (i * blockSize),
					(int) (state.getCols() * blockSize), (int) (i * blockSize));
		}
		// vertical lines
		for (int i = 0; i <= state.getCols(); i++) {
			g.drawLine((int) (i * blockSize), 0, (int) (i * blockSize),
					(int) (state.getCols() * blockSize));
		}

		// draw images
		for (int i = 0; i < state.getRows(); i++) {
			for (int j = 0; j < state.getCols(); j++) {

				switch (state.getCell(i, j)) {
				case ConwayGameStateConstants.PLAYER1_CELL:
					g.drawImage(P1_logo, (int) (blockSize * j),
							(int) (blockSize * i), (int) blockSize,
							(int) blockSize, this);
					break;
				case ConwayGameStateConstants.PLAYER2_CELL:
					g.drawImage(P2_logo, (int) (blockSize * j),
							(int) (blockSize * i), (int) blockSize,
							(int) blockSize, this);
					break;
				case ConwayGameStateConstants.DEAD_CELL:
					continue;
				}
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
	private void drawCurrentActions(Graphics g, Cells actions, Color color) {

		// initial state doesn't have actions
		if (actions == null)
			return;

		g.setColor(color);
		for (int i = 0; i < actions.size(); i++) {
			Cell c = actions.get(i);
			g.fillRect((int) (blockSize * c.getCol()),
					(int) (blockSize * c.getRow()), (int) blockSize,
					(int) blockSize);
		}
		g.setColor(gridColor);
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void signalCompleted(String message) {
		// TODO Auto-generated method stub

	}

}
