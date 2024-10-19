package app.service;

import app.model.inventory.Medication;
import java.util.ArrayList;
import java.util.List;

public class InventoryService {

    private static final List<Medication> medications = new ArrayList<>();
	
    public static void add(Medication medication) {
        medications.add(medication);
    }
}