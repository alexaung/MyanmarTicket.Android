package com.takemetomyanmar.myanmarticket.model.AirportTransfer;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by AlexAung on 2/7/2015.
 */
public class Booking implements Serializable{

    private String id;
    private String code;
    private Date bookingDate;

    private Personal bookBy;
    private Personal leadPassenger;
    private Transfer transfer;

    public Booking() {};

    public String getId(){
        return this.id;
    }

    public String getCode() {return this.code; }

    public Date getBookingDate(){
        return this.bookingDate;
    }

    public void setBookingDate(Date bookingDate){ this.bookingDate = bookingDate; }

    public Personal getBookBy(){
        return this.bookBy;
    }

    public void setBookBy(Personal bookBy){
        this.bookBy = bookBy;
    }

    public Personal getLeadPassenger(){
        return this.leadPassenger;
    }

    public void setLeadPassenger(Personal leadPassenger){
        this.leadPassenger = leadPassenger;
    }

    public Transfer getTransfer(){
        return this.transfer;
    }

    public void setTransfer(Transfer transfer){
        this.transfer = transfer;
    }

}
