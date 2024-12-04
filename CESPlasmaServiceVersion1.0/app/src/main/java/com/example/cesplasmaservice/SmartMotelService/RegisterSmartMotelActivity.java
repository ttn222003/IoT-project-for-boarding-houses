package com.example.cesplasmaservice.SmartMotelService;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterSmartMotelActivity extends AppCompatActivity {
    EditText addressEditText;
    // Tạo các biến cho phần UI bên xml hiển thị
    String[] districtsListItems = {"Quận 1", "Quận 2", "Quận 3", "Quận 4", "Quận 5", "Quận 6", "Quận 7", "Quận 8", "Quận 9", "Quận 10", "Quận 11", "Quận 12","Quận Bình Tân", "Quận Bình Thạnh", "Quận Phú Nhuận", "Quận Tân Phú", "Thành phố Thủ Đức"};
    AutoCompleteTextView districtsAutoCompleteTextView;
    ArrayAdapter<String> adapterDistricts;

    String[] theNumberOfFansListItems = {"1", "2"};
    AutoCompleteTextView theNumberOfFansAutoCompleteTextView;
    ArrayAdapter<String> adapterTheNumberOfFans;

    String[] theNumberOfLampsListItems = {"1", "2"};
    AutoCompleteTextView theNumberOfLampsAutoCompleteTextView;
    ArrayAdapter<String> adapterTheNumberOfLamps;

    private ImageView uploadImageImageView;
    Button registerButton;

    // Tạo các biến được sử dụng để gửi lên Firebase
    String the_district;
    String the_number_of_fans;
    String the_number_of_lamps;
    private Uri imageUri;
    final private StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_smart_motel);

        // Lấy thông tin username nhận được từ Login
        SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREFERENCE", MODE_PRIVATE);
        String getUsernameLogin = sharedPreferences.getString("username", "");

        addressEditText = findViewById(R.id.addressInputEditText);
        theNumberOfFansAutoCompleteTextView = findViewById(R.id.theNumberOfFansAutoCompleteTextView);
        theNumberOfLampsAutoCompleteTextView = findViewById(R.id.theNumberOfLampsAutoCompleteTextView);
        districtsAutoCompleteTextView = findViewById(R.id.districtAutoCompleteTextView);
        uploadImageImageView = findViewById(R.id.uploadImageImageView);
        registerButton = findViewById(R.id.registerButton);

        adapterTheNumberOfFans = new ArrayAdapter<String>(this, R.layout.the_number_of_fans, theNumberOfFansListItems);
        adapterTheNumberOfLamps = new ArrayAdapter<String>(this, R.layout.the_number_of_lamps, theNumberOfLampsListItems);
        adapterDistricts = new ArrayAdapter<String>(this, R.layout.list_district, districtsListItems);

        // Lấy thông tin các quận đã chọn và lưu vào biến the_district
        getTheDistrict();
        // Lấy thông tin các quận đã chọn và lưu vào biến the_number_of_fans
        getTheNumberOfFans();
        // Lấy thông tin các quận đã chọn và lưu vào biến the_number_of_lamps
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
                            // Lưu ảnh vào biến imageUri
                            imageUri = data.getData();
                            uploadImageImageView.setImageURI(imageUri);
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
                activityResultLauncher.launch(photoPicker);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address = getAdress() + ", " + the_district;
                String path = "Người thuê dịch vụ/" + getUsernameLogin + "/Smart Motel/" + address;
                DatabaseReference refUser = FirebaseDatabase.getInstance().getReference(path);

                refUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            showErrorDialog();
                        }
                        else
                        {
                            // Tạo node thông tin
                            setInformation(path, address, the_number_of_fans, the_number_of_lamps, false, getUsernameLogin);

                            // Tạo node control (node có thể thêm bớt tùy vào board IoT)
                            setControl(path, the_number_of_fans, the_number_of_lamps);

                            if (imageUri != null){
                                uploadToFirebase(imageUri, path);
                            } else{
                                Toast.makeText(RegisterSmartMotelActivity.this, "Please select image", Toast.LENGTH_SHORT).show();
                            }

                            showSuccessDialog();
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
    @Function: Lấy thông tin địa chỉ từ ô địa chỉ và chuyển đổi ký tự / thành -
    @Parameter: Không có
    @Return: String
    */
    public String getAdress(){
        String address = addressEditText.getText().toString();
        String new_address = "";

        for(int i = 0; i < address.length(); i++){
            if(address.charAt(i) != '/') new_address += address.charAt(i);
            else                         new_address += "-";
        }

        return new_address;
    }

    /*
    @Function: Lấy thông tin quận
    @Parameter: Không có
    @Return: String
    */
    public String getTheDistrict() {
        districtsAutoCompleteTextView.setAdapter(adapterDistricts);
        districtsAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                the_district = adapterView.getItemAtPosition(position).toString();
            }
        });
        return the_district;
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
    @Function: Gửi các thông tin có được lên Firebase
    @Parameter: Path: Đường dẫn tới Firebase, address: Địa chỉ, the_number_of_fans: Số lượng quạt, the_number_of_lamps: Số lượng đèn
    @Return: Không có
    */
    public void setInformation(String path, String address, String the_number_of_fans, String the_number_of_lamps, boolean Available, String username)
    {
        DatabaseReference refUser = FirebaseDatabase.getInstance().getReference(path);
        refUser.child("Available").setValue(Available);
        refUser.child("address").setValue(address);
        refUser.child("theNumberOfFans").setValue(the_number_of_fans);
        refUser.child("theNumberOfLamps").setValue(the_number_of_lamps);
        refUser.child("name").setValue(username);
    }

    /*
    @Function: Tạo các control cho smart motel với thông tin đã nhập
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
    private void uploadToFirebase(Uri uri, String path){
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(path + "/Images");
        final StorageReference imageReference = storageRef.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        imageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
    private void showSuccessDialog() {
        ConstraintLayout successConstraintLayout = findViewById(R.id.dialogBoxRegisterSuccessAlertLayout);
        View view = LayoutInflater.from(RegisterSmartMotelActivity.this).inflate(R.layout.dialog_box_register_success_alert, successConstraintLayout);

        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterSmartMotelActivity.this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /*
    @Function: Hiển thị thông báo không thành công lên màn hình
    @Parameter: Không có
    @Return: Không có
    */
    private void showErrorDialog() {
        ConstraintLayout errorConstraintLayout = findViewById(R.id.dialogBoxRegisteredAlertLayout);
        View view = LayoutInflater.from(RegisterSmartMotelActivity.this).inflate(R.layout.dialog_box_registered_alert, errorConstraintLayout);

        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterSmartMotelActivity.this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void onResumed() {
        super.onResume();
    }
}