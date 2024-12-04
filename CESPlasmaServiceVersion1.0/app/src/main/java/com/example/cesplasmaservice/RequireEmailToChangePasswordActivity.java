package com.example.cesplasmaservice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class RequireEmailToChangePasswordActivity extends AppCompatActivity {
    EditText emailTochangePassword;
    Button nextButton;
    FirebaseAuth auth;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_require_email_to_change_password);

        // Gán (Ánh xạ) các biến bên dưới vào các ID trong layout activity_reuquire_email_to_change_password.xml
        emailTochangePassword = findViewById(R.id.emailEditText);
        nextButton = findViewById(R.id.btnNext);
        auth = FirebaseAuth.getInstance();

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = emailTochangePassword.getText().toString();

                if(!email.isEmpty())
                {
                    resetPassword();
                }
                else
                {
                    emailTochangePassword.setError("Please enter your email");
                }
            }
        });
    }

    private void resetPassword()
    {
        // Gửi yêu cầu thay đổi mật khẩu đến email đã nhập
        auth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(RequireEmailToChangePasswordActivity.this, "Please check your email", Toast.LENGTH_SHORT).show();
                Intent intentToChangePassword = new Intent(RequireEmailToChangePasswordActivity.this, LoginActivity.class);
                startActivity(intentToChangePassword);
            }
        }).addOnFailureListener(new OnFailureListener() {
            // Nếu email không được đăng ký thì báo lỗi
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RequireEmailToChangePasswordActivity.this, "Error: -", Toast.LENGTH_SHORT).show();
            }
        });
    }
}