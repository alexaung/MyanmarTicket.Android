package com.takemetomyanmar.myanmarticket.model.Authentication;

/**
 * Created by AMO on 2/13/2015.
 */
public class LoginRequest {
    private String email;
    private String password;

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public LoginRequest(){}

    public LoginRequest(String email, String password){
        this.email = email;
        this.password = password;
    }
}
