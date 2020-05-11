package com.example.ottomate;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
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

public class Kitchen extends AppCompatActivity {

    private Chronometer mChronometer_bulb, mChronometer_chimney, mChronometer_microwave;
    private CountDownTimer mCountDownTimer;
    private static final String CHANNEL_ID = "_id_kitchen";
    private long mElapsedTime;
    private int mPowerBulb, mPowerChimney, mPowerMicrowave;
    private String KITCHEN_POWER_BULB_KEY = "bedroom1_bulb";
    private String KITCHEN_POWER_CHIMNEY_KEY = "bedroom1_ac";
    private String KITCHEN_POWER_MICROWAVE_KEY = "bedroom1_fan";
    private int KITCHEN_DEFAULT_POWER_BULB_VALUE = 7;
    private int KITCHEN_DEFAULT_POWER_CHIMNEY_VALUE = 1500;
    private int KITCHEN_DEFAULT_POWER_MICROWAVE_VALUE = 60;

    //text views
    private TextView mBulbPowerTextView, mChimneyPowerTextView, mMicrowavePowerTextView;

    private DatabaseReference mBulbReference;
    private DatabaseReference mChimneyReference;
    private DatabaseReference mMicrowaveReference;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kitchen);

        Switch switch_bulb, switch_chimney, switch_microwave;

        //firebase instances
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        mBulbReference = mFirebaseDatabase.getReference().child("Kitchen").child("Light Bulb");
        mChimneyReference = mFirebaseDatabase.getReference().child("Kitchen").child("Chimney");
        mMicrowaveReference = mFirebaseDatabase.getReference().child("Kitchen").child("Microwave");

        //first of all fetch all the values from firebase
        FirebaseRemoteConfigSettings configSettings =new FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(1).build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        Map<String, Object> defaultConfigMap = new HashMap<>();
        defaultConfigMap.put(KITCHEN_POWER_BULB_KEY, KITCHEN_DEFAULT_POWER_BULB_VALUE);
        defaultConfigMap.put(KITCHEN_POWER_CHIMNEY_KEY, KITCHEN_DEFAULT_POWER_CHIMNEY_VALUE);
        defaultConfigMap.put(KITCHEN_POWER_MICROWAVE_KEY, KITCHEN_DEFAULT_POWER_MICROWAVE_VALUE);
        mFirebaseRemoteConfig.setDefaultsAsync(defaultConfigMap);
        fetchConfig();

        mChronometer_bulb = findViewById(R.id.chronometer_bulb);
        mChronometer_bulb.setVisibility(View.INVISIBLE);

        mChronometer_chimney = findViewById(R.id.chronometer_chimney);
        mChronometer_chimney.setVisibility(View.INVISIBLE);

        mChronometer_microwave = findViewById(R.id.chronometer_microwave);
        mChronometer_microwave.setVisibility(View.INVISIBLE);

        mBulbPowerTextView = findViewById(R.id.power_bulb);
        mChimneyPowerTextView = findViewById(R.id.power_chimney);
        mMicrowavePowerTextView = findViewById(R.id.power_microwave);

        mPowerBulb = KITCHEN_DEFAULT_POWER_BULB_VALUE;
        mPowerChimney = KITCHEN_DEFAULT_POWER_CHIMNEY_VALUE;
        mPowerMicrowave = KITCHEN_DEFAULT_POWER_MICROWAVE_VALUE;

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

        switch_chimney = findViewById(R.id.switch_chimney);
        switch_chimney.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    //ON state
                    mChronometer_chimney.start();
                    mChronometer_chimney.setBase(SystemClock.elapsedRealtime());

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
                    mChronometer_chimney.stop();
                    showElapsedTime(mChronometer_chimney, mPowerChimney, mChimneyReference);
                    if (mElapsedTime < 5000){
                        mCountDownTimer.cancel();
                    }
                }
            }
        });

        switch_microwave = findViewById(R.id.switch_microwave);
        switch_microwave.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    //ON state
                    mChronometer_microwave.start();
                    mChronometer_microwave.setBase(SystemClock.elapsedRealtime());

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
                    mChronometer_microwave.stop();
                    showElapsedTime(mChronometer_microwave, mPowerMicrowave, mMicrowaveReference);
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
                        mChimneyPowerTextView.setText(mFirebaseRemoteConfig.getString(KITCHEN_POWER_CHIMNEY_KEY));
                        mBulbPowerTextView.setText(mFirebaseRemoteConfig.getString(KITCHEN_POWER_BULB_KEY));
                        mMicrowavePowerTextView.setText(mFirebaseRemoteConfig.getString(KITCHEN_POWER_MICROWAVE_KEY));
                        KITCHEN_DEFAULT_POWER_CHIMNEY_VALUE = (int) mFirebaseRemoteConfig.getLong(KITCHEN_POWER_CHIMNEY_KEY);
                        KITCHEN_DEFAULT_POWER_BULB_VALUE = (int) mFirebaseRemoteConfig.getLong(KITCHEN_POWER_BULB_KEY);
                        KITCHEN_DEFAULT_POWER_MICROWAVE_VALUE = (int) mFirebaseRemoteConfig.getLong(KITCHEN_POWER_MICROWAVE_KEY);
                    }
                });
    }
}
