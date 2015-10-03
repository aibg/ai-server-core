package hr.best.ai.games;

import hr.best.ai.games.conway.GameState;
import hr.best.ai.games.conway.rulesets.Ruleset1;
import hr.best.ai.games.sum.SumState;
import hr.best.ai.gl.GameContext;

/**
 * Utility class. Creates game context with default parameters.
 */
public class GameContextFactory {
	
    private GameContextFactory() {
    }

    public static GameContext getSumGameInstance() {
        return new GameContext(new SumState(0), 2);
    }
    public static GameContext getConwayGameInstance() {
    	
    	//TODO this is just for now
    	int [][] field=new int[10][15];
    	field[4][5]=GameState.PLAYER1_CELL;
    	field[5][5]=GameState.PLAYER1_CELL;
    	field[5][4]=GameState.PLAYER1_CELL;
    	
    	field[7][9]=GameState.PLAYER2_CELL;
    	field[8][9]=GameState.PLAYER2_CELL;
    	field[8][8]=GameState.PLAYER2_CELL;
    	
        return new GameContext(new Ruleset1(field), 2);
    }
}
