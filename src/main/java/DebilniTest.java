import hr.best.ai.Bucket;
import hr.best.ai.GameContext;
import hr.best.ai.games.sum.SumAction;
import hr.best.ai.games.sum.SumState;
import hr.best.ai.gl.State;
import hr.best.ai.server.ClientThreadDummy;
import hr.best.ai.server.IClient;

public class DebilniTest {
    public static void main(String[] args) {
        State st = new SumState(0);

        GameContext gc = new GameContext(st, () -> new Bucket(10,10,10));

        IClient c1 = new ClientThreadDummy(gc);
        IClient c2 = new ClientThreadDummy(gc);

        System.out.println(java.lang.System.currentTimeMillis());

        gc.startGame();

        System.out.println(java.lang.System.currentTimeMillis());
        gc.commitAction(0, new SumAction(2));
        System.out.println(java.lang.System.currentTimeMillis());
        gc.commitAction(1, new SumAction(3));
        System.out.println(java.lang.System.currentTimeMillis());


        gc.commitAction(0, new SumAction(12));
        System.out.println(java.lang.System.currentTimeMillis());

        gc.commitAction(1, new SumAction(311));
        System.out.println(java.lang.System.currentTimeMillis());

        gc.stopGame();
    }
}
