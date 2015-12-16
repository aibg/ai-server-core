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
}
