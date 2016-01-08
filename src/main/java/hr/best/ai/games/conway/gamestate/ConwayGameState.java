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
 * Game state of Game of Life(GoL). Contains largest part of GoL game logic. It
 * is result of nextState(actions) method from previous ConwayGameState (initial
 * state is the exception obviously).
 */
public class ConwayGameState implements State {

	/**
	 * Number of cells each player gets to activate per turn in an action (this
	 * can be stored and transfered to next turn but only up to maxCellCapacity)
	 */
	private final int cellGainPerTurn;
	
	/**
	 * Maximum number of cells player can store to use in an action
	 */
	private final int maxCellCapacity;
	
	/**
	 * Maximum distance from a friendly alive cell form which can a dead cell be
	 * activated in player action (manhattan or 1-norm, check out
	 * distanceFromFriendlyCell method for details)
	 */
	private final int maxColonisationDistance;
	
	/**
	 * Current iteration or current state (initial state is state 0)
	 */
	private final int currIteration;
	
	/**
	 * Maximum number of times nextState(actions) can be called before method
	 * isFinal() returns true
	 */
	private final int maxGameIterations;
		
	/**
	 * Represents the game field for this state where each element is one of the
	 * ConwayGameStateConstants: DEAD_CELL, PLAYER1_CELL or PLAYER2_CELL.
	 */
	private final CellType[][] field;
	
	/**
	 * The amount of cells player 1 can activate in an action this turn. Always
	 * less or equal to maxCellCapacity and larger or equal to cellGainPerTurn
	 */
	private final int p1_cells;
	
	/**
	 * Cells player 1 activated last turn as a result of his action
	 */
	private final Cells lastTurnP1;
	
	/**
	 * The amount of cells player 2 can activate in an action this turn
	 */
	private final int p2_cells;
	
	/**
	 * Cells player 2 activated last turn as a result of his action
	 */
	private final Cells lastTurnP2;
	
	/**
	 * Functions used to calculate which cell gets activated or died depending
	 * on the game rules. fromEmpty determines whether an inactive cell should
	 * be activated.
	 * See {@link Rulesets} for more info
	 */
	private final Function<Pair<Integer, Integer>, CellType> fromEmpty;
	
	/**
	 * Functions used to calculate which cell gets activated or died depending
	 * on the game rules. fromOccupied determines whether an active cell should
	 * stay active.
	 * See {@link Rulesets} for more info
	 */
	private final Function<Triple<Integer, Integer, CellType>, CellType> fromOccupied;

	/**
	 * Current amount of player 1 living cells
	 */
	private final int p1count;
	
	/**
	 * Current amount of player 2 living cells
	 */
	private final int p2count;

	/**
	 * Only existing constructor. Completely determines the state. Most of the parameters 
	 * are self-explanatory, check the class implementation for explanations about the rest
	 */
	private ConwayGameState(
			int cellGainPerTurn,
			int maxCellCapacity,
			int maxColonisationDistance,
			int currIteration,
			int maxGameIterations,
			CellType[][] field,
			int p1_cells,
			Cells lastTurnP1,
			int p2_cells,
			Cells lastTurnP2,
			Function<Pair<Integer, Integer>, CellType> fromEmpty,
			Function<Triple<Integer, Integer, CellType>, CellType> fromOccupied
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
		this.p1count=countCells(CellType.P1);
		this.p2count=countCells(CellType.P2);

	}
	
	/**
	 * @return current iteration
	 */
	public int getIteration() {
		return currIteration;
	}

    public int getP1Remainingcells() {
        return p1_cells;
    }

    public int getP2Remainingcells() {
        return p2_cells;
    }
    /**
     * @return cells player 1 activated with his last action
     */
	public Cells getPlayer1Actions() {
		return lastTurnP1;
	}

	/**
	 * @return cells player 2 activated with his last action
	 */
	public Cells getPlayer2Actions() {
		return lastTurnP2;
	}

	/**
	 * Counts the number of cells in this state
	 * 
	 * @param player
	 *            only sensible options are (from ConwayGameStateConstants)
	 *            DEAD_CELL, PLAYER1_CELL and PLAYER2_CELL
	 * @return number of that kind of cells in the playing field
	 */
	private int countCells(CellType player) {
		int count = 0;

		for (int i = 0; i < getRows(); i++) {
			for (int j = 0; j < getCols(); j++) {
				if (getCell(i, j) == player)
					count++;
			}
		}
		return count;
	}
	
	/**
	 * @return number of player 1 living cells
	 */
	public int getP1LiveCellcount() {
		return p1count;
	}
	/**
	 * @return number of player 1 living cells
	 */
	public int getP2LiveCellcount() {
		return p2count;
	}
	/**
	 * @return number of game field rows
	 */
	public int getRows() {
		return field.length;
	}

	/**
	 * @return number of game field columns
	 */
	public int getCols() {
		return field[0].length;
	}

	/**
	 * This implements the game field as a torus.<br>
	 * 
	 * For example:<br>
	 * 
	 * if the field has dimensions 5x6, getCell(-3,2), getCell(2,2), getCell(7,8)
	 * all return the same value<br>
	 * 
	 * or:<br>
	 * 
	 * getCell(x+k1*getRows(),y+k2*getCols()) returns the same value for any
	 * sensible choice of integers k1 and k2
	 * 
	 * @param row
	 * @param col
	 * @return cell value, one of the ConwayGameStateConstants DEAD_CELL,
	 *         PLAYER1_CELL, or PLAYER2_CELL
	 */
	public CellType getCell(int row, int col) {
		return getCellOnTorus(row, col, field);
	}
	
	/**
	 * This method effectively 'transforms' two-dimensional integer array into torus 
	 * (or a donut with hole through the middle) 
	 * @param row cell row(can be any integer)
	 * @param col cell column(can be any integer)
	 * @param gameField the two-dim integer array
	 * @return cell value on that position(row,col) on a torus
	 */
	private static CellType getCellOnTorus(int row, int col, CellType[][] gameField) {
		return gameField[Math.floorMod(row, gameField.length)][Math.floorMod(col, gameField[0].length)];
	}

	/**
	 * Json object containing JsonArray representing game field and two
	 * JsonPrimitives with current bucket values.
	 */
	@Override
	public JsonObject toJSONObject() {
		JsonObject json = toJSONObjectAsPlayer(CellType.P1.getID());
		json.remove("cellsRemaining");
		json.addProperty("cellsRemainingP1", p1_cells);
		json.addProperty("cellsRemainingP2", p2_cells);
		return json;
	}

	/**
	 * Player 1 has ID 0, player 2 has ID 1
	 * 
	 * Creates personalized json object from this state ready to be sent to
	 * players. Object contains following json elements:<br>
	 * "field"(json array),<br>
	 * "cellsRemaining",<br>
	 * "cellGainPerTurn",<br>
	 * "maxCellCapacity",<br>
	 * "maxColonisationDistance",<br>
	 * "currIteration",<br>
	 * "maxGameIterations"
	 * 
	 * field array is array of length getRows() and each array element is a
	 * string representing one field row. In each row, '.' represents a dead
	 * cell, '#' represents friendly cell, and 'O' enemy cell<br>
	 * 
	 * Example:<br><code>
	 * "field":<br>
	 * ["........##......"<br>
	 * ,".........#.#...."<br>
	 * ,"........#.....#."<br>
	 * ,"....O....##....."<br>
	 * ,"..O...O..#####.."<br>
	 * ,"..O...O........."]</code>
	 * 
	 */
	@Override
	public JsonObject toJSONObjectAsPlayer(int playerId) {
		JsonObject json = new JsonObject();

		JsonArray array = new JsonArray();
		json.add("field", array);

		for (int i = 0; i < getRows(); i++) {
			StringBuilder sb = new StringBuilder();
			for (int j = 0; j < getCols(); j++) {
				switch (field[i][j]) {
				case P1:
					sb.append(playerId == CellType.P1.getID() ? "#" : "O");
					break;
				case P2:
					sb.append(playerId == CellType.P2.getID() ? "#"	: "O");
					break;
				case DEAD:
					sb.append(".");
					break;
				}
			}
			array.add(new JsonPrimitive(sb.toString()));
		}

		json.addProperty("cellsRemaining",
				playerId == CellType.P1.getID() ? p1_cells : p2_cells);
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
	/**
	 * Checks if this state is final state (max iterations reached)
	 */
	@Override
	public boolean isFinal() {
		return currIteration >= maxGameIterations;
	}
	/**
	 * Returns the distance between the queried location and nearest friendly
	 * cell (only checks a square around the location with sides of length
	 * 2*maxSearchDistance + 1)
	 * 
	 * @param row
	 * @param col
	 * @param cell_type
	 *            one of the ConwayGameStateConstants: PLAYER1_CELL or
	 *            PLAYER2_CELL are sensible
	 * @param maxSearchDistance
	 *            how far around the location will this method search, if no
	 *            friendly cells are inside that square, it returns maximum
	 *            integer value
	 * @return manhattan or 1-distance to nearest friendly cell
	 */
	private int distanceToFriendlyCell(int row, int col, CellType type,
			int maxSearchDistance) {
		int distance = Integer.MAX_VALUE;

		for (int r = row - maxSearchDistance; r <= row + maxSearchDistance; ++r) {
			for (int c = col - maxSearchDistance; c <= col + maxSearchDistance; ++c)
				if (getCell(r, c) == type)
					distance = Math.min(distance,Math.max(Math.abs(r - row),Math.abs(c - col)));
		}
		return distance;
	}
	/**
	 * Counts the number of friendly cell neighbors.
	 * 
	 * @param row
	 *            row of a cell in question
	 * @param col
	 *            row of a cell in question
	 * @param cell_type
	 *            which type of cell does it consider for counting (PLAYER1_CELL
	 *            or PLAYER2_CELL)
	 * @param gameField
	 * 			  two-dimensional array on which the neighbors are calculated           
	 * @return number of friendly neighbors
	 */
	private static int getSurroundingCellCount(int row, int col,CellType type, CellType[][] gameField) {

		int dr[] = { -1, -1, -1, 0, 0, 1, 1, 1 };
		int dc[] = { -1, 0, 1, -1, 1, -1, 0, 1 };
		int sol = 0;
		for (int i = 0; i < dr.length; ++i) {
			sol += getCellOnTorus(row + dr[i], col + dc[i], gameField) == type ? 1 : 0;
		}
		return sol;
	}
	
	/**
	 * creates and returns next game state(new ConwayGameState object) based on players actions
	 */
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
			if (CellType.isPlayerCell(getCell(c.getRow(), c.getCol())))
				throw new IllegalArgumentException("P1 tried to activate a living cell");

		for (Cell c : p2)
            if (CellType.isPlayerCell(getCell(c.getRow(), c.getCol())))
                throw new IllegalArgumentException("P1 tried to activate a living cell");

        /**
		 * Distance checks
		 */
		for (Cell c : p1) {
			if (distanceToFriendlyCell(c.getRow(), c.getCol(), CellType.P1,
					this.maxColonisationDistance) > maxColonisationDistance)
				throw new IllegalArgumentException("P2 over the distance");
		}
		for (Cell c : p2) {
			if (distanceToFriendlyCell(c.getRow(), c.getCol(), CellType.P2,
					this.maxColonisationDistance) > maxColonisationDistance)
				throw new IllegalArgumentException("P2 over the distance");
		}

		/**
		 * removing duplicate cells
		 * */
		p1.removeAll(p2);
		p2.removeAll(p1);

		/**
		 * Copies the field and does further calculations with the copy
		 */
		CellType[][] fieldCopy=new CellType[getRows()][getCols()];
		for(int i=0;i<getRows();i++)
			for(int j=0;j<getCols();j++)
				fieldCopy[i][j]=field[i][j];

		/**
		 * Appending to field
		 */
		for (Cell a : p1) {
			// TODO handle invalid cells
			fieldCopy[a.getRow()][a.getCol()] = CellType.P1;
		}

		for (Cell b : p2) {
			// TODO handle invalid cells
			fieldCopy[b.getRow()][b.getCol()] = CellType.P2;
		}

		/**
		 * generate new state matrix
		 * */
		CellType[][] sol = new CellType[getRows()][getCols()];
		for (int i = 0; i < getRows(); ++i)
			for (int j = 0; j < getCols(); ++j) {
                final CellType currentCell = fieldCopy[i][j];
                if (CellType.isPlayerCell(currentCell)) {
                    final int friendlyCellCount = getSurroundingCellCount(i, j, currentCell, fieldCopy);
                    final int enemyCellCount = getSurroundingCellCount(i, j, CellType.inversePlayerCell(currentCell), fieldCopy);
                    sol[i][j] = fromOccupied.apply(Triple.of(
                            friendlyCellCount
                            , enemyCellCount
                            , currentCell)
                    );
				} else {
                    final int p1CellCount = getSurroundingCellCount(i, j,
                            CellType.P1, fieldCopy);
                    final int p2CellCount = getSurroundingCellCount(i, j,
                            CellType.P2, fieldCopy);
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
            return CellType.P1.getID();
        if (p1count == p2count)
            return -1;
        return CellType.P2.getID();
    }
    
    public static Builder newBuilder(int rows, int cols) {
        return new Builder(rows, cols);
    }
    
    public static class Builder {

        private Builder(int rows, int cols){
            field = new CellType[rows][cols];
            for (int i = 0; i < rows; ++i)
                for (int j = 0; j < cols; ++j)
                    field[i][j] = CellType.DEAD;
        }

        /**
         * Attributes
         */
        private int cellGainPerTurn = 1;
        private int maxCellCapacity = 5;
        private int maxColonisationDistance = 4;
        private int maxGameIterations = 10000;
        private final CellType[][] field;
        private int startingCells = 5;
        /**
         * (P1, P2) -> resulting cell
         */
        Function<Pair<Integer, Integer>, CellType> fromEmpty;

        /**
         * (Pa, Pb, player a)
         * for example lets have this situation (cell under consideration is in the middle):
         * 111
         * 112
         * 111
         *
         * we'd have (7, 1, P1)
         *
         * and in this case:
         * 111
         * 122
         * 111
         *
         * we'd have (1, 7, P2)
         */
        Function<Triple<Integer, Integer, CellType>, CellType> fromOccupied;

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

        public Builder setCell(int row, int col, CellType cellType) {
            this.field[row][col] = cellType;
            return this;
        }

        /**
         *
         * @param fromEmpty function which takes #neighbouring P1 cells, #neigbouring P2 cells and returns resulting cell
         */
        public Builder setFromEmpty(Function<Pair<Integer, Integer>, CellType> fromEmpty) {
            this.fromEmpty = fromEmpty;
            return this;
        }

        /**
         *
         * @param fromOccupied function which takes #neighbouring a cells, #neigbouring b cells and returns resulting cell.
         * @return
         */
        public Builder setFromOccupied(Function<Triple<Integer, Integer, CellType>, CellType> fromOccupied) {
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
