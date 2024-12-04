package com.example.smartmotel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ManageYourSmartMotelActivity extends AppCompatActivity {
    CardView informationUserCardView, controlSmartMotelCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_your_smart_motel);
        informationUserCardView = findViewById(R.id.informationUserCardView);
        controlSmartMotelCardView = findViewById(R.id.controlSmartMotelCardView);

        // Lấy thông tin username nhận được từ DashboardActivity
        SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREFERENCE", MODE_PRIVATE);
        String getUsernameLogin = sharedPreferences.getString("username", "");

        String path = "Người thuê trọ/" + getUsernameLogin + "/Đã thuê/State";
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(path);

        informationUserCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Intent intentToInformationUser = new Intent(ManageYourSmartMotelActivity.this, InformationUserActivity.class);
                        startActivity(intentToInformationUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        controlSmartMotelCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.getValue(boolean.class))
                        {
                            Intent intentToControlSmartMotel = new Intent(ManageYourSmartMotelActivity.this, ControlSmartMotelActivity.class);
                            startActivity(intentToControlSmartMotel);
                        }
                        else
                        {
                            showNotRegisteredSuccessAlert();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });


        // Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottomManageMotel);

        bottomNavigationView.setOnItemSelectedListener(menuItem -> {
            if (menuItem.getItemId() == R.id.bottomManageMotel) {
                return true;
            } else if (menuItem.getItemId() == R.id.bottomSearch) {
                startActivity(new Intent(getApplicationContext(), SearchSmartMotelActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
                finish();
                return true;
            }
            return false;
        });
    }

    /*
    @Function: Hiển thị thông báo chưa thuê trọ
    @Parameter: Không có
    @Return: Không có
    */
    private void showNotRegisteredSuccessAlert() {
        ConstraintLayout notRegisteredSuccessAlertConstraintLayout = findViewById(R.id.dialogBoxNotRegisteredAlertLayout);
        View view = LayoutInflater.from(ManageYourSmartMotelActivity.this).inflate(R.layout.dialog_box_not_registered, notRegisteredSuccessAlertConstraintLayout);

        AlertDialog.Builder builder = new AlertDialog.Builder(ManageYourSmartMotelActivity.this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}