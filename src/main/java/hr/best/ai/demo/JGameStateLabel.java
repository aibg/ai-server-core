package hr.best.ai.demo;

import com.google.gson.JsonObject;
import hr.best.ai.gl.NewStateObserver;
import hr.best.ai.gl.State;

import javax.swing.*;

/**
 * For demo purposes how would someone implement game dependent components.
 */
public class JGameStateLabel extends JTextArea implements NewStateObserver {

    @Override
    public void signalNewState(State state) {
        SwingUtilities.invokeLater(() -> this.setText(state.toJSONObject().toString()));
    }

    @Override
    public void signalError(JsonObject message) {
        SwingUtilities.invokeLater(() -> this.setText(message.toString()));
    }

    @Override
    public void close() throws Exception {
    }
}
