package app.db.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.controller.AppController;
import app.service.CsvReaderService;

public abstract class Parser<T> {

    private static CsvReaderService csvReaderService = AppController.getCsvReaderService();

    public List<T> load(String filepath) throws IOException {
        List<List<String>> rawData = csvReaderService.read(filepath);
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
        csvReaderService.write(filepath, deserializedList);
    }

    public abstract T serialize(List<String> data);
    public abstract List<String> deserialize(T data);
}