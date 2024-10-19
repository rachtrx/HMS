package app.model.user_input.options;

import app.model.user_input.menus.BaseMenu;

/**
* Menu option. (Equivalent to transition + actions in FSM)
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public interface BaseOption {
    String getLabel();

    BaseMenu getNextMenu();

    void executeAction() throws Exception;
}