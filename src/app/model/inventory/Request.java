package app.model.inventory;

import app.db.DatabaseManager;
import app.utils.EnumUtils;
import app.utils.LoggerUtils;

import java.util.ArrayList;
import java.util.List;
import app.model.ISerializable;

public class Request implements ISerializable {
    private static int uuid = 1;

    private final int id;
    private final int medicationId;
    private final int count;
    private Status status;

    public enum Status {
        PENDING {
            @Override
            public String toString() {
                return "Pending";
            }
        },
        APPROVED {
            @Override
            public String toString() {
                return "Approved";
            }
        },
        REJECTED {
            @Override
            public String toString() {
                return "Rejected";
            }
        },
    }

    public static void setUuid(int value) {
        uuid = value;
        LoggerUtils.info("New Request UUID set to " + value);
    }

    private Request(int medicationId, int count) {
        this.id = Request.uuid++;
        LoggerUtils.info("Request ID created with" + this.id);
        this.medicationId = medicationId;
        this.count = count;
        this.status = Status.PENDING;
    }

    public static Request create(int medicationId, int count) {
        Request request = new Request(medicationId, count);
        DatabaseManager.add(request);
        LoggerUtils.info("Request created");
        return request;
    }

    protected Request(List<String> row) {
        LoggerUtils.info(String.join(", ", row));
        this.id = Integer.parseInt(row.get(0));
        this.medicationId = Integer.parseInt(row.get(1));
        this.count = Integer.parseInt(row.get(2));
        this.status = EnumUtils.fromString(Status.class, row.get(3));
        Request.setUuid(Math.max(Request.uuid, this.id+1));
    }

    @Override
    public List<String> serialize() {
        List<String> row = new ArrayList<>();
        row.add(String.valueOf(this.getId()));
        row.add(String.valueOf(this.getMedicationId()));
        row.add(String.valueOf(this.getCount()));
        row.add(String.valueOf(this.getStatus().toString()));
        return row;
    }

    public void printRequest() {
        System.out.println("Request ID: " + id);
        System.out.println("Medication ID: " + medicationId);
        System.out.println("Quantity Requested: " + count);
        System.out.println("Status: " + status);
    }

    public int getId() {
        return id;
    }

    public int getMedicationId() {
        return medicationId;
    }

    public int getCount() {
        return count;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
        DatabaseManager.update(this);
    }
}
