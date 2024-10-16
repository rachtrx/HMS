package app.model.users;

import java.util.Objects;

public abstract class User { // Handles Logging in
    
    public abstract void displayUserMenu();
    private int hospitalId;
    private String pw;
    private String name;
    private char gender;

    public User(String hospitalId, String pw, String name, char gender) {
        this.hospitalId = Integer.parseInt(hospitalId);
        this.pw = pw;
        this.name = name;
        this.gender = gender;
    }

    public String getGender() {
        return gender == 'M' ? "Male" : "Female";
    }

    public int getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(int hospitalId) {
        this.hospitalId = hospitalId;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(hospitalId, user.hospitalId) &&
           Objects.equals(pw, user.pw);
    }
}
