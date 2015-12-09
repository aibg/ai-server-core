package hr.best.ai.games.conway.gamestate;

import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

/**
 * Created by lpp on 10/27/15.
 */
public class ConwayGameStateBuilder {

    private ConwayGameStateBuilder(int rows, int cols){
        field = new int[rows][cols];
        for (int i = 0; i < rows; ++i)
            for (int j = 0; j < cols; ++j)
                field[i][j] = ConwayGameStateConstants.DEAD_CELL;
    }
    public static ConwayGameStateBuilder newConwayGameStateBuilder(int rows, int cols) {
        return new ConwayGameStateBuilder(rows, cols);
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

    public ConwayGameStateBuilder setStartingCells(int startingCells) {
        this.startingCells = startingCells;
        return this;
    }

    public ConwayGameStateBuilder setCellGainPerTurn(int cellGainPerTurn) {
        this.cellGainPerTurn = cellGainPerTurn;
        return this;
    }

    public ConwayGameStateBuilder setMaxCellCapacity(int maxCellCapacity) {
        this.maxCellCapacity = maxCellCapacity;
        return this;
    }

    public ConwayGameStateBuilder setMaxColonisationDistance(int maxColonisationDistance) {
        this.maxColonisationDistance = maxColonisationDistance;
        return this;
    }

    public ConwayGameStateBuilder setMaxGameIterations(int maxGameIterations) {
        this.maxGameIterations = maxGameIterations;
        return this;
    }

    public ConwayGameStateBuilder setCell(int row, int col, int cellType) {
        this.field[row][col] = cellType;
        return this;
    }

    /**
     *
     * @param fromEmpty function which takes #neighbouring P1 cells, #neigbouring P2 cells and returns resulting cell
     */
    public ConwayGameStateBuilder setFromEmpty(Function<Pair<Integer, Integer>, Integer> fromEmpty) {
        this.fromEmpty = fromEmpty;
        return this;
    }

    /**
     *
     * @param fromOccupied function which takes #neighbouring a cells, #neigbouring b cells and returns resulting cell.
     * @return
     */
    public ConwayGameStateBuilder setFromOccupied(Function<Triple<Integer, Integer, Integer>, Integer> fromOccupied) {
        this.fromOccupied = fromOccupied;
        return this;
    }

    public ConwayGameStateBuilder setRuleset(String name) {
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
