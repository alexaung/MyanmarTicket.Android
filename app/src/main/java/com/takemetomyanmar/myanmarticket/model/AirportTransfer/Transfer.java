package com.takemetomyanmar.myanmarticket.model.AirportTransfer;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by AlexAung on 2/7/2015.
 */
public class Transfer implements Serializable{
    private String id;
    private String service;
    private String pickUp;
    private String flightNo;
    private String from;
    private String to;
    private int noOfPassenger;
    private int noOfLuggage;
    private double rate;
    private String transferDate;
    private String car_Id;
    private Car car;

    public Transfer(){};

    public Transfer(String id, String service, String pickUp, String flightNo, String from, String to, int noOfPassenger, int noOfLuggage, Date transferDate, Car car){
        this.id = id;
        this.service = service;
        this.pickUp = pickUp;
        this.flightNo = flightNo;
        this.from = from;
        this.to = to;
        this.noOfPassenger = noOfPassenger;
        this.noOfLuggage = noOfLuggage;
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        this.transferDate = dateFormat.format(transferDate);
        this.car = car;
    }

    public String getId(){
        return this.id;
    }

    public String getService(){
        return this.service;
    }

    public void setService(String service){
        this.service = service;
    }

    public String getPickUp(){
        return this.pickUp;
    }

    public void setPickUp(String pickUp){
        this.pickUp = pickUp;
    }

    public String getFlightNo(){
        return this.flightNo;
    }

    public void setFlightNo(String flightNo){
        this.flightNo = flightNo;
    }

    public String getFrom(){
        return this.from;
    }

    public void setFrom(String from){
        this.from = from;
    }

    public String getTo(){
        return this.to;
    }

    public void setTo(String to){
        this.to = to;
    }

    public int getNoOfPassenger(){
        return this.noOfPassenger;
    }

    public void setNoOfPassenger(int noOfPassenger){
        this.noOfPassenger = noOfPassenger;
    }

    public int getNoOfLuggage(){
        return this.noOfLuggage;
    }

    public void setNoOfLuggage(int noOfLuggage){
        this.noOfLuggage = noOfLuggage;
    }

    public double getRate() { return this.rate; }

    public void setRate(double rate) { this.rate = rate; }

    public Date getTransferDate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(this.transferDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return convertedDate;
    }

    public void setTransferDate(Date transferDate){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        this.transferDate = dateFormat.format(transferDate);
    }

    public String getCar_Id() { return  this.car_Id; }

    public void setCar_Id(String car_Id) { this.car_Id = car_Id; }

    public Car getCar(){
        return this.car;
    }

    public void setCar(Car car){
        this.car = car;
    }

//    @Override
//    public int describeContents() {
//        // TODO Auto-generated method stub
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(service);
//        dest.writeString(pickUp);
//        dest.writeString(flightNo);
//        dest.writeString(from);
//        dest.writeString(to);
//        dest.writeInt(noOfPassenger);
//        dest.writeInt(noOfLuggage);
//        dest.writeSerializable(date);
//        dest.writeSerializable(car);
//    }
//
//    private Transfer(Parcel in){
//        this.service = in.readString();
//        this.pickUp = in.readString();
//        this.flightNo = in.readString();
//        this.from = in.readString();
//        this.to = in.readString();
//        this.noOfPassenger = in.readInt();
//        this.noOfLuggage = in.readInt();
//        this.date = (Date) in.readSerializable();
//        this.car = (Car) in.readSerializable();
//    }
//
//    public static final Parcelable.Creator<Transfer> CREATOR = new Parcelable.Creator<Transfer>() {
//
//        @Override
//        public Transfer createFromParcel(Parcel source) {
//            return new Transfer(source);
//        }
//
//        @Override
//        public Transfer[] newArray(int size) {
//            return new Transfer[size];
//        }
//    };
}
