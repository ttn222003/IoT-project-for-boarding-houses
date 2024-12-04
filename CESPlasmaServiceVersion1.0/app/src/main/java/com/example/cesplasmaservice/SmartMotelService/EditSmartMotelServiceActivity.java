package com.example.cesplasmaservice.SmartMotelService;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cesplasmaservice.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EditSmartMotelServiceActivity extends AppCompatActivity {
    // Tạo các biến cho phần UI bên xml hiển thị
    AutoCompleteTextView addressAutoCompleteTextView;
    ArrayAdapter<String> adapterAddress;

    String[] theNumberOfFansListItems = {"1", "2"};
    AutoCompleteTextView theNumberOfFansAutoCompleteTextView;
    ArrayAdapter<String> adapterTheNumberOfFans;

    String[] theNumberOfLampsListItems = {"1", "2"};
    AutoCompleteTextView theNumberOfLampsAutoCompleteTextView;
    ArrayAdapter<String> adapterTheNumberOfLamps;

    private ImageView uploadImageImageView;
    Button editButton;

    // Tạo các biến được sử dụng để gửi lên Firebase
    String the_address;
    String the_number_of_fans;
    String the_number_of_lamps;
    private ArrayList<Uri> imageUriList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_smart_motel_service);

        // Lấy thông tin username nhận được từ Login
        SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREFERENCE", MODE_PRIVATE);
        String getUsernameLogin = sharedPreferences.getString("username", "");

        theNumberOfFansAutoCompleteTextView = findViewById(R.id.theNumberOfFansAutoCompleteTextView);
        theNumberOfLampsAutoCompleteTextView = findViewById(R.id.theNumberOfLampsAutoCompleteTextView);
        addressAutoCompleteTextView = findViewById(R.id.addressAutoCompleteTextView);
        uploadImageImageView = findViewById(R.id.uploadImageImageView);
        editButton = findViewById(R.id.editButton);

        // Tạo danh sách lưu trữ các địa chỉ
        List<String> addressList = new ArrayList<>();
        DatabaseReference infoRef = FirebaseDatabase.getInstance().getReference("Người thuê dịch vụ/" + getUsernameLogin + "/Smart Motel");
        infoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren())
                {
                    String address = dataSnapshot.child("address").getValue(String.class);
                    if(address != null)
                    {
                        addressList.add(address);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapterAddress = new ArrayAdapter<String>(this, R.layout.list_district, addressList);
        adapterTheNumberOfFans = new ArrayAdapter<String>(this, R.layout.the_number_of_fans, theNumberOfFansListItems);
        adapterTheNumberOfLamps = new ArrayAdapter<String>(this, R.layout.the_number_of_lamps, theNumberOfLampsListItems);

        getTheAddress();
        getTheNumberOfFans();
        getTheNumberOfLamps();

        // Lấy ảnh từ thư viện
        // Mở trình chọn ảnh trong thư viện
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            imageUriList = new ArrayList<>();

                            if(data.getClipData() != null)
                            {
                                int count = data.getClipData().getItemCount();
                                for(int i = 0; i < count; i++)
                                {
                                    imageUriList.add(data.getClipData().getItemAt(i).getUri());
                                }
                            }
                            else if(data.getData() != null)
                            {
                                imageUriList.add(data.getData());
                            }

                            if(!imageUriList.isEmpty())
                            {
                                uploadImageImageView.setImageURI(imageUriList.get(0));
                            }
                        }
                    }
                }
        );

        // Chọn ImageView để lấy ảnh rồi hiển thị lên trong ImageView
        uploadImageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent();
                photoPicker.setAction(Intent.ACTION_GET_CONTENT);
                photoPicker.setType("image/*");
                photoPicker.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                activityResultLauncher.launch(Intent.createChooser(photoPicker, "Select picture"));
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = "Người thuê dịch vụ/" + getUsernameLogin + "/Smart Motel/" + the_address;
                DatabaseReference addressRef = FirebaseDatabase.getInstance().getReference(path);
                addressRef.child("theNumberOfFans").setValue(the_number_of_fans);
                addressRef.child("theNumberOfLamps").setValue(the_number_of_lamps);

                DatabaseReference controlRef = FirebaseDatabase.getInstance().getReference(path + "/Control");
                controlRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            // Thong bao xoa thanh cong
                        }
                        else
                        {
                            // Thong bao xoa that bai
                        }
                    }
                });

                setControl(path, the_number_of_fans, the_number_of_lamps);

                if(imageUriList != null)
                {
                    uploadToFirebase(imageUriList, path);
                }

                showEditSuccessDialog();
            }
        });

    }

    /*
    @Function: Lấy thông tin dia chi
    @Parameter: Không có
    @Return: String
    */
    public String getTheAddress() {
        addressAutoCompleteTextView.setAdapter(adapterAddress);
        addressAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                the_address = adapterView.getItemAtPosition(position).toString();
            }
        });
        return the_address;
    }

    /*
    @Function: Lấy thông tin số lượng quạt
    @Parameter: Không có
    @Return: String
    */
    public String getTheNumberOfFans() {
        theNumberOfFansAutoCompleteTextView.setAdapter(adapterTheNumberOfFans);
        theNumberOfFansAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                the_number_of_fans = adapterView.getItemAtPosition(position).toString();
            }
        });
        return the_number_of_fans;
    }

    /*
    @Function: Lấy thông tin số đèn
    @Parameter: Không có
    @Return: String
    */
    public String getTheNumberOfLamps(){
        theNumberOfLampsAutoCompleteTextView.setAdapter(adapterTheNumberOfLamps);
        theNumberOfLampsAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                the_number_of_lamps = adapterView.getItemAtPosition(position).toString();
            }
        });
        return the_number_of_lamps;
    }

    /*
    @Function: Thay đổi các control cho smart motel với thông tin đã nhập
    @Parameter: Path: Đường dẫn tới Firebase, number_of_fans: Số lượng quạt, number_of_lamps: Số lượng đèn
    @Return: String
    */
    public void setControl(String path, String number_of_fans, String number_of_lamps){
        DatabaseReference refControl = FirebaseDatabase.getInstance().getReference(path).child("Control");

        if(Objects.equals(number_of_fans, "1"))
        {
            refControl.child("fan 1").setValue(false);
        }
        else if(Objects.equals(number_of_fans, "2"))
        {
            refControl.child("fan 1").setValue(false);
            refControl.child("fan 2").setValue(false);
        }

        if(Objects.equals(number_of_lamps, "1"))
        {
            refControl.child("lamp 1").setValue(false);
        }
        else if(Objects.equals(number_of_lamps, "2"))
        {
            refControl.child("lamp 1").setValue(false);
            refControl.child("lamp 2").setValue(false);
        }
    }

    /*
    @Function: Upload ảnh lên Firebase storage
    @Parameter: Uri: Ảnh muốn upload, path: Đường dẫn tới Firebase
    @Return: Không có
    */
    private void uploadToFirebase(ArrayList<Uri> imageUries, String path){
        final StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(path + "/Images");

        for(Uri imageUri : imageUries)
        {
            final StorageReference imageReference = storageRef.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            imageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String key = databaseReference.push().getKey();
                            databaseReference.child(key).setValue(uri.toString());
                        }
                    });
                }
            });
        }

    }
    private String getFileExtension(Uri fileUri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(fileUri));
    }

    /*
    @Function: Hiển thị thông báo thành công lên màn hình
    @Parameter: Không có
    @Return: Không có
    */
    private void showEditSuccessDialog() {
        ConstraintLayout errorConstraintLayout = findViewById(R.id.dialogBoxEditSuccessAlertLayout);
        View view = LayoutInflater.from(EditSmartMotelServiceActivity.this).inflate(R.layout.dialog_box_edit_success_alert, errorConstraintLayout);

        AlertDialog.Builder builder = new AlertDialog.Builder(EditSmartMotelServiceActivity.this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}