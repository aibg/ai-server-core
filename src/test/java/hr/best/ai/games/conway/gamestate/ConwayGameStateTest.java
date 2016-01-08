package hr.best.ai.games.conway.gamestate;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import hr.best.ai.gl.Action;

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
        state = ConwayGameState.newBuilder(10, 10).setRuleset("diff")
                .setCell(4, 4, CellType.P1)
                .setCell(4, 5, CellType.P1)
                .setCell(5, 4, CellType.P1)
                .setCell(5, 5, CellType.P1)
                .setCell(7, 6, CellType.P2)
                .setCell(7, 7, CellType.P2)
                .setCell(8, 6, CellType.P2)
                .setCell(9, 7, CellType.P2)
                .setCell(8, 8, CellType.P2)
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

        assertEquals(nState.getCell(3, 4), CellType.P1);
        assertEquals(nState.getCell(3,5), CellType.P1);

        assertEquals(nState.getCell(4,3), CellType.P1);
        assertEquals(nState.getCell(4,4), CellType.DEAD);
        assertEquals(nState.getCell(4,5), CellType.DEAD);
        assertEquals(nState.getCell(4,6), CellType.P1);

        assertEquals(nState.getCell(5,4), CellType.P1);
        assertEquals(nState.getCell(5,5), CellType.P1);
    }
    
    @Test
    public void toJsonObjectAsPlayerTest() {
    	String[] gameField=new String[10];
    	gameField[0]="..........";
    	gameField[1]="..........";
    	gameField[2]="..........";
    	gameField[3]="..........";
    	gameField[4]="....##....";
    	gameField[5]="....##....";
    	gameField[6]="..........";
    	gameField[7]="......OO..";
    	gameField[8]="......O.O.";
    	gameField[9]=".......O..";
    	
    	
    	JsonObject p1=state.toJSONObjectAsPlayer(CellType.P1.getID());
    	JsonArray fieldArray1=p1.get("field").getAsJsonArray();
    	for(int i=0;i<fieldArray1.size();i++){
    		assertEquals(fieldArray1.get(i).getAsString(), gameField[i]);
    	}
    	
    	
    	gameField[0]="..........";
    	gameField[1]="..........";
    	gameField[2]="..........";
    	gameField[3]="..........";
    	gameField[4]="....OO....";
    	gameField[5]="....OO....";
    	gameField[6]="..........";
    	gameField[7]="......##..";
    	gameField[8]="......#.#.";
    	gameField[9]=".......#..";
    	
    	JsonObject p2=state.toJSONObjectAsPlayer(CellType.P2.getID());
    	JsonArray fieldArray2=p2.get("field").getAsJsonArray();
        for(int i=0;i<fieldArray2.size();i++){
        	assertEquals(fieldArray2.get(i).getAsString(), gameField[i]);
        }
    	
    }
    
    @Test
    public void toJsonObjectTest() {
    	String[] gameField=new String[10];
    	gameField[0]="..........";
    	gameField[1]="..........";
    	gameField[2]="..........";
    	gameField[3]="..........";
    	gameField[4]="....##....";
    	gameField[5]="....##....";
    	gameField[6]="..........";
    	gameField[7]="......OO..";
    	gameField[8]="......O.O.";
    	gameField[9]=".......O..";
    	
    	
    	JsonObject p1=state.toJSONObject();
    	JsonArray fieldArray1=p1.get("field").getAsJsonArray();
    	for(int i=0;i<fieldArray1.size();i++){
    		assertEquals(fieldArray1.get(i).getAsString(), gameField[i]);
    	}
    	  	
    }
}
