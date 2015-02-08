package com.takemetomyanmar.myanmarticket.model.AirportTransfer;

import java.io.Serializable;

/**
 * Created by aungmo on 8/2/15.
 */
public class Personal implements Serializable{

    private String id;
    private String title;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;

    public Personal(){}

    public Personal(String id, String title, String firstName, String lastName, String email, String phone){
        this.id = id;
        this.title = title;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }

    public String getId(){
        return this.id;
    }

    public String getTitle(){
        return this.title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getFirstName(){
        return this.firstName;
    }

    public void setFirstName(String firstName){
        this.firstName = firstName;
    }

    public String getLastName(){
        return this.lastName;
    }

    public void setLastName(String lastName){
        this.lastName = lastName;
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
