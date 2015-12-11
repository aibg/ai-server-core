package hr.best.ai.games.conway.gamestate;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

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
	private final int p1count;
	private final int p2count;

	private ConwayGameState(
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
			Function<Pair<Integer, Integer>, Integer> fromEmpty,
			Function<Triple<Integer, Integer, Integer>, Integer> fromOccupied
			) {
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
		this.p1count=countCells(ConwayGameStateConstants.PLAYER1_CELL);
		this.p2count=countCells(ConwayGameStateConstants.PLAYER2_CELL);

	}

	public int getIteration() {
		return currIteration;
	}

    public int getP1Remainingcells() {
        return p1_cells;
    }

    public int getP2Remainingcells() {
        return p2_cells;
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

	public int getRows() {
		return field.length;
	}

	public int getCols() {
		return field[0].length;
	}

	public int getCell(int row, int col) {
		return torus(row, col, field);
	}

	private static int torus(int row, int col, int[][] gameField) {
		return gameField[Math.floorMod(row, gameField.length)][Math.floorMod(col, gameField[0].length)];
	}

	/**
	 * Json object containing JsonArray representing game field and two
	 * JsonPrimitives with current bucket values.
	 */
	@Override
	public JsonObject toJSONObject() {
		JsonObject json = toJSONObjectAsPlayer(ConwayGameStateConstants.PLAYER1_ID);
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
					sb.append(playerId == ConwayGameStateConstants.PLAYER1_ID ? "#"
							: "O");
					break;
				case ConwayGameStateConstants.PLAYER2_CELL:
					sb.append(playerId == ConwayGameStateConstants.PLAYER2_ID ? "#"
							: "O");
					break;
				case ConwayGameStateConstants.DEAD_CELL:
					sb.append(".");
					break;
				}
			}
			array.add(new JsonPrimitive(sb.toString()));
		}

		json.addProperty("cellsRemaining",
				playerId == ConwayGameStateConstants.PLAYER1_ID ? p1_cells
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

	private static int getSurroundingCellCount(int row, int col,int cell_type, int[][] gameField) {

		int dr[] = { -1, -1, -1, 0, 0, 1, 1, 1 };
		int dc[] = { -1, 0, 1, -1, 1, -1, 0, 1 };
		int sol = 0;
		for (int i = 0; i < dr.length; ++i) {
			sol += torus(row + dr[i], col + dc[i], gameField) == cell_type ? 1 : 0;
		}
		return sol;
	}

	@Override
	public ConwayGameState nextState(List<Action> actionList) {

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

		//check if activating living cells
		for (Cell c : p1)
			if (ConwayGameStateConstants.isPlayer(getCell(c.getRow(), c.getCol())))
				throw new IllegalArgumentException("P1 tried to activate a living cell");

		for (Cell c : p2)
            if (ConwayGameStateConstants.isPlayer(getCell(c.getRow(), c.getCol())))
                throw new IllegalArgumentException("P1 tried to activate a living cell");

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

		int[][] fieldCopy=new int[getRows()][getCols()];
		for(int i=0;i<getRows();i++)
			for(int j=0;j<getCols();j++)
				fieldCopy[i][j]=field[i][j];

		/**
		 * Appending to field
		 */
		for (Cell a : p1) {
			// TODO handle invalid cells
			fieldCopy[a.getRow()][a.getCol()] = ConwayGameStateConstants.PLAYER1_CELL;
		}

		for (Cell b : p2) {
			// TODO handle invalid cells
			fieldCopy[b.getRow()][b.getCol()] = ConwayGameStateConstants.PLAYER2_CELL;
		}

		/**
		 * generate new state matrix
		 * */
		int[][] sol = new int[getRows()][getCols()];
		for (int i = 0; i < getRows(); ++i)
			for (int j = 0; j < getCols(); ++j) {
                final int currentCell = fieldCopy[i][j];
                if (ConwayGameStateConstants.isPlayer(currentCell)) {
                    final int friendlyCellCount = getSurroundingCellCount(i, j, currentCell, fieldCopy);
                    final int enemyCellCount = getSurroundingCellCount(i, j, ConwayGameStateConstants
                                    .inversePlayer(currentCell), fieldCopy);
                    sol[i][j] = fromOccupied.apply(Triple.of(
                            friendlyCellCount
                            , enemyCellCount
                            , currentCell)
                    );
				} else {
                    final int p1CellCount = getSurroundingCellCount(i, j,
                            ConwayGameStateConstants.PLAYER1_CELL, fieldCopy);
                    final int p2CellCount = getSurroundingCellCount(i, j,
                            ConwayGameStateConstants.PLAYER2_CELL, fieldCopy);
                    sol[i][j] = fromEmpty.apply(Pair.of(
                            p1CellCount
                            , p2CellCount
                    ));
				}
			}
		return new ConwayGameState(cellGainPerTurn, maxCellCapacity,
				maxColonisationDistance, currIteration + 1, maxGameIterations,
				sol, p1_cells_new, p1, p2_cells_new, p2, fromEmpty,
				fromOccupied);
	}

    @Override
    public int getWinner() {
        if (!this.isFinal())
            throw new IllegalStateException("Cannot get winner while game is running.");
        if (p1count > p2count)
            return ConwayGameStateConstants.PLAYER1_CELL;
        if (p1count == p2count)
            return -1;
        return ConwayGameStateConstants.PLAYER2_CELL;
    }
    
    public static class Builder {

        private Builder(int rows, int cols){
            field = new int[rows][cols];
            for (int i = 0; i < rows; ++i)
                for (int j = 0; j < cols; ++j)
                    field[i][j] = ConwayGameStateConstants.DEAD_CELL;
        }
        public static Builder newBuilder(int rows, int cols) {
            return new Builder(rows, cols);
        }

        /**
         * Attributes
         */
        private int cellGainPerTurn = 1;
        private int maxCellCapacity = 5;
        private int maxColonisationDistance = 4;
        private int maxGameIterations = 10000;
        private final int[][] field;
        private int startingCells = 5;
        /**
         * (P1, P2) -> resulting cell
         */
        Function<Pair<Integer, Integer>, Integer> fromEmpty;

        /**
         * (Pa, Pb, player a)
         * for example lets have this situation (cell under consideration is in the middle):
         * 111
         * 112
         * 111
         *
         * we'd have (7, 1, 1)
         *
         * and in this case:
         * 111
         * 122
         * 111
         *
         * we'd have (1, 7, 2)
         */
        Function<Triple<Integer, Integer, Integer>, Integer> fromOccupied;

        public Builder setStartingCells(int startingCells) {
            this.startingCells = startingCells;
            return this;
        }

        public Builder setCellGainPerTurn(int cellGainPerTurn) {
            this.cellGainPerTurn = cellGainPerTurn;
            return this;
        }

        public Builder setMaxCellCapacity(int maxCellCapacity) {
            this.maxCellCapacity = maxCellCapacity;
            return this;
        }

        public Builder setMaxColonisationDistance(int maxColonisationDistance) {
            this.maxColonisationDistance = maxColonisationDistance;
            return this;
        }

        public Builder setMaxGameIterations(int maxGameIterations) {
            this.maxGameIterations = maxGameIterations;
            return this;
        }

        public Builder setCell(int row, int col, int cellType) {
            this.field[row][col] = cellType;
            return this;
        }

        /**
         *
         * @param fromEmpty function which takes #neighbouring P1 cells, #neigbouring P2 cells and returns resulting cell
         */
        public Builder setFromEmpty(Function<Pair<Integer, Integer>, Integer> fromEmpty) {
            this.fromEmpty = fromEmpty;
            return this;
        }

        /**
         *
         * @param fromOccupied function which takes #neighbouring a cells, #neigbouring b cells and returns resulting cell.
         * @return
         */
        public Builder setFromOccupied(Function<Triple<Integer, Integer, Integer>, Integer> fromOccupied) {
            this.fromOccupied = fromOccupied;
            return this;
        }

        public Builder setRuleset(String name) {
            if (!Rulesets.fromEmpty.containsKey(name)) {
                throw new IllegalArgumentException(name + " is not recognized game ruleset. Supported ones are: " +
                        Rulesets.fromEmpty.keySet().toString());
            }
            setFromEmpty(Rulesets.fromEmpty.get(name));
            setFromOccupied(Rulesets.fromOccupied.get(name));
            return this;
        }

        int getRows() {
            return field.length;
        }

        int getCols() {
            return field[0].length;
        }


        public ConwayGameState getState() {
            return new ConwayGameState
                    ( cellGainPerTurn
                            , maxCellCapacity
                            , maxColonisationDistance
                            , 0
                            , maxGameIterations
                            , field
                            , startingCells
                            , new Cells()
                            , startingCells
                            , new Cells()
                            , fromEmpty
                            , fromOccupied);
        }
    }

}
