package app.model.user_input.menus;

import app.model.user_input.options.BaseSelectOption;
import app.model.user_input.options.SelectPatientMedicalRecordOption;
import app.model.users.Patient;
import app.service.UserService;
import java.util.List;
import java.util.stream.Collectors;

/**
* Select a patient's medical record to view.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-20
*/
public class SelectPatientMedicalRecordMenu extends LoggedInMenu {

    public SelectPatientMedicalRecordMenu() {
        super(
            "Select Patient To View Medical Record",
            SelectPatientMedicalRecordMenu.getPatientOptions()
        );
    }

    private static List<BaseSelectOption> getPatientOptions() {
        return UserService.getAllUserByType(Patient.class)
            .stream()
            .map(patient -> new SelectPatientMedicalRecordOption((Patient)patient))
            .collect(Collectors.toList());
    }
}
