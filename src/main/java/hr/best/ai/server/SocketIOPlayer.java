package hr.best.ai.server;

import java.io.IOException;
import java.net.Socket;

/**
 * IPlayer which communicates over TCP/IP socket
 */
public class SocketIOPlayer extends IOPlayer {

    private final Socket socket;
    public SocketIOPlayer(Socket socket, String name) throws IOException {
        super(socket.getInputStream(), socket.getOutputStream(), name);
        this.socket = socket;
    }

    public SocketIOPlayer(Socket socket) throws IOException {
        this(socket, String.format("%s:%d", socket.getInetAddress().toString(), socket.getPort()));
    }

    @Override
    public void close() throws Exception{
        super.close();
        this.socket.close();
    }

}
