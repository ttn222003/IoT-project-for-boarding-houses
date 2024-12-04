package com.example.smartmotel;

import android.content.Intent;
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

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DetailedInformationSearchActivity extends AppCompatActivity {
    TextView addressDetailedTextView;
    TextView numberOfFansDetailedTextView;
    TextView numberOfLampsDetailedTextView;
    ImageSlider imageSlider;
    Button registerButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_information_search);

        addressDetailedTextView = findViewById(R.id.addressDetailedTextView);
        numberOfFansDetailedTextView = findViewById(R.id.numberOfFansDetailedTextView);
        numberOfLampsDetailedTextView = findViewById(R.id.numberOfLampsDetailedTextView);
        imageSlider = findViewById(R.id.imageSliderCardView);
        registerButton = findViewById(R.id.registerButton);

        Intent getIntentFromAdapterSearch = getIntent();
        String getAddress = getIntentFromAdapterSearch.getStringExtra("Address");
        String getUsername = getIntentFromAdapterSearch.getStringExtra("Name");
        String getUsernameLogin = getIntentFromAdapterSearch.getStringExtra("Username Login");

        displayAddress(getAddress);
        displayNumberOfFans(getUsername, getAddress);
        displayNumberOfLamps(getUsername, getAddress);
        displayImageSlider(getUsername, getAddress);
        registSmartMotel(getUsernameLogin, getAddress, getUsername);
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

    void registSmartMotel(String usernameLogin, String address, String username)
    {
        String pathState = "Người thuê trọ/" + usernameLogin + "/Đã thuê/State";
        String pathAddress = "Người thuê trọ/" + usernameLogin + "/Đã thuê/address";
        String pathUsername = "Người thuê trọ/" + usernameLogin + "/Đã thuê/name";
        String pathMotelAvailable = "Người thuê dịch vụ/" + username + "/Smart Motel/" + address + "/Available";

        DatabaseReference stateRef = FirebaseDatabase.getInstance().getReference(pathState);
        DatabaseReference addressRef = FirebaseDatabase.getInstance().getReference(pathAddress);
        DatabaseReference usernameRef = FirebaseDatabase.getInstance().getReference(pathUsername);
        DatabaseReference motelAvailableRef = FirebaseDatabase.getInstance().getReference(pathMotelAvailable);


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stateRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.getValue(boolean.class) == false)
                        {
                            motelAvailableRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.getValue(boolean.class) == false)
                                    {
                                        stateRef.setValue(true);
                                        addressRef.setValue(address);
                                        usernameRef.setValue(username);
                                        motelAvailableRef.setValue(true);
                                        showRegisterSuccessAlert();
                                    }
                                    else
                                    {
                                        showUnavailableMotelAlert();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        else
                        {
                            showRegisteredAlert();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    /*
    @Function: Hiển thị thông báo trọ đã thuê rồi
    @Parameter: Không có
    @Return: Không có
    */
    private void showRegisteredAlert() {
        ConstraintLayout registeredAlertConstraintLayout = findViewById(R.id.dialogBoxRegisteredAlertLayout);
        View view = LayoutInflater.from(DetailedInformationSearchActivity.this).inflate(R.layout.dialog_box_registered_alert, registeredAlertConstraintLayout);

        AlertDialog.Builder builder = new AlertDialog.Builder(DetailedInformationSearchActivity.this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /*
    @Function: Hiển thị thông báo thuê trọ thành công
    @Parameter: Không có
    @Return: Không có
    */
    private void showRegisterSuccessAlert() {
        ConstraintLayout registeredSuccessAlertConstraintLayout = findViewById(R.id.dialogBoxRegisterSuccessAlertLayout);
        View view = LayoutInflater.from(DetailedInformationSearchActivity.this).inflate(R.layout.dialog_box_register_success, registeredSuccessAlertConstraintLayout);

        AlertDialog.Builder builder = new AlertDialog.Builder(DetailedInformationSearchActivity.this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /*
    @Function: Hiển thị thông báo trọ đã có người thuê
    @Parameter: Không có
    @Return: Không có
    */
    private void showUnavailableMotelAlert() {
        ConstraintLayout unavailableMotelAlertConstraintLayout = findViewById(R.id.dialogBoxUnavalibleMotelAlertLayout);
        View view = LayoutInflater.from(DetailedInformationSearchActivity.this).inflate(R.layout.dialog_box_unavailable_motel, unavailableMotelAlertConstraintLayout);

        AlertDialog.Builder builder = new AlertDialog.Builder(DetailedInformationSearchActivity.this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}