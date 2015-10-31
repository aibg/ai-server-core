package hr.best.ai.games.conway;

import hr.best.ai.games.GameContextFactory;
import hr.best.ai.games.conway.visualization.GameBar;
import hr.best.ai.games.conway.visualization.GameGrid;
import hr.best.ai.gl.GameContext;
import hr.best.ai.gl.State;
import hr.best.ai.server.ProcessIOPlayer;

import javax.swing.*;
import java.awt.*;

/**
 * Created by lpp on 10/31/15.
 */
public class RunGame {
    public static void addVizualization(GameContext gc) throws Exception{
        GameBar bar = new GameBar();
        GameGrid grid = new GameGrid();

        SwingUtilities.invokeAndWait(() -> {
            JFrame f = new JFrame("DemoConway");

            f.setExtendedState(JFrame.MAXIMIZED_BOTH);
            f.getContentPane().add(bar, BorderLayout.NORTH);
            f.getContentPane().add(grid, BorderLayout.CENTER);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            f.setVisible(true);
        });

        gc.addObserver(bar);
        gc.addObserver(grid);
    }

    public static void main(String[] args) throws Exception {
        State st = GameContextFactory.bigDemoState();
        GameContext gc = new GameContext(st, 2);
        gc.addPlayer(new ProcessIOPlayer("/home/lpp/Documents/BEST/AI/python-bindings/main.py"));
        gc.addPlayer(new ProcessIOPlayer("/home/lpp/Documents/BEST/AI/python-bindings/main.py"));
        RunGame.addVizualization(gc);
        gc.play();
    }
}
