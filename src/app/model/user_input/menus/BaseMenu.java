package app.model.user_input.menus;

import app.model.user_input.options.BaseOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
/**
* Menu shown to users. (Equivalent to state in FSM)
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public abstract class BaseMenu {
    // Static Scanner instance, shared across all child classes
    protected static Scanner scanner = new Scanner(System.in);

    private List<BaseOption> options;
    private final String title;

    private ArrayList<State> states;


    public BaseMenu(List<BaseOption> options, String title) {
        this.setOptions(options);
        this.title = title;
    }

    /** 
     * @return List<BaseOption>
     */
    public List<BaseOption> getOptions() {
        return options;
    }

    /** 
     * @param options
     */
    public final void setOptions(List<BaseOption> options) {
        IntStream.range(0, options.size())
            .mapToObj(optionsIndex -> {
                BaseOption newOption = options.get(optionsIndex);
                newOption.setMatchPattern(String.join(
                    "|",
                    newOption.getMatchPattern(),
                    String.format("%d[ ]+(\\.)?", optionsIndex)
                ));
                return newOption;
            }).collect(Collectors.toList());
    }

    /** 
     * @return String
     */
    public String getTitle() {
        return title;
    }

    /**
     * Display all options. Default.
     */
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
    public void display(List<BaseOption> matchedOptions) {
        this.display(false, matchedOptions);
    };

    private void display(boolean noMatchesFound, List<BaseOption> matchedOptions) {
        this.clearScreen();
        System.out.println(this.getTitle());
        if (noMatchesFound) {
            System.out.println("No option matched your selection. Please try again:");
        } else {
            System.out.println(
                matchedOptions.size() < this.options.size() ?
                "Please be more specific:" :
                "Please select an option:"
            );
        }
        IntStream.range(0, 30).forEach(n -> System.out.print("━"));
        (IntStream.range(0, matchedOptions.size()))
            .forEach(optionIndex -> System.out.println(String.format(
                "%d. %s",
                optionIndex,
                matchedOptions.get(optionIndex).getLabel()
            )));
    }

    public void clearScreen() {
        System.out.print("\033[H\033[2J");  
        System.out.flush();
    }

    public abstract void next(String userInput);
}