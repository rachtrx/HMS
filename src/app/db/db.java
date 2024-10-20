package app.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.controller.AppController;
import app.service.CsvReaderService;
import app.model.appointments.Appointment;
import app.model.appointments.AppointmentOutcomeRecord;
import app.model.appointments.DoctorEvent;
import app.model.appointments.Prescription;
import app.model.inventory.Medication;
import app.model.inventory.MedicationOrder;
import app.model.users.Patient;
import app.model.users.User;
import app.model.users.staff.Admin;
import app.model.users.staff.Doctor;
import app.model.users.staff.Pharmacist;

public class db {

    public static final String USER_FILEPATH = "src/resources/User_List.csv";
    public static final String PATIENT_FILEPATH = "src/resources/Patient_List.csv";
    public static final String STAFF_FILEPATH = "src/resources/Staff_List.csv";
    public static final String DOCTOR_FILEPATH = "src/resources/Lookup_Doctor.csv";
    public static final String PHARMACIST_FILEPATH = "src/resources/Lookup_Pharmacist.csv";
    public static final String ADMIN_FILEPATH = "src/resources/Lookup_Admin.csv";
    public static final String APPOINTMENT_FILEPATH = "src/resources/Appointment_List.csv";
    public static final String OUTCOME_FILEPATH = "src/resources/Appointment_Outcome_List.csv";
    public static final String ORDER_FILEPATH = "src/resources/Order_List.csv";
    public static final String MEDICATION_FILEPATH = "src/resources/Medication_List.csv";

    public static void init() throws Exception {

        CsvReaderService csvReaderService = AppController.getCsvReaderService();

        // Load Medication, key on ID
        List<List<String>> rawMedication = csvReaderService.read(MedicationTable.getFilename());
        List<Medication> medicationList = new ArrayList<>();

        for (List<String> row : rawMedication) {
            Medication medication = new Medication(row);
            medicationList.add(medication);
        }

        // Load Orders, Initialise, create order map, key on prescription ID?
        List<List<String>> rawOrders = csvReaderService.read(OrderTable.getFilename());
        List<MedicationOrder> medicationOrderList = new ArrayList<>();
        Map<String, List<MedicationOrder>> prescriptionToOrderMap = new HashMap<>();

        for (List<String> row : rawOrders) {
            MedicationOrder medicationOrder = new MedicationOrder(row); // Assuming MedicationOrder constructor takes row data
            medicationOrderList.add(medicationOrder);
            prescriptionToOrderMap.computeIfAbsent(String.valueOf(medicationOrder.getPrescriptionId()), k -> new ArrayList<>()).add(medicationOrder);
        }

        // Load Prescriptions, Initialise from orders, create prescription map, key on ID
        List<List<String>> rawPrescriptions = csvReaderService.read(PrescriptionTable.getFilename());
        
        List<Prescription> prescriptionList = new ArrayList<>();
        Map<String, Prescription> prescriptionMap = new HashMap<>();

        for (List<String> prescriptionRow : rawPrescriptions) {

            if (prescriptionToOrderMap.get(prescriptionRow.get(0)) != null) {
                // Proceed only if prescriptionToOrderMap.get(prescriptionRow.get(0)) is not null
                Prescription prescription = new Prescription(prescriptionRow, prescriptionToOrderMap.get(prescriptionRow.get(0)));
                prescriptionList.add(prescription);
                prescriptionMap.put(String.valueOf(prescription.getId()), prescription);
            }
        }

        System.out.println("Prescriptions added");

        // Load Appointments, key on eventId
        List<List<String>> rawAppointments = csvReaderService.read(AppointmentTable.getFilename());

        Map<String, List<String>> eventToAppointmentMap = new HashMap<>();

        for (List<String> row : rawAppointments) {
            eventToAppointmentMap.put(row.get(1), row); // map for eventId
            
        }

        // Load Doctor Events, try to join with Appointments. Initialise appointments and busy events. Create Appointment map, key on appointmentId
        List<List<String>> rawDoctorEvents = csvReaderService.read(DoctorEventTable.getFilename());
        List<Appointment> appointmentList = new ArrayList<>(); // List od all events
        Map<String, List<DoctorEvent>> doctorToEventMap = new HashMap<>(); // doctorId: List<DoctorEvent>
        Map<String, List<Appointment>> patientToAppointmentMap = new HashMap<>(); // patientId: List<Appointment>
        Map<String, Appointment> appointmentMap = new HashMap<>();
        
        for (List<String> doctorEventRow : rawDoctorEvents) {
            System.out.println(doctorEventRow);
            if (eventToAppointmentMap.get(doctorEventRow.get(0)) != null) {
                // Event is linked to an appointment
                List<String> appointmentRow = eventToAppointmentMap.get(doctorEventRow.get(0));
                Appointment appointment = new Appointment(appointmentRow, doctorEventRow);
                
                appointmentList.add(appointment); // IMPT: add to the appointment list
                appointmentMap.put(String.valueOf(appointment.getAppointmentId()), appointment);
        
                doctorToEventMap.computeIfAbsent(String.valueOf(appointment.getDoctorId()), k -> new ArrayList<>()).add(appointment);
                patientToAppointmentMap.computeIfAbsent(String.valueOf(appointment.getPatientId()), k -> new ArrayList<>()).add(appointment);
            } else {
                DoctorEvent doctorEvent = new DoctorEvent(doctorEventRow);
                doctorToEventMap.computeIfAbsent(String.valueOf(doctorEvent.getDoctorId()), k -> new ArrayList<>()).add(doctorEvent);
            }
        }

        // Load Appointment Outcomes. Initialise with PrescriptionIds and appointmentIds
        List<List<String>> rawOutcomes = csvReaderService.read(AppointmentOutcomeTable.getFilename());
        
        List<AppointmentOutcomeRecord> outcomeList = new ArrayList<>();
        Map<String, List<AppointmentOutcomeRecord>> patientToOutcomeMap = new HashMap<>();

        for (List<String> outcomeRow : rawOutcomes) {
            // System.out.println(outcomeRow);
            Appointment appointment = appointmentMap.get(outcomeRow.get(1));
            Prescription prescription = prescriptionMap.get(outcomeRow.get(2));
            if (appointment != null && prescription != null) {
                AppointmentOutcomeRecord outcomeRecord = new AppointmentOutcomeRecord(
                    outcomeRow,
                    appointment,
                    prescription 
                );
                
                outcomeList.add(outcomeRecord);
                int patientId = outcomeRecord.getAppointment().getPatientId();
                patientToOutcomeMap.computeIfAbsent(String.valueOf(patientId), k -> new ArrayList<>()).add(outcomeRecord);
            }
        }
        
        // Load Patients
        // Load Doctor
        // Load Pharmacist
        // Load Admin
        // Load Staff

        // Create mappings and initialise
        List<List<String>> rawPatients = csvReaderService.read(PatientTable.getFilename());
        Map<String, List<String>> patientMap = new HashMap<>();
        for (List<String> patientRow : rawPatients) {
            patientMap.put(patientRow.get(1), patientRow);
        }

        List<List<String>> rawDoctors = csvReaderService.read(DoctorTable.getFilename());
        Map<String, List<String>> doctorsMap = new HashMap<>();
        for (List<String> doctorRow : rawDoctors) {
            doctorsMap.put(doctorRow.get(1), doctorRow);
        }
        List<List<String>> rawPharmacists = csvReaderService.read(PharmacistTable.getFilename());
        Map<String, List<String>> pharmacistsMap = new HashMap<>();
        for (List<String> pharmacistRow : rawPharmacists) {
            pharmacistsMap.put(pharmacistRow.get(1), pharmacistRow);
        }
        List<List<String>> rawAdmins = csvReaderService.read(AdminTable.getFilename());
        Map<String, List<String>> adminsMap = new HashMap<>();
        for (List<String> adminRow : rawAdmins) {
            adminsMap.put(adminRow.get(1), adminRow);
        }

        // Initialise by looping on rawStaff
        List<List<String>> rawStaff = csvReaderService.read(StaffTable.getFilename());
        Map<String, List<String>> staffMap = new HashMap<>();
        for (List<String> staffRow : rawStaff) {
            staffMap.put(staffRow.get(1), staffRow);
        }

        List<List<String>> rawUsers = csvReaderService.read(UserTable.getFilename());

        List<User> usersList = new ArrayList<>(); // List to store the Patient objects
        
        for (List<String> userRow : rawUsers) {
            // check for patient
            if (patientMap.get(userRow.get(0)) != null) {
                List<String> patientRow = patientMap.get(userRow.get(0));
                Patient patient; // Declare the patient variable
                
                if (patientToOutcomeMap.get(patientRow.get(0)) == null && patientToAppointmentMap.get(patientRow.get(0)) == null) { // Check if patientId has no outcomes and no appointments
                    patient = new Patient(patientRow, userRow, new ArrayList<>(), new ArrayList<>()); // Create Patient with empty lists
                } else if (patientToOutcomeMap.get(patientRow.get(0)) == null) { // Check if patientId has no outcomes but has appointments
                    patient = new Patient(patientRow, userRow, patientToAppointmentMap.get(patientRow.get(0)), new ArrayList<>()); // Create Patient with appointments and empty outcome list
                } else {
                    // Patient has both appointments and outcomes
                    patient = new Patient(patientRow, userRow, patientToAppointmentMap.get(patientRow.get(0)), patientToOutcomeMap.get(patientRow.get(0))); // Create Patient with appointments and outcomes
                }
                
                // Add the newly created patient to the patients list
                usersList.add(patient);
            } else if (staffMap.get(userRow.get(0)) == null) {
                System.err.println("Missing information for" + userRow.get(1));
            } else {
                List<String> staffRow = staffMap.get(userRow.get(0));
                if (doctorsMap.get(staffRow.get(0)) != null) { // check staff id in doctor
                    List<String> doctorRow = doctorsMap.get(staffRow.get(0));
                    if (doctorToEventMap.get(doctorRow.get(0)) != null) { // with events
                        List<DoctorEvent> doctorEvents = doctorToEventMap.get(doctorRow.get(0));
                        Doctor doctor = new Doctor(doctorRow, staffRow, userRow, doctorEvents);
                        usersList.add(doctor);
                    } else { // no events
                        Doctor doctor = new Doctor(doctorRow, staffRow, userRow, new ArrayList<>());
                        usersList.add(doctor);
                    }
                } else if (pharmacistsMap.get(staffRow.get(0)) != null) {
                    List<String> pharmacistRow = pharmacistsMap.get(staffRow.get(0));
                    Pharmacist pharmacist = new Pharmacist(pharmacistRow, staffRow, userRow);
                    usersList.add(pharmacist);
                } else if (adminsMap.get(staffRow.get(0)) != null) {
                    List<String> adminRow = adminsMap.get(staffRow.get(0));
                    Admin admin = new Admin(adminRow, staffRow, userRow);
                    usersList.add(admin);
                }
            }
        }

        // Print prescription list
        // Print users list
        System.out.println("\nMedication List:");
        for (Medication med : medicationList) {
            System.out.println(med);
        }
        // Print users list
        System.out.println("\nOrder List:");
        for (MedicationOrder order : medicationOrderList) {
            System.out.println(order);
        }

        // Print users list
        System.out.println("\nAppointment List:");
        for (Appointment appointment : appointmentList) {
            System.out.println(appointment);
        }

        System.out.println("\nOutcome List:");
        for (AppointmentOutcomeRecord outcome : outcomeList) {
            System.out.println(outcome);
        }

        // Print users list
        System.out.println("\nUsers List:");
        for (User user : usersList) {
            System.out.println(user);
        }

        System.out.println("Prescriptions:");
        for (Prescription prescription : prescriptionList) {
            System.out.println(prescription);
        }
    }
}
