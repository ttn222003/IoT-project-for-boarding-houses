package com.example.smartmotel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class SearchSmartMotelActivity extends AppCompatActivity {
    String[] districtsListItems = {"Quận 1", "Quận 2", "Quận 3", "Quận 4", "Quận 5", "Quận 6", "Quận 7", "Quận 8", "Quận 9", "Quận 10", "Quận 11", "Quận 12","Quận Bình Tân", "Quận Bình Thạnh", "Quận Phú Nhuận", "Quận Tân Phú", "Thành phố Thủ Đức"};
    AutoCompleteTextView districtAutoCompleteTextView;
    ArrayAdapter<String> adapterDistricts;

    String[] stateListItems = {"Đã thuê", "Còn trống"};
    AutoCompleteTextView stateAutoCompleteTextView;
    ArrayAdapter<String> adapterStates;
    RecyclerView recyclerView;
    ArrayList<InformationSearchClass> informationSearchList;
    AdapterSearch adapter;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Người thuê dịch vụ");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_smart_motel);

        // Lấy thông tin username nhận được từ Login
        SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREFERENCE", MODE_PRIVATE);
        String getUsernameLogin = sharedPreferences.getString("username", "");

        districtAutoCompleteTextView = findViewById(R.id.districtAutoCompleteTextView);
        adapterDistricts = new ArrayAdapter<String>(this, R.layout.list_district, districtsListItems);

        stateAutoCompleteTextView = findViewById(R.id.stateAutoCompleteTextView);
        adapterStates = new ArrayAdapter<String>(this, R.layout.list_state, stateListItems);
        // Recyler View
        recyclerView = findViewById(R.id.searchSmartMotelRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        informationSearchList = new ArrayList<>();
        adapter = new AdapterSearch(informationSearchList, this);
        recyclerView.setAdapter(adapter);

        // Lấy tất cả thông tin trên RTDB và hiển thị lên Recycler View
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                informationSearchList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    DataSnapshot smartMotelSnapshot = userSnapshot.child("Smart Motel");

                    for (DataSnapshot motelSnapshot : smartMotelSnapshot.getChildren()) {

                        DataSnapshot imagesSnapshot = motelSnapshot.child("Images");

                        InformationSearchClass informationSearchClass = new InformationSearchClass();
                        informationSearchClass.setAddress(motelSnapshot.child("address").getValue(String.class));
                        informationSearchClass.setUsername(motelSnapshot.child("name").getValue(String.class));
                        informationSearchClass.setUsernameLogin(getUsernameLogin);

                        for (DataSnapshot imageSnapshot : imagesSnapshot.getChildren()) {
                            String imageUrl = imageSnapshot.getValue(String.class);
                            if (imageUrl != null) {
                                informationSearchClass.setImageURL(imageUrl);
                                informationSearchList.add(informationSearchClass);
                            }
                            break;
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Filter để lọc tên quận và hiển thị tất cả các smart motel có trong quận đó
        districtAutoCompleteTextView.setAdapter(adapterDistricts);
        districtAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String the_district = adapterView.getItemAtPosition(position).toString();

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        informationSearchList.clear();
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            DataSnapshot smartMotelSnapshot = userSnapshot.child("Smart Motel");

                            for (DataSnapshot motelSnapshot : smartMotelSnapshot.getChildren()) {
                                String district_RTDB = motelSnapshot.child("address").getValue(String.class);

                                if (district_RTDB != null && Objects.requireNonNull(district_RTDB).contains(the_district)) {
                                    DataSnapshot imagesSnapshot = motelSnapshot.child("Images");

                                    InformationSearchClass informationSearchClass = new InformationSearchClass();
                                    informationSearchClass.setAddress(motelSnapshot.child("address").getValue(String.class));
                                    informationSearchClass.setUsername(motelSnapshot.child("name").getValue(String.class));
                                    informationSearchClass.setUsernameLogin(getUsernameLogin);

                                    for (DataSnapshot imageSnapshot : imagesSnapshot.getChildren()) {
                                        String imageUrl = imageSnapshot.getValue(String.class);
                                        if (imageUrl != null) {
                                            informationSearchClass.setImageURL(imageUrl);
                                            informationSearchList.add(informationSearchClass);
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        // Filter để lọc tên tình trạng trống của nhà trọ và hiển thị tất cả các smart motel tùy theo tình trạng đó
        stateAutoCompleteTextView.setAdapter(adapterStates);
        stateAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String the_state = adapterView.getItemAtPosition(position).toString();

                if(the_state.equals("Đã thuê"))
                {
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            informationSearchList.clear();
                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                DataSnapshot smartMotelSnapshot = userSnapshot.child("Smart Motel");

                                for (DataSnapshot motelSnapshot : smartMotelSnapshot.getChildren()) {
                                    boolean available = Boolean.TRUE.equals(motelSnapshot.child("Available").getValue(boolean.class));

                                    if (available) {
                                        DataSnapshot imagesSnapshot = motelSnapshot.child("Images");

                                        InformationSearchClass informationSearchClass = new InformationSearchClass();
                                        informationSearchClass.setAddress(motelSnapshot.child("address").getValue(String.class));
                                        informationSearchClass.setUsername(motelSnapshot.child("name").getValue(String.class));
                                        informationSearchClass.setUsernameLogin(getUsernameLogin);

                                        for (DataSnapshot imageSnapshot : imagesSnapshot.getChildren()) {
                                            String imageUrl = imageSnapshot.getValue(String.class);
                                            if (imageUrl != null) {
                                                informationSearchClass.setImageURL(imageUrl);
                                                informationSearchList.add(informationSearchClass);
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else if(the_state.equals("Còn trống"))
                {
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            informationSearchList.clear();
                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                DataSnapshot smartMotelSnapshot = userSnapshot.child("Smart Motel");

                                for (DataSnapshot motelSnapshot : smartMotelSnapshot.getChildren()) {
                                    boolean available = Boolean.TRUE.equals(motelSnapshot.child("Available").getValue(boolean.class));

                                    if (!available) {
                                        DataSnapshot imagesSnapshot = motelSnapshot.child("Images");

                                        InformationSearchClass informationSearchClass = new InformationSearchClass();
                                        informationSearchClass.setAddress(motelSnapshot.child("address").getValue(String.class));
                                        informationSearchClass.setUsername(motelSnapshot.child("name").getValue(String.class));
                                        informationSearchClass.setUsernameLogin(getUsernameLogin);

                                        for (DataSnapshot imageSnapshot : imagesSnapshot.getChildren()) {
                                            String imageUrl = imageSnapshot.getValue(String.class);
                                            if (imageUrl != null) {
                                                informationSearchClass.setImageURL(imageUrl);
                                                informationSearchList.add(informationSearchClass);
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        // Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottomSearch);

        bottomNavigationView.setOnItemSelectedListener(menuItem -> {
            if (menuItem.getItemId() == R.id.bottomManageMotel) {
                Intent intentToManageMotel = new Intent(getApplicationContext(), ManageYourSmartMotelActivity.class);
                startActivity(intentToManageMotel);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
                finish();
                return true;
            } else if (menuItem.getItemId() == R.id.bottomSearch) {
                return true;
            }
            return false;
        });
    }
}