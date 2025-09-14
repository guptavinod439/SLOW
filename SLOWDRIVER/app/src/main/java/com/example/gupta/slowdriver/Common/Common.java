package com.example.gupta.slowdriver.Common;

import android.location.Location;

import com.example.gupta.slowdriver.Model.User;
import com.example.gupta.slowdriver.Remote.FCMClient;
import com.example.gupta.slowdriver.Remote.IFCMService;
import com.example.gupta.slowdriver.Remote.IGoogleAPI;
import com.example.gupta.slowdriver.Remote.RetrofitClient;


public class Common {

    public static String currentToken="";



    public static final String driver_tbl="Drivers";
    public static final String user_driver_tbl="DriversInformation";
    public static final String user_rider_tbl="RidersInformation";
    public static final String pickup_request_tbl="PickUpRequest";
    public static final String token_tbl="Tokens";

    public static User currentUser;

    public static Location mLastLocation=null;


    public static final String baseURL="https://maps.googleapis.com";
    public static final String fcmURL="https://fcm.googleapis.com/";
    public static  IGoogleAPI getGoogleAPI(){
        return RetrofitClient.getClient(baseURL).create(IGoogleAPI.class);
    }
    public static IFCMService getFCMService(){
        return FCMClient.getClient(fcmURL).create(IFCMService.class);
    }

}
