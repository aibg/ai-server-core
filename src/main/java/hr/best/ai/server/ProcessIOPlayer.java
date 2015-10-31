package hr.best.ai.server;

import java.io.IOException;
import java.util.List;

/**
 * IPlayer which uses system process to communicate with
 */
public class ProcessIOPlayer extends IOPlayer {
    private final Process process;

    private ProcessIOPlayer(Process process)throws IOException {
        super(process.getInputStream(), process.getOutputStream());
        this.process = process;
    }

    public ProcessIOPlayer(String... command) throws IOException{
        this(new ProcessBuilder(command).start());
    }

    public ProcessIOPlayer(List<String> command) throws IOException{
        this(new ProcessBuilder(command).start());
    }

    @Override
    public String getName() {
        return "Process default player";
    }

    @Override
    public void close() throws Exception {
        super.close();
        process.destroy();
    }
}
