package app.constants;

/**
* Blood type categories.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public enum BloodType {
  A_PLUS {
    @Override
    public String toString() {
      return "A+";
    }
  },
  A_MINUS {
    @Override
    public String toString() {
      return "A-";
    }
  },
  B_PLUS {
    @Override
    public String toString() {
      return "B+";
    }
  },
  B_MINUS {
    @Override
    public String toString() {
      return "B-";
    }
  },
  AB_PLUS {
    @Override
    public String toString() {
      return "AB+";
    }
  },
  AB_MINUS {
    @Override
    public String toString() {
      return "AB-";
    }
  },
  O_PLUS {
    @Override
    public String toString() {
      return "O+";
    }
  },
  O_MINUS {
    @Override
    public String toString() {
      return "O-";
    }
  }
}