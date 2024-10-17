package app.constants.exceptions;
/**
* Exit application.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class ExitApplication extends Throwable {

  public ExitApplication() {
    super(String.join(
      "\n",
      "Thank you and see you again!",
      "_________________________________________",
      "                            __________   |\\",
      "                           |  SC2002  |  | \\",
      "  _____                    |  ~~~*~~~ |  |  \\",
      " |     |  (((        .--.  |__________|  |",
      " |     | ~OvO~ __   (////)               |",
      " |     | ( _ )|==|   \\__/                |",
      " |o    |  \\_/ |_(|  /    \\   _______     |",
      " |     | //|\\\\   \\\\//|  |\\\\  |__o__|     |",
      " |   __|//\\_/\\\\ __\\/ |__|//  |__o__|     |",
      " |  |==\"\"//=\\\\\"\"====|||||)   |__o__|     |",
      "_|__||_|_||_||_____||||||____|__o__|_____|",
      "    ||  (_) (_)    ||||||                |",
      "    []             [(_)(_)"
    ));
  }
}