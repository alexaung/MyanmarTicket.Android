package com.takemetomyanmar.myanmarticket.model.AirportTransfer;

/**
 * Created by AlexAung on 2/1/2015.
 */
public class Airport {
    private String code;
    private String name;

    public Airport(){}

    public Airport(String code, String name){
        this.code = code;
        this.name = name;
    }

    public String getCode(){
        return this.code;
    }

    public void setCode(String code){
        this.code = code;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }
}
