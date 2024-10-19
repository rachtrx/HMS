package app.model.user_input.menus;
/**
* Menu shown to users. (Equivalent to state in FSM)
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public abstract class BaseInputMenu extends BaseMenu {

    public BaseInputMenu(String title) {
        super(title);
    }

    @Override
    public void display() {
        System.out.println(this.getTitle());
    };
}