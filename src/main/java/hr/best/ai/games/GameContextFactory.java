package hr.best.ai.games;

import hr.best.ai.games.conway.gamestate.CellType;
import hr.best.ai.games.conway.gamestate.ConwayGameState;
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
        return ConwayGameState.newBuilder(10,15)
                .setCell(4,5, CellType.P1)
                .setCell(5,5, CellType.P1)
                .setCell(5,4, CellType.P1)
                .setCell(7,9, CellType.P2)
                .setCell(8,9, CellType.P2)
                .setCell(8,8, CellType.P2);
    }

    public static State demoState() {
        return ConwayGameState.newBuilder(12, 12)
                .setRuleset("classic")
                        // P1 Oscilator
                .setCell(2, 1, CellType.P1)
                .setCell(3, 1, CellType.P1)
                .setCell(4, 1, CellType.P1)
                .setCell(1, 2, CellType.P1)
                .setCell(2, 2, CellType.P1)
                .setCell(3, 2, CellType.P1)
                        // P2 Oscilator
                .setCell(7 ,  7, CellType.P2)
                .setCell(7 ,  8, CellType.P2)
                .setCell(8 ,  7, CellType.P2)
                .setCell(8 ,  8, CellType.P2)
                .setCell(9 ,  9, CellType.P2)
                .setCell(9 , 10, CellType.P2)
                .setCell(10,  9, CellType.P2)
                .setCell(10, 10, CellType.P2)
                .getState();
    }

    public static State bigDemoState() {
        return ConwayGameState.newBuilder(100, 100)
                .setRuleset("classic")
                        // P1 Oscilator
                .setCell(2, 1, CellType.P1)
                .setCell(3, 1, CellType.P1)
                .setCell(4, 1, CellType.P1)
                .setCell(1, 2, CellType.P1)
                .setCell(2, 2, CellType.P1)
                .setCell(3, 2, CellType.P1)
                        // P2 Oscilator
                .setCell(7 ,  7, CellType.P2)
                .setCell(7 ,  8, CellType.P2)
                .setCell(8 ,  7, CellType.P2)
                .setCell(8 ,  8, CellType.P2)
                .setCell(9 ,  9, CellType.P2)
                .setCell(9 , 10, CellType.P2)
                .setCell(10,  9, CellType.P2)
                .setCell(10, 10, CellType.P2)
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
                        return CellType.P1;
                    if (a.getLeft() == 0 && a.getRight() == 3)
                        return CellType.P2;
                    return CellType.DEAD;
                })
                .setFromOccupied((Triple<Integer, Integer, CellType> a) -> {
                            int diff = a.getLeft() - a.getMiddle();
                            return diff == 2 || diff == 3 ? a.getRight() : CellType.DEAD;
                        }
                )
                .getState();

        return new GameContext(state, 2);
    }
}
