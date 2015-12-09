package hr.best.ai.games.conway.gamestate;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import hr.best.ai.gl.Action;

public class ImmutableStateTest {

	@Test
	public void test() {
		ConwayGameState state = ConwayGameStateBuilder.newConwayGameStateBuilder(10, 10)
				.setRuleset("diff")
				.setCell(4, 4, ConwayGameStateConstants.PLAYER1_CELL)
				.setCell(4, 5, ConwayGameStateConstants.PLAYER1_CELL)
				.setCell(5, 4, ConwayGameStateConstants.PLAYER1_CELL)
				.setCell(5, 5, ConwayGameStateConstants.PLAYER1_CELL)
				.getState();
		ArrayList<Action> actions=new ArrayList<>();
		Cells cells=new Cells();
		cells.add(new Cell(3, 4));
		cells.add(new Cell(3, 5));
		actions.add(cells);
		actions.add(new Cells());
		state.nextState(actions);
		try{
			state.nextState(actions);
			fail("should have thrown illegal argument exception");
		}
		catch(IllegalArgumentException e){
		}
		
		
	}

}
