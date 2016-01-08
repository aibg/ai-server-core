package hr.best.ai.games.conway.gamestate;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.HashMap;
import java.util.function.Function;

/**
 * Collection of static classes representing different rules for determining how
 * next state gets calculated. It's a singleton.<br>
 * <br>
 * There are two functions in each ruleset: fromEmpty and fromOccupied.<br>
 * fromEmpty determines how a dead cell changes when state changes, and fromOccupied 
 * how living cell changes.<br><br>
 * Each cell on the grid changes according to these two functions:<br>
 * fromEmpty    (player1 neighbors, player2 neighbors)-> cell type<br>
 * fromOccupied (friendly neighbors, enemy neighbors, cell_type) -> cell type
 * 
 */
public class Rulesets {
    /**
     * Classic ruleset:<br>
     * same four rules as found on wikipedia's "Conway's Game of Life" article
     * with the difference: dead cell doesn't activate in case of enemy active
     * neighbors
     */
    public static class Ruleset1 {
    	
        /**
         * Cell activates only with exactly 3 friendly neighbors and 0 enemy
         * neighbors
         */
        public final static CellType fromEmpty(Pair<Integer, Integer> a) {
            if (a.getLeft() == 3 && a.getRight() == 0)
                return CellType.P1;
            if (a.getLeft() == 0 && a.getRight() == 3)
                return CellType.P2;
            return CellType.DEAD;
        }
        
        /**
         * Cell stays active only with 2 or 3 friendly neighbors
         */
        public final static CellType fromOccupied(Triple<Integer, Integer, CellType> a) {
            return a.getLeft() == 2 || a.getLeft() == 3
                    ? a.getRight() : CellType.DEAD;
        }
    }

    /**
     * Diff(differential) ruleset:<br>
     * same four rules as found on wikipedia's "Conway's Game of Life" article
     * with the difference being that neighbors are calculated by subtracting
     * enemy neighbors from friendly neighbors
     */
    public static class DiffRuleset {
    	
        /**
         * Cell activates only with 2 or 3 neighbors (#friendly neighbors-#enemy neighbors)
         */
        public final static CellType fromEmpty(Pair<Integer, Integer> a) {
            if (a.getLeft() - a.getRight() == 3)
                return CellType.P1;
            if (a.getLeft() - a.getRight() == -3)
                return CellType.P2;
            return CellType.DEAD;
        }
        
        /**
         * Cell activates only with 2 or 3 neighbors (friendly-enemy)
         */
        public final static CellType fromOccupied(Triple<Integer, Integer, CellType> a) {
            switch (a.getLeft() - a.getMiddle()) {
                case 2:
                case 3:
                    return a.getRight();
                default:
                    return CellType.DEAD;
            }
        }
    }

    /**
     * Hashmap of fromEmpty rules (functions)
     */
    public final static HashMap<String, Function<Pair<Integer, Integer>, CellType>> fromEmpty = new HashMap<>();
    
    /**
     * Hashmap of fromEmpty rules (functions)
     */
    public final static HashMap<String, Function<Triple<Integer, Integer, CellType>, CellType>> fromOccupied = new
            HashMap<>();

    private static Rulesets singleton = new Rulesets();

    /**
     * Fills hashmaps with classic and diff (differential) ruleset
     */
    private Rulesets() {
        fromEmpty.put("classic", Ruleset1::fromEmpty);
        fromOccupied.put("classic", Ruleset1::fromOccupied);

        fromEmpty.put("diff", DiffRuleset::fromEmpty);
        fromOccupied.put("diff", DiffRuleset::fromOccupied);
    }

    /**
     * @return the only instance of Rulesets
     */
    public static Rulesets getInstance() {
        return singleton;
    }
}
