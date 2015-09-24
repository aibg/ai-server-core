import hr.best.ai.games.GameContextFactory;
import hr.best.ai.games.sum.SumDummyPlayer;
import hr.best.ai.gl.GameContext;
import hr.best.ai.gl.IPlayer;
import hr.best.ai.server.IOPlayer;
import hr.best.ai.server.ProcessIOPlayer;
import hr.best.ai.server.SocketIOPlayer;

import java.io.IOException;
import java.net.ServerSocket;

public class DebilniTest {

    static void runTestGame(IPlayer a, IPlayer b) {
        GameContext gc = GameContextFactory.getSumGameInstance();
        gc.registerPlayer(a);
        gc.registerPlayer(b);
        try {
            gc.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void f1() {
        runTestGame(new SumDummyPlayer(1, "+1"), new SumDummyPlayer(2, "+2"));
    }

    static void  f2() throws IOException {
        int port = 5858;
        try (ServerSocket socket = new ServerSocket(port, 50, null)) {
            IPlayer p1 = new SocketIOPlayer(socket.accept());
            IPlayer p2 = new SocketIOPlayer(socket.accept());
            runTestGame(p1, p2);
        }
    }

    static void f3() throws IOException {
        ProcessBuilder player = new ProcessBuilder("python", "/tmp/a.py");
        IPlayer p1 = new ProcessIOPlayer(player.start());
        IPlayer p2 = new ProcessIOPlayer(player.start());
        GameContext gc = GameContextFactory.getSumGameInstance();
        runTestGame(p1,p2);
    }

    public static void main(String[] args) throws IOException{
        f3();
    }
}
