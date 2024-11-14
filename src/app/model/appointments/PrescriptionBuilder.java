package app.model.appointments;

import app.model.Builder;
import app.model.inventory.MedicationOrder;

import java.util.List;

public class PrescriptionBuilder extends Builder<Prescription> {

    private List<MedicationOrder> medicationOrders;

    public PrescriptionBuilder(List<String> row, List<MedicationOrder> medicationOrders) throws Exception {
        super(row);
        this.medicationOrders = medicationOrders;
    }

    public PrescriptionBuilder(Prescription instance) {
        super(instance);
    }
    
    @Override
    public Prescription deserialize() throws Exception {
        return new Prescription(this.row, this.medicationOrders);
    }
}
