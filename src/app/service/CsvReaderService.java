package app.service;

import app.utils.LoggerUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvReaderService {

    public static List<List<String>> read(String filePath) throws IOException {

        List<List<String>> data = new ArrayList<>();
        String line;
    
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // Skip header
    
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                List<String> row = new ArrayList<>();
                for (String value : values) {
                    row.add(value);
                }
                data.add(row);
            }
            LoggerUtils.info("Data: " + data);
        }
        return data;
    }
    
    public static void write(String filePath, List<List<String>> data) throws IOException {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (List<String> row : data) {
                String line = String.join(",", row);
                bw.write(line);
                bw.newLine();
            }
        }
        LoggerUtils.info("Data has been written to " + filePath);
    }

    
}
