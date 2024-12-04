package com.example.cesplasmaservice.dashboardSignedIn;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.cesplasmaservice.R;
import com.example.cesplasmaservice.SmartMotelService.EditSmartMotelServiceActivity;
import com.example.cesplasmaservice.SmartMotelService.ManageSmartMotelServiceActivity;
import com.example.cesplasmaservice.SmartMotelService.RegisterSmartMotelActivity;

public class SmartMotelSignedInActivity extends AppCompatActivity {
    CardView EditInformationMotelCardView, ManageMotelCardView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_motel_signed_in);

        // Lấy thông tin username nhận được từ Login
        SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREFERENCE", MODE_PRIVATE);
        String getUsernameLogin = sharedPreferences.getString("username", "");

        EditInformationMotelCardView = findViewById(R.id.editInformationMotelCardView);
        ManageMotelCardView = findViewById(R.id.manageMotelCardView);

        EditInformationMotelCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(getUsernameLogin);
            }
        });

        ManageMotelCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToManageSmartMotelService = new Intent(SmartMotelSignedInActivity.this, ManageSmartMotelServiceActivity.class);
                startActivity(intentToManageSmartMotelService);
            }
        });
    }

    private void showDialog(String username) {
        // Hiển thị dialog của Activity muốn chuyển đến
        ConstraintLayout RegisterAndEditServiceConstraintLayout = findViewById(R.id.dialogRegisterAndEditServiceConstraintLayout);
        View view = LayoutInflater.from(SmartMotelSignedInActivity.this).inflate(R.layout.dialog_box_register_and_edit_information_motel_service, RegisterAndEditServiceConstraintLayout);
        Button RegisterButton = view.findViewById(R.id.registerButton);
        Button EditButton = view.findViewById(R.id.editButton);

        AlertDialog.Builder builder = new AlertDialog.Builder(SmartMotelSignedInActivity.this);

        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        // Chuyển đến RegisterSmartMotelActivity
        RegisterButton.findViewById(R.id.registerButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToRegisterSmartMotelService = new Intent(SmartMotelSignedInActivity.this, RegisterSmartMotelActivity.class);
                startActivity(intentToRegisterSmartMotelService);
                alertDialog.dismiss();
            }
        });

        // Chuyển đến EditSmartMotelServiceActivity
        EditButton.findViewById(R.id.editButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToEditSmartMotelService = new Intent(SmartMotelSignedInActivity.this, EditSmartMotelServiceActivity.class);
                startActivity(intentToEditSmartMotelService);
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }
}