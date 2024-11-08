package app.model.user_input;

import java.util.ArrayList;
import java.util.List;

public class TablePrinter {
    private final List<String> headers = new ArrayList<>();
    private final List<Integer> columnWidths = new ArrayList<>();

    public TablePrinter(List<String> headers, List<Integer> columnWidths) {
        if (headers.size() != columnWidths.size()) {
            throw new IllegalArgumentException("Headers and column widths must match in size.");
        }
        this.headers.addAll(headers);
        this.columnWidths.addAll(columnWidths);
    }

    // Method to print the table header
    public void printHeader() {
        for (int i = 0; i < headers.size(); i++) {
            System.out.printf("%-" + columnWidths.get(i) + "s", headers.get(i));
        }
        System.out.println();
        System.out.println("=".repeat(columnWidths.stream().mapToInt(Integer::intValue).sum()));
    }

    // Method to print a single row of data
    public void printRow(List<String> rowData) {
        List<String[]> wrappedData = new ArrayList<>();

        for (int i = 0; i < rowData.size(); i++) {
            String wrappedText = wrapText(rowData.get(i), columnWidths.get(i));
            wrappedData.add(wrappedText.split("\n"));
        }

        int maxLines = wrappedData.stream().mapToInt(arr -> arr.length).max().orElse(1);

        for (int line = 0; line < maxLines; line++) {
            for (int col = 0; col < headers.size(); col++) {
                String[] lines = wrappedData.get(col);
                String toPrint = (line < lines.length) ? lines[line] : "";
                System.out.printf("%-" + columnWidths.get(col) + "s", toPrint);
            }
            System.out.println();
        }
    }

    // Helper method to wrap text based on column width
    private String wrapText(String text, int width) {
        StringBuilder wrappedText = new StringBuilder();
        int currentIndex = 0;

        while (currentIndex < text.length()) {
            int endIndex = Math.min(currentIndex + width, text.length());
            wrappedText.append(text, currentIndex, endIndex).append("\n");
            currentIndex += width;
        }

        return wrappedText.toString().trim(); // Remove trailing newline
    }
}
