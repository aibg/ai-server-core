package hr.best.ai.games;

import hr.best.ai.games.sum.SumState;
import hr.best.ai.gl.GameContext;
import hr.best.ai.gl.IBucket;

/**
 * Utility class. Creates game context with default parameters.
 */
public class GameContextFactory {
    private GameContextFactory() {
    }

    public static GameContext getSumGameInstance() {
        return new GameContext(new SumState(0), () -> new IBucket() {
            @Override
            public void tick() {
            }

            @Override
            public boolean tok() {
                return true;
            }

            @Override
            public boolean ok() {
                return true;
            }

            @Override
            public long getMills() {
                return 1000;
            }
        });
    }
}
