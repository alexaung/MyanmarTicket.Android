package com.takemetomyanmar.myanmarticket.model.AirportTransfer;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Created by AlexAung on 2/7/2015.
 */
public class Booking implements Serializable{

    private String id;
    private String code;
    private String bookingDate;
    private String paymentId;
    private String paymentState;

    private Personal bookBy;
    private Personal leadPassenger;
    private ArrayList<Transfer> transfers;

    public Booking() {};

    public String getId(){
        return this.id;
    }

    public String getCode() {return this.code; }

    public void setCode(String code){ this.code = code; }

    public Date getBookingDate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(this.bookingDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return convertedDate;

    }

    public void setBookingDate(Date bookingDate){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        this.bookingDate = dateFormat.format(bookingDate);
    }

    public String getPaymentId() { return this.paymentId; }

    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public String getPaymentState() { return this.paymentState; }

    public void setPaymentState(String paymentState) { this.paymentState = paymentState; }

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

    public ArrayList<Transfer> getTransfers(){
        return this.transfers;
    }

    public void setTransfers(ArrayList<Transfer> transfers){
        this.transfers = transfers;
    }

}
