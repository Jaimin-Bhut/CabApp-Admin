package com.jb.dev.cabapp_admin.model;

public class BookingModel {

    private String user_name;
    private String user_phone_number;
    private String date;
    private String time;
    private double start_lat;
    private double start_long;
    private double end_lat;
    private double end_long;
    private String user_email;
    private String Name;
    private String Phone;
    private String Date;
    private String Time;
    private double distance;
    private double price;
    private String cab_name;
    private String cab_number;
    private String cab_driver;
    private String current_location;
    private String destination_location;
    private int otp;
//    private String DriverId;

    public BookingModel() {
    }

    public BookingModel(String user_name, String user_phone, String date, String time, String current_location, String destination_location, double start_lat, double start_long, double end_lat, double end_long, String email, String name, String phone, String Date, String Time, double distance, double startLat, double startLong, double endLat, double endLong, double price, String cab_name, String cab_number, String cab_driver, int otp) {
        this.user_name = user_name;
        this.user_phone_number = user_phone;
        this.user_email = email;
        this.Name = name;
        this.Phone = phone;
        this.otp = otp;
        this.Date = Date;
        this.Time = Time;
        this.date = date;
        this.time = time;
        this.distance = distance;
        this.start_lat = start_lat;
        this.start_long = start_long;
        this.end_lat = end_lat;
        this.end_long = end_long;
        this.price = price;
        this.cab_name = cab_name;
        this.cab_number = cab_number;
        this.cab_driver = cab_driver;
        this.current_location = current_location;
        this.destination_location = destination_location;
        //        this.DriverId = driverId;
    }

    //
//    public String getDriverId() {
//        return DriverId;
//    }
//
//    public void setDriverId(String driverId) {
//        DriverId = driverId;
//    }

    public int getOtp() {
        return otp;
    }

    public String getuser_name() {
        return user_name;
    }

    public String getEmail() {
        return user_email;
    }

    public String getName() {
        return Name;
    }

    public String getPhone() {
        return Phone;
    }

    public String getdate() {
        return date;
    }

    public String gettime() {
        return time;
    }

    public String getUser_phone_number() {
        return user_phone_number;
    }

    public double getDistance() {
        return distance;
    }

    public String getDestination_location() {
        return destination_location;
    }

    public double getStart_lat() {
        return start_lat;
    }

    public double getStart_long() {
        return start_long;
    }

    public double getEnd_lat() {
        return end_lat;
    }

    public double getEnd_long() {
        return end_long;
    }

    public double getPrice() {
        return price;
    }

    public String getcab_name() {
        return cab_name;
    }

    public String getcab_number() {
        return cab_number;
    }

    public String getcab_driver() {
        return cab_driver;
    }

    public String getCurrent_location() {
        return current_location;
    }

}
