import hr.best.ai.games.GameContextFactory;
import hr.best.ai.games.sum.SumDummyPlayer;
import hr.best.ai.gl.GameContext;
import hr.best.ai.gl.IPlayer;
import hr.best.ai.gl.NewStateObserver;
import hr.best.ai.gl.State;
import hr.best.ai.server.ProcessIOPlayer;
import hr.best.ai.server.SocketIOPlayer;

import java.io.IOException;
import java.net.ServerSocket;

public class DebilniTest {

    final static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(DebilniTest.class);

    static void runTestGame(IPlayer a, IPlayer b, NewStateObserver... observers) {
        GameContext gc = GameContextFactory.getSumGameInstance();
        gc.addPlayer(a);
        gc.addPlayer(b);
        for (NewStateObserver observer : observers) {
            gc.addObserver(observer);
        }
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
        ProcessBuilder player = new ProcessBuilder("bash", "-c", "while :; do echo '{\"value\":2}'; sleep 1; done");
        IPlayer p1 = new ProcessIOPlayer(player.start());
        IPlayer p2 = new ProcessIOPlayer(player.start());
        runTestGame(p1,p2);
    }

    static void f4() throws IOException {
        ProcessBuilder player = new ProcessBuilder("bash", "-c", "while :; do echo '{\"value\":2}'; sleep 1; done");
        IPlayer p1 = new ProcessIOPlayer(player.start());
        IPlayer p2 = new ProcessIOPlayer(player.start());
        NewStateObserver a = new NewStateObserver() {
            @Override
            public void signalNewState(State state) {
                logger.info(state.toJSONObject());
            }

            @Override
            public void signalCompleted(String message) {
                logger.info(message);
            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public void close() throws Exception {
            }
        };
        runTestGame(p1,p2, a);
    }

    public static void main(String[] args) throws IOException{
        f4();
    }
}
