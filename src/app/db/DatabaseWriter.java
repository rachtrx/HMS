package app.db;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import app.model.appointments.Appointment;
import app.model.appointments.Appointment.AppointmentStatus;
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
import app.model.users.staff.Staff;
import app.service.AppointmentService;
import app.service.CsvReaderService;
import app.service.MedicationService;
import app.service.UserService;

import java.io.IOException;

public class DatabaseWriter {



    public void load() throws IOException {
        List<List<String>> medicationRows = CsvReaderService.read(FilePath.MEDICATIONS.toString());
        Set<String> medicationIds = new HashSet<>();

        List<Medication> medicationList = deserializeMedication(medicationRows);

    }

    public void run() throws IOException {

        List<User> users = UserService.getAllUsers(); // Assuming UserService is defined
        List<Medication> medications = MedicationService.getAllMedications();
        List<Patient> patients = new ArrayList<>();
        List<Staff> staffs = new ArrayList<>();
        List<Doctor> doctors = new ArrayList<>();
        List<Pharmacist> pharmacists = new ArrayList<>();
        List<Admin> admins = new ArrayList<>();
        List<DoctorEvent> doctorEvents = new ArrayList<>();
        List<Appointment> appointments = new ArrayList<>();
        List<AppointmentOutcomeRecord> appointmentOutcomes = new ArrayList<>();
        List<Prescription> prescriptions = new ArrayList<>();
        List<MedicationOrder> medicationOrders = new ArrayList<>();
        

        for (User user : users) { // Use 'User' instead of 'const User'
            if (user instanceof Patient p) {
                patients.add(p);
            } else if (user instanceof Staff s) {
                if (s instanceof Doctor d) {
                    doctors.add(d);
                    for (DoctorEvent e : d.getDoctorEvents()) {
                        if (e instanceof Appointment a) {
                            if (a.getAppointmentOutcome() != null) {

                                AppointmentOutcomeRecord r = a.getAppointmentOutcome();
                                if (r == null) continue;
                                Prescription p = r.getPrescription();
                                if (p==null) continue;
                                List<MedicationOrder> orders = p.getMedicationOrders();
                                if (orders.isEmpty()) continue;
                                appointments.add(a);
                                appointmentOutcomes.add(r);
                                prescriptions.add(p);
                                medicationOrders.addAll(orders);
                            } else if (a.getAppointmentStatus() == AppointmentStatus.CONFIRMED || a.getAppointmentStatus() == AppointmentStatus.PENDING) {
                                appointments.add(a); // CHECK STATUS
                            }
                        }
                    }
                } else if (user instanceof Pharmacist p) {
                    pharmacists.add(p);
                } else if (user instanceof Admin a) {
                    admins.add(a);
                }
            }
        }

        CsvReaderService.write(FilePath.USERS, serializeUsers(users));
        CsvReaderService.write(FilePath.STAFF, serializeStaff(staff));
        CsvReaderService.write(FilePath.PATIENTS, serializePatients(patients));
        CsvReaderService.write(FilePath.DOCTORS, serializeDoctors(doctors));
        CsvReaderService.write(FilePath.PHARMACISTS, serializePharmacists(pharmacists));
        CsvReaderService.write(FilePath.ADMINS, serializeAdmins(admins));
        CsvReaderService.write(FilePath.DOCTOR_EVENTS, serializeDoctorEvents(doctorEvents));
        CsvReaderService.write(FilePath.APPOINTMENTS, serializeAppointments(appointments));
        CsvReaderService.write(FilePath.APPOINTMENT_OUTCOMES, serializeAppointmentOutcomes(appointmentOutcomes));
        CsvReaderService.write(FilePath.PRESCRIPTIONS, serializePrescriptions(prescriptions));
        CsvReaderService.write(FilePath.MEDICATION_ORDERS, serializeMedicationOrders(medicationOrders));
        CsvReaderService.write(FilePath.MEDICATIONS, serializeMedications(medications));
    }

    public static List<List<String>> serializeUsers(List<User> users) {
        return users.stream()
            .map(User::serialize)
            .collect(Collectors.toList());
    }
    
    public List<List<String>> serializeStaff(List<Staff> staffs) {
        return staffs.stream()
            .map(Staff::serialize)
            .collect(Collectors.toList());

    } 

    public List<List<String>> serializePatients(List<Patient> patients) {
        return patients.stream()
            .map(Patient::serialize)
            .collect(Collectors.toList());
    } 

    public List<List<String>> serializeDoctors(List<Doctor> doctors) {
        return doctors.stream()
            .map(Doctor::serialize)
            .collect(Collectors.toList());
    }

    public List<List<String>> serializePharmacists(List<Pharmacist> pharmacists) {
        return pharmacists.stream()
            .map(Pharmacist::serialize)
            .collect(Collectors.toList());
    }

    public List<List<String>> serializeAdmins(List<Admin> admins) {
        return admins.stream()
            .map(Admin::serialize)
            .collect(Collectors.toList());
    }

    public List<List<String>> serializeDoctorEvents(List<DoctorEvent> events) {
        return events.stream()
            .map(DoctorEvent::serialize)
            .collect(Collectors.toList());
    }

    public List<List<String>> serializeAppointments(List<Appointment> appointments) {
        return appointments.stream()
            .map(Appointment::serialize) // Serialize each appointment
            .collect(Collectors.toList()); // Collect into a List<List<String>>
    }

    public List<List<String>> serializeAppointmentOutcomes(List<AppointmentOutcomeRecord> outcomeRecords) {
        return outcomeRecords.stream()
            .map(AppointmentOutcomeRecord::serialize)
            .collect(Collectors.toList());
    }

    public List<List<String>> serializePrescriptions(List<Prescription> prescriptions) {
        return prescriptions.stream()
            .map(Prescription::serialize)
            .collect(Collectors.toList());
    }

    public List<List<String>> serializeMedicationOrders(List<MedicationOrder> medicationOrders) {
        return medicationOrders.stream()
            .map(MedicationOrder::serialize)
            .collect(Collectors.toList());
    }

    public List<List<String>> serializeMedications(List<Medication> medications) {
        return medications.stream()
            .map(Medication::serialize)
            .collect(Collectors.toList());
    }
}
