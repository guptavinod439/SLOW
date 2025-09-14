package com.example.gupta.slowdriver.Remote;

import com.example.gupta.slowdriver.Model.FCMResponse;
import com.example.gupta.slowdriver.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface IFCMService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAfn9IzT4:APA91bE1Y4n7QZHeaYaIs6iECPieV4qNA12rwqlkAgoUVKT1bDjMfG4D_jY9jexmREJ75OfmAdy15Q79nA-mUtuGLADukaa-g9Atk5MyYyI2OWpkY7jNq4k7oWJ2vcBY8Cl4ZrKHnhPY"
    })
    @POST("fcm/send")
    Call<FCMResponse> sendMessage(@Body Sender body);
}
