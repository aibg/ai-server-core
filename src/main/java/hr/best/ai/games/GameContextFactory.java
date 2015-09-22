package hr.best.ai.games;

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
}
