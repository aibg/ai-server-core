package hr.best.ai.games.conway.visualization;

import java.awt.BorderLayout;

import hr.best.ai.gl.NewStateObserver;
import hr.best.ai.gl.State;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class PlayerInfoPanel extends JPanel implements NewStateObserver{

	public PlayerInfoPanel(int playerID) {
		setOpaque(false);
		JPanel innerPanel=new JPanel();
		add(innerPanel,BorderLayout.CENTER);
		innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.PAGE_AXIS));
		innerPanel.add(new JLabel("player"+playerID));
		innerPanel.add(new JLabel("infoinfo info"));
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void signalNewState(State state) {
		
	}

	@Override
	public void signalCompleted(String message) {
		// TODO Auto-generated method stub
		
	}

}
