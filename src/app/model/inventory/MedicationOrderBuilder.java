package app.model.inventory;

import app.model.Builder;
import java.util.List;

public final class MedicationOrderBuilder extends Builder<MedicationOrder> {

    public MedicationOrderBuilder(List<String> row) throws Exception {
        super(row);
    }

    public MedicationOrderBuilder(MedicationOrder instance) {
        super(instance);
    }

    @Override
    public MedicationOrder deserialize() {
        return new MedicationOrder(this.row);
    }
}