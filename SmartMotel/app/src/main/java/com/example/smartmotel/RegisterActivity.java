package com.example.smartmotel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    EditText emailEditText, usernameEditText, passwordEditText;
    Button registerButton;
    ProgressBar progressBar;
    TextView loginTextView;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Gán (Ánh xạ) các biến bên dưới đến các ID trong layout activity_login.xml
        emailEditText = findViewById(R.id.emailEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton);
        progressBar = findViewById(R.id.progressBar);
        loginTextView = findViewById(R.id.loginTextView);

        auth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isUsernameValidated() || !isPasswordValidated() || !isEmailValidated())
                {
                    return;
                }
                else
                {
                    String email = emailEditText.getText().toString().trim();
                    String username = usernameEditText.getText().toString().trim();
                    String password = passwordEditText.getText().toString().trim();

                    String path = "Người thuê trọ/" + username;
                    databaseReference = FirebaseDatabase.getInstance().getReference(path);

                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                databaseReference.child("email").setValue(email);
                                databaseReference.child("name").setValue(username);
                                databaseReference.child("Đã thuê").child("State").setValue(false);
                                databaseReference.child("Đã thuê").child("address").setValue("");
                                databaseReference.child("Đã thuê").child("name").setValue("");

                                Intent intentToLogin = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intentToLogin);
                                progressBar.setVisibility(View.VISIBLE);
                                registerButton.setAlpha(0.5f);
                            }
                            else
                            {

                            }
                        }
                    });
                }
            }
        });

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToLogin = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intentToLogin);
            }
        });
    }

    /*
    @Function: Kiểm tra ô email có trống hay không
    @Parameter: Không có
    @Return: Kiểu boolean
    */
    private Boolean isEmailValidated() {
        // Lấy giá trị của ô email
        String val = emailEditText.getText().toString().trim();

        // Kiểm tra xem có trống hay không
        if (val.isEmpty())
        {
            emailEditText.setError("Email is required");
            return false;
        }
        else
        {
            emailEditText.setError(null);
            return true;
        }
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

    // Được gọi moỗi khi activity trở lại tương tác với người dùng
    protected void onResume()
    {
        super.onResume();
        progressBar.setVisibility(View.GONE);
        registerButton.setAlpha(1.0f);
    }
}