package com.example.gupta.slowdriver;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gupta.slowdriver.Common.Common;
import com.example.gupta.slowdriver.Model.FCMResponse;
import com.example.gupta.slowdriver.Model.Notification;
import com.example.gupta.slowdriver.Model.Sender;
import com.example.gupta.slowdriver.Model.Token;
import com.example.gupta.slowdriver.Remote.IFCMService;
import com.example.gupta.slowdriver.Remote.IGoogleAPI;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerCall extends AppCompatActivity {


    TextView txtTime,txtAddress,txtDistance;
    Button btnCancel,btnAccept;

    MediaPlayer mediaPlayer;
    IGoogleAPI mService;
    IFCMService mFCMService;


    String customerId;
    double lat,lng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_call);

        mService=Common.getGoogleAPI();
        mFCMService = Common.getFCMService();




        txtAddress=(TextView)findViewById(R.id.txtAddress);
        txtDistance=(TextView)findViewById(R.id.txtDistance);
        txtTime=(TextView)findViewById(R.id.txtTime);

        btnAccept = (Button)findViewById(R.id.btnAccept);
        btnCancel = (Button)findViewById(R.id.btnDecline);



        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!TextUtils.isEmpty(customerId))
                    cancelBooking(customerId);

            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!TextUtils.isEmpty(customerId))
                    acceptbooking(customerId);


                Intent intent = new Intent(CustomerCall.this,DriverTracking.class);
                intent.putExtra("lat",lat);
                intent.putExtra("lng",lng);
                intent.putExtra("customerId",customerId);


                startActivity(intent);
                finish();
            }
        });

        mediaPlayer=MediaPlayer.create(this,R.raw.achievementunlocked);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        if(getIntent()!=null){
            lat=getIntent().getDoubleExtra("lat",-1.0);
             lng=getIntent().getDoubleExtra("lng",-1.0);

            customerId = getIntent().getStringExtra("customer");
            getDirection(lat,lng);
        }






    }

    private void acceptbooking(String customerId) {

        Token token = new Token(customerId);

        Notification notification = new Notification("NoticeTSuccess","Driver has accepted your request");
        Sender sender = new Sender(token.getToken(),notification);

        mFCMService.sendMessage(sender)
                .enqueue(new Callback<FCMResponse>() {
                    @Override
                    public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {

                        if(response.body().success==1)
                        {
                            Toast.makeText(CustomerCall.this,"Accepted",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<FCMResponse> call, Throwable t) {

                    }
                });


    }

    private void cancelBooking(String customerId) {

        Token token = new Token(customerId);

        Notification notification = new Notification("NoticeTFail","Driver has cancelled your request");
        Sender sender = new Sender(token.getToken(),notification);

        mFCMService.sendMessage(sender)
                .enqueue(new Callback<FCMResponse>() {
                    @Override
                    public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {

                        if(response.body().success==1)
                        {
                            Toast.makeText(CustomerCall.this,"Cancelled",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<FCMResponse> call, Throwable t) {

                    }
                });
    }

    private void getDirection(double lat,double lng) {



        String requestApi=null;
        try{
            requestApi="https://maps.googleapis.com/maps/api/directions/json?"+
                    "mode=driving&"+
                    "transit_routing_preference=less_driving&"+
                    "origin="+Common.mLastLocation.getLatitude()+","+Common.mLastLocation.getLongitude()+"&"+
                    "destination="+lat+","+lng+"&"+
                    "key="+getResources().getString(R.string.google_direction_api);
            Log.d("tag",requestApi.toString());
            mService.getPath(requestApi)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {

                            try {
                                JSONObject jsonObject=new JSONObject(response.body().toString());
                               JSONArray routes=jsonObject.getJSONArray("routes");
                               JSONObject object=routes.getJSONObject(0);
                               JSONArray legs=object.getJSONArray("legs");
                               JSONObject legsObject=legs.getJSONObject(0);
                               JSONObject distance=legsObject.getJSONObject("distance");

                               txtDistance.setText(distance.getString("text"));

                               JSONObject time=legsObject.getJSONObject("duration");
                               txtTime.setText(time.getString("text"));


                                String address=legsObject.getString("end_address");
                                txtAddress.setText(address);


                            }
                            catch (Exception e){

                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(CustomerCall.this,""+t.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        catch (Exception e){

            e.printStackTrace();

        }
    }


    @Override
    protected void onStop() {
        mediaPlayer.release();
        super.onStop();

    }

    @Override
    protected void onPause() {
        mediaPlayer.release();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mediaPlayer.start();
    }
}
