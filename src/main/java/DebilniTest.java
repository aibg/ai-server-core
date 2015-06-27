import hr.best.ai.games.sum.SumAction;
import hr.best.ai.games.sum.SumState;
import hr.best.ai.gl.Bucket;
import hr.best.ai.gl.GameContext;
import hr.best.ai.gl.State;
import hr.best.ai.server.ClientThreadDummy;
import hr.best.ai.server.IClient;
import hr.best.ai.server.Server;

public class DebilniTest {

    static void f1() {
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

    static void f2() {
        Server a = new Server(1337);
        a.start();
    }

    public static void main(String[] args) {
        f2();
    }
}
