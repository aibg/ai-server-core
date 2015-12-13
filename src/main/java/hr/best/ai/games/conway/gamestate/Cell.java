package hr.best.ai.games.conway.gamestate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/**
 * Part of player action which contains coordinates of a cell to be activated
 * next turn.
 * 
 * @author andrej
 */
public class Cell {
	private final int row;
	private final int col;

	public Cell(int row, int col) {
		this.row = row;
		this.col = col;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}
	/**
	* Cell json array with row and column as json primitives.<br>
	* Example:<br>
	* Cell in 1st row and t4th column is serialized as: [1,4].<br> 
	* Note that row and column indexing is 0-based.
	*/
    public JsonElement toJSON() {
        JsonArray sol = new JsonArray();
        sol.add(new JsonPrimitive(row));
        sol.add(new JsonPrimitive(col));
        return sol;
    }
    
    /**
     * Json string.
     */
	@Override
	public String toString() {
        return toJSON().toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + col;
		result = prime * result + row;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cell other = (Cell) obj;
		if (col != other.col)
			return false;
		if (row != other.row)
			return false;
		return true;
	}

}
