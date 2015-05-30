package hr.best.ai.games.sum;

import hr.best.ai.gl.Action;

public class SumAction implements Action {
    int value;

    public SumAction(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
