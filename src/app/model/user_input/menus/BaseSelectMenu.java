package app.model.user_input.menus;

import app.constants.exceptions.ExitApplication;
import app.model.user_input.options.BaseOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class BaseSelectMenu extends BaseMenu {
    protected enum DisplayMode {
        MATCH_FOUND,
        NO_MATCH_FOUND,
        MULTIPLE_MATCHES_FOUND,
        INITIAL
    }
    protected List<BaseOption> options = new ArrayList<>();
    protected DisplayMode displayMode;
    protected ArrayList<BaseOption> matchingOptions;
    
    public BaseSelectMenu(
        String title,
        List<BaseOption> options
    ) {
        super(title);
        this.options = options;
        this.displayMode = DisplayMode.INITIAL;
    }
    
    /** 
    * @return List<BaseOption>
    */
    public List<BaseOption> getOptions() {
        return this.options;
    }

    protected final void addOption(BaseOption newOption) {
        this.addOptions(new ArrayList<>(Arrays.asList(newOption)));
    }

    protected final void addOptions(List<BaseOption> newOptions) {
        this.options.addAll(newOptions);
    }
    
    private List<BaseOption> getNumberedOptions() {
        return this.getNumberedOptions(true);
    }

    private List<BaseOption> getUnNumberedOptions() {
        return this.getNumberedOptions(false);
    }

    private List<BaseOption> getNumberedOptions(boolean getNumbered) {
        return IntStream.range(0, this.options.size())
            .filter(optionsIndex -> getNumbered == this.options.get(optionsIndex).isNumberedOption())
            .mapToObj(optionsIndex -> this.options.get(optionsIndex))
            .collect(Collectors.toCollection(ArrayList::new));
    }

    private List<BaseOption> getFilteredOptions(String userInput, boolean numbered) {
        List<BaseOption> filteredOptions = this.getNumberedOptions(numbered);
        return IntStream.range(0, filteredOptions.size())
            .mapToObj(optionsIndex -> {
                BaseOption option = filteredOptions.get(optionsIndex);
                Pattern matchPattern = Pattern.compile(
                    numbered ? 
                        String.join(
                            "|",
                            String.format("^%d", optionsIndex+1),
                            option.getMatchPattern(),
                            String.format("%d[ ]+(\\.)?%s",
                                optionsIndex+1,
                                option.getMatchPattern()
                            )
                        ) : option.getMatchPattern(),
                    Pattern.CASE_INSENSITIVE
                );
                Matcher matcher = matchPattern.matcher(userInput);
                return matcher.find() ? option : null;
            }).filter(Objects::nonNull)
            .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public void displayTitle() {
        super.displayTitle();
        System.out.println("");
        switch (this.displayMode) {
            case NO_MATCH_FOUND -> System.out.println("No option matched your selection. Please try again:");
            case MULTIPLE_MATCHES_FOUND -> System.out.println("Please be more specific:");
            case INITIAL -> System.out.println("Please select an option:");
            default -> { return; }
        }
        System.out.println("");
    }

    public void displayNumberedOptions() {
        List<BaseOption> matches = (
                this.matchingOptions == null || this.matchingOptions.size() < 1
            ) ? this.getNumberedOptions() : this.matchingOptions;
        IntStream.range(0, matches.size())
            .forEach(optionIndex -> System.out.println(String.format(
                "%d. %s",
                optionIndex + 1,
                matches.get(optionIndex).getLabel()
            )));
    }

    public void displayUnNumberedOptions() {
        this.getUnNumberedOptions().forEach(option -> System.out.println(option.getLabel()));
    }

    @Override
    public void display() {
        this.displayTitle();
        this.displayNumberedOptions();
        System.out.println("");
        this.displayUnNumberedOptions();
        System.out.println("");
    }
    
    @Override
    public BaseMenu next(String userInput) throws Exception {
        this.matchingOptions = (ArrayList<BaseOption>) this.getFilteredOptions(userInput, true);
        List<BaseOption> unNumberedMatches = this.getFilteredOptions(userInput, false);
        int numberOfMatches = unNumberedMatches.size() + this.matchingOptions.size();
        if (numberOfMatches < 1) {
            this.displayMode = DisplayMode.NO_MATCH_FOUND;
        } else if (numberOfMatches > 1) {
            this.displayMode = DisplayMode.MULTIPLE_MATCHES_FOUND;
        } else {
            try {
                unNumberedMatches.addAll(this.matchingOptions);
                BaseOption option = unNumberedMatches.get(0);
                option.executeAction();
                return option.getNextMenu();
            } catch (Exception e) {
                throw e;
            } catch (Error e) {
                System.out.println("Something went wrong. Please contact your administrator and try again.");
                System.out.println("Exiting application...");
                throw new ExitApplication();
            }
        }

        return this;
    }
}
