package hr.best.ai.games.conway;


import hr.best.ai.games.conway.visualization.GameBarPanel;
import hr.best.ai.games.conway.visualization.GameGridPanel;
import hr.best.ai.games.conway.visualization.PlayerInfoPanel;
import hr.best.ai.gl.AbstractPlayer;
import hr.best.ai.gl.GameContext;
import hr.best.ai.server.ConfigUtilities;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.google.gson.JsonObject;
import org.apache.log4j.Logger;


/**
 * Created by lpp on 10/31/15.
 */
public class RunGame {

    final static Logger logger = Logger.getLogger(RunGame.class);

    public static void addVisualization(GameContext gc, ConwayGameState initialState, String p1name, String p2name) throws
            Exception {

		Color p1color = Color.white;
		Color p2color = new Color(248,156,16);
		Color gridColor = new Color(200, 200, 200, 200);
		int barHeight = 30;

		SwingUtilities.invokeAndWait(() -> {
			JFrame frame = new JFrame("Conway");
			frame.setSize(new Dimension(1280,800));
			frame.setVisible(true);

			//TODO the following -1 hack must be changed
			Dimension gridSize=new Dimension(frame.getWidth()-frame.getInsets().left-frame.getInsets().right,frame.getHeight()-frame.getInsets().top-frame.getInsets().bottom-barHeight-1);

			GameGridPanel grid = new GameGridPanel(initialState, p1color, p2color, gridColor, gridSize);
			PlayerInfoPanel p1info=new PlayerInfoPanel(ConwayGameStateConstants.PLAYER1_CELL,p1color,p1name);
			PlayerInfoPanel p2info=new PlayerInfoPanel(ConwayGameStateConstants.PLAYER2_CELL,p2color,p2name);

			// bar setup
			GameBarPanel bar = new GameBarPanel(initialState,p1color, p2color);
			bar.setPreferredSize(new Dimension(0, barHeight));
			frame.getContentPane().add(bar, BorderLayout.NORTH);

			// background setup
			JPanel background=null;
			try {
				Image back=ImageIO.read(RunGame.class.getResource("/pozadina-crna-elektronika.png"));
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

				background.add(p1info);
				background.add(grid);
				background.add(p2info);

				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				frame.setVisible(true);

				gc.addObserver(bar);
				gc.addObserver(grid);
				gc.addObserver(p1info);
				gc.addObserver(p2info);
			});

	}

    public static void main(String[] args) throws Exception {
        try {
            Rulesets.getInstance(); // loading the class static part into JVM
            JsonObject config = ConfigUtilities.configFromCMDArgs(args);

            final List<AbstractPlayer> players = ConfigUtilities.istantiateAllPlayersFromConfig(
                    config.getAsJsonArray("players")
                    , config.get("port").getAsInt()
            );

            ConwayGameState initialState = (ConwayGameState) ConfigUtilities.genInitState(config);

            try(GameContext gc = new GameContext(initialState, 2)) {
                players.forEach(gc::addPlayer);

                if (config.get("visualization").getAsBoolean()) {
                    RunGame.addVisualization(
                            gc
                            , initialState
                            , players.get(0).getName()
                            , players.get(1).getName()
                    );
                    Thread.sleep(2000);
                }
                gc.play();
            }

        } catch (Exception ex) {
            if (ConfigUtilities.socket != null) {
                ConfigUtilities.socket.close();
            }
            ConfigUtilities.socket = null;
            logger.error(ex);
        }
    }
}
