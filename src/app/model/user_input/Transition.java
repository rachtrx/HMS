import app.model.user_input.FunctionalInterfaces.NextAction;

public class Transition {

    private final NextAction nextAction;
    private final MenuState nextState;
    private final MenuState exitState;

    public Transition(NextAction nextAction, MenuState nextState, MenuState exitState) {
        this.nextAction = nextAction;
        this.nextState = nextState;
        this.exitState = exitState;
    }

    public MenuState getExitState() {
        return exitState;
    }

    public MenuState getNextState() {
        return nextState;
    }

    public NextAction getNextAction() {
        return nextAction;
    }
}
