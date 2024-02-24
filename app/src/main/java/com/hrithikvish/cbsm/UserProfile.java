package com.hrithikvish.cbsm;

//for offline usage
public class UserProfile {
    private String name;
    private String email;
    private String clg;

    public UserProfile(String name, String email, String clg) {
        this.name = name;
        this.email = email;
        this.clg = clg;
    }

    public String getName() {
        return name != null ? name : "";
    }

    public String getEmail() {
        return email != null ? email : "";
    }

    public String getClg() {
        return clg != null ? clg : "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setClg(String clg) {
        this.clg = clg;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", clg='" + clg + '\'' +
                '}';
    }

}
