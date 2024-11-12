package app.view;

import app.view.FunctionalInterfaces.NextAction;

public class Input {

    protected MenuState nextMenuState;
    protected MenuState exitMenuState;
    protected NextAction<Exception> nextAction = (formData) -> formData;
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

    public Input setNextAction(NextAction<Exception> nextAction) {
        this.nextAction = nextAction;
        return this;
    }
}