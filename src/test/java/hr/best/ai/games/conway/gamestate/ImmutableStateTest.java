package hr.best.ai.games.conway.gamestate;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import hr.best.ai.gl.Action;
import hr.best.ai.gl.State;

public class ImmutableStateTest {

	private State defaultTestStateSetup() {
		return ConwayGameStateBuilder.newConwayGameStateBuilder(10, 10).setRuleset("diff")
				.setCell(4, 4, ConwayGameStateConstants.PLAYER1_CELL)
				.setCell(4, 5, ConwayGameStateConstants.PLAYER1_CELL)
				.setCell(5, 4, ConwayGameStateConstants.PLAYER1_CELL)
				.setCell(5, 5, ConwayGameStateConstants.PLAYER1_CELL).getState();

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

		State state = defaultTestStateSetup();
		List<Action> actions = defaultTestActionsSetup();

		state.nextState(actions);
		state.nextState(actions);

	}

	@Test(expected=IllegalArgumentException.class)
	public void nextStateExceptionTest() {

		State state = defaultTestStateSetup();
		List<Action> actions = defaultTestActionsSetup();

		state.nextState(actions).nextState(actions);

	}

}
