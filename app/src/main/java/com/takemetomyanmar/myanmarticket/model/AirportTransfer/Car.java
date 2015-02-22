package com.takemetomyanmar.myanmarticket.model.AirportTransfer;

import java.io.Serializable;

/**
 * Created by AMO on 2/5/2015.
 */
public class Car  implements Serializable {
    private String id;
    private String name;
    private String description;
    private int seatingCapacity;
    private int luggage;
    private double rates;
    private String image;

    public Car(){}

    public Car(String id, String name, String description, int seatingCapacity, int luggage, float rates, String image){
        this.id = id;
        this.name = name;
        this.description = description;
        this.seatingCapacity = seatingCapacity;
        this.luggage = luggage;
        this.rates = rates;
        this.image = image;
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

    public String getDescription(){
        return this.description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public int getSeatingCapacity(){
        return this.seatingCapacity;
    }

    public void setSeatingCapacity(int luggage){
        this.luggage = luggage;
    }

    public int getLuggage(){
        return this.luggage;
    }

    public void setLuggage(int luggage){
        this.luggage = luggage;
    }

    public double getRates(){
        return this.luggage;
    }

    public void setRates(double rates){
        this.rates = rates;
    }

    public String getImage(){
        return this.image;
    }

    public void setImage(String image){
        this.image = image;
    }
}
