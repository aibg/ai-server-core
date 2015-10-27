package hr.best.ai.games.conway;


import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import hr.best.ai.gl.Action;
/**
 * One action for game of life. It's a list of cells to be activated next turn.
 * @author nmiculinic
 */
public class Cells extends ArrayList<Cell> implements Action {

    public static Cells fromJsonObject (JsonObject object) {
        JsonArray array = object.get("cells").getAsJsonArray();
        Cells actions = new Cells();
        for (JsonElement cell : array) {
            String[] coordinate = cell.getAsString().split(",");
            actions.add(new Cell(Integer.parseInt(coordinate[0]), Integer
                    .parseInt(coordinate[1])));
        }
        return actions;
    }
}
