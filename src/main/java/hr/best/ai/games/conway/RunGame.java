package hr.best.ai.games.conway;

import hr.best.ai.games.GameContextFactory;
import hr.best.ai.games.conway.players.DoNothingPlayerDemo;
import hr.best.ai.games.conway.visualization.GameBarPanel;
import hr.best.ai.games.conway.visualization.GameGridPanel;
import hr.best.ai.gl.GameContext;
import hr.best.ai.gl.State;
import hr.best.ai.server.ProcessIOPlayer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.kitfox.svg.app.beans.SVGPanel;

/**
 * Created by lpp on 10/31/15.
 */
public class RunGame {
	private static Color p1color = Color.red;
	private static Color p2color = Color.blue;
	private static Color gridColor = Color.black;
	
	private static int barHeight = 30;
	private static String p1Logo="src/main/resources/BEST_ZG_mali.png";
	private static String p2Logo="src/main/resources/Untitled-3.png";

	public static void addVisualization(GameContext gc) throws Exception {
		GameBarPanel bar = new GameBarPanel(p1color, p2color);
		GameGridPanel grid = new GameGridPanel(p1Logo,p2Logo,p1color,p2color,gridColor);

		SwingUtilities.invokeAndWait(() -> {

			JFrame f = new JFrame("DemoConway");
			f.setExtendedState(JFrame.MAXIMIZED_BOTH);
			// bar setup
				bar.setPreferredSize(new Dimension(0, barHeight));
				f.getContentPane().add(bar, BorderLayout.NORTH);

				// background setup
				SVGPanel background = new SVGPanel();
				background.setScaleToFit(true);
				background.setAntiAlias(true);
				background.setSvgURI(new File(
						"src/main/resources/pozadina-proba.svg").toURI());
				f.getContentPane().add(background, BorderLayout.CENTER);

				// grid setup
				grid.setOpaque(false);
				background.add(grid, BorderLayout.CENTER);
				
				//TODO side setup (just for moving grid to the middle)
				JPanel p1info=new JPanel();
				p1info.setOpaque(false);
				background.add(p1info,BorderLayout.WEST);
				
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setVisible(true);
				f.pack();
				
				//sets p1info size to allow the grid square to be in screen center
				//TODO change this, it doesn't always work
				p1info.setPreferredSize(new Dimension((f.getWidth()-f.getHeight()+f.getInsets().top+barHeight)/2,0));
								
			});

		gc.addObserver(bar);
		gc.addObserver(grid);
	}

	public static void main(String[] args) throws Exception {
		State st = GameContextFactory.demoState();
		GameContext gc = new GameContext(st, 2);

		gc.addPlayer(new ProcessIOPlayer(new ProcessBuilder("java", "-jar",
				"/home/andrej/Desktop/testbot.jar").start()));
		gc.addPlayer(new DoNothingPlayerDemo());
		RunGame.addVisualization(gc);
		gc.play();
	}
}
