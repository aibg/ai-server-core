package hr.best.ai.demo;

import hr.best.ai.gl.GameContext;
import hr.best.ai.gl.NewStateObserver;
import hr.best.ai.gl.State;

import javax.swing.*;
import java.awt.*;

/**
 * For demo purposes how would someone implement game dependent components.
 */
public class JGameStateLabel extends JTextArea implements NewStateObserver {

    @Override
    public void signalNewState(State state) {
        SwingUtilities.invokeLater(() -> this.setText(state.toJSONObject().toString()));
    }

    @Override
    public void signalCompleted(String message) {
        SwingUtilities.invokeLater(() -> this.setText(message));
    }

    @Override
    public void close() throws Exception {
    }
}
