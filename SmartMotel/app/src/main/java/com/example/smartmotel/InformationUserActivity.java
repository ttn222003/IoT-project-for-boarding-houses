package com.example.smartmotel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InformationUserActivity extends AppCompatActivity {
    TextView usernameTextView, stateTextView, addressTextView;
    Button rentButton, unRentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_user);

        usernameTextView = findViewById(R.id.usernameTextView);
        stateTextView = findViewById(R.id.stateTextView);
        addressTextView = findViewById(R.id.addressTextView);
        rentButton = findViewById(R.id.rentButton);
        unRentButton = findViewById(R.id.unRentButton);

        // Lấy thông tin username nhận được từ DashboardActivity
        SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREFERENCE", MODE_PRIVATE);
        String getUsernameLogin = sharedPreferences.getString("username", "");

        getAndDisplayInformationUser(getUsernameLogin);

        rentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToSearchSmartMotel = new Intent(InformationUserActivity.this, SearchSmartMotelActivity.class);
                intentToSearchSmartMotel.putExtra("username", getUsernameLogin);
                startActivity(intentToSearchSmartMotel);
            }
        });

        unRentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = "Người thuê trọ/" + getUsernameLogin + "/Đã thuê";
                DatabaseReference stateRef = FirebaseDatabase.getInstance().getReference(path + "/State");
                DatabaseReference addressRef = FirebaseDatabase.getInstance().getReference(path + "/address");
                DatabaseReference nameOwnerRef = FirebaseDatabase.getInstance().getReference(path + "/name");

                stateRef.setValue(false);

                nameOwnerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String nameOwner = snapshot.getValue(String.class);

                        addressRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String address = snapshot.getValue(String.class);
                                String pathAvailableMotel = "Người thuê dịch vụ/" + nameOwner + "/Smart Motel/" + address + "/Available";

                                DatabaseReference availableRef = FirebaseDatabase.getInstance().getReference(pathAvailableMotel);

                                availableRef.setValue(false);
                                addressRef.setValue("");
                                nameOwnerRef.setValue("");

                                showCancelRegistrationAlert();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    /*
    @
    @
    @
    */
    private void getAndDisplayInformationUser(String username)
    {
        String path = "Người thuê trọ/" + username + "/Đã thuê";
        DatabaseReference stateRef = FirebaseDatabase.getInstance().getReference(path + "/State");
        DatabaseReference addressRef = FirebaseDatabase.getInstance().getReference(path + "/address");
        DatabaseReference nameRef = FirebaseDatabase.getInstance().getReference("Người thuê trọ/" + username + "/name");

        stateRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue(boolean.class) == true)
                {
                    stateTextView.setText("Đã thuê trọ");
                }
                else
                {
                    stateTextView.setText("Chưa thuê trọ");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        addressRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String address = snapshot.getValue(String.class);
                String new_address = "";

                for(int i = 0; i < address.length(); i++){
                    if(address.charAt(i) != '-') new_address += address.charAt(i);
                    else                         new_address += "/";
                }
                addressTextView.setText(new_address);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        nameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.getValue(String.class);
                usernameTextView.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /*
    @Function: Hiển thị thông báo đã hủy trọ thành công
    @Parameter: Không có
    @Return: Không có
    */
    private void showCancelRegistrationAlert() {
        ConstraintLayout cancelRegistrationConstraintLayout = findViewById(R.id.dialogBoxCancelRegistrationAlertLayout);
        View view = LayoutInflater.from(InformationUserActivity.this).inflate(R.layout.dialog_box_cancel_registration, cancelRegistrationConstraintLayout);

        AlertDialog.Builder builder = new AlertDialog.Builder(InformationUserActivity.this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}