package hr.best.ai.demo;

import hr.best.ai.games.GameContextFactory;
import hr.best.ai.games.conway.RunGame;
import hr.best.ai.games.conway.players.DoNothingPlayerDemo;
import hr.best.ai.games.sum.SumDummyPlayer;
import hr.best.ai.gl.GameContext;
import hr.best.ai.gl.IPlayer;
import hr.best.ai.gl.NewStateObserver;
import hr.best.ai.gl.State;
import hr.best.ai.server.ProcessIOPlayer;
import hr.best.ai.server.SocketIOPlayer;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;

public class DebilniTest {

	final static org.apache.log4j.Logger logger = org.apache.log4j.Logger
			.getLogger(DebilniTest.class);

    public static int port = 5858;

	static void runGameContext(GameContext gc, IPlayer a, IPlayer b, NewStateObserver... observers) throws Exception{
		gc.addPlayer(a);
		gc.addPlayer(b);
        Arrays.asList(observers).stream().forEach(gc::addObserver);
		gc.play();
	}

	static void simpleDummySumGame() throws Exception {
        runGameContext(GameContextFactory.getSumGameInstance(), new SumDummyPlayer(1, "+1"), new SumDummyPlayer(2, "+2"));
	}

	static void socketDummySumGame() throws Exception {
		int port = 5858;
		try (ServerSocket socket = new ServerSocket(port, 50, null)) {
			IPlayer p1 = new SocketIOPlayer(socket.accept());
			IPlayer p2 = new SocketIOPlayer(socket.accept());
            runGameContext(GameContextFactory.getSumGameInstance(), p1, p2);
		}
	}

	static void processDummySumGame() throws Exception {
		ProcessBuilder player = new ProcessBuilder("bash", "-c",
				"while :; do echo '{\"value\":2}'; sleep 1; done");
		IPlayer p1 = new ProcessIOPlayer(player.start());
		IPlayer p2 = new ProcessIOPlayer(player.start());
        runGameContext(GameContextFactory.getSumGameInstance(), p1, p2);
	}

    public static void f7() throws Exception {
        State st = GameContextFactory.demoState();
        GameContext gc = new GameContext(st, 2);
        gc.addPlayer(new DoNothingPlayerDemo());
        gc.addPlayer(new DoNothingPlayerDemo());
        RunGame.addVizualization(gc);
        gc.play();
    }

    public static void f8() throws Exception {
        State st = GameContextFactory.demoState();
        ServerSocket socket = new ServerSocket(port, 50, null);
        GameContext gc = new GameContext(st, 2);
        gc.addPlayer(new SocketIOPlayer(socket.accept()));
        gc.addPlayer(new DoNothingPlayerDemo());
        RunGame.addVizualization(gc);
        gc.play();
    }

    public static void f9() throws Exception{
        GameContext gc = new GameContext(GameContextFactory.demoState(), 2);
        gc.addPlayer(new ProcessIOPlayer(new ProcessBuilder
                ("python3", "/home/lpp/Documents/BEST/AI/python-bindings/main.py").start
                ()));
        gc.addPlayer(new DoNothingPlayerDemo());
        gc.play();
    }

    public static void main(String[] args) throws Exception {
		f9();
	}
}
