package hr.best.ai.server;

import java.io.IOException;
import java.util.List;

/**
 * IPlayer which uses system process to communicate with
 */
public class ProcessIOPlayer extends IOPlayer {
    private final Process process;

    private ProcessIOPlayer(Process process, String name) throws IOException {
        super(process.getInputStream(), process.getOutputStream(), name);
        this.process = process;
    }

    public ProcessIOPlayer(List<String> command, String name) throws IOException{
        this(new ProcessBuilder(command).start(), name);
    }

    public ProcessIOPlayer(List<String> command) throws IOException{
        this(new ProcessBuilder(command).start(), "PIoPlayer");
    }


    @Override
    public void close() throws Exception {
        super.close();
        process.destroy();
    }
}
