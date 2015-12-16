package hr.best.ai.games.conway.visualization;

import hr.best.ai.games.conway.gamestate.CellType;
import hr.best.ai.games.conway.gamestate.ConwayGameState;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

/**
 * Utility class with various constants (visualization) and helper functions.
 *
 * @author Neven Miculinic
 */
public class ConwayUtilities {

    final static Logger logger = Logger.getLogger(ConwayUtilities.class);

    public final static Color p1color = Color.white;
    //Orange(together with white part of or AIBattleground Visual identity)
    public final static Color p2color = new Color(248,156,16);
    public final static Color gridColor = new Color(200, 200, 200, 200);
    public final static int barHeight = 30;

    /**
     * Sets visualization elements on a JFrame with size <code>windowSize</code>. 
     * Grid panel is in the middle, surrounded by two info panels to the left and right.
     * Player percentage bar takes up the top of the frame.
     * 
     * @param windowSize JFrame dimension
     * @param grid central panel with game grid
     * @param p1info left panel with some information about player 1
     * @param p2info right panel with some information about player 2
     * @param bar top panel with percentage bar
     * @return constructed JFrame
     */
    public static JFrame composeVisualization(Dimension windowSize, GameGridPanel grid, PlayerInfoPanel p1info, PlayerInfoPanel p2info, GameBarPanel bar) {
        JFrame frame = new JFrame("Conway");
        frame.setSize(windowSize);
        frame.setVisible(true);

        Dimension gridSize = windowSize.getSize();
        gridSize.height -= 30 + 1 + barHeight;
        gridSize.width -= 10;
        grid.setComponentSize(gridSize);

        frame.getContentPane().add(bar, BorderLayout.NORTH);

        // background setup
        JPanel background=null;
        try {
            Image back= ImageIO.read(ConwayUtilities.class.getResource("/pozadina-crna-elektronika.png"));
            background=new JPanel(){
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Color current=g.getColor();
                    g.setColor(Color.black);
                    g.fillRect(0, 0, getWidth(), getHeight());
                    g.setColor(current);
                    g.drawImage(back, 0, 0, getWidth(), getHeight(), this);

                }
            };
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
        }
        background.setLayout(new BoxLayout(background, BoxLayout.LINE_AXIS));

        frame.getContentPane().add(background, BorderLayout.CENTER);

        background.add(p1info, BorderLayout.WEST);
        background.add(grid, BorderLayout.CENTER);
        background.add(p2info, BorderLayout.EAST);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        return frame;
    }

    /**
     * Default player and grid colors.
     */
    public static GameGridPanel getDefaultGameGridPanel(ConwayGameState initialState) {
        return new GameGridPanel(initialState, p1color, p2color, gridColor);
    }
    
    /**
     * Default player and grid colors.
     */
    public static GameGridWithActionsPanel getGameGridWithActionsPanel(ConwayGameState initialState) {
        return new GameGridWithActionsPanel(initialState, p1color, p2color, gridColor);
    }
    
    /**
     * Default player 2 color.
     * @param p2name player 2 name
     */
    public static PlayerInfoPanel getP2DefaultInfoPanel(String p2name) {
        return new PlayerInfoPanel(CellType.P2, p2color, p2name);
    }

    /**
     * Default player 1 color.
     * @param p1name player 1 name
     */
    public static PlayerInfoPanel getP1DefaultInfoPanel(String p1name) {
        return new PlayerInfoPanel(CellType.P1, p1color, p1name);
    }

    /**
     * Default player colors and default bar height.
     */
    public static GameBarPanel getDefaultGameBarPanel(ConwayGameState initialState) {
        GameBarPanel bar = new GameBarPanel(initialState, p1color, p2color);
        bar.setPreferredSize(new Dimension(0, ConwayUtilities.barHeight));
        return bar;
    }
}
