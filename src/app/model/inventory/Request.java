package app.model.inventory;

import app.constants.exceptions.NonNegativeException;

public class Request {
    private static int uuid = 1;

    private int requestId;
    private int medicationId;
    private int count;
    private Status status;

    public enum Status {
        PENDING, APPROVED, REJECTED
    }

    public Request(int medicationId, int count) {
        this.requestId = uuid++;
        this.medicationId = medicationId;
        this.count = count;
        this.status = Status.PENDING;
    }

//     public void printRequest() {
//         System.out.println("Request ID: " + requestId);
//         System.out.println("Medication ID: " + medicationId);
//         System.out.println("Quantity Requested: " + count);
//         System.out.println("Status: " + status);
//     }

//     public int getRequestId() {
//         return requestId;
//     }

//     // public int getMedicationId() {
//     //     return medicationId;
//     // }

//     public int getCount() {
//         return count;
//     }

//     // public void setCount(int count) { 
//     //     this.count = count;
//     // }

//     public Status getStatus() {
//         return status;
//     }

//     public void setStatus(Status status) {
//         this.status = status;
//     }
}
