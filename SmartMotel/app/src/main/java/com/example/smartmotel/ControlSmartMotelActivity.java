package com.example.smartmotel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ControlSmartMotelActivity extends AppCompatActivity {
    Switch fan1, fan2, lamp1, lamp2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_smart_motel);

        fan1 = findViewById(R.id.smartMotelFan1);
        fan2 = findViewById(R.id.smartMotelFan2);
        lamp1 = findViewById(R.id.smartMotelLamp1);
        lamp2 = findViewById(R.id.smartMotelLamp2);

        // Lấy thông tin username nhận được từ Login
        SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREFERENCE", MODE_PRIVATE);
        String getUsernameLogin = sharedPreferences.getString("username", "");

        toggleFan1(getUsernameLogin);
        toggleFan2(getUsernameLogin);
        toggleLamp1(getUsernameLogin);
        toggleLamp2(getUsernameLogin);
    }

    public void toggleFan1(String usernameLogin)
    {
        DatabaseReference refName = FirebaseDatabase.getInstance().getReference("Người thuê trọ/" + usernameLogin + "/Đã thuê/name");
        DatabaseReference refAddress = FirebaseDatabase.getInstance().getReference("Người thuê trọ/" + usernameLogin + "/Đã thuê/address");

        refName.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name ;
                name = snapshot.getValue(String.class);

                refAddress.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String address;
                        address = snapshot.getValue(String.class);

                        DatabaseReference refFan1 = FirebaseDatabase.getInstance().getReference("Người thuê dịch vụ/" + name + "/Smart Motel/" + address + "/Control/fan 1");
                        // Lấy thông tin trạng thái hiện tại của switch (realtime) và set trạng thái hiện tại đó cho switch
                        refFan1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists())
                                {
                                    boolean statusSwitch = snapshot.getValue(boolean.class);
                                    fan1.setChecked(statusSwitch);

                                    // Set trạng thái của switch khi click
                                    fan1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                                            refFan1.setValue(isChecked);
                                        }
                                    });
                                }
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void toggleFan2(String usernameLogin)
    {
        DatabaseReference refName = FirebaseDatabase.getInstance().getReference("Người thuê trọ/" + usernameLogin + "/Đã thuê/name");
        DatabaseReference refAddress = FirebaseDatabase.getInstance().getReference("Người thuê trọ/" + usernameLogin + "/Đã thuê/address");

        refName.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name ;
                name = snapshot.getValue(String.class);

                refAddress.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String address;
                        address = snapshot.getValue(String.class);

                        DatabaseReference refFan2 = FirebaseDatabase.getInstance().getReference("Người thuê dịch vụ/" + name + "/Smart Motel/" + address + "/Control/fan 2");
                        // Lấy thông tin trạng thái hiện tại của switch (realtime) và set trạng thái hiện tại đó cho switch
                        refFan2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists())
                                {
                                    boolean statusSwitch = snapshot.getValue(boolean.class);
                                    fan2.setChecked(statusSwitch);

                                    // Set trạng thái của switch khi click
                                    fan2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                                            refFan2.setValue(isChecked);
                                        }
                                    });
                                }
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void toggleLamp1(String usernameLogin)
    {
        DatabaseReference refName = FirebaseDatabase.getInstance().getReference("Người thuê trọ/" + usernameLogin + "/Đã thuê/name");
        DatabaseReference refAddress = FirebaseDatabase.getInstance().getReference("Người thuê trọ/" + usernameLogin + "/Đã thuê/address");

        refName.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name ;
                name = snapshot.getValue(String.class);

                refAddress.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String address;
                        address = snapshot.getValue(String.class);

                        DatabaseReference refLamp1 = FirebaseDatabase.getInstance().getReference("Người thuê dịch vụ/" + name + "/Smart Motel/" + address + "/Control/lamp 1");
                        // Lấy thông tin trạng thái hiện tại của switch (realtime) và set trạng thái hiện tại đó cho switch
                        refLamp1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists())
                                {
                                    boolean statusSwitch = snapshot.getValue(boolean.class);
                                    lamp1.setChecked(statusSwitch);

                                    // Set trạng thái của switch khi click
                                    lamp1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                                            refLamp1.setValue(isChecked);
                                        }
                                    });
                                }
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void toggleLamp2(String usernameLogin)
    {
        DatabaseReference refName = FirebaseDatabase.getInstance().getReference("Người thuê trọ/" + usernameLogin + "/Đã thuê/name");
        DatabaseReference refAddress = FirebaseDatabase.getInstance().getReference("Người thuê trọ/" + usernameLogin + "/Đã thuê/address");

        refName.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name ;
                name = snapshot.getValue(String.class);

                refAddress.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String address;
                        address = snapshot.getValue(String.class);

                        DatabaseReference refLamp2 = FirebaseDatabase.getInstance().getReference("Người thuê dịch vụ/" + name + "/Smart Motel/" + address + "/Control/lamp 2");
                        // Lấy thông tin trạng thái hiện tại của switch (realtime) và set trạng thái hiện tại đó cho switch
                        refLamp2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot){
                                if(snapshot.exists())
                                {
                                    boolean statusSwitch = snapshot.getValue(boolean.class);
                                    lamp2.setChecked(statusSwitch);

                                    // Set trạng thái của switch khi click
                                    lamp2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                                            refLamp2.setValue(isChecked);
                                        }
                                    });
                                }
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}