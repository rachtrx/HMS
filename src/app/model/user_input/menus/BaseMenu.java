package app.model.user_input.menus;

import app.model.user_input.options.BaseOption;
import java.util.ArrayList;
import java.util.stream.IntStream;
/**
* Menu shown to users. (Equivalent to state in FSM)
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public abstract class BaseMenu {
    private ArrayList<BaseOption> options;
    private String title;

    public BaseMenu(ArrayList<BaseOption> options, String title) {
        this.options = options.stream().map(option -> {
            option.setMatchPattern(String.join("|", option.));
        });
        this.title = title;
    }

    public ArrayList<BaseOption> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<BaseOption> options) {
        this.options = options;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void display() {
        this.clearScreen();
        System.out.println(this.getTitle());
        IntStream.range(0, 30).forEach(n -> System.out.print("━"));
        IntStream
            .range(0, this.getOptions().size())
            .forEach(optionIndex -> System.out.println(String.format(
                "%d. %s",
                optionIndex,
                this.getOptions().get(optionIndex)
            )));
    };

    public void clearScreen() {
        System.out.print("\033[H\033[2J");  
        System.out.flush();
    }

    public void next(String userInput) {
        
    }
}