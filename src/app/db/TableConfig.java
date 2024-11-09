package app.db;

import java.util.List;

public enum TableConfig {
    USERS(
        List.of("User ID", "Username", "Password", "Name", "Gender"),
        "Users",
        "src/resources/User_List.csv",
        0
    ),
    PATIENTS(
        List.of("Patient ID", "User ID", "Mobile", "Home", "Contact Information", "Date of Birth", "Blood Type"),
        "Patients",
        "src/resources/Patient_List.csv",
        0
    ),
    STAFF(
        List.of("Staff ID", "User ID", "Age"),
        "Staff",
        "src/resources/Staff_List.csv",
        0
    ),
    DOCTORS(
        List.of("ID", "Staff ID"),
        "Doctors",
        "src/resources/Doctor_List.csv",
        0
    ),
    PHARMACISTS(
        List.of("ID", "Staff ID"),
        "Pharmacists",
        "src/resources/Pharmacist_List.csv",
        0
    ),
    ADMINS(
        List.of("ID", "Staff ID"),
        "Admins",
        "src/resources/Admin_List.csv",
        0
    ),
    DOCTOR_EVENTS(
        List.of("ID", "Doctor ID", "Timeslot"),
        "DoctorEvents",
        "src/resources/Doctor_Event_List.csv",
        0
    ),
    APPOINTMENTS(
        List.of("ID", "Doctor Event ID", "Patient ID", "Status"),
        "Appointments",
        "src/resources/Appointment_List.csv",
        0
    ),
    APPOINTMENT_OUTCOMES(
        List.of("ID", "Appointment ID", "Service Type", "Notes"),
        "AppointmentOutcomes",
        "src/resources/Appointment_Outcome_List.csv",
        0
    ),
    PRESCRIPTIONS(
        List.of("Prescription ID", "Prescription Status", "Outcome ID"),
        "Prescriptions",
        "src/resources/Prescription_List.csv",
        0
    ),
    MEDICATION_ORDERS(
        List.of("ID", "Prescription ID", "Medication ID", "Quantity"),
        "MedicationOrders",
        "src/resources/Order_List.csv",
        0
    ),
    REQUESTS(
        List.of("ID", "Medication ID", "Count", "Status"),
        "Requests",
        "src/resources/Request_List.csv",
        0
    ),
    MEDICATIONS(
        List.of("ID", "Medicine Name", "Initial Stock", "Low Stock Level Alert"),
        "Medications",
        "src/resources/Medication_List.csv",
        0
    );

    private final List<String> headers;
    private final String tableName;
    private final String filePath;
    private final int pKeyCol;

    TableConfig(List<String> headers, String tableName, String filePath, int pKeyCol) {
        this.headers = headers;
        this.tableName = tableName;
        this.filePath = filePath;
        this.pKeyCol = pKeyCol;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public String getTableName() {
        return tableName;
    }

    public String getFilePath() {
        return filePath;
    }

    public int getPKeyCol() {
        return pKeyCol;
    }
}

