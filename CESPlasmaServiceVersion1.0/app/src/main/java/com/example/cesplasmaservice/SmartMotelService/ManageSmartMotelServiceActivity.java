package com.example.cesplasmaservice.SmartMotelService;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cesplasmaservice.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ManageSmartMotelServiceActivity extends AppCompatActivity {
    RecyclerView manageSmartMotelRecyclerView;
    ArrayList<InformationManageClass> informationManageList;
    AdapterManage adapterManage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_smart_motel_service);

        // Lấy thông tin username nhận được từ Login
        SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREFERENCE", MODE_PRIVATE);
        String getUsernameLogin = sharedPreferences.getString("username", "");

        manageSmartMotelRecyclerView = findViewById(R.id.manageSmartMotelRecyclerView);
        manageSmartMotelRecyclerView.setHasFixedSize(true);
        manageSmartMotelRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        informationManageList = new ArrayList<>();
        adapterManage = new AdapterManage(informationManageList, this);
        manageSmartMotelRecyclerView.setAdapter(adapterManage);

        String path = "Người thuê dịch vụ/" + getUsernameLogin + "/Smart Motel/";
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(path);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                informationManageList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    DataSnapshot imagesSnapshot = dataSnapshot.child("Images");

                    InformationManageClass informationSearchClass = new InformationManageClass();
                    informationSearchClass.setAddress(dataSnapshot.child("address").getValue(String.class));
                    informationSearchClass.setUsername(dataSnapshot.child("name").getValue(String.class));

                    for (DataSnapshot imageSnapshot : imagesSnapshot.getChildren()) {
                        String imageUrl = imageSnapshot.getValue(String.class);
                        if (imageUrl != null) {
                            informationSearchClass.setImageURL(imageUrl);
                            informationManageList.add(informationSearchClass);
                        }
                        break;
                    }
                }

                adapterManage.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}