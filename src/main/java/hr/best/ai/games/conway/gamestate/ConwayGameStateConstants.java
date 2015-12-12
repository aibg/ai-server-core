package hr.best.ai.games.conway.gamestate;

/**
 * Constants and some static methods used mostly in ConwayGameState.
 */
public class ConwayGameStateConstants {
	
    public static final int DEAD_CELL = 0;
    public static final int PLAYER1_CELL = 1;
    
    //TODO change to 2?
    public static final int PLAYER2_CELL = 10;
    
    public static final int PLAYER1_ID = 0;
    public static final int PLAYER2_ID = 1;
    
    /**
     * @param player player constant
     * @return other player constant
     */
    public static int inversePlayer(int player) {
        switch (player) {
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
    
    /**
     * Checks if cell is alive or dead.
     * 
     * @param value cell value
     * @return <code>true</code> if its alive, <code>false</code> otherwise
     */
    public static boolean isPlayer(int value) {
        switch (value) {
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
