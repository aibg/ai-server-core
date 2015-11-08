package hr.best.ai.games.conway.visualization;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class LogoPanel extends JPanel {

	private Image scaledLogo;

	public LogoPanel(Image logo,Dimension size) {
		
		scaledLogo = logo.getScaledInstance((int)size.getWidth(), -1,
				Image.SCALE_DEFAULT);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(scaledLogo, (getWidth()-scaledLogo.getWidth(null))/2, 0, null);
	}

}
