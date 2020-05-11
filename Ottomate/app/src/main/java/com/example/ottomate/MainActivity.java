package com.example.ottomate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextClock;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    CardView mLivingRoom, mBedroom1, mKitchen, mBedroom2, mBathroom, mLobby;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLivingRoom = findViewById(R.id.living_room);
        mLivingRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LivingRoom.class);
                startActivity(intent);
            }
        });

        mBedroom1 = findViewById(R.id.bedroom1);
        mBedroom1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Bedroom1.class);
                startActivity(intent);
            }
        });

        mKitchen = findViewById(R.id.kitchen);
        mKitchen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Kitchen.class);
                startActivity(intent);
            }
        });

        mBedroom2 = findViewById(R.id.bedroom2);
        mBedroom2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Bedroom2.class);
                startActivity(intent);
            }
        });

        mBathroom = findViewById(R.id.bathroom);
        mBathroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Bathroom.class);
                startActivity(intent);
            }
        });

        mLobby = findViewById(R.id.lobby);
        mLobby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Lobby.class);
                startActivity(intent);
            }
        });
    }
}
