package hr.best.ai.games.conway;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.sun.istack.internal.NotNull;

import hr.best.ai.exceptions.InvalidActionException;
import hr.best.ai.gl.Action;
import hr.best.ai.gl.State;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.function.Function;

/**
 * Game state of Game of Life.
 */
public class ConwayGameState implements State {

	private final int cellGainPerTurn;
	private final int maxCellCapacity;
	private final int maxColonisationDistance;
	private final int currIteration;
	private final int maxGameIterations;
	private final int[][] field;
	private final int p1_cells;
	private final Cells lastTurnP1;
	private final int p2_cells;
	private final Cells lastTurnP2;
	private final Function<Pair<Integer, Integer>, Integer> fromEmpty;
	private final Function<Triple<Integer, Integer, Integer>, Integer> fromOccupied;
	private final int p1score;
	private final int p2score;
	private final int p1count;
	private final int p2count;

	public ConwayGameState(
			int cellGainPerTurn,
			int maxCellCapacity,
			int maxColonisationDistance,
			int currIteration,
			int maxGameIterations,
			int[][] field,
			int p1_cells,
			Cells lastTurnP1,
			int p2_cells,
			Cells lastTurnP2,
			@NotNull Function<Pair<Integer, Integer>, Integer> fromEmpty,
			@NotNull Function<Triple<Integer, Integer, Integer>, Integer> fromOccupied,
			int p1score, int p2score) {
		this.cellGainPerTurn = cellGainPerTurn;
		this.maxCellCapacity = maxCellCapacity;
		this.maxColonisationDistance = maxColonisationDistance;
		this.currIteration = currIteration;
		this.maxGameIterations = maxGameIterations;
		this.field = field;
		this.p1_cells = p1_cells;
		this.lastTurnP1 = lastTurnP1;
		this.p2_cells = p2_cells;
		this.lastTurnP2 = lastTurnP2;
		this.fromEmpty = fromEmpty;
		this.fromOccupied = fromOccupied;
		this.p1score = p1score;
		this.p2score = p2score;
		this.p1count=countCells(ConwayGameStateConstants.PLAYER1_CELL);
		this.p2count=countCells(ConwayGameStateConstants.PLAYER2_CELL);

	}

	public Cells getPlayer1Actions() {
		return lastTurnP1;
	}

	public Cells getPlayer2Actions() {
		return lastTurnP2;
	}

	private int countCells(int player) {
		int count = 0;

		for (int i = 0; i < getRows(); i++) {
			for (int j = 0; j < getCols(); j++) {
				if (getCell(i, j) == player)
					count++;
			}
		}
		return count;
	}

	public int getP1LiveCellcount() {
		return p1count;
	}

	public int getP2LiveCellcount() {
		return p2count;
	}

	public int getP1Score() {
		return p1score;
	}

	public int getP2Score() {
		return p2score;
	}

	public int getRows() {
		return field.length;
	}

	public int getCols() {
		return field[0].length;
	}

	public int getCell(int row, int col) {
		return field[Math.floorMod(row, field.length)][Math.floorMod(col, field[0].length)];
	}

	/**
	 * Json object containing JsonArray representing game field and two
	 * JsonPrimitives with current bucket values.
	 */
	@Override
	public JsonObject toJSONObject() {
		JsonObject json = toJSONObjectAsPlayer(ConwayGameStateConstants.PLAYER1_CELL);
		json.remove("cellsRemaining");
		json.addProperty("cellsRemainingP1", p1_cells);
		json.addProperty("cellsRemainingP2", p2_cells);
		return json;
	}

	@Override
	public JsonObject toJSONObjectAsPlayer(int playerId) {
		JsonObject json = new JsonObject();

		JsonArray array = new JsonArray();
		json.add("field", array);

		for (int i = 0; i < getRows(); i++) {
			StringBuilder sb = new StringBuilder();
			for (int j = 0; j < getCols(); j++) {
				switch (field[i][j]) {
				case ConwayGameStateConstants.PLAYER1_CELL:
					sb.append(playerId == ConwayGameStateConstants.PLAYER1_CELL ? "#"
							: "O");
					break;
				case ConwayGameStateConstants.PLAYER2_CELL:
					sb.append(playerId == ConwayGameStateConstants.PLAYER1_CELL ? "O"
							: "#");
					break;
				case ConwayGameStateConstants.DEAD_CELL:
					sb.append(".");
					break;
				}
			}
			array.add(new JsonPrimitive(sb.toString()));
		}

		json.addProperty("cellsRemaining",
				playerId == ConwayGameStateConstants.PLAYER1_CELL ? p1_cells
						: p2_cells);
		json.addProperty("cellGainPerTurn", cellGainPerTurn);
		json.addProperty("maxCellCapacity", maxCellCapacity);
		json.addProperty("maxColonisationDistance", maxColonisationDistance);
		json.addProperty("currIteration", currIteration);
		json.addProperty("maxGameIterations", maxGameIterations);
		return json;
	}

	@Override
	public String toString() {
		return this.toJSONObject().toString();
	}

	public Action parseAction(JsonObject action) throws InvalidActionException {
		return Cells.fromJsonObject(action);
	}

	@Override
	public boolean isFinal() {
		return currIteration >= maxGameIterations;
	}

	private int distanceToFriendlyCell(int row, int col, int cell_type,
			int maxSearchDistance) {
		int distance = Integer.MAX_VALUE;
		
		for (int r = row - maxSearchDistance; r <= row + maxSearchDistance; ++r) {
			for (int c = col - maxSearchDistance; c <= col + maxSearchDistance; ++c)
				if (getCell(r, c) == cell_type)
					distance = Math.min(distance,Math.max(Math.abs(r - row),Math.abs(c - col)));
		}
		return distance;
	}

	private int getSurroundingCellCount(int row, int col, int cell_type) {
		int dr[] = { -1, -1, -1, 0, 0, 1, 1, 1 };
		int dc[] = { -1, 0, 1, -1, 1, -1, 0, 1 };
		int sol = 0;
		for (int i = 0; i < dr.length; ++i) {
			sol += getCell(row + dr[i], col + dc[i]) == cell_type ? 1 : 0;
		}
		return sol;
	}

	@Override
	public State nextState(List<Action> actionList) {

		Cells p1 = (Cells) actionList.get(0);
		Cells p2 = (Cells) actionList.get(1);

		/**
		 * TODO: verify all values are non-null
		 */

		/**
		 * Bucketization handler
		 */
		int p1_cells_new = p1_cells - p1.size();
		int p2_cells_new = p2_cells - p2.size();
		if (p1_cells_new < 0 || p2_cells_new < 0) {
			throw new IllegalArgumentException("too much cells" + p1_cells
					+ " " + p2_cells);
		}
		p1_cells_new = Math
				.min(maxCellCapacity, p1_cells_new + cellGainPerTurn);
		p2_cells_new = Math
				.min(maxCellCapacity, p2_cells_new + cellGainPerTurn);

		// calculate score

		int p1score_new = p1score + p1count;
		int p2score_new = p2score + p2count;

		
		//check if activating live cells
		for (Cell c : p1)
			if (getCell(c.getRow(),c.getCol()) == ConwayGameStateConstants.PLAYER2_CELL) 
				throw new IllegalArgumentException("P1 tried to activate a cell on top of other player's live cell");
		
	
		for (Cell c : p2)
			if (getCell(c.getRow(),c.getCol()) == ConwayGameStateConstants.PLAYER2_CELL) 
				throw new IllegalArgumentException("P2 tried to activate a cell on top of other player's live cell");
		

				/**
		 * Distance checks
		 */
		for (Cell c : p1) {
			if (distanceToFriendlyCell(c.getRow(), c.getCol(),
					ConwayGameStateConstants.PLAYER1_CELL,
					this.maxColonisationDistance) > maxColonisationDistance)
				throw new IllegalArgumentException("P2 over the distance");
		}
		for (Cell c : p2) {
			if (distanceToFriendlyCell(c.getRow(), c.getCol(),
					ConwayGameStateConstants.PLAYER2_CELL,
					this.maxColonisationDistance) > maxColonisationDistance)
				throw new IllegalArgumentException("P2 over the distance");
		}

		/**
		 * removing duplicate cells
		 * */
		p1.removeAll(p2);
		p2.removeAll(p1);

		/**
		 * Appending to field
		 */
		for (Cell a : p1) {
			// TODO handle invalid cells
			field[a.getRow()][a.getCol()] = ConwayGameStateConstants.PLAYER1_CELL;
		}

		for (Cell b : p2) {
			// TODO handle invalid cells
			field[b.getRow()][b.getCol()] = ConwayGameStateConstants.PLAYER2_CELL;
		}

		/**
		 * generate new state matrix
		 * */
		int[][] sol = new int[getRows()][getCols()];
		for (int i = 0; i < getRows(); ++i)
			for (int j = 0; j < getCols(); ++j) {
				if (ConwayGameStateConstants.isPlayer(field[i][j])) {
					sol[i][j] = fromOccupied.apply(Triple.of(
							getSurroundingCellCount(i, j, field[i][j]),
							getSurroundingCellCount(i, j,
									ConwayGameStateConstants
											.inversePlayer(field[i][j])),
							field[i][j]));
				} else {
					sol[i][j] = fromEmpty.apply(Pair.of(
							getSurroundingCellCount(i, j,
									ConwayGameStateConstants.PLAYER1_CELL),
							getSurroundingCellCount(i, j,
									ConwayGameStateConstants.PLAYER2_CELL)));
				}
			}
		return new ConwayGameState(cellGainPerTurn, maxCellCapacity,
				maxColonisationDistance, currIteration + 1, maxGameIterations,
				sol, p1_cells_new, p1, p2_cells_new, p2, fromEmpty,
				fromOccupied, p1score_new, p2score_new);
	}

    @Override
    public int getWinner() {
        if (!this.isFinal())
            throw new IllegalStateException("Cannot get winner while game is running.");
        if (p1score > p2score)
            return ConwayGameStateConstants.PLAYER1_CELL;
        if (p1score == p2score)
            return -1;
        return ConwayGameStateConstants.PLAYER2_CELL;
    }
}
