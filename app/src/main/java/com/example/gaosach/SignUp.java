package com.example.gaosach;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gaosach.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SignUp extends AppCompatActivity  implements View.OnClickListener{
    EditText edtPhone , edtPassword,edtName, edtEmail;

    Button btnSignUp;
    TextView mHaveAccount;
    ProgressBar progressBar;


    ProgressDialog progressDialog;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

//        ActionBar actionBar= getSupportActionBar();
//        actionBar.setTitle("Tạo Tài Khoản");
//        //creta bach button
//
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setDisplayShowHomeEnabled(true);


        edtPhone =  findViewById(R.id.edtPhone);
        edtPassword =  findViewById(R.id.edtPassword);
        edtName= findViewById(R.id.edtName);
        edtEmail =  findViewById(R.id.edtEmail);
        btnSignUp=  findViewById(R.id.btnSignUp);
        mHaveAccount= findViewById(R.id.have_account);

        mAuth= FirebaseAuth.getInstance();
        findViewById(R.id.btnSignUp).setOnClickListener(this);



        //handle signin text view
        mHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUp.this,SignIn.class));
            }
        });





    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser() !=null){


        }
    }
    private void SignUpUser(){
        final String Name= edtName.getText().toString().trim();
        final String Email= edtEmail.getText().toString().trim();
        final String Phone= edtPhone.getText().toString().trim();
        String Password= edtPassword.getText().toString().trim();

        if(Name.isEmpty()){
            edtName.setError(getString(R.string.input_error_name));
            edtName.requestFocus();
        }
        if (Email.isEmpty()) {
            edtEmail.setError(getString(R.string.input_error_email));
            edtEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            edtEmail.setError(getString(R.string.input_error_email_invalid));
            edtEmail.requestFocus();
            return;
        }

        if (Password.isEmpty()) {
            edtPassword.setError(getString(R.string.input_error_password));
            edtPassword.requestFocus();
            return;
        }

        if (Password.length() < 6) {
            edtPassword.setError(getString(R.string.input_error_password_length));
            edtPassword.requestFocus();
            return;
        }

        if (Phone.isEmpty()) {
            edtPhone.setError(getString(R.string.input_error_phone));
            edtPhone.requestFocus();
            return;
        }

        if (Phone.length() != 10) {
            edtPhone.setError(getString(R.string.input_error_phone_invalid));
            edtPhone.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(Email,Password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            User user= new User(
                                    Name,
                                    Email

                            );
                            FirebaseDatabase.getInstance().getReference("User")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(SignUp.this,"Đăng kí thành công",Toast.LENGTH_SHORT).show();
                                        Intent signup= new Intent(SignUp.this,Home.class);
                                        startActivity(signup);


                                    }else {
                                        Toast.makeText(SignUp.this, "Xác thực thất bại", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });

                        }else {
                            Toast.makeText(SignUp.this, task.getException().getMessage(),Toast.LENGTH_SHORT).show();

                        }

                    }
                });


    }

//    @Override
//    public boolean onSupportNavigateUp() {
//        onBackPressed();
//
//        return super.onSupportNavigateUp();
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSignUp:
                SignUpUser();
                break;

        }

    }
}
