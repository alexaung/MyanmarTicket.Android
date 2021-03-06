package com.takemetomyanmar.myanmarticket.model.AirportTransfer;

import java.io.Serializable;

/**
 * Created by aungmo on 8/2/15.
 */
public class Personal implements Serializable{

    private String id;
    private String name;
    private String email;
    private String phone;

    public Personal(){}

    public Personal(String id, String name, String email, String phone){
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
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
