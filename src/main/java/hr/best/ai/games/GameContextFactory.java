package hr.best.ai.games;

import hr.best.ai.games.conway.gamestate.ConwayGameState;
import hr.best.ai.games.conway.gamestate.ConwayGameStateConstants;
import hr.best.ai.games.conway.gamestate.Rulesets.*;
import hr.best.ai.games.sum.SumState;
import hr.best.ai.gl.GameContext;
import hr.best.ai.gl.State;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

/**
 * Utility class. Creates game context with default parameters.
 */
public class GameContextFactory {
	
    private GameContextFactory() {
    }

    public static GameContext getSumGameInstance() {
        return new GameContext(new SumState(0), 2);
    }
    public static ConwayGameState.Builder getBasicGrid() {
        return ConwayGameState.Builder.newBuilder(10,15)
                .setCell(4,5, ConwayGameStateConstants.PLAYER1_CELL)
                .setCell(5,5, ConwayGameStateConstants.PLAYER1_CELL)
                .setCell(5,4, ConwayGameStateConstants.PLAYER1_CELL)
                .setCell(7,9, ConwayGameStateConstants.PLAYER2_CELL)
                .setCell(8,9, ConwayGameStateConstants.PLAYER2_CELL)
                .setCell(8,8, ConwayGameStateConstants.PLAYER2_CELL);
    }

    public static State demoState() {
        return ConwayGameState.Builder.newBuilder(12, 12)
                .setRuleset("classic")
                        // P1 Oscilator
                .setCell(2, 1, ConwayGameStateConstants.PLAYER1_CELL)
                .setCell(3, 1, ConwayGameStateConstants.PLAYER1_CELL)
                .setCell(4, 1, ConwayGameStateConstants.PLAYER1_CELL)
                .setCell(1, 2, ConwayGameStateConstants.PLAYER1_CELL)
                .setCell(2, 2, ConwayGameStateConstants.PLAYER1_CELL)
                .setCell(3, 2, ConwayGameStateConstants.PLAYER1_CELL)
                        // P2 Oscilator
                .setCell(7 ,  7, ConwayGameStateConstants.PLAYER2_CELL)
                .setCell(7 ,  8, ConwayGameStateConstants.PLAYER2_CELL)
                .setCell(8 ,  7, ConwayGameStateConstants.PLAYER2_CELL)
                .setCell(8 ,  8, ConwayGameStateConstants.PLAYER2_CELL)
                .setCell(9 ,  9, ConwayGameStateConstants.PLAYER2_CELL)
                .setCell(9 , 10, ConwayGameStateConstants.PLAYER2_CELL)
                .setCell(10,  9, ConwayGameStateConstants.PLAYER2_CELL)
                .setCell(10, 10, ConwayGameStateConstants.PLAYER2_CELL)
                .getState();
    }

    public static State bigDemoState() {
        return ConwayGameState.Builder.newBuilder(100, 100)
                .setRuleset("classic")
                        // P1 Oscilator
                .setCell(2, 1, ConwayGameStateConstants.PLAYER1_CELL)
                .setCell(3, 1, ConwayGameStateConstants.PLAYER1_CELL)
                .setCell(4, 1, ConwayGameStateConstants.PLAYER1_CELL)
                .setCell(1, 2, ConwayGameStateConstants.PLAYER1_CELL)
                .setCell(2, 2, ConwayGameStateConstants.PLAYER1_CELL)
                .setCell(3, 2, ConwayGameStateConstants.PLAYER1_CELL)
                        // P2 Oscilator
                .setCell(7 ,  7, ConwayGameStateConstants.PLAYER2_CELL)
                .setCell(7 ,  8, ConwayGameStateConstants.PLAYER2_CELL)
                .setCell(8 ,  7, ConwayGameStateConstants.PLAYER2_CELL)
                .setCell(8 ,  8, ConwayGameStateConstants.PLAYER2_CELL)
                .setCell(9 ,  9, ConwayGameStateConstants.PLAYER2_CELL)
                .setCell(9 , 10, ConwayGameStateConstants.PLAYER2_CELL)
                .setCell(10,  9, ConwayGameStateConstants.PLAYER2_CELL)
                .setCell(10, 10, ConwayGameStateConstants.PLAYER2_CELL)
                .getState();
    }



    public static GameContext getConwayGameInstance() {
    	
    	//TODO this is just for now
        State state = getBasicGrid()
                .setFromEmpty(Ruleset1::fromEmpty)
                .setFromOccupied(Ruleset1::fromOccupied)
                .getState();
        
        return new GameContext(state, 2);
    }

    public static GameContext getConwayGameInstanceR2() {
        State state = getBasicGrid()
                .setFromEmpty((Pair<Integer, Integer> a) -> {
                    if (a.getLeft() == 3 && a.getRight() == 0)
                        return ConwayGameStateConstants.PLAYER1_CELL;
                    if (a.getLeft() == 0 && a.getRight() == 3)
                        return ConwayGameStateConstants.PLAYER2_CELL;
                    return ConwayGameStateConstants.DEAD_CELL;
                })
                .setFromOccupied((Triple<Integer, Integer, Integer> a) -> {
                            int diff = a.getLeft() - a.getRight();
                            return diff == 2 || diff == 3 ? a.getRight() : ConwayGameStateConstants
                                    .DEAD_CELL;
                        }
                )
                .getState();

        return new GameContext(state, 2);
    }
}
