package hr.best.ai.games.conway.gamestate;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import hr.best.ai.gl.Action;
import hr.best.ai.gl.State;

public class ConwayGameStateTest {

    private ConwayGameState state;

    @Before
    public void setUp() throws Exception {
        /**
         * ..........
         * ..........
         * ..........
         * ..........
         * ....##....
         * ....##....
         * ..........
         * ......OO..
         * ......O.O.
         * .......O..
         */
        state = ConwayGameStateBuilder.newConwayGameStateBuilder(10, 10).setRuleset("diff")
                .setCell(4, 4, ConwayGameStateConstants.PLAYER1_CELL)
                .setCell(4, 5, ConwayGameStateConstants.PLAYER1_CELL)
                .setCell(5, 4, ConwayGameStateConstants.PLAYER1_CELL)
                .setCell(5, 5, ConwayGameStateConstants.PLAYER1_CELL)
                .setCell(7, 6, ConwayGameStateConstants.PLAYER2_CELL)
                .setCell(7, 7, ConwayGameStateConstants.PLAYER2_CELL)
                .setCell(8, 6, ConwayGameStateConstants.PLAYER2_CELL)
                .setCell(9, 7, ConwayGameStateConstants.PLAYER2_CELL)
                .setCell(8, 8, ConwayGameStateConstants.PLAYER2_CELL)
                .getState();
    }

	private List<Action> defaultTestActionsSetup() {
		List<Action> actions = new ArrayList<>();
		Cells cells = new Cells();
		cells.add(new Cell(3, 4));
		cells.add(new Cell(3, 5));
		actions.add(cells);
		actions.add(new Cells());
		return actions;
	}

	@Test
	public void immutableNextStateTest() {
		List<Action> actions = defaultTestActionsSetup();
		state.nextState(actions);
		state.nextState(actions);
	}

	@Test(expected=IllegalArgumentException.class)
	public void nextStateExceptionTest() {
		List<Action> actions = defaultTestActionsSetup();

		state.nextState(actions).nextState(actions);

	}

    @Test
    public void properSurroundingCellTest() {

        /**
         * x are action fields
         *
         * ..........
         * ..........
         * ..........
         * ....xx....
         * ....##....
         * ....##....
         * ..........
         * ......OO..
         * ......O.O.
         * .......O..
         */

        ConwayGameState nState = state.nextState(defaultTestActionsSetup());

        /**
         * expected to get
         *
         * ..........
         * ..........
         * ..........
         * ....##....
         * ...#..#...
         * ....##....
         * ..........
         * ......OO..
         * ......O.O.
         * .......O..
         */

        assertEquals(nState.getCell(3, 4), ConwayGameStateConstants.PLAYER1_CELL);
        assertEquals(nState.getCell(3,5), ConwayGameStateConstants.PLAYER1_CELL);

        assertEquals(nState.getCell(4,3), ConwayGameStateConstants.PLAYER1_CELL);
        assertEquals(nState.getCell(4,4), ConwayGameStateConstants.DEAD_CELL);
        assertEquals(nState.getCell(4,5), ConwayGameStateConstants.DEAD_CELL);
        assertEquals(nState.getCell(4,6), ConwayGameStateConstants.PLAYER1_CELL);

        assertEquals(nState.getCell(5,4), ConwayGameStateConstants.PLAYER1_CELL);
        assertEquals(nState.getCell(5,5), ConwayGameStateConstants.PLAYER1_CELL);
    }
    
    @Test
    public void toJsonObjectAsPlayerTest() {
    	
    	
    	
    	
    }
}
