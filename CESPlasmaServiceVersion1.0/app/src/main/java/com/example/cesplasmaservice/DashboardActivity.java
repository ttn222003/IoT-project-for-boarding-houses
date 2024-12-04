package com.example.cesplasmaservice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.cesplasmaservice.dashboardNotSignedIn.SmartMotelNotSignedInActivity;
import com.example.cesplasmaservice.dashboardSignedIn.SmartMotelSignedInActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashboardActivity extends AppCompatActivity {
    CardView InformationUserCardView, SmartMotelCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Lấy thông tin username nhận được từ Login
        SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREFERENCE", MODE_PRIVATE);
        String getUsernameLogin = sharedPreferences.getString("username", "");

        InformationUserCardView = findViewById(R.id.informationUserCardView);
        SmartMotelCardView = findViewById(R.id.smartMotelCardView);

        InformationUserCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToInformationUser = new Intent(DashboardActivity.this, InformationUserActivity.class);
                startActivity(intentToInformationUser);
            }
        });

        SmartMotelCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lấy thông tin đăng ký dịch vụ của người dùng
                DatabaseReference refStateSmartMotel = FirebaseDatabase.getInstance().getReference("Người thuê dịch vụ/" + getUsernameLogin + "/Smart Motel/Register");

                refStateSmartMotel.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean stateSmartMotel = snapshot.getValue(boolean.class);

                        // Nếu đăng ký rồi thì chuyển sang Smart Motel SignedInActivity
                        if(stateSmartMotel)
                        {
                            Intent intentToSmartMotel = new Intent(DashboardActivity.this, SmartMotelSignedInActivity.class);
                            startActivity(intentToSmartMotel);
                        }
                        // Nếu chưa đăng ký thì chuyển sang Smart Motel NotSignedInActivity
                        else
                        {
                            Intent intentToSmartMotel = new Intent(DashboardActivity.this, SmartMotelNotSignedInActivity.class);
                            startActivity(intentToSmartMotel);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }
}