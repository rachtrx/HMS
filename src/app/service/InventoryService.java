package app.service;

import app.model.inventory.Medication;
import java.util.ArrayList;
import java.util.List;

import app.model.inventory.Medication;
import app.model.inventory.Request;
import java.util.*;

import com.sun.net.httpserver.Request;

// public class InventoryService {
// 	private List<Medication> medicationList = new ArrayList<>();
//     private List<Request> requestList = new ArrayList<>();

//     public Medication getMedication(int medicationId) {
//         for (Medication med : medicationList) {
//             if (med.getId == medicationId) {
//                 return med;
//             }
//         }

//         return null;
//     }

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

//     public Request addRequest(int medicationId, int count) {
//         Request newReq = new Request(medicationId, count);
//         requestList.add(newReq);

//         return newReq;
//     }

//     public void submitReplenishRequest(int medicationId, int count) {
//         Medication med = getMedication(medicationId);

//         if (med != null) {
//             Request newRequest = new Request(medicationId, count);
//             requestList.add(newRequest);
//             System.out.println("Replenishment request submitted for medication: " + med.getName);
//         } else {
//             System.out.println("Medication not found in inventory");
//         }
//     }

//     public void approveReplenishRequest(int requestId) {
//         Request reqToApprove = null;

//         for (Request req : requestList) {
//             if (req.getRequestId() == requestId) {
//                 reqToApprove = req;
//                 break;
//             }
//         }

//         if (reqToApprove != null) {
//             Medication med = getMedication(reqToApprove.getMedicationId());
//             if (med != null) {
//                 try {
//                     med.setStock(med.getStock() + reqToApprove.getCount());
//                     reqToApprove.setStatus(Request.Status.APPROVED);
//                     System.out.println("Replenishment request approved for medication: " + med.getName);

//                 } catch (Exception e) {
//                     System.out.println("Failed to update stock: " +e.getMessage());
//                 }
//             }
//             requestList.remove(reqToApprove);
//         } else {
//             System.out.println("Replensihment request not found.");
//         }
//     }

// }