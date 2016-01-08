package hr.best.ai.gl;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class GameContextTest {

    AbstractPlayer p1;
    AbstractPlayer p2;
    NewStateObserver obs;
    List<Runnable> verifyQueue;
    GameContext gc;
    List<State> states;

    @Before
    public void setUp() throws Exception {
        p1 = mock(AbstractPlayer.class);
        p2 = mock(AbstractPlayer.class);
        obs = mock(NewStateObserver.class);

        when(p1.getName()).thenReturn("p1");
        when(p2.getName()).thenReturn("p2");


        verifyQueue = new ArrayList<>();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test(expected = IllegalStateException.class)
    public void testInsufficientPlayers() throws Exception {
        GameContext gc = new GameContext(mock(State.class), 2, 2);
        gc.play();
    }

    @Test(expected = IllegalStateException.class)
    public void testTooMuchPlayers() throws Exception {
        GameContext gc = new GameContext(mock(State.class), 2, 2);
        gc.addPlayer(p1);
        gc.addPlayer(p1);
        gc.addPlayer(p1);
        gc.play();
    }

    @Test
    public void testFinalStatePlay() throws Exception {
        genGameContext(0, p1, p2);

        gc.play();
        verify(p1, never()).signalNewState(any());
        verify(p2, never()).signalNewState(any());
        ordinarilyClosing(p1, p2);
    }

    @Test
    public void oneTurnPlay() throws Exception {
        genGameContext(1, p1, p2);

        gc.play();

        verifyQueue.forEach(x -> x.run());
        ordinarilyClosing(p1, p2);
    }

    @Test
    public void oneTurnWithObserversPlay() throws Exception {
        genGameContext(1, p1, p2);
        gc.addObserver(obs);

        gc.play();

        verifyQueue.forEach(x -> x.run());
        verifyObservetion(
                Arrays.asList(obs),
                states
        );
        ordinarilyClosing(p1, p2);
    }

    @Test
    public void twoTurnPlay() throws Exception {
        genGameContext(3, p1, p2);

        gc.play();

        verifyQueue.forEach(x -> x.run());
        ordinarilyClosing(p1, p2);
    }

    @Test
    public void tenTurnGameWithObserver() throws Exception {
        genGameContext(10, p1, p2);
        gc.addObserver(obs);

        gc.play();

        verifyQueue.forEach(x -> x.run());
        verifyObservetion(
                Arrays.asList(obs),
                states
        );
        ordinarilyClosing(p1, p2);
    }

    private void genGameContext(int numberOfTurns, AbstractPlayer ... players) throws Exception{
        genStates(numberOfTurns, players);
        gc = new GameContext(states.get(0), players.length);
        for (AbstractPlayer player : players)
            gc.addPlayer(player);
    }

    private void genStates(int numberOfTurns, AbstractPlayer ... players) throws Exception {
        states = new ArrayList<>();

        for (int i = 0; i < numberOfTurns; ++i) {
            State state = mock(State.class);
            when(state.isFinal()).thenReturn(false);
            states.add(state);
        }

        State finalState = mock(State.class);
        when(finalState.isFinal()).thenReturn(true);
        states.add(finalState);

        for (int i = 0; i < numberOfTurns; ++i) {
            wiringThing(states.get(i), states.get(i + 1), players);
        }
    }

    private void ordinarilyClosing(AbstractPlayer...players) throws Exception {
        for (AbstractPlayer p : players) {
            verify(p, atLeastOnce()).close();
            verify(p, never()).sendError(any());
            verify(p, atLeast(0)).getName();
            verifyNoMoreInteractions(p);
        }
    }

    private void verifyObservetion(List<NewStateObserver> observers, List<State> states) {
        observers.forEach(observer -> {
                    for (State state : states)
                        verify(observer, times(1)).signalNewState(state);
                }
        );
    }

    @Test
    public void wiringIntegrationTest() throws Exception {
        State state0 = mock(State.class);
        State state1 = mock(State.class);

        wiringThing(state0, state1, p1, p2);
        Action a1 = state0.parseAction(p1.signalNewState(state0.toJSONObjectAsPlayer(0)));
        Action a2 = state0.parseAction(p2.signalNewState(state0.toJSONObjectAsPlayer(1)));

        assertNotNull("p1 actions shouldn't be null", a1);
        assertNotNull("p2 actions shouldn't be null", a2);

        State nextState = state0.nextState(Arrays.asList(a1, a2));
        assertNotNull("next state shouldn't be null", nextState);
        assertTrue("nextState should equal final state", nextState == state1);

        verifyQueue.forEach(x -> x.run());
    }

    private void wiringThing(State current, State next,  AbstractPlayer ... players)
            throws
            Exception{
        List<Action> actions = new ArrayList<>();
        for (int i = 0; i < players.length; ++i) {
            final AbstractPlayer p = players[i];

            // generate unique action
            Action action = genUniqueAction();
            actions.add(action);
            JsonObject jsonState = genUniqueJsonObject();
            JsonObject jsonAction = genUniqueJsonObject();

            when(current.toJSONObjectAsPlayer(i)).thenReturn(jsonState);
            when(p.signalNewState(jsonState)).thenReturn(jsonAction);
            when(current.parseAction(jsonAction)).thenReturn(action);

            final int index = i;
            verifyQueue.add(() -> {
                try {
                    verify(current, times(1)).toJSONObjectAsPlayer(index);
                    verify(p, times(1)).signalNewState(jsonState);
                    verify(current, times(1)).parseAction(jsonAction);
                } catch (Exception e) {
                    fail(e.getMessage());
                    e.printStackTrace();
                }
            });
        }
        when(current.nextState(actions)).thenReturn(next);
        verifyQueue.add(() -> {
            verify(current, times(1)).nextState(actions);
        });
    }

    private Action genUniqueAction() {
        return new Action() {
            @Override
            public boolean equals(Object obj) {
                return this == obj;
            }
        };
    }

    private JsonObject genUniqueJsonObject() {
        JsonObject sol = new JsonObject();
        sol.add("UID", new JsonPrimitive(UUID.randomUUID().toString()));
        return sol;
    }
}
