package com.example.cesplasmaservice.SmartMotelService;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.cesplasmaservice.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DetailedInformationActivity extends AppCompatActivity {
    TextView addressDetailedTextView;
    TextView numberOfFansDetailedTextView;
    TextView numberOfLampsDetailedTextView;
    ImageSlider imageSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_information);

        addressDetailedTextView = findViewById(R.id.addressDetailedTextView);
        numberOfFansDetailedTextView = findViewById(R.id.numberOfFansDetailedTextView);
        numberOfLampsDetailedTextView = findViewById(R.id.numberOfLampsDetailedTextView);
        imageSlider = findViewById(R.id.imageSliderCardView);

        // Lấy thông tin username nhận được từ Login
        SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREFERENCE", MODE_PRIVATE);
        String getUsernameLogin = sharedPreferences.getString("username", "");

        Intent getIntentFromAdapterSearch = getIntent();
        String getAddress = getIntentFromAdapterSearch.getStringExtra("Address");

        displayAddress(getAddress);
        displayNumberOfFans(getUsernameLogin, getAddress);
        displayNumberOfLamps(getUsernameLogin, getAddress);
        displayImageSlider(getUsernameLogin, getAddress);
    }

    void displayAddress(String address)
    {
        String new_address = "";
        for (int i = 0; i < address.length(); i++)
        {
            if(address.charAt(i) != '-') new_address += address.charAt(i);
            else                         new_address += "/";
        }
        addressDetailedTextView.setText(new_address);
    }

    void displayNumberOfFans(String username, String address)
    {
        String path = "Người thuê dịch vụ/" + username + "/Smart Motel/" + address;
        DatabaseReference numFanRef = FirebaseDatabase.getInstance().getReference(path);

        numFanRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String numOfFan = snapshot.child("theNumberOfFans").getValue(String.class);
                numberOfFansDetailedTextView.setText(numOfFan);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void displayNumberOfLamps(String username, String address)
    {
        String path = "Người thuê dịch vụ/" + username + "/Smart Motel/" + address;
        DatabaseReference numLampRef = FirebaseDatabase.getInstance().getReference(path);

        numLampRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String numOfLamps = snapshot.child("theNumberOfLamps").getValue(String.class);
                numberOfLampsDetailedTextView.setText(numOfLamps);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void displayImageSlider(String username, String address)
    {
        final List<SlideModel> slideModelList = new ArrayList<>();
        String path = "Người thuê dịch vụ/" + username + "/Smart Motel/" + address + "/Images";
        DatabaseReference imageReference = FirebaseDatabase.getInstance().getReference(path);

        imageReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    String imageURL = dataSnapshot.getValue(String.class);
                    slideModelList.add(new SlideModel(imageURL, ScaleTypes.FIT));
                    imageSlider.setImageList(slideModelList, ScaleTypes.FIT);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}