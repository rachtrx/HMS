package app.constants;

/**
* Exit application.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-18
*/
public enum AppMetadata {
    APP_FULL_NAME {
        @Override
        public String toString() {
            return "Health Management Service";
        }
    },
    APP_SHORT_NAME {
        @Override
        public String toString() {
            return "HMS";
        }
    }
}
