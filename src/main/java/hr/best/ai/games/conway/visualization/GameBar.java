package hr.best.ai.games.conway.visualization;

import java.awt.Color;
import java.awt.Graphics;

import hr.best.ai.games.conway.GameState;
import hr.best.ai.gl.NewStateObserver;
import hr.best.ai.gl.State;

import javax.swing.JPanel;

public class GameBar extends JPanel implements NewStateObserver {

	private volatile GameState state;

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

		if(state==null){
			return;
		}
		
		
		// TODO maknut ovo
		int blockSize = 32;

		int[][] field = state.getField();
		int height = field.length;
		int width = field[0].length;

		// counting active cells
		int p1 = 0, p2 = 0;

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (field[i][j] == GameState.PLAYER1_CELL) {
					p1++;
				} else if (field[i][j] == GameState.PLAYER2_CELL) {
					p2++;
				}
			}
		}

		// calculating the location of color change
		int divide = (int) (1.0 * width * blockSize / (p1 + p2) * p1);

		// drawing
		g.setColor(Color.red);
		g.fillRect(0, 0, divide, height * blockSize);
		g.setColor(Color.blue);
		g.fillRect(divide, 0, width * blockSize - divide +1, height * blockSize);

	}

}
