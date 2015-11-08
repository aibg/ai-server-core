package hr.best.ai.games.conway;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kitfox.svg.app.beans.SVGPanel;
import hr.best.ai.games.conway.visualization.GameBarPanel;
import hr.best.ai.games.conway.visualization.GameGridPanel;
import hr.best.ai.games.conway.visualization.PlayerInfoPanel;
import hr.best.ai.gl.*;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Action;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lpp on 11/8/15.
 */
public class HistoricRunGame extends JPanel {

    final static Logger logger = Logger.getLogger(HistoricRunGame.class);

    public static void main(String[] args) throws Exception {
        Rulesets.getInstance(); // loading the class static part into JVM
        final JsonParser parser = new JsonParser();
        JsonObject config;

        if (args.length == 0) {
            System.out.println("Falling back to default game configuration.");
            config = parser.parse(new InputStreamReader(RunGame.class.getClassLoader().getResourceAsStream("defaultConfig.json"), StandardCharsets.UTF_8)).getAsJsonObject();
        } else {
            System.out.println("Using " + args[0] + " configuration file");
            config = parser.parse(new FileReader(args[0])).getAsJsonObject();
        }

        final List<ConwayGameState> stanje = new ArrayList<>();
        final List<NewStateObserver> observers = new ArrayList<>();

        try (GameContext gc = RunGame.initializeGC(config)) {
            logger.debug("Starting game simulation");
            gc.addObserver(new NewStateObserver() {
                @Override
                public void signalNewState(State state) {
                    stanje.add((ConwayGameState) state);
                }

                @Override
                public void signalError(JsonObject message) {
                }

                @Override
                public String getName() {
                    return "historic propagation";
                }

                @Override
                public void close() throws Exception {
                }
            });
            gc.play();

            Color p1color = Color.white;
            Color p2color = new Color(248, 156, 16);
            Color gridColor = new Color(200, 200, 200, 200);

            int barHeight = 30;
            Image p1Logo = ImageIO.read(RunGame.class.getResource("/BEST_ZG_mali.png"));
            Image p2Logo = ImageIO.read(RunGame.class.getResource("/Untitled-3.png"));

            SwingUtilities.invokeAndWait(() -> {

                JFrame frame = new JFrame("Conway");

                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(frame.getGraphicsConfiguration());
                Dimension frameSize = new Dimension(screenSize.width, screenSize.height - screenInsets.top);

                frame.setSize(new Dimension(1280, 800));
                //frame.setSize(frameSize);
                //frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                frame.setVisible(true);

                //TODO the following -1 hack must be changed
                Dimension gridSize = new Dimension(frame.getWidth() - frame.getInsets().left - frame.getInsets().right, frame.getHeight() - frame.getInsets().top - frame.getInsets().bottom - barHeight - 1);

                GameGridPanel grid = new GameGridPanel(stanje.get(0), null, null, p1color, p2color, gridColor, gridSize);

                PlayerInfoPanel p1info = new PlayerInfoPanel(ConwayGameStateConstants.PLAYER1_CELL, p1color, "P1");
                PlayerInfoPanel p2info = new PlayerInfoPanel(ConwayGameStateConstants.PLAYER2_CELL, p2color, "P2");

                // bar setup
                GameBarPanel bar = new GameBarPanel(stanje.get(0), p1color, p2color);
                bar.setPreferredSize(new Dimension(0, barHeight));
                frame.getContentPane().add(bar, BorderLayout.NORTH);

                // background setup
                SVGPanel background = new SVGPanel();
                background.setLayout(new BoxLayout(background, BoxLayout.LINE_AXIS));
                background.setScaleToFit(true);
                background.setAntiAlias(true);
                try {
                    background.setSvgResourcePath("/background.svg");
                } catch (Exception e) {
                    // TODO
                    e.printStackTrace();
                }
                frame.getContentPane().add(background, BorderLayout.CENTER);

                background.add(p1info);
                background.add(grid);
                background.add(p2info);

                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                frame.setVisible(true);

                observers.add(bar);
                observers.add(grid);
                observers.add(p1info);
                observers.add(p2info);
            });
        }
    }
}
