package hr.best.ai.games.conway.rulesets;


import java.util.List;

import hr.best.ai.games.conway.Cell;
import hr.best.ai.games.conway.GameState;

public class Ruleset1 extends GameState {

	public Ruleset1(int height, int width) {
		super(height, width);
	}

	public Ruleset1(int[][] field) {
		super(field);
	}

	public Ruleset1(int[][] field, int maxIter) {
		super(field, maxIter);
	}

	public Ruleset1(int[][] field, int iteration, int winner) {
		super(field, iteration, winner);
	}

	public Ruleset1(int[][] field, int iteration, int winner, int V, int K,
			int d, int maxIterations, List<Cell> p1, List<Cell> p2) {
		super(field, iteration, winner, V, K, d, maxIterations, p1, p2);
	}

	@Override
	protected void calculate(int[][] sum) {

		int height = sum.length;
		int width = sum[0].length;

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				switch (field[i][j]) {
				case PLAYER1_CELL:
					if (sum[i][j] != 2 * PLAYER1_CELL
							&& sum[i][j] != 3 * PLAYER1_CELL) {
						field[i][j] = DEAD_CELL;
					}
					break;
				case PLAYER2_CELL:
					if (sum[i][j] != 2 * PLAYER2_CELL
							&& sum[i][j] != 3 * PLAYER2_CELL) {
						field[i][j] = DEAD_CELL;
					}
					break;
				default:
					if (sum[i][j] == 3 * PLAYER1_CELL) {
						field[i][j] = PLAYER1_CELL;
					}
					if (sum[i][j] == 3 * PLAYER2_CELL) {
						field[i][j] = PLAYER2_CELL;
					}
				}
			}
		}
	}

}
