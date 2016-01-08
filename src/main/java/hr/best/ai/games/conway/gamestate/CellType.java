package hr.best.ai.games.conway.gamestate;

public enum CellType {

    DEAD(-1), P1(0), P2(1);

    private int playerID;

    CellType(int value) {
        this.playerID = value;
    }

    public int getID() {
        return playerID;
    }
    
    /**
     * Checks if cell is alive or dead.
     * 
     * @param value cell value
     * @return <code>true</code> if its alive, <code>false</code> otherwise
     */
    public static boolean isPlayerCell(CellType value) {
        switch (value) {
            case P1:
            case P2:
                return true;
            case  DEAD:
                return false;
            default:
                throw new IllegalArgumentException();
        }
    }
    
    /**
     * @param player player constant
     * @return other player constant
     */
    public static CellType inversePlayerCell(CellType value) {
        switch (value) {
            case P1:
                return CellType.P2;
            case P2:
                return CellType.P1;
            case  DEAD:
                return CellType.DEAD;
            default:
                throw new IllegalArgumentException();
        }
    }
}
