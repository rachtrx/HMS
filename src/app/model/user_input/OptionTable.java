package app.model.user_input;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class OptionTable {
    private final List<Option> numberedOptions = new ArrayList<>();
    private final List<Option> unNumberedOptions = new ArrayList<>();
    private final List<Option> displayOptions = new ArrayList<>();
    private final Map<String, Integer> numberedColumnConfig = new LinkedHashMap<>();  // Dynamic column widths
    private final Map<String, Integer> displayColumnConfig = new LinkedHashMap<>();

    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";

    public OptionTable(List<Option> options) {
        options.forEach(option -> {
            switch (option.getOptionType()) {
                case NUMBERED:
                    numberedOptions.add(option);
                    determineColumnConfig(option.getDisplayFields(), numberedColumnConfig);
                    break;
                case UNNUMBERED:
                    unNumberedOptions.add(option);
                    break;
                case DISPLAY:
                    displayOptions.add(option);
                    determineColumnConfig(option.getDisplayFields(), displayColumnConfig);
                    break;
            }
        });
    }

    private void determineColumnConfig(Map<String, String> displayFields, Map<String, Integer> columnConfig) {
        displayFields.forEach((key, value) -> {
            int valueLength = Math.max(key.length(), value.length());
            columnConfig.put(key, Math.max(columnConfig.getOrDefault(key, 0), valueLength + 2));
        });
    }

    // Print a header based on the column config and display flag
    private void printHeader(Map<String, Integer> columnConfig, boolean isDisplay) {
        if (!isDisplay) {
            System.out.printf(RED + "%-8s" + RESET, "Select");
        }
        columnConfig.forEach((header, width) -> System.out.printf("%-" + width + "s", header));
        System.out.println();
        System.out.println("=".repeat((isDisplay ? 0 : 5) + columnConfig.values().stream().mapToInt(Integer::intValue).sum()));
    }

    // Print a row based on display fields, index, and display flag
    private void printRow(Map<String, String> displayFields, int index, Map<String, Integer> columnConfig, boolean isDisplay) {
        if (!isDisplay) {
            System.out.printf(RED + "%-8d" + RESET, index);
        }
        columnConfig.forEach((header, width) -> {
            String cellText = displayFields.getOrDefault(header, "");
            System.out.printf("%-" + width + "s", cellText);
        });
        System.out.println();
    }

    // Print all DISPLAY options
    private void printDisplayOptions() {
        if (!displayOptions.isEmpty()) {
            printHeader(displayColumnConfig, true);
            displayOptions.forEach(option -> printRow(option.getDisplayFields(), 0, displayColumnConfig, true));
            System.out.println("\n");
        }
    }

    // Print all NUMBERED options
    private void printNumberedOptions() {
        if (!numberedOptions.isEmpty()) {
            printHeader(numberedColumnConfig, false);
            int index = 1;
            for (Option option : numberedOptions) {
                printRow(option.getDisplayFields(), index++, numberedColumnConfig, false);
            }
            System.out.println("\n");
        }
    }

    // Print all UNNUMBERED options in a simple table
    private void printUnNumberedOptions() {
        if (!unNumberedOptions.isEmpty()) {
            System.out.printf(RED + "%-8s" + RESET + "%-20s%n", "Select", "Action");
            System.out.println("=".repeat(28));
            for (Option option : unNumberedOptions) {
                System.out.printf(RED + "%-8s" + RESET + "%-20s%n",
                    option.getDisplayFields().getOrDefault("Select", ""),
                    option.getDisplayFields().getOrDefault("Action", ""));
            }
            System.out.println("\n");
        }
    }

    public void printTable() {
        printDisplayOptions();
        printNumberedOptions();
        printUnNumberedOptions();
    }

    // Method to retrieve numbered options only
    public List<Option> getNumberedOptions(boolean getNumbered) {
        return getNumbered ? numberedOptions : unNumberedOptions;
    }

    public List<Option> getFilteredOptions(String userInput) {
        List<Option> allOptions = new ArrayList<>();
        allOptions.addAll(numberedOptions);
        allOptions.addAll(unNumberedOptions);
    
        return allOptions.stream()
            .filter(option -> {
                String pattern = option.getOptionType() == Option.OptionType.NUMBERED
                    ? String.format("^%d(\\.)?$|%s", numberedOptions.indexOf(option) + 1, option.matchPattern)
                    : option.matchPattern;
                
                Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(userInput);
                // System.out.println(userInput);
                // System.err.println(option.matchPattern);
                boolean isFound = matcher.find();
                // System.out.println(isFound);
                return isFound;
            })
            .sorted(Comparator.comparing(option -> option.getOptionType() == Option.OptionType.NUMBERED ? 0 : 1)) // Sort by NUMBERED then UNNUMBERED
            .collect(Collectors.toList());
    }
}
