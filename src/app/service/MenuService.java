package app.service;

import app.model.user_input.States;
import app.model.user_input.Transitions;
import app.utils.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
* Controls which menus to show (Equivalent to machine in FSM)
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-21
*/
public class MenuService {

    private static States state = States.LANDING;

    public static States getState() {
        return MenuService.state;
    }

    public static void next(String userInput) throws Exception {
        List<Transitions> matches = Arrays.stream(Transitions.values())
            .filter(transition -> MenuService.isMatch(userInput, transition))
            .collect(Collectors.toCollection(ArrayList::new));
        if (matches.size() < 1) {
            throw new Exception("No option matched your selection. Please try again:");
        } else if (matches.size() > 1) {
            throw new Exception("Please be more specific:");
        }
        MenuService.state = matches.get(0).getDestination();
    }

    private static boolean isMatch(String userInput, Transitions transition) {
        Pattern matchPattern = Pattern.compile(
            transition.getMatchPattern(),
            transition.shouldParseUserInput() ? Pattern.CASE_INSENSITIVE : null
        );
        Matcher matcher = matchPattern.matcher(
            transition.shouldParseUserInput() ? StringUtils.parseUserInput(userInput) : userInput
        );
        return matcher.find();
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");  
        System.out.flush();
        System.out.print("\n\n"); // add buffer rows between states 
    }
}
