package hr.best.ai.games.conway.visualization;

import hr.best.ai.games.conway.gamestate.ConwayGameState;
import hr.best.ai.gl.NewStateObserver;
import hr.best.ai.gl.State;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.google.gson.JsonObject;
/**
 * Visualizes the game. On each new state draws the grid and living cells.
 */
@SuppressWarnings("serial")
public class GameGridPanel extends JPanel implements NewStateObserver {

	final static Logger logger = Logger.getLogger(GameGridPanel.class);
	
	private volatile ConwayGameState state;
	
	/**
	 * Size of one grid element. Cell size.
	 */
	protected int blockSize;

	protected final Color player1Color;
	protected final Color player2Color;
	private final Color gridColor;

	/**
	 * Sets the initial state and colors.
	 */
	public GameGridPanel(ConwayGameState initialState, Color player1Color, Color player2Color,
			Color gridColor) {

		this.state = initialState;

		this.player1Color = player1Color;
		this.player2Color = player2Color;
		this.gridColor = gridColor;

		setOpaque(false);
		setVisible(true);
	}

	/**
	 * Calculates and sets grid size based on available space.
	 * @param drawSpace allowed drawing space
	 */
	public void setComponentSize(Dimension drawSpace) {
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
	
	/**
	 * Paints new state.
	 */
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

		setComponentSize(getParent().getBounds().getSize());

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

		// draw player color squares
		for (int i = 0; i < state.getRows(); i++) {
			for (int j = 0; j < state.getCols(); j++) {
				switch (state.getCell(i, j)) {
				case P1:
                    paintCell(g, i, j, player1Color);
					break;
				case P2:
                    paintCell(g, i, j, player2Color);
                    break;
				case DEAD:
					continue;
				}
			}
		}
	}

    protected void paintCell(Graphics g, int row, int col, Color color) {
        g.setColor(color);
        g.fillRect(blockSize * col, blockSize * row, blockSize, blockSize);
    }

	@Override
	public void close() throws Exception {
	}

	@Override
	public void signalError(JsonObject message) {
		// TODO Auto-generated method stub
	}
}
