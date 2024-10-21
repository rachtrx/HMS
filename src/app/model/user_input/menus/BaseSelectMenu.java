package app.model.user_input.menus;

import app.constants.exceptions.ExitApplication;
import app.model.user_input.options.BaseOption;
import java.util.ArrayList;
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
        this.addOptionsAtEnd(options);
        this.displayMode = DisplayMode.INITIAL;
    }
    
    /** 
    * @return List<BaseOption>
    */
    public List<BaseOption> getOptions() {
        return this.options;
    }
    
    protected final void addOptionsAtStart(List<BaseOption> newOptions) {
        newOptions.addAll(this.options);
        this.options = newOptions;
    }

    protected final void addOptionsAtEnd(List<BaseOption> newOptions) {
        this.options.addAll(newOptions);
    }
    
    @Override
    public void display() {
        this.displayTitle();
        switch (this.displayMode) {
            case NO_MATCH_FOUND -> System.out.println("No option matched your selection. Please try again:");
            case MULTIPLE_MATCHES_FOUND -> System.out.println("Please be more specific:");
            case INITIAL -> System.out.println("Please select an option:");
            default -> { return; }
        }
        List<BaseOption> matches = (
                this.matchingOptions == null || this.matchingOptions.size() < 1
            ) ? this.options : this.matchingOptions;
        IntStream.range(0, matches.size())
            .forEach(optionIndex -> System.out.println(String.format(
                "%d. %s",
                optionIndex + 1,
                matches.get(optionIndex).getLabel()
            )));
    }
    
    @Override
    public BaseMenu next(String userInput) throws Exception {
        this.matchingOptions = IntStream.range(0, this.options.size())
            .mapToObj(optionsIndex -> {
                BaseOption option = this.options.get(optionsIndex);
                Pattern matchPattern = Pattern.compile(
                    String.join(
                        "|",
                        String.format("^%d", optionsIndex+1),
                        option.getMatchPattern(),
                        String.format("%d[ ]+(\\.)?%s",
                            optionsIndex+1,
                            option.getMatchPattern()
                        )
                    ), Pattern.CASE_INSENSITIVE
                );
                Matcher matcher = matchPattern.matcher(userInput);
                return matcher.find() ? option : null;
            }).filter(Objects::nonNull)
            .collect(Collectors.toCollection(ArrayList::new));

        if (matchingOptions.size() < 1) {
            this.displayMode = DisplayMode.NO_MATCH_FOUND;
            this.matchingOptions = (ArrayList<BaseOption>) this.options;
        } else if (matchingOptions.size() > 1) {
            this.displayMode = DisplayMode.MULTIPLE_MATCHES_FOUND;
        } else {
            try {
                BaseOption option = matchingOptions.get(0);
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
