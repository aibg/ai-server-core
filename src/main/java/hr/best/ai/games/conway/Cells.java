package hr.best.ai.games.conway;


import java.util.ArrayList;
import java.util.List;

import hr.best.ai.gl.Action;
/**
 * One action for game of life. It's a list of cells to be activated next turn.
 * @author andrej
 */
public class Cells implements Action {

	private List<Cell> list = new ArrayList<Cell>();

	public Cell get(int index) {
		return list.get(index);
	}

	public void add(Cell cell) {
		list.add(cell);
	}

	public int size() {
		return list.size();
	}
	
	public boolean contains(Cell cell){
		return list.contains(cell);
	}
	
	public void remove(Cell cell){
		list.remove(cell);
	}

}
