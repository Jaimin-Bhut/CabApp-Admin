package com.jb.dev.cabapp_admin.helper;

public interface Constants {

    String IS_ADMIN_EMAIL = "isAdminEmail";

    int DRIVER_REFRESH_RESULT_CODE = 9001;
    int CAB_REFRESH_RESULT_CODE = 9000;
    int CARD_REFRESH_RESULT_CODE = 9002;

    String DRIVER_COLLECTION_REFERENCE_KEY = "Driver_Data";
    String DRIVER_NAME_KEY = "Name";
    String DRIVER_ADDRESS_KEY = "Address";
    String DRIVER_PHONE_NUMBER_KEY = "Phone";
    String DRIVER_EMAIL_KEY = "Email";
    String DRIVER_PASSWORD_KEY = "Password";

    String CAB_COLLECTION_REFERENCE_KEY = "Cab_Data";
    String CAB_NAME_KEY = "CabName";
    String CAB_NUMBER_KEY = "CabNumber";
    String CAB_PERSON_CAPACITY_KEY = "CabPerCapacity";
    String CAB_LAUGAGE_CAPACITY_KEY = "CabLaugageCapacity";
    String CAB_DRIVER_KEY = "CabDriver";
    String CAB_STATUS_KEY = "CabStatus";
    String CAB_AREA_KEY = "CabArea";

    String BOOKED = "Booked";
    String DRIVER_ID = "driver_id";
    String DRIVER_DETAILS = "driver_details";
    String CAB_ID = "cab_id";
    String CAB_DETAILS = "cab_details";
    String AVAILABLE = "Available";

    String BOOKING_COLLECTION_REFERENCE_KEY = "Booking_Data";
    String BOOKING_USER_NAME_KEY = "user_name";
    String BOOKING_USER_EMAIL_KEY = "user_email";
    String BOOKING_USER_PHONE_NUMBER_KEY = "user_phone_number";
    String BOOKING_START_LATITUDE_KEY = "start_lat";
    String BOOKING_START_LONGITUDE_KEY = "start_long";
    String BOOKING_END_LATITUDE_KEY = "end_lat";
    String BOOKING_END_LONGITUDE_KEY = "end_long";
    String BOOKING_DATE_KEY = "date";
    String BOOKING_TIME_KEY = "time";
    String BOOKING_PRICE_KEY = "price";
    String BOOKING_DISTANCE_KEY = "distance";
    String BOOKING_CAB_NAME_KEY = "cab_name";
    String BOOKING_CAB_NUMBER_KEY = "cab_number";
    String BOOKING_CAB_DRIVER_KEY = "cab_driver";
    String BOOKING_CURRENT_LOCATION = "current_location";
    String BOOKING_DESTINATION_LOCATION = "destination_location";
    String BOOKING_OTP_KEY = "otp";
}
