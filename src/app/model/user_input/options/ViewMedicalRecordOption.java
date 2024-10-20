package app.model.user_input.options;

import app.model.user_input.menus.BaseMenu;
import app.model.user_input.menus.LoginUsernameMenu;
import app.model.user_input.menus.SelectPatientMedicalRecordMenu;
import app.model.user_input.menus.ViewMedicalRecordMenu;
import app.model.users.Patient;
import app.service.UserService;

/**
* Option that points to view patient medical records.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-20
*/
public class ViewMedicalRecordOption extends BaseSelectOption {
    public ViewMedicalRecordOption() {
        super("(view)?( )?medical( )?(record)?");
    }

    @Override
    public String getLabel() {
        return "View Medical Record";
    }

    @Override
    public BaseMenu getNextMenu() {
        return UserService.getCurrentUser() == null ?
            new LoginUsernameMenu() : (
                UserService.getCurrentUser() instanceof Patient ?
                   new ViewMedicalRecordMenu((Patient) UserService.getCurrentUser()) :
                   new SelectPatientMedicalRecordMenu()
            );
    }

    @Override
    public void executeAction() throws Exception {}
}