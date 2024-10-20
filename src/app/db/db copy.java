package app.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

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
        Set<String> medicationIds = new HashSet<>();

        for (List<String> row : rawMedication) {
            Medication medication = new Medication(row);
            medicationList.add(medication);
            medicationIds.add(row.get(0));
        }

        // Load Patients
        // Load Doctor
        // Load Pharmacist
        // Load Admin
        // Load Staff

        // Map<String, String> patientToUserMap = new HashMap<>();
        // Map<String, String> staffToUserMap = new HashMap<>();
        // Map<String, String> doctorToStaffMap = new HashMap<>();
        // Map<String, String> pharmacistToStaffMap = new HashMap<>();
        // Map<String, String> adminToStaffMap = new HashMap<>();

        
        Map<String, List<String>> userMap = new HashMap<>();
        Map<String, List<String>> patientsMap = new HashMap<>();
        Map<String, List<String>> staffMap = new HashMap<>();
        Map<String, List<String>> doctorsMap = new HashMap<>();
        Map<String, List<String>> pharmacistsMap = new HashMap<>();
        Map<String, List<String>> adminsMap = new HashMap<>();

        List<List<String>> rawUsers = csvReaderService.read(UserTable.getFilename());
        for (List<String> userRow : rawUsers) {
            userMap.put(userRow.get(0), userRow);
        }
        
        // Create mappings and initialise
        List<List<String>> rawPatients = csvReaderService.read(PatientTable.getFilename());
        
        for (List<String> patientRow : rawPatients) {
            
            if (userMap.get(patientRow.get(1)) != null) {
                patientsMap.put(patientRow.get(1), patientRow);
            }
            else System.out.println("Patient ID" + patientRow.get(0) + "is not found in User List");
        }

        // Initialise by looping on rawStaff
        List<List<String>> rawStaff = csvReaderService.read(StaffTable.getFilename());
        
        for (List<String> staffRow : rawStaff) {
            staffMap.put(staffRow.get(1), staffRow);
        }

        List<List<String>> rawDoctors = csvReaderService.read(DoctorTable.getFilename());
       
        for (List<String> doctorRow : rawDoctors) {
            if (staffMap.get(doctorRow.get(1)) != null) {
                List<String> staffRow = staffMap.get(doctorRow.get(1));
                if (userMap.get(staffRow.get(1)) != null) {
                    doctorsMap.put(doctorRow.get(1), doctorRow);
                } else System.out.println("Doctor ID" + doctorRow.get(0) + "is not found in User List");
            } else System.out.println("Doctor ID" + doctorRow.get(0) + "is not found in Staff List");
        }
        List<List<String>> rawPharmacists = csvReaderService.read(PharmacistTable.getFilename());
        
        for (List<String> pharmacistRow : rawPharmacists) {
            if (staffMap.get(pharmacistRow.get(1)) != null) {
                List<String> staffRow = staffMap.get(pharmacistRow.get(1));
                if (userMap.get(staffRow.get(1)) != null) {
                    pharmacistsMap.put(pharmacistRow.get(1), pharmacistRow);
                } else System.out.println("Pharmacist ID" + pharmacistRow.get(0) + "is not found in User List");
            } else System.out.println("Pharmacist ID" + pharmacistRow.get(0) + "is not found in Staff List");
        }
        List<List<String>> rawAdmins = csvReaderService.read(AdminTable.getFilename());
        
        for (List<String> adminRow : rawAdmins) {
            if (staffMap.get(adminRow.get(1)) != null) {
                List<String> staffRow = staffMap.get(adminRow.get(1));
                if (userMap.get(staffRow.get(1)) != null) {
                    adminsMap.put(adminRow.get(1), adminRow);
                } else System.out.println("Admin ID" + adminRow.get(0) + "is not found in User List");
            } else System.out.println("Admin ID" + adminRow.get(0) + "is not found in Staff List");
        }

        // Load Appointments, key on eventId
        List<List<String>> rawAppointments = csvReaderService.read(AppointmentTable.getFilename());
        List<List<String>> rawDoctorEvents = csvReaderService.read(DoctorEventTable.getFilename());
        Map<String, List<String>> eventToAppointmentMap = new HashMap<>();
        Map<String, List<String>> appointmentToEventMap = new HashMap<>();
        Map<String, List<String>> eventMap = new HashMap<>();
        Map<String, List<String>> appointmentMap = new HashMap<>();
        Map<String, List<String>> doctorToEventMap = new HashMap<>(); // doctorId: List<String>
        Map<String, List<String>> patientToAppointmentMap = new HashMap<>(); // patientId: List<String>

        for (List<String> row : rawAppointments) {
            eventToAppointmentMap.put(row.get(1), row); // map for eventId   
        }

        // Load Doctor Events, try to join with Appointments. Initialise appointments and busy events. Create Appointment map, key on appointmentId
        
        for (List<String> doctorEventRow : rawDoctorEvents) {
            System.out.println(doctorEventRow);
            if (doctorsMap.get(doctorEventRow.get(1)) == null) {
                System.out.println("Event ID" + doctorEventRow.get(0) + "with Doctor ID" + doctorEventRow.get(1) + "did not match with any doctor");
            } else {
                if (eventToAppointmentMap.get(doctorEventRow.get(0)) != null) {
                    // Event is linked to an appointment
                    List<String> appointmentRow = eventToAppointmentMap.get(doctorEventRow.get(0));
                    if (patientsMap.get(appointmentRow.get(2)) == null) {
                        System.out.println("Appointment ID" + appointmentRow.get(0) + "with Patient ID" + doctorEventRow.get(2) + "did not match with any patient");
                    }

                    appointmentMap.put(appointmentRow.get(0), appointmentRow);
                    patientToAppointmentMap.computeIfAbsent(String.valueOf(appointmentRow.get(2)), k -> new ArrayList<>()).add(appointmentRow.get(0));
                    appointmentToEventMap.put(appointmentRow.get(0), doctorEventRow);
                }

                // Add for doctor
                eventMap.put(doctorEventRow.get(0), doctorEventRow); // map for eventId   
                doctorToEventMap.computeIfAbsent(String.valueOf(doctorEventRow.get(1)), k -> new ArrayList<>()).add(doctorEventRow.get(0));
            }
        }
        // At this point, all doctor events have a dpctpr and patient

        // Load Appointment Outcomes. Initialise with PrescriptionIds and appointmentIds
        List<List<String>> rawOutcomes = csvReaderService.read(AppointmentOutcomeTable.getFilename());
        
        Map<String, List<String>> outcomeMap = new HashMap<>();
        Map<String, String> appointmentToOutcomeMap = new HashMap<>();

        for (List<String> outcomeRow : rawOutcomes) {
            // System.out.println(outcomeRow);
            List<String> appointmentRow = appointmentMap.get(outcomeRow.get(1));
            if (appointmentRow == null) System.out.println("Missing Appointment for Outcome ID" + outcomeRow.get(0));
            else {
                if (!appointmentRow.get(3).equals(Appointment.AppointmentStatus.COMPLETED.toString())) {
                    System.out.println("Outcome ID" + outcomeRow.get(0) + "has uncompleted Appointment ID" + appointmentRow.get(0));
                } else {
                    outcomeMap.put(outcomeRow.get(0), outcomeRow);
                    appointmentToOutcomeMap.put(appointmentRow.get(0), outcomeRow.get(0));
                }
            }
        }
        // At this point, for each outcome theres is a completed validated appointment.

        // Load Orders, Initialise, create order map, key on prescription ID?
        List<List<String>> rawOrders = csvReaderService.read(OrderTable.getFilename());
        Map<String, List<List<String>>> prescriptionToOrderMap = new HashMap<>();
        Map<String, List<String>> prescriptionMap = new HashMap<>();
        Map<String, List<String>> outcomeToPrescriptionMap = new HashMap<>();

        for (List<String> row : rawOrders) {
            prescriptionToOrderMap.computeIfAbsent(row.get(1), k -> new ArrayList<>()).add(row);
        }

        // Load Prescriptions, Ensure each prescription has a appointmentoutcome and orders
        List<List<String>> rawPrescriptions = csvReaderService.read(PrescriptionTable.getFilename());
        
        

        for (List<String> prescriptionRow : rawPrescriptions) {

            if (prescriptionToOrderMap.get(prescriptionRow.get(0)) == null) {
                // Proceed only if prescriptionToOrderMap.get(prescriptionRow.get(0)) is not null
                System.out.println("Missing Orders for Prescription ID" + prescriptionRow.get(0));
            } else if (outcomeMap.get(prescriptionRow.get(2)) == null) {
                System.out.println("Missing Outcome for Prescription ID" + prescriptionRow.get(0));
            } else {
                prescriptionMap.put(String.valueOf(prescriptionRow.get(0)), prescriptionRow);
                outcomeToPrescriptionMap.put(String.valueOf(prescriptionRow.get(2)), prescriptionRow);
            }
        }
        System.out.println("Prescriptions loaded");

        // START LOADING
        Map<String, Appointment> createdAppointmentMap = new HashMap<>();
        List<Appointment> appointmentList = new ArrayList<>(); // List of all events
        List<User> usersList = new ArrayList<>();


        patientsMap.forEach((patientId, patientRow) -> {

            List<String> userRow = userMap.get(patientRow.get(1));

            List<String> appointmentIds = patientToAppointmentMap.get(patientId);

            List<Appointment> indivAppointmentList = new ArrayList<>(); 

            List<AppointmentOutcomeRecord> appointmentRecords = new ArrayList<>();

            for (String appointmentId : appointmentIds) {
                List<String> appointmentRow = appointmentMap.get(appointmentId);
                List<String> doctorEventRow = appointmentToEventMap.get(appointmentId);

                if (appointmentRow == null) {
                    System.out.println("Unknown Error: Could not find appointment ID" + appointmentId);
                    continue;
                }

                String outcomeId = appointmentToOutcomeMap.get(appointmentId);
                // APPOINTMENT FOUND
                if (appointmentToOutcomeMap.get(appointmentId) != null) { 
                    // Build the other required fields

                    List<String> prescriptionRow = outcomeToPrescriptionMap.get(outcomeId);
                    if (prescriptionRow == null) {
                        System.out.println("No prescription found for Outcome ID" + outcomeId);
                        continue;
                    }
                    List<List<String>> orderRows = prescriptionToOrderMap.get(prescriptionRow.get(0));
                    if (orderRows == null){
                        System.out.println("No order found for Outcome ID" + outcomeId + "and prescription ID" + prescriptionRow.get(0));
                        continue;
                    }

                    List<MedicationOrder> medicationOrders = new ArrayList<>();
                    for (List<String> orderRow : orderRows) {
                        if(!medicationIds.contains(orderRow.get(2))) {
                            System.out.println("No medication found for Outcome ID" + outcomeId + ", prescription ID" + prescriptionRow.get(0) + " and order Id" + orderRow.get(0));
                            continue;
                        }

                        MedicationOrder medicationOrder = new MedicationOrder(orderRow);
                        medicationOrders.add(medicationOrder);
                    }

                    if (medicationOrders.isEmpty()) {
                        System.out.println("Medication orders for" + outcomeId + ", prescription ID" + prescriptionRow.get(0) + " could not be initialised.");
                        continue;
                    }

                    try {
                        Prescription prescription = new Prescription(prescriptionRow, medicationOrders);
                        Appointment appointment = new Appointment(appointmentRow, doctorEventRow);
                        AppointmentOutcomeRecord appointmentOutcomeRecord = new AppointmentOutcomeRecord(patientRow, appointment, prescription);

                        appointmentList.add(appointment);
                        createdAppointmentMap.put(String.valueOf(appointment.getAppointmentId()), appointment);
                        appointmentRecords.add(appointmentOutcomeRecord);
                    } catch (Exception e) {
                        System.out.println("Failed to load Appointment ID " + appointmentId);
                    }
                } else {
                    if (appointmentRow.get(3).equals(Appointment.AppointmentStatus.COMPLETED.toString())) System.out.println("Cannot initialise completed appointment ID" + appointmentId + "without an outcome record");

                    else {
                        try {
                            Appointment appointment = new Appointment(appointmentRow, doctorEventRow);
                            indivAppointmentList.add(appointment);
                            appointmentList.add(appointment);
                            createdAppointmentMap.put(String.valueOf(appointment.getAppointmentId()), appointment);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }
                try {
                    Patient patient = new Patient(patientRow, userRow, appointmentList, appointmentRecords);
                    usersList.add(patient);
                } catch (Exception e) {
                    System.out.println("Failed to initialise Patient ID" + patientId + ". This can have issues as appointments, outcomes, prescriptions and orders may have been initialised.");
                }
            }
        });

        doctorsMap.forEach((doctorId, doctorRow) -> {

            List<DoctorEvent> indivEventList = new ArrayList<>(); 
            
            List<String> eventIds = doctorToEventMap.get(doctorId);
            for (String eventId : eventIds) {
                List<String> doctorEventRow = eventMap.get(eventId);
                List<String> appointmentRow = eventToAppointmentMap.get(eventId);
                if (appointmentRow != null) {
                    Appointment appointment = createdAppointmentMap.get(appointmentRow.get(0));
                    if (appointment != null) indivEventList.add(appointment); // may not have init properly in patient 
                } else {
                    try {
                        DoctorEvent doctorEvent = new DoctorEvent(doctorEventRow);
                        indivEventList.add(doctorEvent);
                    } catch (Exception e) {
                        System.out.println("There was an error setting up Doctor Event ID " + eventId);
                    }
                    
                }
            }
            List<String> staffRow = staffMap.get(doctorRow.get(1));
            List<String> userRow = userMap.get(staffRow.get(1));
            try {
                Doctor doctor = new Doctor(doctorRow, staffRow, userRow, indivEventList);
                usersList.add(doctor);
            } catch (Exception e) {
                System.out.println("Failed to initialise Doctor ID" + doctorId + ". This can have issues as appointments have been added to users.");
            }
            
        });

        pharmacistsMap.forEach((pharmacistId, pharmacistRow) -> {
            List<String> staffRow = staffMap.get(pharmacistRow.get(1));
            List<String> userRow = userMap.get(staffRow.get(1));
            try {
                Pharmacist pharmacist = new Pharmacist(pharmacistRow, staffRow, userRow);
                usersList.add(pharmacist);
            } catch (Exception e) {
                System.out.println("Failed to initialise Pharmacist ID" + pharmacistId);
            }
        });

        adminsMap.forEach((adminId, adminRow) -> {
            List<String> staffRow = staffMap.get(adminRow.get(1));
            List<String> userRow = userMap.get(staffRow.get(1));
            try {
                Admin admin = new Admin(adminRow, staffRow, userRow);
                usersList.add(admin);
            } catch (Exception e) {
                System.out.println("Failed to initialise Admin ID" + adminId);
            }
        });

        // Print prescription list
        // Print users list
        System.out.println("\nMedication List:");
        for (Medication med : medicationList) {
            System.out.println(med);
        }

        // Print users list
        System.out.println("\nAppointment List:");
        for (Appointment appointment : appointmentList) {
            System.out.println(appointment);
        }

        // Print users list
        System.out.println("\nUsers List:");
        for (User user : usersList) {
            System.out.println(user);
        }
    }
}
