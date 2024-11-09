package app.constants.exceptions;

import app.constants.AppMetadata;

/**
* Exit application.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class ExitApplication extends BaseCustomException {

  public ExitApplication() {
    super(String.join(
      "\n",
      String.format("\n\nThank you for using %s!", AppMetadata.APP_FULL_NAME.toString()),
      "  _________________________________________",
      " |                            __________   |",
      " |                           |  SC2002  |  |",
      " |  _____                    |  ~~~*~~~ |  |",
      " | |     |  (((        .--.  |__________|  |",
      " | |     | ~OvO~ __   (////)               |",
      " | |     | ( _ )|==|   \\__/                |",
      " | |o    |  \\_/ |_(|  /    \\   _______     |",
      " | |     | //|\\\\   \\\\//|  |\\\\  |__o__|     |",
      " | |   __|//\\_/\\\\ __\\/ |__|//  |__o__|     |",
      " | |  |==\"\"//=\\\\\"\"=====||||)   |__o__|     |",
      " |_|__||_|_||_||_______||||____|__o__|_____|",
      "      ||  (_) (_)      ||||                |",
      "      []              (_)(_)"
    ));
  }
}