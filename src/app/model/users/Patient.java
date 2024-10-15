package app.model.users;

public class Patient extends User {

    private MedicalRecord medicalRecord;

    public Patient(String patientID, String name, String doB, char gender, String bloodType, String email) {
        super(patientID, "password", name, gender);
        medicalRecord = new MedicalRecord(patientID, name, doB, gender, bloodType, email); // TODO duplicating names?
    }

    public String getPatientId() {
        return "P" + medicalRecord.getPatientId();
    }

    public void displayUserMenu() {
        // TODO
    }
}