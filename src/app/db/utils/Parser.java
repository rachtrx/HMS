package app.db.utils;

import app.service.CsvReaderService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class Parser<T> {

    public List<T> load(String filepath) throws IOException {
        List<List<String>> rawData = CsvReaderService.read(filepath);
        List<T> serializedList = new ArrayList<>();
        for (List<String> row : rawData) {
            T serialized = serialize(row);
            serializedList.add(serialized);
        }
        return serializedList;
    }

    public void write(String filepath, ArrayList<T> data) throws IOException {
        List<List<String>> deserializedList = new ArrayList<>();
        for (T item : data) {
            List<String> row = deserialize(item);
            deserializedList.add(row);
        }
        CsvReaderService.write(filepath, deserializedList);
    }

    public abstract T serialize(List<String> data);
    public abstract List<String> deserialize(T data);
}