package app.db;

import app.controller.AppController;
import app.model.inventory.MedicationOrder;
import app.service.CsvReaderService;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

public class OrderTable {

    private static CsvReaderService csvReaderService = AppController.getCsvReaderService();

    private static final String filename = "src/resources/Order_List.csv";

    public static String getFilename() {
        return filename;
    }

    // Create a new MedicationOrder record
    public static void create(MedicationOrder order) {
        List<String> orderStr = new ArrayList<>();

        orderStr.add(String.valueOf(order.getId()));
        orderStr.add(String.valueOf(order.getMedicationId()));
        orderStr.add(String.valueOf(order.getPrescriptionId()));
        orderStr.add(String.valueOf(order.getQuantity()));

        // Convert to a List<List<String>> to represent the CSV rows
        List<List<String>> orderData = new ArrayList<>();
        orderData.add(orderStr);

        try {
            csvReaderService.write(filename, orderData); // Append new order data
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Edit an existing MedicationOrder record
    public static void edit(MedicationOrder order) {
        try {
            List<List<String>> allOrders = csvReaderService.read(filename);
            List<List<String>> updatedOrders = new ArrayList<>();

            // Find the order to edit by matching the id
            for (List<String> orderData : allOrders) {
                if (orderData.get(0).equals(String.valueOf(order.getId()))) {
                    orderData.set(1, String.valueOf(order.getMedicationId())); // Update the quantity
                    orderData.set(2, String.valueOf(order.getPrescriptionId())); // Update the prescriptionId
                    orderData.set(3, String.valueOf(order.getQuantity())); 
                }
                updatedOrders.add(orderData);
            }

            csvReaderService.write(filename, updatedOrders); // Overwrite with updated data

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Delete a MedicationOrder record by id
    public static void delete(MedicationOrder order) {
        try {
            List<List<String>> allOrders = csvReaderService.read(filename);
            List<List<String>> updatedOrders = new ArrayList<>();

            for (List<String> orderData : allOrders) {
                if (!orderData.get(0).equals(String.valueOf(order.getId()))) {
                    updatedOrders.add(orderData); // Add all except the one with id
                }
            }

            csvReaderService.write(filename, updatedOrders); // Overwrite with updated data

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// public class OrderTable extends Parser<Medication> {

//     @Override
//     public Medication serialize(List<String> row) {
//         return new Medication(row.get(0), row.get(1), row.get(2), row.get(3));
//     }

//     @Override
//     public List<String> deserialize(Medication medication) {
//         List<String> row = new ArrayList<>();
//         row.add(String.valueOf(medication.getId()));
//         row.add(String.valueOf(medication.getStock()));
//         row.add(medication.getName());
//         row.add(String.valueOf(medication.getLowAlertLevel()));
//         return row;
//     }
// }
