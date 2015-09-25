package hr.best.ai.games.conway.rulesets;


import java.util.List;

import hr.best.ai.games.conway.Cell;
import hr.best.ai.games.conway.GameState;


public class Ruleset2 extends GameState {

	public Ruleset2(int height, int width) {
		super(height, width);
	}

	public Ruleset2(int[][] field) {
		super(field);
	}
	public Ruleset2(int[][] field,int maxIter) {
		super(field,maxIter);
	}
	public Ruleset2(int[][] field, int iteration, int winner) {
		super(field, iteration, winner);
	}
	public Ruleset2(int[][] field, int iteration, int winner, int V, int K,int d, int maxIterations,List<Cell> p1,List<Cell> p2) {
		super(field, iteration, winner,V,K,d,maxIterations,p1,p2);
	}
	@Override
	protected void calculate(int[][] sum) {
		int height = sum.length;
		int width = sum[0].length;

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int p1 = sum[i][j] % PLAYER2_CELL;
				int p2 = sum[i][j] / PLAYER2_CELL;
				int result = p1 - p2;

				switch (field[i][j]) {
				case PLAYER1_CELL:
					if (result != 2 && result != 3) {
						field[i][j] = DEAD_CELL;
					}
					break;
				case PLAYER2_CELL:
					if (result != -2 && result != -3) {
						field[i][j] = DEAD_CELL;
					}
					break;
				default:
					if (result == 3) {
						field[i][j] = PLAYER1_CELL;
					}
					if (result == -3) {
						field[i][j] = PLAYER2_CELL;
					}

				}
			}
		}

	}

}
