package hr.best.ai.server;

import hr.best.ai.gl.GameContext;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Game Manager singleton.
 */
public enum GameManager {
    INSTANCE;

    private final ConcurrentHashMap<Integer, GameContext> mapping = new ConcurrentHashMap<>();

    private AtomicInteger cnt = new AtomicInteger(0);

    public int newGameContext(GameContext game) {
        int c = cnt.addAndGet(1);
        mapping.put(c, game);
        return c;
    }

    public GameContext getGameContext(int c) {
        return mapping.get(c);
    }
}
