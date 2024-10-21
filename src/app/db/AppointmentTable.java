package app.db;

import app.model.appointments.Appointment;
import app.service.CsvReaderService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AppointmentTable {

    private static final String filename = "src/resources/Appointment_List.csv";

    public static String getFilename() {
        return filename;
    }

    public static void create(Appointment appointment) {
        List<String> appointmentStr = new ArrayList<>();

        appointmentStr.add(String.valueOf(appointment.getAppointmentId()));
        appointmentStr.add(String.valueOf(appointment.getId())); // doctor event id
        appointmentStr.add(String.valueOf(appointment.getPatientId()));
        appointmentStr.add(String.valueOf(appointment.getAppointmentStatus().toString()));

        // Convert to a List<List<String>> to represent the CSV rows
        List<List<String>> appointmentData = new ArrayList<>();
        appointmentData.add(appointmentStr);

        try {
            CsvReaderService.write(filename, appointmentData); // Append new appointment data
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void edit(Appointment appointment) {
        try {
            List<List<String>> allAppointments = CsvReaderService.read(filename);
            List<List<String>> updatedAppointments = new ArrayList<>();

            // Find the appointment to edit by matching the appointmentId
            for (List<String> appointmentData : allAppointments) {
                if (appointmentData.get(0).equals(String.valueOf(appointment.getAppointmentId()))) {
                    appointmentData.set(1, String.valueOf(appointment.getId()));
                    appointmentData.set(2, String.valueOf(appointment.getPatientId())); // Update the patientId
                    appointmentData.set(3, String.valueOf(appointment.getAppointmentStatus().toString()));
                }
                updatedAppointments.add(appointmentData);
            }

            CsvReaderService.write(filename, updatedAppointments); // Overwrite with updated data

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void delete(Appointment appointment) {
        try {
            List<List<String>> allAppointments = CsvReaderService.read(filename);
            List<List<String>> updatedAppointments = new ArrayList<>();

            for (List<String> appointmentData : allAppointments) {
                if (!appointmentData.get(0).equals(String.valueOf(appointment.getAppointmentId()))) {
                    updatedAppointments.add(appointmentData); // Add all except the one with appointmentId
                }
            }

            CsvReaderService.write(filename, updatedAppointments); // Overwrite with updated data

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
}