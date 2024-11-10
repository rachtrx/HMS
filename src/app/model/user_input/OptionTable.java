package app.model.user_input;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class OptionTable {
    private final List<Option> numberedOptions = new ArrayList<>();
    private final List<Option> unNumberedOptions = new ArrayList<>();
    private final Map<String, Integer> columnConfig = new LinkedHashMap<>();  // Dynamic column widths

    public OptionTable(List<Option> options) {
        for (Option option : options) {
            if (option.isNumberedOption()) {
                numberedOptions.add(option);
                determineColumnConfig(option.getDisplayFields());
            } else {
                unNumberedOptions.add(option);
            }
        }
    }

    private void determineColumnConfig(Map<String, String> displayFields) {
        displayFields.forEach((key, value) -> {
            int valueLength = Math.max(key.length(), value.length()); 
            columnConfig.put(key, Math.max(columnConfig.getOrDefault(key, 0), valueLength + 2)); 
        });
    }

    // Print header dynamically for numbered options
    private void printNumberedHeader() {
        System.out.printf("%-7s", "Index"); 
        columnConfig.forEach((header, width) -> System.out.printf("%-" + width + "s", header));
        System.out.println();
        System.out.println("=".repeat(5 + columnConfig.values().stream().mapToInt(Integer::intValue).sum()));
    }

    // Print a single row for numbered options
    private void printNumberedRow(Map<String, String> displayFields, int index) {
        System.out.printf("%-7d", index);
        columnConfig.forEach((header, width) -> {
            String cellText = displayFields.getOrDefault(header, "");
            System.out.printf("%-" + width + "s", cellText);
        });
        System.out.println();
    }

    private void printUnNumberedTable() {
        System.out.printf("%-20s%-20s%n", "Other Options", "Action");
        System.out.println("=".repeat(40));
        for (Option option : unNumberedOptions) {
            System.out.printf("%-20s%-20s%n",
                option.getDisplayFields().getOrDefault("Other Options", ""),
                option.getDisplayFields().getOrDefault("Action", ""));
        }
    }

    public void printTable() {
        if (!numberedOptions.isEmpty()) {
            printNumberedHeader();
            int index = 1;
            for (Option option : numberedOptions) {
                printNumberedRow(option.getDisplayFields(), index++); 
            }
        }

        System.out.println("\n");

        if (!unNumberedOptions.isEmpty()) {
            printUnNumberedTable();  // Print unnumbered options without index
        }
    }

    public void displayOptions() {
        printTable();
    }

    // Method to retrieve numbered options only
    public List<Option> getNumberedOptions(boolean getNumbered) {
        return getNumbered ? numberedOptions : unNumberedOptions;
    }

    public List<Option> getFilteredOptions(String userInput, boolean numbered) {
        List<Option> filteredOptions = this.getNumberedOptions(numbered);

        return IntStream.range(0, filteredOptions.size())
            .mapToObj(index -> {
                Option option = filteredOptions.get(index);

                // Build regex pattern to match by index or matchPattern
                String pattern = numbered
                    ? String.format("^%d(\\.)?$|%s", index + 1, option.matchPattern)
                    : option.matchPattern;

                Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(userInput);
                return matcher.find() ? option : null;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
}
