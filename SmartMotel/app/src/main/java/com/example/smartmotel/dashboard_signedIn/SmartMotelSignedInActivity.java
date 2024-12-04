package com.example.smartmotel.dashboard_signedIn;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smartmotel.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class SmartMotelSignedInActivity extends AppCompatActivity {
    Switch relay1, relay2, relay3, relay4, relay5, relay6, relay7, relay8;
    LineChart monitor;
    ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
    LineDataSet lineDataSet = new LineDataSet(null, null);
    LineData lineData;
    // Đặt biến dataVals là global để khi cập nhật sẽ không xóa đi các dữ liệu trước đó
    private ArrayList<Entry> dataVals = new ArrayList<Entry>();
    public static int timestamp = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_motel_signed_in);

        monitor = findViewById(R.id.monitor);

        relay1 = findViewById(R.id.smartMotelRelay1);
        relay2 = findViewById(R.id.smartMotelRelay2);
        relay3 = findViewById(R.id.smartMotelRelay3);
        relay4 = findViewById(R.id.smartMotelRelay4);
        relay5 = findViewById(R.id.smartMotelRelay5);
        relay6 = findViewById(R.id.smartMotelRelay6);
        relay7 = findViewById(R.id.smartMotelRelay7);
        relay8 = findViewById(R.id.smartMotelRelay8);

        // Monitor
        getFlowData();

        // Control
        toggleRelay1();
        toggleRelay2();
        toggleRelay3();
        toggleRelay4();
        toggleRelay5();
        toggleRelay6();
        toggleRelay7();
        toggleRelay8();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // Lấy dữ liệu cảm biến từ Realtime Database
    public void getFlowData()
    {
        DatabaseReference refFlowData;
        // Lấy thông tin username nhận được từ DashboardActivity
        Intent getUsernameInfoFromDashboard = getIntent();
        String getUsernameInfo = getUsernameInfoFromDashboard.getStringExtra("username");
        refFlowData = FirebaseDatabase.getInstance().getReference(getUsernameInfo + "/smartMotel/flow");

        refFlowData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Lấy dữ liệu cảm biến từ Realtime Database
                float flowData = snapshot.getValue(float.class);

                // Thêm dữ liệu vào ArrayList
                dataVals.add(new Entry(timestamp, flowData));

                // Nếu ArrayList lớn hơn 5 thì xóa phần tử đầu tiên để đảm bảo đồ thị chỉ hiển thị 5 điểm dữ liệu
                if(dataVals.size() >= 5)
                {
                    dataVals.remove(0);
                }

                // Cài đặt đồ thị, có thể chỉnh sửa tùy chọn và tìm thêm trên mạng
                monitor.setDragEnabled(true);
                monitor.setScaleEnabled(false);
                monitor.setBackgroundColor(Color.WHITE);
                monitor.setDrawGridBackground(false);

                XAxis botAxis = monitor.getXAxis();
                botAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                monitor.getAxisRight().setEnabled(false);
                botAxis.setDrawGridLines(false);

                YAxis leftAxis = monitor.getAxisLeft();
                leftAxis.setAxisMaximum(100);
                leftAxis.setAxisMinimum(0);
                leftAxis.setDrawGridLines(false);

                // Vẽ biểu đồ đường với các tập điểm dữ liệu dataVals
                lineDataSet.setValues(dataVals);

                lineDataSet.setLabel("Flow Data");
                lineDataSet.setFillAlpha(110);
                lineDataSet.setColor(Color.RED);
                lineDataSet.setLineWidth(2f);
                lineDataSet.setValueTextSize(10f);

                iLineDataSets.clear();
                // iLineDataSets là một ArrayList chứa các LineDataSet (cho phép vẽ nhiều đường trong cùng 1 đồ thị, nhưng ở đây ta chỉ có 1 đồ thị nên tham số truyền vào chỉ có lineDataSet)
                iLineDataSets.add(lineDataSet);

                // lineData dùng để chứa toàn bộ các dữ liệu của đồ thị mà ta muốn hiển thị lên đồ thị.
                lineData = new LineData(iLineDataSets);
                monitor.clear();
                monitor.setData(lineData);
                monitor.invalidate();
                timestamp++;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Điều khiển Relay
    public void toggleRelay1()
    {
        DatabaseReference refRelay1;
        // Lấy thông tin username nhận được từ DashboardActivity
        Intent getUsernameInfoFromDashboard = getIntent();
        String getUsernameInfo = getUsernameInfoFromDashboard.getStringExtra("username");
        refRelay1 = FirebaseDatabase.getInstance().getReference(getUsernameInfo + "/smartMotel/control/relay1");

        // Lấy thông tin trạng thái hiện tại của switch (realtime) và set trạng thái hiện tại đó cho switch
        refRelay1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean statusSwitch = snapshot.getValue(boolean.class);
                relay1.setChecked(statusSwitch);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Set trạng thái của switch khi click
        relay1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                refRelay1.setValue(isChecked);
            }
        });
    }
    public void toggleRelay2()
    {
        DatabaseReference refRelay2;
        Intent getUsernameInfoFromDashboard = getIntent();
        String getUsernameInfo = getUsernameInfoFromDashboard.getStringExtra("username");
        refRelay2 = FirebaseDatabase.getInstance().getReference(getUsernameInfo + "/smartMotel/control/relay2");

        // Lay thong tin trang thai hien tai cua switch (realtime) va set trang thai hien tai do cho switch
        refRelay2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean statusSwitch = snapshot.getValue(boolean.class);
                relay2.setChecked(statusSwitch);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Set trang thai cua switch khi click
        relay2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                refRelay2.setValue(isChecked);
            }
        });
    }

    public void toggleRelay3()
    {
        DatabaseReference refRelay3;
        Intent getUsernameInfoFromDashboard = getIntent();
        String getUsernameInfo = getUsernameInfoFromDashboard.getStringExtra("username");
        refRelay3 = FirebaseDatabase.getInstance().getReference(getUsernameInfo + "/smartMotel/control/relay3");

        // Lay thong tin trang thai hien tai cua switch (realtime) va set trang thai hien tai do cho switch
        refRelay3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean statusSwitch = snapshot.getValue(boolean.class);
                relay3.setChecked(statusSwitch);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Set trang thai cua switch khi click
        relay3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                refRelay3.setValue(isChecked);
            }
        });
    }

    public void toggleRelay4()
    {
        DatabaseReference refRelay4;
        Intent getUsernameInfoFromDashboard = getIntent();
        String getUsernameInfo = getUsernameInfoFromDashboard.getStringExtra("username");
        refRelay4 = FirebaseDatabase.getInstance().getReference(getUsernameInfo + "/smartMotel/control/relay4");

        // Lay thong tin trang thai hien tai cua switch (realtime) va set trang thai hien tai do cho switch
        refRelay4.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean statusSwitch = snapshot.getValue(boolean.class);
                relay4.setChecked(statusSwitch);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Set trang thai cua switch khi click
        relay4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                refRelay4.setValue(isChecked);
            }
        });
    }

    public void toggleRelay5()
    {
        DatabaseReference refRelay5;
        Intent getUsernameInfoFromDashboard = getIntent();
        String getUsernameInfo = getUsernameInfoFromDashboard.getStringExtra("username");
        refRelay5 = FirebaseDatabase.getInstance().getReference(getUsernameInfo + "/smartMotel/control/relay5");

        // Lay thong tin trang thai hien tai cua switch (realtime) va set trang thai hien tai do cho switch
        refRelay5.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean statusSwitch = snapshot.getValue(boolean.class);
                relay5.setChecked(statusSwitch);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Set trang thai cua switch khi click
        relay5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                refRelay5.setValue(isChecked);
            }
        });
    }

    public void toggleRelay6()
    {
        DatabaseReference refRelay6;
        Intent getUsernameInfoFromDashboard = getIntent();
        String getUsernameInfo = getUsernameInfoFromDashboard.getStringExtra("username");
        refRelay6 = FirebaseDatabase.getInstance().getReference(getUsernameInfo + "/smartMotel/control/relay6");

        // Lay thong tin trang thai hien tai cua switch (realtime) va set trang thai hien tai do cho switch
        refRelay6.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean statusSwitch = snapshot.getValue(boolean.class);
                relay6.setChecked(statusSwitch);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Set trang thai cua switch khi click
        relay6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                refRelay6.setValue(isChecked);
            }
        });
    }

    public void toggleRelay7()
    {
        DatabaseReference refRelay7;
        Intent getUsernameInfoFromDashboard = getIntent();
        String getUsernameInfo = getUsernameInfoFromDashboard.getStringExtra("username");
        refRelay7 = FirebaseDatabase.getInstance().getReference(getUsernameInfo + "/smartMotel/control/relay7");

        // Lay thong tin trang thai hien tai cua switch (realtime) va set trang thai hien tai do cho switch
        refRelay7.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean statusSwitch = snapshot.getValue(boolean.class);
                relay7.setChecked(statusSwitch);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Set trang thai cua switch khi click
        relay7.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                refRelay7.setValue(isChecked);
            }
        });
    }

    public void toggleRelay8()
    {
        DatabaseReference refRelay8;
        Intent getUsernameInfoFromDashboard = getIntent();
        String getUsernameInfo = getUsernameInfoFromDashboard.getStringExtra("username");
        refRelay8 = FirebaseDatabase.getInstance().getReference(getUsernameInfo + "/smartMotel/control/relay8");

        // Lay thong tin trang thai hien tai cua switch (realtime) va set trang thai hien tai do cho switch
        refRelay8.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean statusSwitch = snapshot.getValue(boolean.class);
                relay8.setChecked(statusSwitch);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Set trang thai cua switch khi click
        relay8.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                refRelay8.setValue(isChecked);
            }
        });
    }
}