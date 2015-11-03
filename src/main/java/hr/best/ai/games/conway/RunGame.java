package hr.best.ai.games.conway;

import hr.best.ai.games.GameContextFactory;
import hr.best.ai.games.conway.players.DoNothingPlayerDemo;
import hr.best.ai.games.conway.visualization.GameBarPanel;
import hr.best.ai.games.conway.visualization.GameGridPanel;
import hr.best.ai.games.conway.visualization.PlayerInfoPanel;
import hr.best.ai.gl.GameContext;
import hr.best.ai.gl.State;
import hr.best.ai.server.ProcessIOPlayer;




import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;




import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;




import com.kitfox.svg.app.beans.SVGPanel;

/**
 * Created by lpp on 10/31/15.
 */
public class RunGame {
	

	public static void addVisualization(GameContext gc) throws Exception {
		
		Color p1color = Color.red;
		Color p2color = Color.blue;
		Color gridColor = new Color(200, 200, 200);
		
		int barHeight = 30;
		String p1Logo="src/main/resources/BEST_ZG_mali.png";
		String p2Logo="src/main/resources/Untitled-3.png";
		
		GameBarPanel bar = new GameBarPanel(p1color, p2color);
		GameGridPanel grid = new GameGridPanel(p1Logo,p2Logo,p1color,p2color,gridColor);

		SwingUtilities.invokeAndWait(() -> {

			JFrame f = new JFrame("DemoConway");
			f.setSize(new Dimension(800,600));
			//f.setExtendedState(JFrame.MAXIMIZED_BOTH);
			// bar setup
				bar.setPreferredSize(new Dimension(0, barHeight));
				f.getContentPane().add(bar, BorderLayout.NORTH);

				// background setup
				SVGPanel background = new SVGPanel();
				background.setLayout(new BoxLayout(background, BoxLayout.LINE_AXIS));
				background.setScaleToFit(true);
				background.setAntiAlias(true);
				try {
					background.setSvgResourcePath("/background.svg");
				} catch (Exception e) {
					e.printStackTrace();
				}
				f.getContentPane().add(background, BorderLayout.CENTER);
				
				background.add(new PlayerInfoPanel(1));
				background.add(grid);
				background.add(new PlayerInfoPanel(2));
				
				
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setVisible(true);
				
				
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
