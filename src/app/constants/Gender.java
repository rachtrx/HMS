package app.constants;

/**
* Gender categories.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public enum Gender {
  MALE {
    @Override
    public String toString() {
      return "Male";
    }
  },
  FEMALE {
    @Override
    public String toString() {
      return "Female";
    }
  },
  NON_BINARY {
    @Override
    public String toString() {
      return "Non-Binary";
    }
  }
}