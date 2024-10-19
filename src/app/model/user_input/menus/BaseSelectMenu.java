package app.model.user_input.menus;

import app.constants.exceptions.ExitApplication;
import app.model.user_input.options.BaseSelectOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class BaseSelectMenu extends BaseMenu {
    protected List<BaseSelectOption> options;

    public BaseSelectMenu(
        String title,
        List<BaseSelectOption> options
    ) {
        super(title);
        this.options = options;
    }

    /** 
     * @return List<BaseSelectOption>
     */
    public List<BaseSelectOption> getOptions() {
        return this.options;
    }

    private List<BaseSelectOption> addOptionNumberRegex(List<BaseSelectOption> options) {
        return IntStream.range(0, options.size())
            .mapToObj(optionsIndex -> {
                BaseSelectOption newOption = options.get(optionsIndex);
                String optionNumberRegex = String.format("%d[ ]+(\\.)?", optionsIndex);
                newOption.setMatchPattern(String.join(
                    "|",
                    newOption.getMatchPattern(),
                    optionNumberRegex,
                    String.join("", optionNumberRegex, newOption.getMatchPattern())
                ));
                return newOption;
            }).collect(Collectors.toList());
    }

    /** 
     * @param newOptions
     */
    protected final void addOptionsToStart(List<BaseSelectOption> newOptions) {
        this.addOptionNumberRegex(options).addAll(this.options);
    }

    /** 
     * @param newOptions
     */
    protected final void addOptionsToEnd(List<BaseSelectOption> newOptions) {
        this.options.addAll(this.addOptionNumberRegex(options));
    }

    /** 
     * @param options
     */
    protected final void setOptions(List<BaseSelectOption> options) {
        this.options = this.addOptionNumberRegex(options);
    }

    /**
     * Display all options. Default.
     */
    @Override
    public void display() {
        this.display(false, new ArrayList<>());
    };

    /** 
     * Use when user's input does not match any option. Displays all menu options.
     * 
     * @param noMatchesFound
     */
    public void display(boolean noMatchesFound) {
        this.display(true, this.getOptions());
    };

    /** Use when 
     * 
     * @param matchedOptions All matches that match user's possible input.
     */
    public void display(List<BaseSelectOption> matchedOptions) {
        this.display(false, matchedOptions);
    };

    private void display(boolean noMatchesFound, List<BaseSelectOption> matchedOptions) {
        if (this.getTitle() != null) {
            System.out.println(this.getTitle());
        }
        if (noMatchesFound) {
            System.out.println("No option matched your selection. Please try again:");
        } else {
            System.out.println(
                matchedOptions.size() < this.options.size() ?
                "Please be more specific:" :
                "Please select an option:"
            );
        }
        IntStream.range(0, 30).forEach(n -> System.out.print("-"));
        System.err.println("\n");
        (IntStream.range(0, matchedOptions.size()))
            .forEach(optionIndex -> System.out.println(String.format(
                "%d. %s",
                optionIndex,
                matchedOptions.get(optionIndex).getLabel()
            )));
    }

    @Override
    public BaseMenu next(String userInput) throws Exception{
        List<BaseSelectOption> matchingOptions = this.getOptions()
            .stream()
            .filter(option -> option.isMatch(userInput))
            .collect(Collectors.toList());
        if (matchingOptions.size() < 1) {
            this.display(true);
        } else if (matchingOptions.size() > 1) {
            this.display(matchingOptions);
        } else {
            BaseSelectOption option = matchingOptions.get(0);
            try {
                option.executeAction();
                option.getNextMenu().display();
                return option.getNextMenu();
            } catch (Exception e) {
                throw e;
            } catch (Error e) {
                System.err.println("Something went wrong. Please contact your administrator and try again.");
                System.err.println("Exiting application...");
                throw new ExitApplication();
            }
        }
        return this;
    }
}
