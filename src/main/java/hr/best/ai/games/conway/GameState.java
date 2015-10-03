package hr.best.ai.games.conway;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import hr.best.ai.exceptions.InvalidActionException;
import hr.best.ai.games.conway.rulesets.Ruleset1;
import hr.best.ai.gl.Action;
import hr.best.ai.gl.State;

/**
 * Game state of Game of Life.
 * 
 * @author andrej
 */
public abstract class GameState implements State {

	// cells gained per turn
	private int cellsPerTurn = 1;

	// bucket capacity
	private int maxBucketCapacity = 5;

	// distance from nearest alive cell
	private int allowedDistance = 4;

	// iterations until game end
	private int maxIterations = 10000;

	public static final int DEAD_CELL = 0;
	public static final int PLAYER1_CELL = 1;
	public static final int PLAYER2_CELL = 10;

	protected int bucket1 = maxBucketCapacity;
	protected int bucket2 = maxBucketCapacity;

	private Cells p1;
	private Cells p2;

	/**
	 * Array with current live cells. 0 for dead cell. 1 for player1, 10 for
	 * player2.
	 */
	protected final int[][] field;

	protected int iteration = 0;

	public static final int RUNNING = 0;

	public static final int PLAYER1_WIN = 1;

	public static final int PLAYER2_WIN = 2;

	public static final int TIE = 3;
	/**
	 * 
	 * 0 still playing<br>
	 * 1 player1 winner<br>
	 * 2 player2 <br>
	 * winner 3 tie
	 */
	protected int winner = RUNNING;

	public int getIteration() {
		return iteration;
	}

	public int getCellsPerTurn() {
		return cellsPerTurn;
	}

	public int getMaxBucketCapacity() {
		return maxBucketCapacity;
	}

	public int getAllowedDistance() {
		return allowedDistance;
	}

	public int getMaxIterations() {
		return maxIterations;
	}

	/**
	 * 1 for player1<br>
	 * 2 for player2
	 * 
	 * 
	 * @return winner
	 */
	public int getWinner() {
		return winner;
	}

	@Override
	public boolean isFinal() {
		return winner > 0;
	}

	protected GameState(int height, int width) {
		this.field = new int[height][width];
	}

	public int[][] getField() {
		return field;
	}

	protected GameState(int[][] field) {
		this.field = field;
	}

	protected GameState(int[][] field, int iteration, int winner) {
		this.field = field;
		this.iteration = iteration;
		this.winner = winner;
	}

	protected GameState(int[][] field, int maxIterations) {
		this.field = field;
		this.maxIterations = maxIterations;
	}

	protected GameState(int[][] field, int iteration, int winner,
			int cellsPerTurn, int maxBucketCapacity, int allowedDistance,
			int maxIterations, Cells p1, Cells p2) {
		this.field = field;
		this.iteration = iteration;
		this.winner = winner;
		this.cellsPerTurn = cellsPerTurn;
		this.maxBucketCapacity = maxBucketCapacity;
		this.allowedDistance = allowedDistance;
		this.maxIterations = maxIterations;
		this.p1 = p1;
		this.p2 = p2;
	}

	public Cells getPlayer1Actions() {
		return p1;
	}

	public Cells getPlayer2Actions() {
		return p2;
	}

	@SuppressWarnings("unchecked")
	@Override
	public GameState nextState(List<Action> actionList) {

		int height = field.length;
		int width = field[0].length;

		// adds cells based on player actions
		p1 = (Cells) actionList.get(0);
		p2 = (Cells) actionList.get(1);

		// checks if cell is legal and removes it otherwise

		removeIllegal(p1, PLAYER1_CELL, PLAYER2_CELL);
		removeIllegal(p2, PLAYER2_CELL, PLAYER1_CELL);

		// removes actions in which both players try to activate identical cell
		for (int i = 0; i < p1.size(); i++) {
			Cell c = p1.get(i);
			if (p2.contains(c)) {
				p1.remove(c);
				p2.remove(c);
				i--;
			}
		}

		// activates up to bucketX cells for each player where X is player
		// number
		activate(p1, bucket1, PLAYER1_CELL);
		activate(p2, bucket2, PLAYER2_CELL);

		// calculates number of active neighbor cells
		int[][] sum = new int[height][width];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				sum[i][j] += field[Math.floorMod(i - 1, height)][Math.floorMod(
						j - 1, width)];
				sum[i][j] += field[Math.floorMod(i - 1, height)][j];
				sum[i][j] += field[Math.floorMod(i - 1, height)][Math.floorMod(
						j + 1, width)];

				sum[i][j] += field[i][Math.floorMod(j - 1, width)];
				sum[i][j] += field[i][Math.floorMod(j + 1, width)];

				sum[i][j] += field[Math.floorMod(i + 1, height)][Math.floorMod(
						j - 1, width)];
				sum[i][j] += field[Math.floorMod(i + 1, height)][j];
				sum[i][j] += field[Math.floorMod(i + 1, height)][Math.floorMod(
						j + 1, width)];
			}
		}

		// sets new values
		calculate(sum);

		// TODO determine winner (must decide win condition) the following
		// should be replaced
		int win = RUNNING;

		int cells1 = 0;
		int cells2 = 0;
		if (iteration >= maxIterations) {
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					if (field[i][j] == PLAYER1_CELL)
						cells1++;
					if (field[i][j] == PLAYER2_CELL)
						cells2++;
				}
			}
			if (cells1 > cells2)
				win = PLAYER1_WIN;
			else if (cells1 < cells2)
				win = PLAYER2_WIN;
			else
				win = TIE;
		}

		bucket1 = (bucket1 + cellsPerTurn) % maxBucketCapacity;
		bucket2 = (bucket2 + cellsPerTurn) % maxBucketCapacity;

		// TODO ha?
		return new Ruleset1(field, iteration + 1, win, cellsPerTurn,
				maxBucketCapacity, allowedDistance, maxIterations, p1, p2);
	}

	/**
	 * Activates up to bucket cells for player. TODO
	 * 
	 * @param pActions
	 * @param bucket
	 * @param playerCell
	 */
	private void activate(Cells pActions, int bucket, int playerCell) {

		for (int i = 0; i < pActions.size() && bucket > 0; i++) {
			Cell c = pActions.get(i);
			field[c.getRow()][c.getCol()] = playerCell;
			bucket--;
		}
	}

	/**
	 * Removes illegal actions(more than <code>allowedDistance</code> away from
	 * other live cells or exactly on top of opponent cell) TODO
	 * 
	 * @param pActions
	 * @param playerCell
	 * @param opponentCell
	 */
	private void removeIllegal(Cells pActions, int playerCell, int opponentCell) {
		for (int i = 0; i < pActions.size(); i++) {
			Cell c = pActions.get(i);
			if (field[c.getRow()][c.getCol()] == opponentCell
					|| !isNear(c, playerCell)) {
				pActions.remove(c);
				i--;
			}
		}
	}

	/**
	 * template method
	 * 
	 * @param sum
	 */
	protected abstract void calculate(int[][] sum);

	/**
	 * Checks if there is a same player cell alive that less than
	 * allowedDistance+1 away.
	 * 
	 * @param c
	 * @param playerCell
	 * @return
	 */
	protected boolean isNear(Cell c, int playerCell) {
		int height = field.length;
		int width = field[0].length;
		for (int row = c.getRow() - allowedDistance; row <= c.getRow()
				+ allowedDistance; row++) {
			for (int col = c.getCol() - allowedDistance; col <= c.getCol()
					+ allowedDistance; col++) {
				if (field[Math.floorMod(row, height)][Math.floorMod(col, width)] == playerCell) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Json object containing JsonArray representing game field and two
	 * JsonPrimitives with current bucket values.
	 */
	@Override
	public JsonObject toJSONObject() {

		int height = field.length;
		int width = field[0].length;

		JsonObject json = new JsonObject();

		JsonArray array = new JsonArray();
		json.add("field", array);

		for (int i = 0; i < height; i++) {
			StringBuilder sb = new StringBuilder();
			for (int j = 0; j < width; j++) {
				switch (field[i][j]) {

				case PLAYER1_CELL:
					sb.append("1");
					break;
				case PLAYER2_CELL:
					sb.append("2");
					break;
				default:
					sb.append("0");
				}
			}
			array.add(new JsonPrimitive(sb.toString()));
		}

		json.add("bucket1", new JsonPrimitive(bucket1));
		json.add("bucket2", new JsonPrimitive(bucket2));

		return json;
	}

	@Override
	public JsonObject toJSONObjectAsPlayer(int playerId) {
		int height = field.length;
		int width = field[0].length;

		JsonObject json = new JsonObject();

		JsonArray array = new JsonArray();
		json.add("field", array);

		for (int i = 0; i < height; i++) {
			StringBuilder sb = new StringBuilder();
			for (int j = 0; j < width; j++) {
				switch (field[i][j]) {

				case PLAYER1_CELL:
					sb.append(String.valueOf(playerId));
					break;
				case PLAYER2_CELL:
					sb.append(String.valueOf(3 - playerId));
					break;
				default:
					sb.append("0");
				}
			}
			array.add(new JsonPrimitive(sb.toString()));
		}

		json.add("bucket", new JsonPrimitive(bucket1));

		return json;
	}

	@Override
	public String toString() {
		int height = field.length;
		int width = field[0].length;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < height; i++) {

			for (int j = 0; j < width; j++) {
				switch (field[i][j]) {

				case PLAYER1_CELL:
					sb.append("1");
					break;
				case PLAYER2_CELL:
					sb.append("2");
					break;
				default:
					sb.append("0");
				}
			}
			sb.append('\n');
		}
		return sb.toString();
	}

	public Action parseAction(JsonObject action) throws InvalidActionException {
		Cells actions = new Cells();

		JsonArray array = action.get("cells").getAsJsonArray();

		for (int i = 0; i < array.size(); i++) {
			String[] coordinate = array.get(i).getAsString().split(",");
			actions.add(new Cell(Integer.parseInt(coordinate[0]), Integer
					.parseInt(coordinate[1])));
		}

		return actions;
	}

}
