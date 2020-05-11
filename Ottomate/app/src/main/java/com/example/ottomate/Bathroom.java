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

public class Bathroom extends AppCompatActivity {

    private Chronometer mChronometer_bulb, mChronometer_heater, mChronometer_exhaust;
    private CountDownTimer mCountDownTimer;
    private static final String CHANNEL_ID = "_id";
    private long mElapsedTime;
    private int mPowerBulb, mPowerHeater, mPowerExhaust;
    private String BATHROOM_POWER_BULB_KEY = "bedroom1_bulb";
    private String BATHROOM_POWER_HEATER_KEY = "bedroom1_ac";
    private String BATHROOM_POWER_EXHAUST_KEY = "bedroom1_fan";
    private int BATHROOM_DEFAULT_POWER_BULB_VALUE = 7;
    private int BATHROOM_DEFAULT_POWER_HEATER_VALUE = 1500;
    private int BATHROOM_DEFAULT_POWER_EXHAUST_VALUE = 60;

    //text views
    private TextView mBulbPowerTextView, mHeaterPowerTextView, mExhaustPowerTextView;

    private DatabaseReference mBulbReference;
    private DatabaseReference mHeaterReference;
    private DatabaseReference mExhaustReference;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bathroom);

        Switch switch_bulb, switch_heater, switch_exhaust;

        //firebase instances
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        mBulbReference = mFirebaseDatabase.getReference().child("Bathroom").child("Light Bulb");
        mHeaterReference = mFirebaseDatabase.getReference().child("Bathroom").child("Water Heater");
        mExhaustReference = mFirebaseDatabase.getReference().child("Bathroom").child("Exhaust Fan");

        //first of all fetch all the values from firebase
        FirebaseRemoteConfigSettings configSettings =new FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(1).build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        Map<String, Object> defaultConfigMap = new HashMap<>();
        defaultConfigMap.put(BATHROOM_POWER_HEATER_KEY, BATHROOM_DEFAULT_POWER_HEATER_VALUE);
        defaultConfigMap.put(BATHROOM_POWER_BULB_KEY, BATHROOM_DEFAULT_POWER_BULB_VALUE);
        defaultConfigMap.put(BATHROOM_POWER_EXHAUST_KEY, BATHROOM_DEFAULT_POWER_EXHAUST_VALUE);
        mFirebaseRemoteConfig.setDefaultsAsync(defaultConfigMap);
        fetchConfig();

        mChronometer_bulb = findViewById(R.id.chronometer_bulb);
        mChronometer_bulb.setVisibility(View.INVISIBLE);

        mChronometer_heater = findViewById(R.id.chronometer_heater);
        mChronometer_heater.setVisibility(View.INVISIBLE);

        mChronometer_exhaust = findViewById(R.id.chronometer_exhaust);
        mChronometer_exhaust.setVisibility(View.INVISIBLE);

        mBulbPowerTextView = findViewById(R.id.power_bulb);
        mHeaterPowerTextView = findViewById(R.id.power_heater);
        mExhaustPowerTextView = findViewById(R.id.power_exhaust);

        mPowerBulb = BATHROOM_DEFAULT_POWER_BULB_VALUE;
        mPowerHeater = BATHROOM_DEFAULT_POWER_HEATER_VALUE;
        mPowerExhaust = BATHROOM_DEFAULT_POWER_EXHAUST_VALUE;

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
                if (isChecked) {
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
                } else {
                    //OFF state
                    mChronometer_bulb.stop();
                    showElapsedTime(mChronometer_bulb, mPowerBulb, mBulbReference);
                    if (mElapsedTime < 5000) {
                        mCountDownTimer.cancel();
                    }
                }
            }
        });

        switch_heater = findViewById(R.id.switch_heater);
        switch_heater.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //ON state
                    mChronometer_heater.start();
                    mChronometer_heater.setBase(SystemClock.elapsedRealtime());

                    mCountDownTimer = new CountDownTimer(5000, 100) {

                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            notificationManager.notify(notificationId, builder.build());
                        }
                    }.start();
                } else {
                    //OFF state
                    mChronometer_heater.stop();
                    showElapsedTime(mChronometer_heater, mPowerHeater, mHeaterReference);
                    if (mElapsedTime < 5000) {
                        mCountDownTimer.cancel();
                    }
                }
            }
        });

        switch_exhaust = findViewById(R.id.switch_exhaust);
        switch_exhaust.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //ON state
                    mChronometer_exhaust.start();
                    mChronometer_exhaust.setBase(SystemClock.elapsedRealtime());

                    mCountDownTimer = new CountDownTimer(5000, 100) {

                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            notificationManager.notify(notificationId, builder.build());
                        }
                    }.start();
                } else {
                    //OFF state
                    mChronometer_exhaust.stop();
                    showElapsedTime(mChronometer_exhaust, mPowerExhaust, mExhaustReference);
                    if (mElapsedTime < 5000) {
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

    private void showElapsedTime(Chronometer chronometer, int power, DatabaseReference databaseReference) {
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
                        mHeaterPowerTextView.setText(mFirebaseRemoteConfig.getString(BATHROOM_POWER_HEATER_KEY));
                        mBulbPowerTextView.setText(mFirebaseRemoteConfig.getString(BATHROOM_POWER_BULB_KEY));
                        mExhaustPowerTextView.setText(mFirebaseRemoteConfig.getString(BATHROOM_POWER_EXHAUST_KEY));
                        BATHROOM_DEFAULT_POWER_HEATER_VALUE = (int) mFirebaseRemoteConfig.getLong(BATHROOM_POWER_HEATER_KEY);
                        BATHROOM_DEFAULT_POWER_BULB_VALUE = (int) mFirebaseRemoteConfig.getLong(BATHROOM_POWER_BULB_KEY);
                        BATHROOM_DEFAULT_POWER_EXHAUST_VALUE = (int) mFirebaseRemoteConfig.getLong(BATHROOM_POWER_EXHAUST_KEY);
                    }
                });
    }
}
