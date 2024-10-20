package app.model.user_input.options;

import app.model.users.Patient;

/**
* Generic option listing each patient's ID and name.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-20
*/
public abstract class PatientNameAndIdOption extends BaseSelectOption {
    private String patientName;
    private int patientId;

    public PatientNameAndIdOption(Patient patient) {
        this(patient.getName(), patient.getPatientId());
    }

    public PatientNameAndIdOption(String patientName, int patientId) {
        super(String.format("%s|[Pp]%d", patientName, patientId));
        this.patientName = patientName;
        this.patientId = patientId;
    }

    public int getPatientId() {
        return patientId;
    }

    @Override
    public final String getLabel() {
        return String.format("%s (ID: P%d)", this.patientName, this.patientId);
    }
}