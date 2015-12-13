package hr.best.ai.games.conway.gamestate;


import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import hr.best.ai.gl.Action;
/**
 * One action for game of life. It's a list of cells to be activated next turn.
 * @author nmiculinic
 */
public class Cells extends ArrayList<Cell> implements Action {
	
    /**
     * Constructs cells object from json array named "cells". Each cell is
     * represented as an json array inside "cells" json array.
     * 
     * @param object json object
     * @return cells object
     */
    public static Cells fromJsonObject (JsonObject object) {
        JsonArray array = object.get("cells").getAsJsonArray();
        Cells actions = new Cells();
        for (JsonElement cell : array) {
            JsonArray coordinate = cell.getAsJsonArray();
            actions.add(new Cell(coordinate.get(0).getAsInt(), coordinate.get(1).getAsInt()));
        }
        return actions;
    }
}
