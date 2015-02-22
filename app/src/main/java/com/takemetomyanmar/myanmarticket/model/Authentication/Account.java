package com.takemetomyanmar.myanmarticket.model.Authentication;

/**
 * Created by aungmo on 20/2/15.
 */
public class Account {
    private String id;
    private String username;
    private String email;
    private String phone;

    public Account(){}

    public Account(String id, String name, String email, String phone){
        this.id = id;
        this.username = name;
        this.email = email;
        this.phone = phone;
    }

    public String getId(){
        return this.id;
    }

    public String getName(){
        return this.username;
    }

    public void setName(String name){
        this.username = name;
    }

    public String getEmail(){
        return this.email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getPhone(){
        return this.phone;
    }

    public void setPhone(String phone){
        this.phone = phone;
    }

}
