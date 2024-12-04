package com.example.smartmotel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.smartmotel.dashboard_signedIn.SmartMotelSignedInActivity;
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

        // Lay thong tin username tu LoginActivity
        Intent getUsernameFromLogin = getIntent();
        String getUsername = getUsernameFromLogin.getStringExtra("username");
        InformationUserCardView = findViewById(R.id.informationUserCardView);
        SmartMotelCardView = findViewById(R.id.smartMotelCardView);

        InformationUserCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToInformationUser = new Intent(DashboardActivity.this, InformationUserActivity.class);
                intentToInformationUser.putExtra("username", getUsername);
                startActivity(intentToInformationUser);
            }
        });

        SmartMotelCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference refStateSmartMotel = FirebaseDatabase.getInstance().getReference(getUsername + "/smartMotel/state");
                refStateSmartMotel.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean stateSmartMotel = snapshot.getValue(boolean.class);
                        if(stateSmartMotel)
                        {
                            Intent intentToSmartMotel = new Intent(DashboardActivity.this, SmartMotelSignedInActivity.class);
                            intentToSmartMotel.putExtra("username", getUsername);
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