package hr.best.ai.demo;

import hr.best.ai.games.GameContextFactory;
import hr.best.ai.games.conway.gamestate.ConwayGameState;
import hr.best.ai.games.conway.RunGame;
import hr.best.ai.games.conway.players.DoNothingPlayerDemo;
import hr.best.ai.games.sum.SumDummyPlayer;
import hr.best.ai.gl.AbstractPlayer;
import hr.best.ai.gl.GameContext;
import hr.best.ai.gl.NewStateObserver;
import hr.best.ai.gl.State;
import hr.best.ai.server.ProcessIOPlayer;
import hr.best.ai.server.SocketIOPlayer;

import java.net.ServerSocket;
import java.util.Arrays;
import java.util.List;

public class DebilniTest {

	final static org.apache.log4j.Logger logger = org.apache.log4j.Logger
			.getLogger(DebilniTest.class);

    public static int port = 5858;

	static void runGameContext(GameContext gc, AbstractPlayer a, AbstractPlayer b, NewStateObserver... observers) throws Exception{
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
			AbstractPlayer p1 = new SocketIOPlayer(socket.accept());
			AbstractPlayer p2 = new SocketIOPlayer(socket.accept());
            runGameContext(GameContextFactory.getSumGameInstance(), p1, p2);
		}
	}

	static void processDummySumGame() throws Exception {
		List<String> player = Arrays.asList("bash", "-c", "while :; do echo '{\"value\":2}'; sleep 1; done");
		AbstractPlayer p1 = new ProcessIOPlayer(player);
		AbstractPlayer p2 = new ProcessIOPlayer(player);
        runGameContext(GameContextFactory.getSumGameInstance(), p1, p2);
	}

    public static void main(String[] args) throws Exception {
	}
}
