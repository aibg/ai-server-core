package hr.best.ai.games.conway.visualization;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.io.IOException;

import hr.best.ai.games.conway.gamestate.CellType;
import hr.best.ai.games.conway.gamestate.ConwayGameState;
import hr.best.ai.gl.NewStateObserver;
import hr.best.ai.gl.State;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.google.gson.JsonObject;

@SuppressWarnings("serial")
public class PlayerInfoPanel extends JPanel implements NewStateObserver {

	private final JLabel infoLabel = new JLabel();
	private final JLabel iterationLabel = new JLabel();

	private final CellType playerCell;
	private final String playerName;
	//private LogoPanel logoPanel;

	public PlayerInfoPanel(CellType playerCell, Color textColor, String playerName) {
		this.playerCell = playerCell;
		this.playerName = playerName;
		setOpaque(false);

		setLayout(new BorderLayout(0, 0));

		// set up info
		infoLabel.setHorizontalAlignment(SwingUtilities.CENTER);
		int score = 0;
		infoLabel.setText("<html><center><h1><b>" + playerName
				+ "</b><h1></center>"
				+ "<br><center><h2>score:<h2></center><br><center><h1>" + score
				+ "</h1></center></html>");
		add(infoLabel, BorderLayout.CENTER);

		// set up iteration
		iterationLabel.setHorizontalAlignment(SwingUtilities.CENTER);
		int iter = 0;
		iterationLabel.setText("<html><center><h1>Iteration:" + iter
				+ "</h1></center></html>");

		// set text color
		infoLabel.setForeground(textColor);
		iterationLabel.setForeground(textColor);

		switch (playerCell) {
		case P1:
			add(iterationLabel, BorderLayout.SOUTH);
			break;
		case P2:
			Image logo = null;
			Dimension logoSize=new Dimension(170,100);
			try {
				logo = ImageIO.read(getClass().getResource("/BEST_ZG_logo.png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			LogoPanel logoPanel= new LogoPanel(logo,logoSize);
			logoPanel.setPreferredSize(logoSize);
			logoPanel.setOpaque(false);
			add(logoPanel, BorderLayout.SOUTH);
					
			break;
		default:
			throw new IllegalArgumentException("invalid player ID");
		}

		Dimension dim = iterationLabel.getPreferredSize();
		iterationLabel.setPreferredSize(dim);
		iterationLabel.setMaximumSize(dim);
		iterationLabel.setMinimumSize(dim);
		//System.out.println(iterationLabel.getPreferredSize());
		//System.exit(0);
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void signalNewState(State state) {

		int iter = ((ConwayGameState) state).getIteration();
		iterationLabel.setText("<html><center><h1>Iteration:" + iter
				+ "</h1></center></html>");

		int score = 0;
		switch (playerCell) {
		case P1:
			score = ((ConwayGameState) state).getP1LiveCellcount();
			break;
		case P2:
			score = ((ConwayGameState) state).getP2LiveCellcount();
			break;
		default:
			break;
		}
		infoLabel.setText("<html><center><h1><b>" + playerName
				+ "</b><h1></center>"
				+ "<br><center><h2>score:<h2></center><br><center><h1>" + score
				+ "</h1></center></html>");

	}

	@Override
	public void signalError(JsonObject message) {
		// TODO Auto-generated method stub

	}

}
