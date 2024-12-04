package com.example.smartmotel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    EditText usernameEditText, passwordEditText;
    Button loginButton;
    ProgressBar progressBar;
    TextView registerTextView, changePasswordTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Gán (Ánh xạ) các biến bên dưới đến các ID trong layout activity_login.xml
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);
        registerTextView = findViewById(R.id.registerTextView);
        changePasswordTextView = findViewById(R.id.changePasswordTextView);

        // Khi nút Login được nhấn
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isUsernameValidated() || !isPasswordValidated())
                {
                    return;
                }
                else
                {
                    String username = usernameEditText.getText().toString().trim();
                    String password = passwordEditText.getText().toString().trim();

                    /* Lấy thông tin username ở trên để dẫn tới đường dẫn /username/email trong RTDB, để lấy thông tin email đã đăng ký.
                       Ta sẽ sử dụng email đó và password đã nhập vào để đăng nhập vào hệ thống với Firebase Authentification */
                    DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Người thuê trọ/" + username + "/email");

                    FirebaseAuth authentication = FirebaseAuth.getInstance();

                    // Với đường dẫn mà refUser đã có được ở trên (Người thuê trọ/username/email), ta dùng addListenerForSingleValueEvent() để lấy thông tin email từ RTDB
                    userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // Nếu snapshot có tồn tại (đường dẫn đó có tồn tại)
                            if(snapshot.exists())
                            {
                                // Lấy thông tin email và lưu vào biến emailFromRTDB
                                String emailFromRTDB = Objects.requireNonNull(snapshot.getValue()).toString();

                                // Kiểm tra xem email lấy được có phải email hợp lệ không (đúng format của email hay không)
                                if(Patterns.EMAIL_ADDRESS.matcher(emailFromRTDB).matches())
                                {
                                    // Kiểm tra email và password có được đăng ký trên Firebase Authentification hay chưa
                                    authentication.signInWithEmailAndPassword(emailFromRTDB, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                        @Override
                                        public void onSuccess(AuthResult authResult) {
                                            // Nếu email và password nhập vào là email đã đăng ký trên Firebase Authentification thì chuyển sang DashboardActivity
                                            Intent intentToSearchSmartMotel = new Intent(LoginActivity.this, SearchSmartMotelActivity.class);

                                            SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREFERENCE", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("username", username);
                                            editor.apply();

                                            startActivity(intentToSearchSmartMotel);
                                            progressBar.setVisibility(View.VISIBLE);
                                            loginButton.setAlpha(0.5f);
                                        }
                                        // Nếu email chưa được đăng ký hoặc sai password thì báo lỗi cho người dùng
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            showWrongAccountOrPasswordAlert();
                                        }
                                    });
                                }
                                else
                                {
                                    showWrongAccountOrPasswordAlert();
                                }
                            }
                            // Nếu snapshot không tồn tại (đường dẫn đó không tồn tại) thì báo lỗi cho người dùng
                            else
                            {
                                showWrongAccountOrPasswordAlert();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        // Nếu muốn đổi mật khẩu thì chuyển sang RequireEmailToChangePassword Activity
        changePasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToRequireEmailToChangePassword = new Intent(LoginActivity.this, RequireEmailToChangePasswordActivity.class);
                startActivity(intentToRequireEmailToChangePassword);
            }
        });

        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToRegister = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intentToRegister);
            }
        });
    }

    /*
    @Function: Kiểm tra ô username có trống hay không
    @Parameter: Không có
    @Return: Kiểu boolean
    */
    private Boolean isUsernameValidated() {
        // Lấy giá trị của ô username
        String val = usernameEditText.getText().toString().trim();

        // Kiểm tra xem có trống hay không
        if (val.isEmpty())
        {
            usernameEditText.setError("Username is required");
            return false;
        }
        else
        {
            usernameEditText.setError(null);
            return true;
        }
    }

    /*
    @Function: Kiểm tra ô password có trống hay không
    @Parameter: Không có
    @Return: Kiểu boolean
    */
    private Boolean isPasswordValidated() {
        String val = passwordEditText.getText().toString();
        if (val.isEmpty())
        {
            passwordEditText.setError("Password is required");
            return false;
        }
        else
        {
            passwordEditText.setError(null);
            return true;
        }
    }

    /*
    @Function: Hiển thị thông báo sai tài khoản hoặc sai mật khẩu
    @Parameter: Không có
    @Return: Không có
    */
    private void showWrongAccountOrPasswordAlert() {
        ConstraintLayout wrongPasswordConstraintLayout = findViewById(R.id.dialogBoxWrongPasswordAlertLayout);
        View view = LayoutInflater.from(LoginActivity.this).inflate(R.layout.dialog_box_wrong_password, wrongPasswordConstraintLayout);

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Được gọi moỗi khi activity trở lại tương tác với người dùng
    protected void onResume()
    {
        super.onResume();
        progressBar.setVisibility(View.GONE);
        loginButton.setAlpha(1.0f);
    }
}