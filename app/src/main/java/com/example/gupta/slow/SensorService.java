package com.example.gupta.slow;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.gsm.SmsManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by fabio on 30/01/2016.
 */
public class SensorService extends Service {


    String s;
    //String mPermission= Manifest.permission.READ_SMS;
    private static final int REQUEST_CODE_PERMISSION = 2;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;
    TextView textView;
    String message,connected;
    public static int n=100;
    GPSTracker gps;


    private SensorManager sm;
    private float acelVal;
    private float acelLast;
    private float shake;
    public int counter=0;
    public SensorService(Context applicationContext) {
        super();
        Log.i("HERE", "here I am!");
    }

    public SensorService() {
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTimer();
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
        Intent broadcastIntent = new Intent(this, SensorRestarterBroadcastReceiver.class);

        sendBroadcast(broadcastIntent);
        stoptimertask();
    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000); //
    }

    /**
     * it sets the timer to print the counter every x seconds 
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                sm.registerListener(sensorListener, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);

                acelVal = SensorManager.GRAVITY_EARTH;
                acelLast = SensorManager.GRAVITY_EARTH;
                shake = 0.00f;
                Log.i("in timer", "in timer ++++  "+ (counter++));
            }
        };
    }

    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    private final SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            acelLast = acelVal ;
            acelVal = (float) Math.sqrt((double)(x*x + y*y + z*z));
            float delta = acelVal - acelLast ;
            shake = shake * 0.9f + delta ;
            if (shake >50){
                Log.d("tag","shaked");
                Toast toast = Toast.makeText(getApplicationContext() , "DO NOT SHAKE ME" , Toast.LENGTH_LONG);
                location();
              toast.show();
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public  void location(){
       /* try {
            if (ActivityCompat.checkSelfPermission(this, mPermission) != MockPackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{mPermission}, REQUEST_CODE_PERMISSION);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        Log.d("tag","location");

        gps = new GPSTracker(SensorService.this);
        if (gps.canGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();


            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: "
                    + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            String lat=String.valueOf(latitude);
            String longi=String.valueOf(longitude);
            message=message+"la:"+lat+"lo"+longi;
            Date currentTime= Calendar.getInstance().getTime();
            Log.d("tag",currentTime.toString());

            n--;
            Log.d("location",message);
            //connected = "false";


                 /*   ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                    if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                        //we are connected to a network
                        connected = "true";
                        Log.d("firebase","firebase");
                    }*/

            // connected = "false";
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> part = smsManager.divideMessage(message);
            smsManager.sendMultipartTextMessage("+919768807310", null, part, null, null);

            getContentResolver().delete(Uri.parse("content://sms/outbox"), "address = ? and body = ?", new String[]{"+918693042739", "sms "});
            getContentResolver().delete(Uri.parse("content://sms/sent"), "address = ? and body = ?", new String[]{"+918693042739", "sms "});
            Log.d("sms", connected + "sms sent");

        }


    }
}