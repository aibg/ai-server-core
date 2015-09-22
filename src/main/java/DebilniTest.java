import hr.best.ai.games.GameContextFactory;
import hr.best.ai.games.sum.SumDummyPlayer;
import hr.best.ai.gl.GameContext;

public class DebilniTest {

    static void f1() {
        GameContext gc = GameContextFactory.getSumGameInstance();
        gc.registerPlayer(new SumDummyPlayer(1, "+1"));
        gc.registerPlayer(new SumDummyPlayer(2, "+2"));
        try {
            gc.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        f1();
    }
}
