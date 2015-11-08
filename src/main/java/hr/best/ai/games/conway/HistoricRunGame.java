package hr.best.ai.games.conway;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kitfox.svg.app.beans.SVGPanel;

import hr.best.ai.games.conway.visualization.GameBarPanel;
import hr.best.ai.games.conway.visualization.GameGridPanel;
import hr.best.ai.games.conway.visualization.HistoricFrame;
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
        
        
            SwingUtilities.invokeLater(()->{
            	HistoricFrame hf=new HistoricFrame(stanje);  
            	hf.setSize(new Dimension(1000,800));
            	//hf.setExtendedState(JFrame.MAXIMIZED_BOTH);
            	hf.setVisible(true);
            });
            
        }
    }
}
