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
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.google.gson.JsonObject;

@SuppressWarnings("serial")
public class GameGridPanel extends JPanel implements NewStateObserver {

	final static Logger logger = Logger.getLogger(GameGridPanel.class);
	private volatile ConwayGameState state;
	private int blockSize;

	private Image P1_logo;
	private Image P2_logo;
	private Color player1Color;
	private Color player2Color;
	private Color gridColor;

	public GameGridPanel(ConwayGameState initialState, Image p1Logo,
			Image p2Logo, Color player1Color, Color player2Color,
			Color gridColor, Dimension containterDimension) {

		this.state = initialState;
		this.P1_logo = p1Logo;
		this.P2_logo = p2Logo;
		
		this.player1Color = player1Color;
		this.player2Color = player2Color;
		this.gridColor = gridColor;

		setSizes(containterDimension);

		setOpaque(false);
		setVisible(true);
		
	}

	private void setSizes(Dimension drawSpace) {
		double blockWidth = drawSpace.width / this.state.getCols();
		double blockHeight = drawSpace.height / this.state.getRows();

		blockSize = Math.toIntExact(Math.round(Math.floor(Math.min(blockWidth,
				blockHeight))));
		
		int width = blockSize * this.state.getCols() + 1;
		int height = blockSize * this.state.getRows() + 1;
		Dimension newSize = new Dimension(width, height);
		setMinimumSize(newSize);
		setPreferredSize(newSize);
		setMaximumSize(newSize);
	}

	@Override
	public void signalNewState(State state) {
		long t = System.currentTimeMillis();
		this.state = (ConwayGameState) state;
		this.repaint(0);
		logger.debug(String.format("Repaint finished: %3dms",
				System.currentTimeMillis() - t));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		// initially called before first state
		if (state == null)
			return;

		setSizes(getParent().getBounds().getSize());
		drawCurrentActions(g, state.getPlayer1Actions(), player1Color.darker().darker().darker());
		drawCurrentActions(g, state.getPlayer2Actions(), player2Color.darker().darker().darker());
		
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

		// draw images (or player color squares)
		for (int i = 0; i < state.getRows(); i++) {
			for (int j = 0; j < state.getCols(); j++) {
				switch (state.getCell(i, j)) {
				case ConwayGameStateConstants.PLAYER1_CELL:
					if(P1_logo==null){
						g.setColor(player1Color);
						g.fillRect(blockSize * j, blockSize * i, blockSize, blockSize);
						break;
					}					
					g.drawImage(P1_logo, blockSize * j, blockSize * i,
							blockSize, blockSize, this);
					break;
				case ConwayGameStateConstants.PLAYER2_CELL:
					if(P1_logo==null){
						g.setColor(player2Color);
						g.fillRect(blockSize * j, blockSize * i, blockSize, blockSize);
						break;
					}
					g.drawImage(P2_logo, blockSize * j, blockSize * i,
							blockSize, blockSize, this);
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
			g.fillRect(blockSize * c.getCol(), blockSize * c.getRow(),
					blockSize, blockSize);
		}
		g.setColor(gridColor);
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void signalError(JsonObject message) {
		// TODO Auto-generated method stub

	}

}
