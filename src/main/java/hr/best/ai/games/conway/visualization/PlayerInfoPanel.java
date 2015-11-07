package hr.best.ai.games.conway.visualization;

import java.awt.BorderLayout;
import java.awt.Color;

import hr.best.ai.games.conway.ConwayGameState;
import hr.best.ai.games.conway.ConwayGameStateConstants;
import hr.best.ai.gl.NewStateObserver;
import hr.best.ai.gl.State;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.google.gson.JsonObject;

@SuppressWarnings("serial")
public class PlayerInfoPanel extends JPanel implements NewStateObserver{

	private final JLabel label=new JLabel();
	private final int playerID;
	private final String playerName;
	
	public PlayerInfoPanel(int playerID,Color textColor, String playerName) {
		this.playerID=playerID;
		this.playerName=playerName;
		setOpaque(false);
		JPanel innerPanel=new JPanel();
		innerPanel.setOpaque(false);
		add(innerPanel,BorderLayout.CENTER);
		innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.PAGE_AXIS));
		
		//add label
		int score=0;
		label.setText("<html><center><h1><b>"+ playerName +"</b><h1></center>"+"<br><center><h2>score:<h2></center><br><center><h1>"+score+"</h1></center></html>");
		innerPanel.add(label);
		
		//set text color
		label.setForeground(textColor);
		
		
		
		
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void signalNewState(State state) {
		int score = 0;
		switch (playerID) {
		case ConwayGameStateConstants.PLAYER1_CELL:
			score=((ConwayGameState)state).getP1LiveCellcount();
			break;
		case ConwayGameStateConstants.PLAYER2_CELL:
			score=((ConwayGameState)state).getP2LiveCellcount();
		break;
		default:
			break;
		}
		label.setText("<html><center><h1><b>"+ playerName +"</b><h1></center>"+"<br><center><h2>score:<h2></center><br><center><h1>"+score+"</h1></center></html>");
	}

	@Override
	public void signalError(JsonObject message) {
		// TODO Auto-generated method stub
		
	}

}
