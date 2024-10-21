package app.model.user_input.menus;

import app.model.user_input.options.BaseOption;
import app.model.user_input.options.ExitOption;
import java.util.List;

/**
* Templates with default option Exit.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public abstract class ExitMenu extends BaseSelectMenu {

    public ExitMenu(
        String title,
        List<BaseOption> options
    ) {
        super(title, options);
        this.addOption(new ExitOption());
    }
}
