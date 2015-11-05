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

@SuppressWarnings("serial")
public class PlayerInfoPanel extends JPanel implements NewStateObserver{

	private JLabel scoreLabel=new JLabel();
	private int playerID;
	
	
	public PlayerInfoPanel(int playerID,Color textColor) {
		this.playerID=playerID;
		setOpaque(false);
		JPanel innerPanel=new JPanel();
		innerPanel.setOpaque(false);
		add(innerPanel,BorderLayout.CENTER);
		innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.PAGE_AXIS));
		
		//adding labels
		
		//TODO player name??		
		innerPanel.add(new JLabel("Player"+playerID));
		innerPanel.add(Box.createVerticalGlue());
		innerPanel.add(new JLabel("SCORE"));
		innerPanel.add(scoreLabel);
		
		//set text color
		for(int i=0;i<innerPanel.getComponentCount();i++){
			innerPanel.getComponent(i).setForeground(textColor);
		}
		
		
		
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
			score=((ConwayGameState)state).getP1Score();
			break;
		case ConwayGameStateConstants.PLAYER2_CELL:
			score=((ConwayGameState)state).getP2Score();
		break;
		default:
			break;
		}
		scoreLabel.setText(String.valueOf(score));
	}

	@Override
	public void signalCompleted(String message) {
		// TODO Auto-generated method stub
		
	}

}
