package hr.best.ai.games.conway;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.HashMap;
import java.util.function.Function;

/**
 * Created by lpp on 11/1/15.
 */
public class Rulesets {

    public static class Ruleset1 {
        public final static Integer fromEmpty(Pair<Integer, Integer> a) {
            if (a.getLeft() == 3 && a.getRight() == 0)
                return ConwayGameStateConstants.PLAYER1_CELL;
            if (a.getLeft() == 0 && a.getRight() == 3)
                return ConwayGameStateConstants.PLAYER2_CELL;
            return ConwayGameStateConstants.DEAD_CELL;
        }

        public final static Integer fromOccupied(Triple<Integer, Integer, Integer> a) {
            return a.getLeft() == 2 || a.getLeft() == 3
                    ? a.getRight() : ConwayGameStateConstants.DEAD_CELL;
        }
    }

    public static class DiffRuleset {
        public final static Integer fromEmpty(Pair<Integer, Integer> a) {
            if (a.getLeft() - a.getRight() == 3)
                return ConwayGameStateConstants.PLAYER1_CELL;
            if (a.getLeft() - a.getRight() == -3)
                return ConwayGameStateConstants.PLAYER2_CELL;
            return ConwayGameStateConstants.DEAD_CELL;
        }

        public final static Integer fromOccupied(Triple<Integer, Integer, Integer> a) {
            switch (a.getLeft() - a.getMiddle()) {
                case 2:
                case 3:
                    return a.getRight();
                //case -2:
                //case -3:
                //    return ConwayGameStateConstants.inversePlayer(a.getRight());
                default:
                    return ConwayGameStateConstants.DEAD_CELL;
            }
        }
    }

    public final static HashMap<String, Function<Pair<Integer, Integer>, Integer>> fromEmpty = new HashMap<>();
    public final static HashMap<String, Function<Triple<Integer, Integer, Integer>, Integer>> fromOccupied = new
            HashMap<>();

    private static Rulesets singleton = new Rulesets();

    private Rulesets() {
        fromEmpty.put("classic", Ruleset1::fromEmpty);
        fromOccupied.put("classic", Ruleset1::fromOccupied);

        fromEmpty.put("diff", DiffRuleset::fromEmpty);
        fromOccupied.put("diff", DiffRuleset::fromOccupied);
    }

    public static Rulesets getInstance() {
        return singleton;
    }
}
