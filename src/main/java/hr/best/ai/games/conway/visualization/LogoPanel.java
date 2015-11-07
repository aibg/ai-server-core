package hr.best.ai.games.conway.visualization;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class LogoPanel extends JPanel {

	private Image logo;
	private boolean sizeSet;

	public LogoPanel(Image logo,Dimension size) {
		this.logo=logo;
		/*Image scaledLogo = logo.getScaledInstance((int)size.getWidth(), -1,
				Image.SCALE_DEFAULT);
		setMinimumSize(new Dimension(scaledLogo.getWidth(null),scaledLogo.getHeight(null)));
		setPreferredSize(new Dimension(scaledLogo.getWidth(null),scaledLogo.getHeight(null)));
		setMaximumSize(new Dimension(scaledLogo.getWidth(null),scaledLogo.getHeight(null)));*/
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Image scaledLogo = logo.getScaledInstance(getWidth(), -1,
				Image.SCALE_DEFAULT);
		/*if(!sizeSet){
		setPreferredSize(new Dimension(getSize().width,scaledLogo.getHeight(null)));
		sizeSet=true;}*/
		g.drawImage(scaledLogo, 0, 0, null);
	}

}
