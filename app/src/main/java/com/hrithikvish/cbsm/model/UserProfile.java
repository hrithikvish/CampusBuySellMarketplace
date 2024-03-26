package com.hrithikvish.cbsm.model;

//for offline usage
public class UserProfile {
    private String emailName;
    private String name;
    private String email;
    private String clg;

    public UserProfile(String emailName, String email, String clg, String name) {
        this.emailName = emailName;
        this.email = email;
        this.clg = clg;
        this.name = name;
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

    public String getEmailName() {
        return emailName;
    }

    public void setEmailName(String emailName) {
        this.emailName = emailName;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "emailName='" + emailName + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", clg='" + clg + '\'' +
                '}';
    }

}
