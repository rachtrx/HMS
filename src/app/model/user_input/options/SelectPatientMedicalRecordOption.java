package app.model.user_input.options;

import app.model.user_input.menus.BaseMenu;
import app.model.user_input.menus.ViewMedicalRecordMenu;
import app.model.users.Patient;

/**
* Option that allows for selecting patient's ID.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-20
*/
public class SelectPatientMedicalRecordOption extends PatientNameAndIdOption {
    public SelectPatientMedicalRecordOption(Patient patient) {
        super(patient.getName(), patient.getPatientId());
    }

    public SelectPatientMedicalRecordOption(String patientName, int patientId) {
        super(patientName, patientId);
    }

    @Override
    public BaseMenu getNextMenu() {
        return new ViewMedicalRecordMenu(this.getPatientId());
    }

    @Override
    public void executeAction() throws Exception {}
}