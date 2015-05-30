import hr.best.ai.GameContext;
import hr.best.ai.IBucket;
import hr.best.ai.games.sum.SumAction;
import hr.best.ai.games.sum.SumState;
import hr.best.ai.gl.State;
import hr.best.ai.server.ClientThread;

public class DebilniTest {
    public static void main(String[] args) {
        State st = new SumState(0);

        GameContext gc = new GameContext(st, () -> {
            return new IBucket() {
                @Override
                public void tick() {
                }

                @Override
                public boolean tok() {
                    return true;
                }

                @Override
                public boolean ok() {
                    return true;
                }
            };
        });

        ClientThread c1 = new ClientThread(gc);
        ClientThread c2 = new ClientThread(gc);

        gc.startGame();

        gc.commitAction(0, new SumAction(2));
        gc.commitAction(1, new SumAction(3));

        gc.commitAction(0, new SumAction(12));
        gc.commitAction(1, new SumAction(311));
    }
}
