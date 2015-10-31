package hr.best.ai.demo;

import hr.best.ai.games.GameContextFactory;
import hr.best.ai.games.conway.ConwayGameStateBuilder;
import hr.best.ai.games.conway.ConwayGameStateConstants;
import hr.best.ai.games.conway.players.DoNothingPlayerDemo;
import hr.best.ai.games.conway.players.GrowerDemo;
import hr.best.ai.games.conway.visualization.GameBar;
import hr.best.ai.games.conway.visualization.GameGrid;
import hr.best.ai.games.sum.SumDummyPlayer;
import hr.best.ai.gl.GameContext;
import hr.best.ai.gl.IPlayer;
import hr.best.ai.gl.NewStateObserver;
import hr.best.ai.gl.State;
import hr.best.ai.server.ProcessIOPlayer;
import hr.best.ai.server.SocketIOPlayer;

import javax.swing.*;

import java.awt.BorderLayout;
import java.io.IOException;
import java.net.ServerSocket;

public class DebilniTest {

	final static org.apache.log4j.Logger logger = org.apache.log4j.Logger
			.getLogger(DebilniTest.class);

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
	
	static void runConwayTestGame(IPlayer a, IPlayer b, NewStateObserver... observers) {
		GameContext gc = GameContextFactory.getConwayGameInstance();
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

	static void f2() throws IOException {
		int port = 5858;
		try (ServerSocket socket = new ServerSocket(port, 50, null)) {
			IPlayer p1 = new SocketIOPlayer(socket.accept());
			IPlayer p2 = new SocketIOPlayer(socket.accept());
			runTestGame(p1, p2);
		}
	}

	static void f3() throws IOException {
		ProcessBuilder player = new ProcessBuilder("bash", "-c",
				"while :; do echo '{\"value\":2}'; sleep 1; done");
		IPlayer p1 = new ProcessIOPlayer(player.start());
		IPlayer p2 = new ProcessIOPlayer(player.start());
		runTestGame(p1, p2);
	}

	static void f4() throws IOException {
		ProcessBuilder player = new ProcessBuilder("bash", "-c",
				"while :; do echo '{\"value\":2}'; sleep 1; done");
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
		runTestGame(p1, p2, a);
	}

	public static void f5() throws Exception {
		JGameStateLabel textArea = new JGameStateLabel();
		SwingUtilities.invokeAndWait(() -> {
			JFrame f = new JFrame("Demo");
			f.add(textArea);
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			f.setSize(250, 250);
			f.setVisible(true);
		});

		ProcessBuilder player = new ProcessBuilder("bash", "-c",
				"while :; do echo '{\"value\":1}'; sleep 1; done");
		IPlayer p1 = new ProcessIOPlayer(player.start());
		IPlayer p2 = new ProcessIOPlayer(player.start());
		runTestGame(p1, p2, textArea);
	}

	public static void f6() throws Exception {
		GameBar bar = new GameBar();
		GameGrid grid = new GameGrid();

		SwingUtilities.invokeAndWait(() -> {
			JFrame f = new JFrame("DemoConway");
			
			f.setExtendedState(JFrame.MAXIMIZED_BOTH); 
			f.getContentPane().add(bar,BorderLayout.NORTH);
			f.getContentPane().add(grid,BorderLayout.CENTER);
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			f.setVisible(true);
		});

		
		
		IPlayer p1 = new GrowerDemo();
		IPlayer p2 = new GrowerDemo();
		runConwayTestGame(p1, p2, bar, grid);
	}

    public static void f7() throws Exception {
        GameBar bar = new GameBar();
        GameGrid grid = new GameGrid();

        SwingUtilities.invokeAndWait(() -> {
            JFrame f = new JFrame("DemoConway");

            f.setExtendedState(JFrame.MAXIMIZED_BOTH);
            f.getContentPane().add(bar,BorderLayout.NORTH);
            f.getContentPane().add(grid,BorderLayout.CENTER);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            f.setVisible(true);
        });

        State st = ConwayGameStateBuilder.newConwayGameStateBuilder(12, 12)
                .setFromEmpty(GameContextFactory.Ruleset1::fromEmpty)
                .setFromOccupied(GameContextFactory.Ruleset1::fromOccupied)
                // P1 Oscilator
                .setCell(2, 1, ConwayGameStateConstants.PLAYER1_CELL)
                .setCell(3, 1, ConwayGameStateConstants.PLAYER1_CELL)
                .setCell(4, 1, ConwayGameStateConstants.PLAYER1_CELL)
                .setCell(1, 2, ConwayGameStateConstants.PLAYER1_CELL)
                .setCell(2, 2, ConwayGameStateConstants.PLAYER1_CELL)
                .setCell(3, 2, ConwayGameStateConstants.PLAYER1_CELL)
                // P2 Oscilator
                .setCell(7 ,  7, ConwayGameStateConstants.PLAYER2_CELL)
                .setCell(7 ,  8, ConwayGameStateConstants.PLAYER2_CELL)
                .setCell(8 ,  7, ConwayGameStateConstants.PLAYER2_CELL)
                .setCell(8 ,  8, ConwayGameStateConstants.PLAYER2_CELL)
                .setCell(9 ,  9, ConwayGameStateConstants.PLAYER2_CELL)
                .setCell(9 , 10, ConwayGameStateConstants.PLAYER2_CELL)
                .setCell(10,  9, ConwayGameStateConstants.PLAYER2_CELL)
                .setCell(10, 10, ConwayGameStateConstants.PLAYER2_CELL)
                .getState();

        GameContext gc = new GameContext(st, 2);
        gc.addPlayer(new DoNothingPlayerDemo());
        gc.addPlayer(new DoNothingPlayerDemo());
        gc.addObserver(bar);
        gc.addObserver(grid);
        gc.play();
    }

    public static void f8() throws Exception {
        GameBar bar = new GameBar();
        GameGrid grid = new GameGrid();

        SwingUtilities.invokeAndWait(() -> {
            JFrame f = new JFrame("DemoConway");

            f.setExtendedState(JFrame.MAXIMIZED_BOTH);
            f.getContentPane().add(bar,BorderLayout.NORTH);
            f.getContentPane().add(grid,BorderLayout.CENTER);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            f.setVisible(true);
        });

        State st = ConwayGameStateBuilder.newConwayGameStateBuilder(12, 12)
                .setFromEmpty(GameContextFactory.Ruleset1::fromEmpty)
                .setFromOccupied(GameContextFactory.Ruleset1::fromOccupied)
                        // P1 Oscilator
                .setCell(2, 1, ConwayGameStateConstants.PLAYER1_CELL)
                .setCell(3, 1, ConwayGameStateConstants.PLAYER1_CELL)
                .setCell(4, 1, ConwayGameStateConstants.PLAYER1_CELL)
                .setCell(1, 2, ConwayGameStateConstants.PLAYER1_CELL)
                .setCell(2, 2, ConwayGameStateConstants.PLAYER1_CELL)
                .setCell(3, 2, ConwayGameStateConstants.PLAYER1_CELL)
                        // P2 Oscilator
                .setCell(7 ,  7, ConwayGameStateConstants.PLAYER2_CELL)
                .setCell(7 ,  8, ConwayGameStateConstants.PLAYER2_CELL)
                .setCell(8 ,  7, ConwayGameStateConstants.PLAYER2_CELL)
                .setCell(8 ,  8, ConwayGameStateConstants.PLAYER2_CELL)
                .setCell(9 ,  9, ConwayGameStateConstants.PLAYER2_CELL)
                .setCell(9 , 10, ConwayGameStateConstants.PLAYER2_CELL)
                .setCell(10,  9, ConwayGameStateConstants.PLAYER2_CELL)
                .setCell(10, 10, ConwayGameStateConstants.PLAYER2_CELL)
                .getState();

        int port = 5858;
        ServerSocket socket = new ServerSocket(port, 50, null);
        GameContext gc = new GameContext(st, 2);
        gc.addPlayer(new SocketIOPlayer(socket.accept()));
        gc.addPlayer(new DoNothingPlayerDemo());
        gc.addObserver(bar);
        gc.addObserver(grid);
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
