import hr.best.ai.games.GameContextFactory;
import hr.best.ai.games.sum.SumDummyPlayer;
import hr.best.ai.gl.GameContext;
import hr.best.ai.gl.IPlayer;
import hr.best.ai.server.ClientThread;

import java.io.IOException;
import java.net.ServerSocket;

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

    static void  f2() throws IOException {
        int port = 5858;
        try (ServerSocket socket = new ServerSocket(port, 50, null)) {
            IPlayer p1 = new ClientThread(socket.accept());
            IPlayer p2 = new ClientThread(socket.accept());
            GameContext gc = GameContextFactory.getSumGameInstance();
            gc.registerPlayer(p1);
            gc.registerPlayer(p2);
            try {
                gc.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException{
        f2();
    }
}
