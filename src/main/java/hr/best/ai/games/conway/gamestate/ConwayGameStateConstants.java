package hr.best.ai.games.conway.gamestate;

/**
 * Created by lpp on 10/27/15.
 */
public class ConwayGameStateConstants {
    public static final int DEAD_CELL = 0;
    public static final int PLAYER1_CELL = 1;
    public static final int PLAYER2_CELL = 10;
    public static int inversePlayer(int p) {
        switch (p) {
            case PLAYER1_CELL:
                return PLAYER2_CELL;
            case PLAYER2_CELL:
                return PLAYER1_CELL;
            case  DEAD_CELL:
                return DEAD_CELL;
            default:
                throw new IllegalArgumentException();
        }
    }
    public static boolean isPlayer(int p) {
        switch (p) {
            case PLAYER1_CELL:
            case PLAYER2_CELL:
                return true;
            case  DEAD_CELL:
                return false;
            default:
                throw new IllegalArgumentException();
        }
    }
}
