package app.model.user_input.menus;

import app.model.user_input.options.BaseOption;
import app.model.user_input.options.LogoutOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
* Templates with default option Exit.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-20
*/
public abstract class LoggedInMenu extends ExitMenu {

    public LoggedInMenu(
        String title,
        List<BaseOption> options
    ) {
        super(title, options);
        this.addOptionsAtStart(new ArrayList<>(Arrays.asList(new LogoutOption())));
    }
}
