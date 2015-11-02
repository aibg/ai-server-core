package hr.best.ai.games.conway.visualization;


import hr.best.ai.games.conway.ConwayGameState;
import hr.best.ai.games.conway.ConwayGameStateConstants;
import hr.best.ai.gl.NewStateObserver;
import hr.best.ai.gl.State;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GameBarPanel extends JPanel implements NewStateObserver {

	private volatile ConwayGameState state;
	private Color player1Color;
	private Color player2Color;

	public GameBarPanel(Color player1Color, Color player2Color) {
		this.player1Color = player1Color;
		this.player2Color = player2Color;
	}

	@Override
	public void signalNewState(State state) {
		this.state = (ConwayGameState) state;
		this.repaint(0);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (state == null)
			return;

		// counting active cells
		int player1count = 0, player2count = 0;

		for (int i = 0; i < state.getRows(); i++) {
			for (int j = 0; j < state.getCols(); j++) {
				switch (state.getCell(i, j)) {
				case ConwayGameStateConstants.PLAYER1_CELL:
					player1count++;
					break;
				case ConwayGameStateConstants.PLAYER2_CELL:
					player2count++;
					break;
				}
			}
		}

		// calculating the location of color change
		int divide = (int) (1.0 * getWidth() / (player1count + player2count) * player1count);

		// drawing
		g.setColor(player1Color);
		g.fillRect(0, 0, divide, getHeight());
		g.setColor(player2Color);
		g.fillRect(divide, 0, getWidth(), getHeight());

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
