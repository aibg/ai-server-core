package hr.best.ai.games.conway.players;

import hr.best.ai.exceptions.InvalidActionException;
import hr.best.ai.games.conway.gamestate.Cell;
import hr.best.ai.games.conway.gamestate.Cells;
import hr.best.ai.gl.AbstractPlayer;

import java.io.IOException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class GrowerDemo extends AbstractPlayer {

	public GrowerDemo() {
        super("grower demo");
	}

	/**
	 * Adds value to every element around matrix[i][j]
	 * 
	 * @param matrix
	 * @param i
	 * @param j
	 * @param value
	 */
	private void addAround(int[][] matrix, int i, int j, int value) {

		int height = matrix.length;
		int width = matrix[0].length;

		matrix[Math.floorMod(i - 1, height)][Math.floorMod(j - 1, width)] += value;
		matrix[Math.floorMod(i - 1, height)][Math.floorMod(j, width)] += value;
		matrix[Math.floorMod(i - 1, height)][Math.floorMod(j + 1, width)] += value;

		matrix[Math.floorMod(i, height)][Math.floorMod(j - 1, width)] += value;
		matrix[Math.floorMod(i, height)][Math.floorMod(j + 1, width)] += value;

		matrix[Math.floorMod(i + 1, height)][Math.floorMod(j - 1, width)] += value;
		matrix[Math.floorMod(i + 1, height)][Math.floorMod(j, width)] += value;
		matrix[Math.floorMod(i + 1, height)][Math.floorMod(j + 1, width)] += value;

	}

	@Override
	public void close() throws Exception {
	}

	@Override
	public void sendError(JsonObject message) {
        System.err.println(message);
	}

	@Override
	public JsonObject signalNewState(JsonObject state) throws IOException,
			InvalidActionException {

		JsonArray jsonField = state.get("field").getAsJsonArray();
		int height = jsonField.size();
		int width = jsonField.get(0).getAsString().length();
		int[][] field = new int[height][width];
		for (int i = 0; i < height; i++) {
			String line = jsonField.get(i).getAsString();
			for (int j = 0; j < width; j++) {
				field[i][j] = Integer.parseInt(line.substring(j, j + 1));
			}

		}

		// no of players neighbor cells for each player cell
		int[][] myNeighbors = new int[height][width];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (field[i][j] == 1)
					addAround(myNeighbors, i, j, 1);
			}
		}

		// no of potential neighbors activated or kept alive
		int[][] potential = new int[height][width];

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int neighbors = myNeighbors[i][j];
				if (field[i][j] == 1) {
					if (neighbors == 1 || neighbors == 2)
						addAround(potential, i, j, 1);
					if (neighbors == 3)
						addAround(potential, i, j, -1);
				} else if (field[i][j] == 0) {
					if (neighbors == 2)
						addAround(potential, i, j, 1);
				} else {
					potential[i][j] -= 10;
					addAround(potential, i, j, -10);
				}

			}
		}

		// finds max potential
		int maxPotential = Integer.MIN_VALUE;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (potential[i][j] >= maxPotential && field[i][j] == 0)
					maxPotential = potential[i][j];
			}
		}

		// list of coordinates with max potential
		Cells candidates = new Cells();
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (potential[i][j] == maxPotential) {
					candidates.add(new Cell(i, j));
				}
			}
		}

		// List<Point> output = new LinkedList<Point>();
		// output.add(candidates.get(0));

		JsonObject output = new JsonObject();
		JsonArray cells = new JsonArray();
		cells.add(candidates.get(0).toJSON());
		output.add("cells", cells);

		return output;
	}
}
