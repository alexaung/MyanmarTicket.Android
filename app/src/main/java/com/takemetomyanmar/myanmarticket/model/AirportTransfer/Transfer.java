package com.takemetomyanmar.myanmarticket.model.AirportTransfer;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by AlexAung on 2/7/2015.
 */
public class Transfer implements Parcelable{
    private String id;
    private String service;
    private String pickUp;
    private String flightNo;
    private String from;
    private String to;
    private int noOfPassenger;
    private int noOfLuggage;
    private Date date;
    private Car car;

    public Transfer(){};

    public Transfer(String id, String service, String pickUp, String flightNo, String from, String to, int noOfPassenger, int noOfLuggage, Date date, Car car){
        this.id = id;
        this.service = service;
        this.pickUp = pickUp;
        this.flightNo = flightNo;
        this.from = from;
        this.to = to;
        this.noOfPassenger = noOfPassenger;
        this.noOfLuggage = noOfLuggage;
        this.date = date;
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

    public Date getDate(){
        return this.date;
    }

    public void setDate(Date date){
        this.date = date;
    }

    public Car getCar(){
        return this.car;
    }

    public void setCar(Car car){
        this.car = car;
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(service);
        dest.writeString(pickUp);
        dest.writeString(flightNo);
        dest.writeString(from);
        dest.writeString(to);
        dest.writeInt(noOfPassenger);
        dest.writeInt(noOfLuggage);
        dest.writeSerializable(date);
        dest.writeSerializable(car);
    }

    private Transfer(Parcel in){
        this.service = in.readString();
        this.pickUp = in.readString();
        this.flightNo = in.readString();
        this.from = in.readString();
        this.to = in.readString();
        this.noOfPassenger = in.readInt();
        this.noOfLuggage = in.readInt();
        this.date = (Date) in.readSerializable();
        this.car = (Car) in.readSerializable();
    }

    public static final Parcelable.Creator<Transfer> CREATOR = new Parcelable.Creator<Transfer>() {

        @Override
        public Transfer createFromParcel(Parcel source) {
            return new Transfer(source);
        }

        @Override
        public Transfer[] newArray(int size) {
            return new Transfer[size];
        }
    };
}
