package app.db;

import java.util.ArrayList;
import java.util.List;

public class Row {
    
    private final int pKeyCol;
    private List<String> data;

    protected Row(int pKeyCol, List<String> data) {
        this.pKeyCol = pKeyCol;
        this.data = data;
    }

    protected String getpKey() {
        return data.get(pKeyCol);
    }

    protected List<String> getData() {
        return data;
    }

    protected void setData(List<String> data) {
        this.data = data;
    }

    protected static List<String> combineData(List<Row> rows) {
        List<String> combinedData = new ArrayList<>();
        for (Row row : rows) {
            combinedData.addAll(row.getData());
        }
        return combinedData;
    }
}
