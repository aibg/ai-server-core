package hr.best.ai.server;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Server implementation who is running the whole show
 */
public class Server {
    private final int port;
    private ServerThread serverThread;
    final static Logger logger = Logger.getLogger(Server.class);

    public Server(int port) {
        this.port = port;
    }

    public synchronized void start() {
        if (serverThread == null) {
            serverThread = new ServerThread();
            serverThread.start();
            logger.info("Server Started");
        }
    }

    public synchronized void stop() {
        if (serverThread != null) {
            serverThread.shutdown();
            serverThread = null;
            logger.info("Server Stopped");
        }
    }

    protected class ServerThread extends Thread {

        private volatile boolean alive = true;

        public void shutdown() {
            alive = false;
        }

        @Override
        public void run() {
            try (ServerSocket socket = new ServerSocket(port, 50, null)) {
                socket.setSoTimeout(5000);
                while (alive) {
                    try {
                        Socket client = socket.accept();
                        ConnectionHandler th = new ConnectionHandler(client);
                        new Thread(th).start();
                    } catch (SocketTimeoutException ignorable) {
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
