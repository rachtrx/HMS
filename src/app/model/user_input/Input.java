package app.model.user_input;

import java.util.List;
import java.util.Map;

import app.model.user_input.FunctionalInterfaces.NextAction;

public class Input {

    protected MenuState nextMenuState;
    protected MenuState exitMenuState;
    protected NextAction nextAction = (formData) -> formData;
    protected boolean requiresConfirmation;
    protected boolean isEditRedirect;

    public boolean isEditRedirect() {
        return isEditRedirect;
    }

    public Input setEditRedirect(boolean isEditRedirect) {
        this.isEditRedirect = isEditRedirect;
        return this;
    }

    public boolean isRequiresConfirmation() {
        return requiresConfirmation;
    }

    public Input setRequiresConfirmation(boolean requiresConfirmation) {
        this.requiresConfirmation = requiresConfirmation;
        return this;
    }

    public MenuState getNextMenuState() {
        return this.nextMenuState;
    }

    public MenuState getExitMenuState() {
        return this.exitMenuState;
    }

    public NextAction getNextAction() {
        return this.nextAction;
    }

    public Input setNextMenuState(MenuState nextMenuState) {
        this.nextMenuState = nextMenuState;
        return this;
    }

    public Input setExitMenuState(MenuState exitMenuState) {
        this.exitMenuState = exitMenuState;
        return this;
    }

    public Input setNextAction(NextAction nextAction) {
        this.nextAction = nextAction;
        return this;
    }
}