package hr.best.ai.games.conway.visualization;

import hr.best.ai.games.conway.gamestate.Cell;
import hr.best.ai.games.conway.gamestate.Cells;
import hr.best.ai.games.conway.gamestate.ConwayGameState;

import java.awt.*;

/**
 * @author Neven Miculinic
 */
public class GameGridWithActionsPanel extends GameGridPanel{
    public GameGridWithActionsPanel(ConwayGameState initialState, Color player1Color, Color player2Color, Color gridColor) {
        super(initialState, player1Color, player2Color, gridColor);
    }

    private Cells p1Actions = new Cells();
    private Cells p2Actions = new Cells();

    public Cells getP1Actions() {
        return p1Actions;
    }

    public void setP1Actions(Cells p1Actions) {
        this.p1Actions = p1Actions;
    }

    public Cells getP2Actions() {
        return p2Actions;
    }

    public void setP2Actions(Cells p2Actions) {
        this.p2Actions = p2Actions;
    }

    public Cell fromPoint(Point screenPoint) {
        return new Cell
                ( screenPoint.y / blockSize
                , screenPoint.x / blockSize);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Cell c : p1Actions)
            this.paintCell(g, c.getRow(), c.getCol(), this.player1Color.darker().darker().darker());
        for (Cell c : p2Actions)
            this.paintCell(g, c.getRow(), c.getCol(), this.player2Color.darker().darker().darker());
    }
}
