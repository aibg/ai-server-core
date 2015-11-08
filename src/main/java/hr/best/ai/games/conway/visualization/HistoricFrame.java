package hr.best.ai.games.conway.visualization;

import hr.best.ai.games.conway.Cell;
import hr.best.ai.games.conway.Cells;
import hr.best.ai.games.conway.ConwayGameState;
import hr.best.ai.games.conway.ConwayGameStateConstants;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.RenderingHints.Key;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class HistoricFrame extends JFrame{

	private int currState;
	
	public HistoricFrame(List<ConwayGameState> stanje) {
		super();
		currState=0;
		
		JPanel statePanel=new JPanel(){
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);

				ConwayGameState state=stanje.get(currState);
				ConwayGameState nextState=null;
				if(currState<stanje.size()-1)
						nextState=stanje.get(currState+1);
				
				double blockWidth = (double) getParent().getBounds().width
						/ state.getCols();
				double blockHeight = (double) getParent().getBounds().height
						/ state.getRows();

				int blockSize = Math.toIntExact(Math.round(Math.floor(Math.min(blockWidth,
						blockHeight))));

				int width = blockSize * state.getCols() + 1;
				int height = blockSize * state.getRows() + 1;
				Dimension newSize = new Dimension(width, height);
				setPreferredSize(newSize);

				// draw grid

				g.setColor(Color.black);
				// horizontal lines
				for (int i = 0; i <= state.getRows(); i++) {
					g.drawLine(0, i * blockSize, state.getCols() * blockSize, i
							* blockSize);
				}
				// vertical lines
				for (int i = 0; i <= state.getCols(); i++) {
					g.drawLine(i * blockSize, 0, i * blockSize, state.getCols()
							* blockSize);
				}

				Color p1color=new Color(100,0,0);
				Color p2color=new Color(0,0,100);
				
				
				// draw live cells
				for (int i = 0; i < state.getRows(); i++) {
					for (int j = 0; j < state.getCols(); j++) {
						switch (state.getCell(i, j)) {
						case ConwayGameStateConstants.PLAYER1_CELL:
							g.setColor(p1color);
							break;
						case ConwayGameStateConstants.PLAYER2_CELL:
							g.setColor(p2color);
							break;
						case ConwayGameStateConstants.DEAD_CELL:
							continue;
						}
						g.fillRect(blockSize * j, blockSize * i, blockSize, blockSize);

					}
				}
				if(nextState!=null){
				drawCurrentActions(g, nextState.getPlayer1Actions(), p1color.brighter().brighter().brighter().brighter(),blockSize);
				drawCurrentActions(g, nextState.getPlayer2Actions(), p2color.brighter().brighter().brighter().brighter(), blockSize);
				}
			}

			private void drawCurrentActions(Graphics g, Cells actions, Color color, int blockSize) {
				if(actions==null){
					return;
				}
				Color before=g.getColor();
				g.setColor(color);
				for (int i = 0; i < actions.size(); i++) {
					Cell c = actions.get(i);
					g.fillRect(blockSize * c.getCol(), blockSize * c.getRow(),
							blockSize, blockSize);
				}
				g.setColor(before);
			}

			
		};

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(statePanel,BorderLayout.CENTER);
	
		
		
		
		addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyPressed(KeyEvent e) {
				super.keyPressed(e);
				
				switch (e.getKeyCode()) {
				case KeyEvent.VK_J:
					if(currState>0)
						--currState;
					
					break;
				case KeyEvent.VK_K:
					if(currState<stanje.size()-1)
						++currState;
					break;

				default:
					break;
				}
				repaint();
			}

			
			
			
			
			
		});
		
		
		
		
		
	}

}
