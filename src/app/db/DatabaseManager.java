package app.db;

import app.model.appointments.Appointment;
import app.model.appointments.AppointmentBuilder;
import app.model.appointments.AppointmentOutcomeBuilder;
import app.model.appointments.AppointmentOutcomeRecord;
import app.model.appointments.DoctorEvent;
import app.model.appointments.DoctorEventBuilder;
import app.model.appointments.Prescription;
import app.model.appointments.PrescriptionBuilder;
import app.model.inventory.Medication;
import app.model.inventory.MedicationBuilder;
import app.model.inventory.MedicationOrder;
import app.model.inventory.MedicationOrderBuilder;
import app.model.users.Patient;
import app.model.users.PatientBuilder;
import app.model.users.User;
import app.model.users.staff.Admin;
import app.model.users.staff.Doctor;
import app.model.users.staff.Pharmacist;
import app.model.users.staff.Staff;
import app.model.users.staff.StaffBuilder;
import app.model.users.staff.StaffBuilder.Role;
import app.service.MedicationService;
import app.service.UserService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class DatabaseManager {

    public static Map<String, Table> tables = new HashMap<>();

    public static Table getTableByConfig(TableConfig tableConfig) {
        return tables.get(tableConfig.getTableName());
    }

    public static void start() throws Exception {

        Table medicationTable = new Table(TableConfig.MEDICATIONS);
        Table userTable = new Table(TableConfig.USERS);
        Table patientTable = new Table(TableConfig.PATIENTS);
        Table staffTable = new Table(TableConfig.STAFF);
        Table doctorTable = new Table(TableConfig.DOCTORS);
        Table pharmacistTable = new Table(TableConfig.PHARMACISTS);
        Table adminTable = new Table(TableConfig.ADMINS);
        Table eventTable = new Table(TableConfig.DOCTOR_EVENTS);
        Table appointmentTable = new Table(TableConfig.APPOINTMENTS);
        Table appointmentOutcomeTable = new Table(TableConfig.APPOINTMENT_OUTCOMES);
        Table prescriptionTable = new Table(TableConfig.PRESCRIPTIONS);
        Table orderTable = new Table(TableConfig.MEDICATION_ORDERS);

        medicationTable.addRelationship(orderTable, 0, 2, DeleteBehavior.CASCADE);
        orderTable.addRelationship(medicationTable, 2, 0, DeleteBehavior.NO_ACTION);

        prescriptionTable.addRelationship(orderTable, 0, 1, DeleteBehavior.CASCADE);
        orderTable.addRelationship(prescriptionTable, 1, 0, DeleteBehavior.CASCADE);

        appointmentOutcomeTable.addRelationship(prescriptionTable, 0, 2, DeleteBehavior.CASCADE);
        prescriptionTable.addRelationship(appointmentOutcomeTable, 2, 0, DeleteBehavior.CASCADE);
        
        appointmentTable.addRelationship(appointmentOutcomeTable, 0, 1, DeleteBehavior.CASCADE);
        appointmentOutcomeTable.addRelationship(appointmentTable, 1, 0, DeleteBehavior.CASCADE);

        patientTable.addRelationship(appointmentTable, 0, 2, DeleteBehavior.CASCADE);
        appointmentTable.addRelationship(patientTable, 2, 0, DeleteBehavior.NO_ACTION);

        doctorTable.addRelationship(appointmentTable, 0, 1, DeleteBehavior.CASCADE);
        appointmentTable.addRelationship(doctorTable, 1, 0, DeleteBehavior.NO_ACTION);

        staffTable.addRelationship(doctorTable, 0, 1, DeleteBehavior.CASCADE);
        doctorTable.addRelationship(staffTable, 1, 0, DeleteBehavior.CASCADE);

        staffTable.addRelationship(pharmacistTable, 0, 1, DeleteBehavior.CASCADE);
        pharmacistTable.addRelationship(staffTable, 1, 0, DeleteBehavior.CASCADE);

        staffTable.addRelationship(adminTable, 0, 1, DeleteBehavior.CASCADE);
        adminTable.addRelationship(staffTable, 1, 0, DeleteBehavior.CASCADE);

        userTable.addRelationship(staffTable, 0, 1, DeleteBehavior.CASCADE);
        staffTable.addRelationship(userTable, 1, 0, DeleteBehavior.CASCADE);

        userTable.addRelationship(patientTable, 0, 1, DeleteBehavior.CASCADE);
        patientTable.addRelationship(userTable, 1, 0, DeleteBehavior.CASCADE);

        doctorTable.addRelationship(eventTable, 0, 1, DeleteBehavior.CASCADE);
        eventTable.addRelationship(doctorTable, 1, 0, DeleteBehavior.NO_ACTION);

        eventTable.addRelationship(appointmentTable, 0, 1, DeleteBehavior.NO_ACTION);
        appointmentTable.addRelationship(eventTable, 1, 0, DeleteBehavior.CASCADE);

        userTable.delRowsWithMissingChildIds(new Table[]{patientTable, staffTable});
        staffTable.delRowsWithMissingParentIds(userTable);
        patientTable.delRowsWithMissingParentIds(userTable);

        staffTable.delRowsWithMissingChildIds(new Table[]{doctorTable, pharmacistTable, adminTable});
        doctorTable.delRowsWithMissingParentIds(staffTable);
        pharmacistTable.delRowsWithMissingParentIds(staffTable);
        adminTable.delRowsWithMissingParentIds(staffTable);

        appointmentTable.delRowsWithMissingParentIds(eventTable);
        appointmentTable.delRowsWithMissingParentIds(patientTable);
        eventTable.delRowsWithMissingParentIds(doctorTable);

        orderTable.delRowsWithMissingParentIds(medicationTable);

        orderTable.delRowsWithMissingParentIds(prescriptionTable); // TODO merge the 2 methods somehow?
        prescriptionTable.delRowsWithMissingChildIds(new Table[]{orderTable});

        prescriptionTable.delRowsWithMissingParentIds(appointmentOutcomeTable); 
        appointmentOutcomeTable.delRowsWithMissingChildIds(new Table[]{prescriptionTable});

        appointmentOutcomeTable.delRowsWithMissingParentIds(appointmentTable);

        tables.put(TableConfig.MEDICATIONS.getTableName(), medicationTable);
        tables.put(TableConfig.USERS.getTableName(), userTable);
        tables.put(TableConfig.PATIENTS.getTableName(), patientTable);
        tables.put(TableConfig.STAFF.getTableName(), staffTable);
        tables.put(TableConfig.DOCTORS.getTableName(), doctorTable);
        tables.put(TableConfig.PHARMACISTS.getTableName(), pharmacistTable);
        tables.put(TableConfig.ADMINS.getTableName(), adminTable);
        tables.put(TableConfig.DOCTOR_EVENTS.getTableName(), eventTable);
        tables.put(TableConfig.APPOINTMENTS.getTableName(), appointmentTable);
        tables.put(TableConfig.APPOINTMENT_OUTCOMES.getTableName(), appointmentOutcomeTable);
        tables.put(TableConfig.PRESCRIPTIONS.getTableName(), prescriptionTable);
        tables.put(TableConfig.MEDICATION_ORDERS.getTableName(), orderTable);

        List<Row> eventRows = eventTable.getRows();

        Map<String, List<Appointment>> patientToApptMap = new HashMap<>();
        Map<String, List<DoctorEvent>> doctorToEventMap = new HashMap<>();

        for (Row eventRow : eventRows) {
            String doctorId = eventRow.getData().get(1);

            Row apptRow = appointmentTable.findByVal(eventRow.getpKey(), 1);

            if (apptRow == null) {
                DoctorEvent doctorEvent = new DoctorEventBuilder(eventRow.getData())
                    .buildInstance()
                    .getInstance();
                doctorToEventMap.computeIfAbsent(doctorId, k -> new ArrayList<>()).add(doctorEvent);
                continue;
            }

            List<String> combinedData = Row.combineData(List.of(eventRow, apptRow));

            String patientId = apptRow.getData().get(2);

            Row apptOutcomeRow = appointmentOutcomeTable.findByVal(apptRow.getpKey(), 1);
            if (apptOutcomeRow == null) {
                Appointment appointment = new AppointmentBuilder(combinedData, null)
                                            .buildInstance()
                                            .getInstance();
                doctorToEventMap.computeIfAbsent(doctorId, k -> new ArrayList<>()).add(appointment);
                patientToApptMap.computeIfAbsent(patientId, k -> new ArrayList<>()).add(appointment); 
                continue;
            }

            String apptOutcomeId = apptOutcomeRow.getpKey();
            Row prescriptionRow = prescriptionTable.findByVal(apptOutcomeId, 2);
            if (prescriptionRow == null) {
                continue;
            } 

            String prescriptionId = prescriptionRow.getpKey();
            List<Row> medicationOrderRows = orderTable.findAllByVal(prescriptionId, 1);

            if (medicationOrderRows.isEmpty()) continue; // TODO 

            try {
                List<MedicationOrder> medicationOrders = new ArrayList<>();
                for (Row medicationRow : medicationOrderRows) {
                    MedicationOrder order = new MedicationOrderBuilder(medicationRow.getData()).buildInstance().getInstance();
                    medicationOrders.add(order);
                }

                Prescription prescription = new PrescriptionBuilder(prescriptionRow.getData(), medicationOrders).buildInstance().getInstance();

                AppointmentOutcomeRecord apptOutcome = new AppointmentOutcomeBuilder(apptOutcomeRow.getData(), prescription).buildInstance().getInstance();

                Appointment appointment = new AppointmentBuilder(combinedData, apptOutcome)
                                                .buildInstance()
                                                .getInstance();

                doctorToEventMap.computeIfAbsent(doctorId, k -> new ArrayList<>()).add(appointment);
                patientToApptMap.computeIfAbsent(patientId, k -> new ArrayList<>()).add(appointment); 
            } catch (Exception e) {
                System.err.println("Error creating Appointment" + e.getMessage());
                e.printStackTrace();
            }
        }

        MedicationService.addMedication(deserializeMedication());
        UserService.addUsers(deserializeUsers(patientToApptMap, doctorToEventMap));
    }

    public static List<Medication> deserializeMedication() throws Exception {
        List<Row> rows = tables.get(TableConfig.MEDICATIONS.getTableName()).getRows();
        List<Medication> medicationList = new ArrayList<>();
        for (Row row : rows) {
            Medication medication = new MedicationBuilder(row.getData())
                .buildInstance()
                .getInstance();
            medicationList.add(medication);
        }
        return medicationList;
    }

    public static List<User> deserializeUsers(
        Map<String, List<Appointment>> patientToApptMap,
        Map<String, List<DoctorEvent>> doctorToEventMap
    ) throws Exception {
        List<User> users = new ArrayList<>();
        List<Row> userRows = tables.get(TableConfig.USERS.getTableName()).getRows();

        for (Row userRow : userRows) {
            try {
                Row patientRow = tables.get(TableConfig.PATIENTS.getTableName()).findByVal(userRow.getpKey(), 1);
                if (patientRow != null) {
                    List<String> combinedData = Row.combineData(List.of(userRow, patientRow));
                    List<Appointment> appointments = patientToApptMap.get(patientRow.getpKey());

                    Patient p = new PatientBuilder(combinedData, appointments).buildInstance().getInstance();
                    users.add(p);

                } else {
                    Row staffRow = tables.get(TableConfig.STAFF.getTableName()).findByVal(userRow.getpKey(), 1);

                    if (staffRow == null) {
                        System.out.println("Error");
                        continue;
                    }

                    Row doctorRow = tables.get(TableConfig.DOCTORS.getTableName()).findByVal(staffRow.getpKey(), 1);
                    if (doctorRow != null) {
                        List<String> combinedData = Row.combineData(List.of(userRow, staffRow, doctorRow));
                        List<DoctorEvent> events = doctorToEventMap.get(doctorRow.getpKey());
                        Staff s = new StaffBuilder(combinedData, Role.DOCTOR, events).buildInstance().getInstance();
                        users.add(s);
                        continue;
                    }

                    Row pharmacistRow = tables.get(TableConfig.PHARMACISTS.getTableName()).findByVal(staffRow.getpKey(), 1);
                    if (pharmacistRow != null) {
                        List<String> combinedData = Row.combineData(List.of(userRow, staffRow, pharmacistRow));
                        Staff s = new StaffBuilder(combinedData, Role.PHARMACIST).buildInstance().getInstance();
                        users.add(s);
                        continue;
                    }

                    Row adminRow = tables.get(TableConfig.ADMINS.getTableName()).findByVal(staffRow.getpKey(), 1);
                    if (adminRow != null) {
                        List<String> combinedData = Row.combineData(List.of(userRow, staffRow, adminRow));
                        Staff s = new StaffBuilder(combinedData, Role.ADMIN).buildInstance().getInstance();
                        users.add(s);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error creating User" + e.getMessage());
                e.printStackTrace();
            }
        }
        return users;
    }

    public static void processRow(Object o, BiConsumer<Table, List<String>> rowProcessor) {
        if (o instanceof Patient p) {
            PatientBuilder pBuilder = new PatientBuilder(p);
            pBuilder.buildRow();
            rowProcessor.accept(getTableByConfig(TableConfig.PATIENTS), pBuilder.getPatientRow());
            rowProcessor.accept(getTableByConfig(TableConfig.USERS), pBuilder.getUserRow());
        } else if (o instanceof Doctor d) {
            StaffBuilder pBuilder = new StaffBuilder(d);
            pBuilder.buildRow();
            rowProcessor.accept(getTableByConfig(TableConfig.DOCTORS), pBuilder.getdeptRow());
            rowProcessor.accept(getTableByConfig(TableConfig.STAFF), pBuilder.getStaffRow());
            rowProcessor.accept(getTableByConfig(TableConfig.USERS), pBuilder.getUserRow());
        } else if (o instanceof Pharmacist p) {
            StaffBuilder pBuilder = new StaffBuilder(p);
            pBuilder.buildRow();
            rowProcessor.accept(getTableByConfig(TableConfig.PHARMACISTS), pBuilder.getdeptRow());
            rowProcessor.accept(getTableByConfig(TableConfig.STAFF), pBuilder.getStaffRow());
            rowProcessor.accept(getTableByConfig(TableConfig.USERS), pBuilder.getUserRow());
        } else if (o instanceof Admin a) {
            StaffBuilder aBuilder = new StaffBuilder(a);
            aBuilder.buildRow();
            rowProcessor.accept(getTableByConfig(TableConfig.ADMINS), aBuilder.getdeptRow());
            rowProcessor.accept(getTableByConfig(TableConfig.STAFF), aBuilder.getStaffRow());
            rowProcessor.accept(getTableByConfig(TableConfig.USERS), aBuilder.getUserRow());
        } else if (o instanceof Appointment a) {
            AppointmentBuilder aBuilder = new AppointmentBuilder(a);
            aBuilder.buildRow();
            rowProcessor.accept(getTableByConfig(TableConfig.DOCTOR_EVENTS), aBuilder.getEventRow());
            rowProcessor.accept(getTableByConfig(TableConfig.APPOINTMENTS), aBuilder.getApptRow());
        } else if (o instanceof DoctorEvent e) {
            DoctorEventBuilder aBuilder = new DoctorEventBuilder(e);
            aBuilder.buildRow();
            rowProcessor.accept(getTableByConfig(TableConfig.DOCTOR_EVENTS), aBuilder.getRow());
        } else if (o instanceof AppointmentOutcomeRecord r) {
            AppointmentOutcomeBuilder rBuilder = new AppointmentOutcomeBuilder(r);
            rBuilder.buildRow();
            rowProcessor.accept(getTableByConfig(TableConfig.APPOINTMENT_OUTCOMES), rBuilder.getRow());
        } else if (o instanceof Prescription p) {
            PrescriptionBuilder pBuilder = new PrescriptionBuilder(p);
            pBuilder.buildRow();
            rowProcessor.accept(getTableByConfig(TableConfig.PRESCRIPTIONS), pBuilder.getRow());
        } else if (o instanceof MedicationOrder x) {
            MedicationOrderBuilder xBuilder = new MedicationOrderBuilder(x);
            xBuilder.buildRow();
            rowProcessor.accept(getTableByConfig(TableConfig.MEDICATION_ORDERS), xBuilder.getRow());
        } else if (o instanceof Medication m) {
            MedicationBuilder mBuilder = new MedicationBuilder(m);
            mBuilder.buildRow();
            rowProcessor.accept(getTableByConfig(TableConfig.MEDICATIONS), mBuilder.getRow());
        }
    }

    public static void add(Object o) {
        processRow(o, Table::addRow);
    }
    
    public static void update(Object o) {
        processRow(o, Table::updateRow);
    }
    

    public static void stop() throws Exception {
        for (Table t : tables.values()) {
            t.writeToCsv();
        }
    }
}
