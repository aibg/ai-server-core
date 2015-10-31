package hr.best.ai.server;

import java.io.IOException;
import java.net.Socket;

/**
 * IPlayer which communicates over TCP/IP socket
 */
public class SocketIOPlayer extends IOPlayer {

    private final Socket socket;
    public SocketIOPlayer(Socket socket) throws IOException {
        super(socket.getInputStream(), socket.getOutputStream());
        this.socket = socket;
    }

    @Override
    public String getName() {
        return "Socket player[" + socket.getInetAddress().toString() + ":" + socket.getPort() + "]";
    }

    @Override
    public void close() throws Exception{
        super.close();
        this.socket.close();
    }

}
