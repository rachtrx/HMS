package app.service;

import app.model.inventory.Medication;
import app.model.inventory.Request;
import java.util.*;
import java.util.stream.Collectors;

public class MedicationService {
	private static List<Medication> medicationList = new ArrayList<>();

	public static void addMedication(List<Medication> medicationList) {
		MedicationService.medicationList.addAll(medicationList);
	}
    
	public static List<Medication> getAllMedications() {
        return medicationList;
    }

    public static Medication getMedication(int medicationId) {
        for (Medication med : medicationList) {
            if (med.getId() == medicationId) {
                return med;
            }
        }

        return null;
    }

	public static List<Request> getAllRequests() {
		return medicationList.stream()
			.flatMap(medication -> medication.getRequestList().stream())
			.collect(Collectors.toList());
	}

//     public Medication createMedication(String name, int initialStock, int lowAlertLevel) {
//         Medication newMed = new Medication(initialStock, name, lowAlertLevel);
//         medicationList.add(newMed);

//         return newMed;
//     }

//     // public void printInventory() {
//     //     for (Medication med : medicationList) {
//     //         System.out.printf("Medication: %s, Stock: %d, Low Stock Alert: %d", med.getName, med.getStock, med.getLowAlertLevel);

//     //     }
//     // }

//     public void printRequests() {
//         for (Request req : requestList) {
//             req.printRequest();
//         }
//     }

    public static void submitReplenishRequest(int medicationId, int count) {
        Medication med = getMedication(medicationId);

        if (med != null) {
            Request newRequest = Request.create(medicationId, count);
			med.addRequest(newRequest);
            System.out.println("Replenishment request submitted for medication: " + med.getName());
        } else {
            System.out.println("Medication not found in inventory");
        }
    }

    public static void approveReplenishRequest(Request req) throws Exception {
		Medication med = getMedication(req.getMedicationId());

		try {
			med.setStock(med.getStock() + req.getCount());
			req.setStatus(Request.Status.APPROVED);
			System.out.println("Replenishment request approved for medication: " + med.getName());

		} catch (Exception e) {
			System.out.println("Failed to update stock: " + e.getMessage());
			throw e;
		}
    }

}