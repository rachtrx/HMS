package app.model.user_input;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import old.Menu;

public class OptionTable {
    private final List<String> headers = new ArrayList<>();
    private final List<Integer> columnWidths = new ArrayList<>();
    private final List<Option> numberedOptions;
    private final List<Option> unNumberedOptions;

    private List<Option> options;
    private List<Option> matchingOptions;

    public OptionTable(List<String> headers, List<Integer> columnWidths, List<Option> numberedOptions, List<Option> unNumberedOptions) {
        if (!headers.isEmpty() && headers.size() != columnWidths.size()) {
            throw new IllegalArgumentException("Headers and column widths must match in size.");
        }
        this.headers.addAll(headers);
        this.columnWidths.addAll(columnWidths);
        this.numberedOptions = new ArrayList<>(numberedOptions);
        this.unNumberedOptions = new ArrayList<>(unNumberedOptions);
    }

    // Method to print the table header for numbered options
    private void printNumberedHeader() {
        System.out.printf("%-5s", "Index");  // Index column for numbered options
        for (int i = 0; i < headers.size(); i++) {
            System.out.printf("%-" + columnWidths.get(i) + "s", headers.get(i));
        }
        System.out.println();
        System.out.println("=".repeat(columnWidths.stream().mapToInt(Integer::intValue).sum() + 5));
    }

    // Method to print the header for unnumbered options
    private void printUnNumberedHeader() {
        System.out.printf("%-20s%-20s%n", "Key", "Action");  // Two columns: Key and Action
        System.out.println("=".repeat(40));
    }

    // Print a single row for numbered options, including the index
    private void printNumberedRow(Map<String, String> displayFields, int index) {
        System.out.printf("%-5d", index);  // Print the index
        List<String[]> wrappedData = new ArrayList<>();

        for (int i = 0; i < headers.size(); i++) {
            String header = headers.get(i);
            String cellText = displayFields.getOrDefault(header, ""); // Fetch cell content by header
            String wrappedText = wrapText(cellText, columnWidths.get(i)); // Wrap text to column width
            wrappedData.add(wrappedText.split("\n"));
        }

        int maxLines = wrappedData.stream().mapToInt(arr -> arr.length).max().orElse(1);

        for (int line = 0; line < maxLines; line++) {
            for (int col = 0; col < headers.size(); col++) {
                String[] lines = wrappedData.get(col);
                String toPrint = (line < lines.length) ? lines[line] : ""; // Print or add blank for shorter cells
                System.out.printf("%-" + columnWidths.get(col) + "s", toPrint);
            }
            System.out.println();
        }
    }

    // Print a single row for unnumbered options (Key and Action)
    private void printUnNumberedRow(Option option) {
        System.out.printf("%-20s%-20s%n", option.getLabel(), option.getMatchPattern());
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

    // Print the full table with separate sections for numbered and unnumbered options
    public void printTable() {
        // Print numbered options with index column
        if (!numberedOptions.isEmpty()) {
            printNumberedHeader();
            int index = 1;
            for (Option option : numberedOptions) {
                printNumberedRow(option.getDisplayFields(), index++);
            }
        }

        // Print unnumbered options with "Key" and "Action" columns
        if (!unNumberedOptions.isEmpty()) {
            printUnNumberedHeader();
            for (Option option : unNumberedOptions) {
                printUnNumberedRow(option);
            }
        }
    }

    private void addOption(Option option) {
        this.addOptions(new ArrayList<>(List.of(option)));
    }

    // Add options and categorize them into numbered or unnumbered
    public void addOptions(List<Option> options) {
        for (Option option : options) {
            if (option.isNumberedOption()) {
                numberedOptions.add(option);
            } else {
                unNumberedOptions.add(option);
            }
        }
    }

    // Method to filter options based on user input
    public List<Option> getFilteredOptions(String userInput, boolean numberedOnly) {
        List<Option> filtered = new ArrayList<>();
        for (Option option : (numberedOnly ? numberedOptions : unNumberedOptions)) {
            if (option.matches(userInput)) {
                filtered.add(option);
            }
        }
        return filtered;
    }

    public void displayOptions() {
        printTable();
    }
}
