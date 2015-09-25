package hr.best.ai.demo;
import hr.best.ai.exceptions.InvalidActionException;
import hr.best.ai.gl.Action;
import hr.best.ai.gl.IPlayer;
import hr.best.ai.gl.State;

import java.awt.Point;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Grower implements IPlayer{

	private int height, width;
	private int[][] field;

	private int playerNo;

	public Grower(int playerNo, int height, int width, int[][] field,
			int bucket, int d) {

		this.height = height;
		this.width = width;
		this.field = field;
		// not using bucket in this bot
		// not using valid distance d either

	}

	public List<Point> calculate() {
		// no of players neighbor cells for each player cell
		int[][] myNeighbors = new int[height][width];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (field[i][j] == playerNo)
					addAround(myNeighbors, i, j, 1);
			}
		}

		// no of potential neighbors activated or kept alive
		int[][] potential = new int[height][width];

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int neighbors = myNeighbors[i][j];
				if (field[i][j] == playerNo) {
					if (neighbors == 1 || neighbors == 2)
						addAround(potential, i, j, 1);
					if (neighbors == 3)
						addAround(potential, i, j, -1);
				} else if (field[i][j] == playerNo) {
					if (neighbors == 3)
						addAround(potential, i, j, 1);
				} else {
					potential[i][j] -= 10;
					addAround(potential, i, j, -10);
				}

			}
		}

		// finds max potential
		int maxPotential = 0;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (potential[i][j] > maxPotential && field[i][j] == 0)
					maxPotential = potential[i][j];
			}
		}

		// list of coordinates with max potential
		List<Point> candidates = new LinkedList<Point>();
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (potential[i][j] == maxPotential) {
					candidates.add(new Point(i, j));
				}
			}
		}
		Collections.shuffle(candidates);

		List<Point> output = new LinkedList<Point>();
		output.add(candidates.get(0));

		return output;
	}

	private void addAround(int[][] matrix, int i, int j, int k) {
		matrix[mod(i - 1, height)][mod(j - 1, width)] += k;
		matrix[mod(i - 1, height)][mod(j, width)] += k;
		matrix[mod(i - 1, height)][mod(j + 1, width)] += k;

		matrix[mod(i, height)][mod(j - 1, width)] += k;
		matrix[mod(i, height)][mod(j + 1, width)] += k;

		matrix[mod(i + 1, height)][mod(j - 1, width)] += k;
		matrix[mod(i + 1, height)][mod(j, width)] += k;
		matrix[mod(i + 1, height)][mod(j + 1, width)] += k;

	}

	private int mod(int a, int length) {

		return (a + length) % length;
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendError(String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Action signalNewState(State state) throws IOException,
			InvalidActionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void signalCompleted(String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
}
