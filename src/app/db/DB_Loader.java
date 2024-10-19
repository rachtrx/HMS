package app.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.db.utils.CsvReaderService;
import app.model.appointments.Appointment;
import app.model.appointments.AppointmentOutcomeRecord;
import app.model.appointments.DoctorEvent;
import app.model.appointments.Prescription;
import app.model.inventory.Medication;
import app.model.inventory.MedicationOrder;
import app.model.inventory.OrderTable;
import app.model.users.Patient;
import app.model.users.User;
import app.model.users.staff.Admin;
import app.model.users.staff.Doctor;
import app.model.users.staff.Pharmacist;

public class DB_Loader {
    public void start() throws Exception {
        // Load Medication, key on ID
        List<List<String>> rawMedication = CsvReaderService.read(MedicationTable.getFilename());
        List<Medication> medicationList = new ArrayList<>();
        Map<String, Medication> medicationMap = new HashMap<>();

        for (List<String> row : rawMedication) {
            Medication medication = new Medication(row);
            medicationList.add(medication);
            medicationMap.put(medication.getId(), medication); // Add to the map by reference
        }

        // Load Orders, Initialise, create order map, key on prescription ID?
        List<List<String>> rawOrders = CsvReaderService.read(OrderTable.getFilename());
        List<MedicationOrder> medicationOrderList = new ArrayList<>();
        Map<String, MedicationOrder> medicationOrderMap = new HashMap<>();

        for (List<String> row : rawOrders) {
            MedicationOrder medicationOrder = new MedicationOrder(row); // Assuming MedicationOrder constructor takes row data
            medicationOrderList.add(medicationOrder);
            medicationOrderMap.put(String.valueOf(medicationOrder.getId()), medicationOrder); // Add to the map by reference
        }

        // Load Prescriptions, Initialise from orders, create prescription map, key on ID
        List<List<String>> rawPrescriptions = CsvReaderService.read(PrescriptionTable.getFilename());
        
        List<Prescription> prescriptionList = new ArrayList<>();
        Map<String, Prescription> prescriptionMap = new HashMap<>();

        for (List<String> row : rawPrescriptions) {
            List<MedicationOrder> indivMedicationOrderList = new ArrayList<>();
            for (MedicationOrder medicationOrder : medicationOrderList) {
                if (medicationOrder.getPrescriptionId() == Integer.parseInt(row.get(0))) {
                    indivMedicationOrderList.add(medicationOrder);
                }
            }
            Prescription prescription = new Prescription(row, indivMedicationOrderList); // Assuming MedicationOrder constructor takes row data
            prescriptionList.add(prescription);
            prescriptionMap.put(String.valueOf(prescription.getId()), prescription);
        }

        // Load Appointments, key on eventId
        List<List<String>> rawAppointments = CsvReaderService.read(AppointmentTable.getFilename());

        Map<String, List<String>> eventToAppointmentMap = new HashMap<>();

        for (List<String> row : rawAppointments) {
            eventToAppointmentMap.put(row.get(3), row); // map for eventId
        }

         // Load Doctor Events, try to join with Appointments. Initialise appointments and busy events. Create Appointment map, key on appointmentId

        List<List<String>> rawDoctorEvents = CsvReaderService.read(AppointmentTable.getFilename());
        List<Appointment> appointmentList = new ArrayList<>(); // List od all events
        Map<String, List<DoctorEvent>> doctorToEventMap = new HashMap<>(); // doctorId: Event
        Map<String, List<Appointment>> patientToAppointmentMap = new HashMap<>(); // patientId: Event
        Map<String, Appointment> appointmentMap = new HashMap<>();

        for (List<String> doctorEventRow : rawDoctorEvents) {
            if (eventToAppointmentMap.get(doctorEventRow.get(0)) != null) {
                List<String> appointmentRow = eventToAppointmentMap.get(doctorEventRow.get(0));
                Appointment appointment = new Appointment(appointmentRow, doctorEventRow);
                appointmentList.add(appointment); // IMPT
                appointmentMap.put(appointment.getAppointmentId(), appointment);
                doctorToEventMap.put(appointment.getDoctorId(), appointment);
                patientToAppointmentMap.add(appointment.getPatientId(), appointment);
            } else {
                DoctorEvent doctorEvent = new DoctorEvent(doctorEventRow);
                doctorToEventMap.put(doctorEvent.getDoctorId(), doctorEvent);
            }
        }

        // Load Appointment Outcomes. Initialise with PrescriptionIds and appointmentIds
        List<List<String>> rawOutcomes = CsvReaderService.read(PrescriptionTable.getFilename());
        
        List<AppointmentOutcomeRecord> outcomeList = new ArrayList<>();
        Map<String, List<AppointmentOutcomeRecord>> patientToOutcomeMap = new HashMap<>();

        for (List<String> outcomeRow : rawOutcomes) {
            if (appointmentMap.get(outcomeRow.get(1)) != null && prescriptionMap.get(outcomeRow.get(2)) != null) {
                AppointmentOutcomeRecord outcomeRecord = new AppointmentOutcomeRecord(
                    outcomeRow, 
                    appointmentMap.get(outcomeRow.get(1)), 
                    prescriptionMap.get(outcomeRow.get(2))
                );
                
                outcomeList.add(outcomeRecord);
                int patientId = outcomeRecord.getAppointment().getPatientId();
                List<AppointmentOutcomeRecord> outcomeRecords = patientToOutcomeMap.getOrDefault(patientId, new ArrayList<>());
                outcomeRecords.add(outcomeRecord);
                patientToOutcomeMap.put(String.valueOf(patientId), outcomeRecords);
            }
        }

        // Load Users
        // Create Users map, key on User ID
        List<List<String>> rawUsers = CsvReaderService.read(UserTable.getFilename());
        
        
        Map<String, List<String>> userMap = new HashMap<>();

        for (List<String> userRow : rawUsers) {
            userMap.put(userRow.get(0), userRow);
        }

        // Load Patients
        List<List<String>> rawPatients = CsvReaderService.read(PatientTable.getFilename());

        // Initialise Patients with user IDs
        List<Patient> patientsList = new ArrayList<>(); // List to store the Patient objects
        for (List<String> patientRow : rawPatients) {
            if (userMap.get(patientRow.get(1)) != null) { // Check if userId exists in userMap
                List<String> userRow = userMap.get(patientRow.get(1)); // Get corresponding userRow
                
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
                patientsList.add(patient);
            }
        }

        // Load Staff

        // Load Doctor and initialise with staff id
        // Load Pharmacist and initialise with staff id
        // Load Admin and initialise with staff id
        
        List<List<String>> rawDoctors = CsvReaderService.read(DoctorTable.getFilename());
        Map<String, List<String>> doctorsMap = new HashMap<>();
        for (List<String> doctorRow : rawDoctors) {
            doctorsMap.put(doctorRow.get(0), doctorRow);
        }
        List<List<String>> rawPharmacists = CsvReaderService.read(PharmacistTable.getFilename());
        Map<String, List<String>> pharmacistsMap = new HashMap<>();
        for (List<String> pharmacistRow : rawPharmacists) {
            pharmacistsMap.put(pharmacistRow.get(0), pharmacistRow);
        }
        List<List<String>> rawAdmins = CsvReaderService.read(AdminTable.getFilename());
        Map<String, List<String>> adminsMap = new HashMap<>();
        for (List<String> adminRow : rawAdmins) {
            adminsMap.put(adminRow.get(0), adminRow);
        }

        // Initialise by looping on rawStaff
        List<List<String>> rawStaff = CsvReaderService.read(StaffTable.getFilename());

        for (List<String> staffRow : rawStaff) {
            if (userMap.get(staffRow.get(1)) != null) { // Check if userId exists in userMap
                List<String> userRow = userMap.get(staffRow.get(1)); // Get corresponding userRow
                if (doctorsMap.get(staffRow.get(0)) != null) {// check staff id in doctor
                    List<String> doctorRow = doctorsMap.get(staffRow.get(0));
                    if (doctorToEventMap.get(doctorRow.get(0)) != null) { // with events
                        List<DoctorEvent> doctorEvents = doctorToEventMap.get(doctorRow.get(0));
                        Doctor doctor = new Doctor(doctorRow, staffRow, userRow, doctorEvents);
                    } else { // no events
                        Doctor doctor = new Doctor(doctorRow, staffRow, userRow, new ArrayList<>());
                    }
                    
                } else if (pharmacistsMap.get(staffRow.get(0)) != null) {
                    List<String> pharmacistRow = pharmacistsMap.get(staffRow.get(0));
                    Pharmacist pharmacist = new Pharmacist(pharmacistRow, staffRow, userRow);
                } else if (adminsMap.get(staffRow.get(0)) != null) {
                    List<String> adminRow = adminsMap.get(staffRow.get(0));
                    Admin admin = new Admin(adminRow, staffRow, userRow);
                }
            }
        }
        

        

        

        List<User> userList = new ArrayList<>();
        
    }
}
