package com.example.ottomate;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;
import java.util.Map;

public class LivingRoom extends AppCompatActivity {

    private Chronometer mChronometer_bulb, mChronometer_ac, mChronometer_fan, mChronometer_tv;
    private CountDownTimer mCountDownTimer;
    private static final String CHANNEL_ID = "_id_living_room";
    private long mElapsedTime;
    private int mPowerBulb, mPowerAC, mPowerFan, mPowerTV;
    private String LIVING_ROOM_POWER_BULB_KEY = "living_room_bulb";
    private String LIVING_ROOM_POWER_AC_KEY = "living_room_ac";
    private String LIVING_ROOM_POWER_FAN_KEY = "living_room_fan";
    private String LIVING_ROOM_POWER_TV_KEY = "living_room_tv";
    private int LIVING_ROOM_DEFAULT_POWER_BULB_VALUE = 7;
    private int LIVING_ROOM_DEFAULT_POWER_AC_VALUE = 2000;
    private int LIVING_ROOM_DEFAULT_POWER_FAN_VALUE = 75;
    private int LIVING_ROOM_DEFAULT_POWER_TV_VALUE = 100;

    //text views
    private TextView mBulbPowerTextView, mACPowerTextView, mFanPowerTextView, mTVPowerTextView;

    private DatabaseReference mBulbReference;
    private DatabaseReference mACReference;
    private DatabaseReference mFanReference;
    private DatabaseReference mTVReference;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.living_room);

        Switch switch_bulb, switch_ac, switch_fan, switch_tv;

        //firebase instances
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        mBulbReference = mFirebaseDatabase.getReference().child("Living Room").child("Light Bulb");
        mACReference = mFirebaseDatabase.getReference().child("Living Room").child("AC");
        mFanReference = mFirebaseDatabase.getReference().child("Living Room").child("Fan");
        mTVReference = mFirebaseDatabase.getReference().child("Living Room").child("TV");

        //first of all fetch all the values from firebase
        FirebaseRemoteConfigSettings configSettings =new FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(1).build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        Map<String, Object> defaultConfigMap = new HashMap<>();
        defaultConfigMap.put(LIVING_ROOM_POWER_AC_KEY, LIVING_ROOM_DEFAULT_POWER_AC_VALUE);
        defaultConfigMap.put(LIVING_ROOM_POWER_BULB_KEY, LIVING_ROOM_DEFAULT_POWER_BULB_VALUE);
        defaultConfigMap.put(LIVING_ROOM_POWER_FAN_KEY, LIVING_ROOM_DEFAULT_POWER_FAN_VALUE);
        defaultConfigMap.put(LIVING_ROOM_POWER_TV_KEY, LIVING_ROOM_DEFAULT_POWER_TV_VALUE);
        mFirebaseRemoteConfig.setDefaultsAsync(defaultConfigMap);
        fetchConfig();

        mChronometer_bulb = findViewById(R.id.chronometer_bulb);
        mChronometer_bulb.setVisibility(View.INVISIBLE);

        mChronometer_ac = findViewById(R.id.chronometer_ac);
        mChronometer_ac.setVisibility(View.INVISIBLE);

        mChronometer_fan = findViewById(R.id.chronometer_fan);
        mChronometer_fan.setVisibility(View.INVISIBLE);

        mChronometer_tv = findViewById(R.id.chronometer_tv);
        mChronometer_tv.setVisibility(View.INVISIBLE);

        mBulbPowerTextView = findViewById(R.id.power_bulb);
        mACPowerTextView = findViewById(R.id.power_ac);
        mFanPowerTextView = findViewById(R.id.power_fan);
        mTVPowerTextView = findViewById(R.id.power_tv);

        mPowerBulb = LIVING_ROOM_DEFAULT_POWER_BULB_VALUE;
        mPowerAC = LIVING_ROOM_DEFAULT_POWER_AC_VALUE;
        mPowerFan = LIVING_ROOM_DEFAULT_POWER_FAN_VALUE;
        mPowerTV = LIVING_ROOM_DEFAULT_POWER_TV_VALUE;

        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.mipmap.icon_yellow)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_yellow))
                .setContentTitle("Ottomate Says Save Electricity")
                .setContentText("Some devices are consuming more power")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // notificationId is a unique int for each notification that you must define
        final int notificationId = 100;

        createNotificationChannel();

        switch_bulb = findViewById(R.id.switch_bulb);
        switch_bulb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    //ON state
                    mChronometer_bulb.start();
                    mChronometer_bulb.setBase(SystemClock.elapsedRealtime());

                    mCountDownTimer = new CountDownTimer(5000, 100) {

                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            notificationManager.notify(notificationId, builder.build());
                        }
                    }.start();
                }
                else{
                    //OFF state
                    mChronometer_bulb.stop();
                    showElapsedTime(mChronometer_bulb, mPowerBulb, mBulbReference);
                    if (mElapsedTime < 5000){
                        mCountDownTimer.cancel();
                    }
                }
            }
        });

        switch_ac = findViewById(R.id.switch_ac);
        switch_ac.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    //ON state
                    mChronometer_ac.start();
                    mChronometer_ac.setBase(SystemClock.elapsedRealtime());

                    mCountDownTimer = new CountDownTimer(5000, 100) {

                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            notificationManager.notify(notificationId, builder.build());
                        }
                    }.start();
                }
                else{
                    //OFF state
                    mChronometer_ac.stop();
                    showElapsedTime(mChronometer_ac, mPowerAC, mACReference);
                    if (mElapsedTime < 5000){
                        mCountDownTimer.cancel();
                    }
                }
            }
        });

        switch_fan = findViewById(R.id.switch_fan);
        switch_fan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    //ON state
                    mChronometer_fan.start();
                    mChronometer_fan.setBase(SystemClock.elapsedRealtime());

                    mCountDownTimer = new CountDownTimer(5000, 100) {

                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            notificationManager.notify(notificationId, builder.build());
                        }
                    }.start();
                }
                else{
                    //OFF state
                    mChronometer_fan.stop();
                    showElapsedTime(mChronometer_fan, mPowerFan, mFanReference);
                    if (mElapsedTime < 5000){
                        mCountDownTimer.cancel();
                    }
                }
            }
        });

        switch_tv = findViewById(R.id.switch_tv);
        switch_tv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    //ON state
                    mChronometer_tv.start();
                    mChronometer_tv.setBase(SystemClock.elapsedRealtime());

                    mCountDownTimer = new CountDownTimer(5000, 100) {

                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            notificationManager.notify(notificationId, builder.build());
                        }
                    }.start();
                }
                else{
                    //OFF state
                    mChronometer_tv.stop();
                    showElapsedTime(mChronometer_tv, mPowerTV, mTVReference);
                    if (mElapsedTime < 5000){
                        mCountDownTimer.cancel();
                    }
                }
            }
        });
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showElapsedTime(Chronometer chronometer, int power, DatabaseReference databaseReference){
        mElapsedTime = SystemClock.elapsedRealtime() - chronometer.getBase();
        Toast.makeText(getApplicationContext(), "Elapsed milliseconds: " + mElapsedTime,
                Toast.LENGTH_LONG).show();
        Device device = new Device((double) mElapsedTime, power);
        databaseReference.push().setValue(device);
    }

    public void fetchConfig(){
        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            boolean updated = task.getResult();
                            Toast.makeText(getApplicationContext(), "Fetch and activate succeeded",
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getApplicationContext(), "Fetch failed",
                                    Toast.LENGTH_SHORT).show();
                        }

                        //applying the data if the fetching is successful
                        mACPowerTextView.setText(mFirebaseRemoteConfig.getString(LIVING_ROOM_POWER_AC_KEY));
                        mBulbPowerTextView.setText(mFirebaseRemoteConfig.getString(LIVING_ROOM_POWER_BULB_KEY));
                        mFanPowerTextView.setText(mFirebaseRemoteConfig.getString(LIVING_ROOM_POWER_FAN_KEY));
                        mTVPowerTextView.setText(mFirebaseRemoteConfig.getString(LIVING_ROOM_POWER_TV_KEY));
                        LIVING_ROOM_DEFAULT_POWER_AC_VALUE = (int) mFirebaseRemoteConfig.getLong(LIVING_ROOM_POWER_AC_KEY);
                        LIVING_ROOM_DEFAULT_POWER_BULB_VALUE = (int) mFirebaseRemoteConfig.getLong(LIVING_ROOM_POWER_BULB_KEY);
                        LIVING_ROOM_DEFAULT_POWER_FAN_VALUE = (int) mFirebaseRemoteConfig.getLong(LIVING_ROOM_POWER_FAN_KEY);
                        LIVING_ROOM_DEFAULT_POWER_TV_VALUE = (int) mFirebaseRemoteConfig.getLong(LIVING_ROOM_POWER_TV_KEY);
                        Log.v("Here you go: ", ""+LIVING_ROOM_DEFAULT_POWER_AC_VALUE);
                    }
                });
    }
}
