package app.model.user_input;

import java.util.List;
import java.util.Map;

import app.model.user_input.FunctionalInterfaces.NextAction;
import app.model.user_input.FunctionalInterfaces.MenuGenerator;

public class Field {

    protected MenuState nextMenuState;
    protected MenuState exitMenuState;
    protected NextAction nextAction = (input, args) -> args;
    protected boolean requiresConfirmation;
    protected boolean isEditRedirect;

    public boolean isEditRedirect() {
        return isEditRedirect;
    }

    public void setEditRedirect(boolean isEditRedirect) {
        this.isEditRedirect = isEditRedirect;
    }

    public boolean isRequiresConfirmation() {
        return requiresConfirmation;
    }

    public void setRequiresConfirmation(boolean requiresConfirmation) {
        this.requiresConfirmation = requiresConfirmation;
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

    public final Field setNextMenuState(MenuState nextMenuState) {
        this.nextMenuState = nextMenuState;
        return this;
    }

    public final Field setExitMenuState(MenuState exitMenuState) {
        this.exitMenuState = exitMenuState;
        return this;
    }

    public Field setNextAction(NextAction nextAction) {
        this.nextAction = nextAction;
        return this;
    }
}