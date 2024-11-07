package app.model.users;

import app.model.Builder;
import app.model.appointments.Appointment;
import java.util.List;

public class PatientBuilder extends Builder<Patient> {
    private List<String> userRow;
    private List<String> patientRow;
    private List<Appointment> appointments;

    public PatientBuilder(List<String> row, List<Appointment> appointments) throws Exception {
        super(row);
        this.appointments = appointments;
    }

    public PatientBuilder(Patient instance) {
        super(instance);
    }
    
    public List<String> getUserRow() {
        return this.userRow;
    }

    public List<String> getPatientRow() {
        return this.patientRow;
    }

    public void setSubRows() {
        this.userRow = this.getSubRow(0, 5);
        this.patientRow = this.getSubRow(5, this.row.size());
    }
    
    @Override
    public Patient deserialize() throws Exception {
        return new Patient(userRow, patientRow, appointments);
    }
}
