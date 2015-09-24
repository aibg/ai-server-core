package hr.best.ai.server;

import java.io.IOException;

/**
 * IPlayer which uses system process to communicate with
 */
public class ProcessIOPlayer extends IOPlayer {
    private final Process process;

    public ProcessIOPlayer(Process process)throws IOException {
        super(process.getInputStream(), process.getOutputStream());
        this.process = process;
    }

    @Override
    public void close() throws Exception {
        super.close();
        process.destroy();
    }
}
