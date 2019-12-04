package com.example.gaosach;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gaosach.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SignIn extends AppCompatActivity {

    EditText edtEmail, edtPassword,edtPhone;
    TextView notHaveAccount;
    Button btnSignIn;

    //declare an instance of firebase

        private FirebaseAuth mAuth;
    ProgressDialog mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        edtPassword = findViewById(R.id.edtPassword);
        edtEmail = findViewById(R.id.edtEmail);
        notHaveAccount = findViewById(R.id.nothave_account);
        btnSignIn = findViewById(R.id.btnSignIn);

        mAuth= FirebaseAuth.getInstance();
        FirebaseDatabase database= FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Email= edtEmail.getText().toString().trim();
                String Password=edtPassword.getText().toString().trim();
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
                else{
                    signInUser(Email,Password);
              }
            }
        });




        //not have account
        notHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignIn.this, SignUp.class));
            }
        });

//        //init prodialog
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Đăng Nhập ...");

    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        if(mAuth.getCurrentUser() !=null){
//
//
//        }
//    }


    private void signInUser(final String Email, final String Password) {


        //show pd
        mDialog.show();

        mAuth.signInWithEmailAndPassword(Email,Password)
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user= new User(
                                    Email,
                                    Password

                            );


                            mDialog.dismiss();
                            Toast.makeText(SignIn.this,"Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignIn.this,Home.class));
                            finish();
                        } else {
                            mDialog.dismiss();
                            Toast.makeText(SignIn.this, "Đăng  nhập thất bại", Toast.LENGTH_SHORT).show();

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mDialog.dismiss();
                //error get and show error meesage
                Toast.makeText(SignIn.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();

        return super.onSupportNavigateUp();
    }


//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.btnSignIn:
//                signInUser();
//                break;
//
//        }
//
//    }


}
