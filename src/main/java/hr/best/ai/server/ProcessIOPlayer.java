package hr.best.ai.server;

import com.google.gson.JsonObject;
import hr.best.ai.exceptions.InvalidActionException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * IPlayer which uses system process to communicate with
 */
public class ProcessIOPlayer extends IOPlayer {
    private final Process process;
    private final BufferedReader error;

    private ProcessIOPlayer(Process process, String name) throws IOException {
        super(process.getInputStream(), process.getOutputStream(), name);
        this.process = process;
        error = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8));
    }

    private static Process runProcessHelper(List<String> command) throws IOException{
        Path wd = Paths.get(".").toAbsolutePath().resolve(command.get(0)).normalize();
        if (Files.isExecutable(wd)) {
            command.set(0, wd.toString());
        }

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(wd.getParent().toFile());

        logger.info("Starting player process with command: " + command.toString() + " with working directory: " + pb
                .directory());
        return pb.start();
    }

    public ProcessIOPlayer(List<String> command, String name) throws IOException{
        this(runProcessHelper(command), name);
    }

    public ProcessIOPlayer(List<String> command) throws IOException{
        this(command, "PIoPlayer: " + String.join(" ", command));
    }

    @Override
    public JsonObject signalNewState(JsonObject state) throws IOException, InvalidActionException {
        try {
            return super.signalNewState(state);
        } finally {
            if (error.ready()) {
                StringBuilder sb = new StringBuilder();
                while (error.ready()) {
                    char[] buff = new char[100];
                    sb.append(buff, 0, error.read(buff, 0, 100));
                }
                logger.error(this.getName() + " stderr:\n" + sb.toString());
            }
        }
    }

    @Override
    public void close() throws Exception {
        super.close();
        process.destroy();
    }
}
