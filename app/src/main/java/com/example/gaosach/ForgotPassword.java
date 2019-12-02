package com.example.gaosach;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {
    EditText edtEmail;
    TextView resendEmail;
    Button btnForgotPassword;

    String email;

    //declare an instance of firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        resendEmail = findViewById(R.id.resendEmail);
        edtEmail = findViewById(R.id.forgotPasswordEmail);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);
        mAuth = FirebaseAuth.getInstance();
        email = edtEmail.getText().toString().trim();

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = edtEmail.getText().toString().trim();
                if (isValidEmail(email)) {
                    sendEmail(email);
                }
            }
        });

        // Navigate to ForgotPassword screen
        resendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = edtEmail.getText().toString().trim();
                if (isValidEmail(email)) {
                    sendEmail(email);
                }
            }
        });
    }

    private boolean isValidEmail(String email) {
        if (email.isEmpty()) {
            edtEmail.setError(getString(R.string.input_error_email));
            edtEmail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError(getString(R.string.input_error_email_invalid));
            edtEmail.requestFocus();
            return false;
        }

        return true;
    }

    private void sendEmail(final String email) {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPassword.this, "Password was sent to your email", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(ForgotPassword.this, SignIn.class));
                        } else {
//                            Toast.makeText(ForgotPassword.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            Toast.makeText(ForgotPassword.this, "Invalid Email", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
