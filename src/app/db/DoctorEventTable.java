package app.db;

import app.model.appointments.DoctorEvent;
import app.service.CsvReaderService;
import app.utils.DateTimeUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DoctorEventTable {

    private static final String filename = "src/resources/Doctor_Event_List.csv";

    public static String getFilename() {
        return filename;
    }

    public static void create(DoctorEvent doctorEvent) {
        List<String> doctorEventStr = new ArrayList<>();

        doctorEventStr.add(String.valueOf(doctorEvent.getId()));
        doctorEventStr.add(String.valueOf(doctorEvent.getDoctorId()));
        doctorEventStr.add(String.valueOf(doctorEvent.getTimeslot()));

        // Convert to a List<List<String>> to represent the CSV rows
        List<List<String>> doctorEventData = new ArrayList<>();
        doctorEventData.add(doctorEventStr);

        try {
            CsvReaderService.write(filename, doctorEventData); // Append new doctorEvent data
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void edit(DoctorEvent doctorEvent) {
        try {
            List<List<String>> allDoctorEvents = CsvReaderService.read(filename);
            List<List<String>> updatedDoctorEvents = new ArrayList<>();

            // Find the doctorEvent to edit by matching the doctorEventId
            for (List<String> doctorEventData : allDoctorEvents) {
                if (doctorEventData.get(0).equals(String.valueOf(doctorEvent.getId()))) {
                    doctorEventData.set(1, String.valueOf(doctorEvent.getDoctorId()));
                    doctorEventData.set(2, DateTimeUtil.printShortDateTime(doctorEvent.getTimeslot())); // Update the patientId
                }
                updatedDoctorEvents.add(doctorEventData);
            }

            CsvReaderService.write(filename, updatedDoctorEvents); // Overwrite with updated data

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void delete(DoctorEvent doctorEvent) {
        try {
            List<List<String>> allDoctorEvents = CsvReaderService.read(filename);
            List<List<String>> updatedDoctorEvents = new ArrayList<>();

            for (List<String> doctorEventData : allDoctorEvents) {
                if (!doctorEventData.get(0).equals(String.valueOf(doctorEvent.getId()))) {
                    updatedDoctorEvents.add(doctorEventData); // Add all except the one with doctorEventId
                }
            }

            CsvReaderService.write(filename, updatedDoctorEvents); // Overwrite with updated data

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
}
