package com.example.gaosach;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gaosach.Common.Common;
import com.example.gaosach.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import io.paperdb.Paper;

import static com.example.gaosach.Common.Common.PASSWORD_KEY;
import static com.example.gaosach.Common.Common.USER_KEY;
import static com.example.gaosach.Common.Validator.isEmpty;
import static com.example.gaosach.Common.Validator.isPassword;
import static com.example.gaosach.Common.Validator.isPhoneNumber;

public class SignIn extends AppCompatActivity {

    EditText edtPhone, edtPassword;
    TextView notHaveAccount, forgotPassword;
    Button btnSignIn, btnProfile;
    TextView txtRememberMe;
    private boolean isRemembered;

    //declare an instance of firebase
    ProgressDialog mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtPassword = findViewById(R.id.edtPassword);
        edtPhone = findViewById(R.id.edtPhone);

        notHaveAccount = findViewById(R.id.nothave_account);
        btnSignIn = findViewById(R.id.btnSignIn);
        txtRememberMe = findViewById(R.id.rememberMe);
        isRemembered = false;

        // Init Paper
        Paper.init(this);

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignIn.this, Profile.class));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = edtPhone.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                if (isEmpty(phoneNumber)) {
                    edtPhone.setError(getString(R.string.input_error_phone));
                    edtPhone.requestFocus();
                    return;
                }

                if (!isPhoneNumber(phoneNumber)) {
                    edtPhone.setError(getString(R.string.input_error_phone_invalid));
                    edtPhone.requestFocus();
                    return;
                }

                if (isEmpty(password)) {
                    edtPassword.setError(getString(R.string.input_error_password));
                    edtPassword.requestFocus();
                    return;
                }

                if (!isPassword(password)) {
                    edtPassword.setError(getString(R.string.input_error_password_length));
                    edtPassword.requestFocus();
                    return;
                }

                signIn(phoneNumber, password, SignIn.this);
            }
        });

        //not have account
        notHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignIn.this, SignUp.class));
            }
        });

        // Click remember me TextView
        txtRememberMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRemembered = !isRemembered;
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

    private String getUserKey(String email) {
        return "";
    }

    private void signIn(final String phoneNumber, final String password, final Context context) {
        final ProgressDialog mDialog = new ProgressDialog(context);
        mDialog.setMessage("Vui lòng đợi...");
        mDialog.show();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference userTable = database.getReference("User");

        userTable.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check if user exits
                if (dataSnapshot.child(phoneNumber).exists()) {
                    // Get user information
                    mDialog.dismiss();
                    User user = dataSnapshot.child(phoneNumber).getValue(User.class);

                    if (user.getPassword().equals(password)) {
                        // Save current user
                        Common.currentUser = user;

                        if (isRemembered) {
                            Paper.book().write(USER_KEY, phoneNumber);
                            Paper.book().write(PASSWORD_KEY, password);
                        }

                        // Navigate to Home
                        startActivity(new Intent(context, Home.class));
                        finish();
                    } else {
                        Toast.makeText(context, "Sai mật khẩu", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mDialog.dismiss();
                    Toast.makeText(context, "Người dùng không tồn tại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

//    private void signInUser(final String Email, final String Password) {
//
//
//        //show pd
//        mDialog.show();
//
//        mAuth.signInWithEmailAndPassword(Email,Password)
//                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            User user= new User(
//                                    Email,
//                                    Password
//
//                            );
//
//
//                            mDialog.dismiss();
//                            Toast.makeText(SignIn.this,"Đăng nhập thành công", Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(SignIn.this,Home.class));
//                            finish();
//                        } else {
//                            mDialog.dismiss();
//                            Toast.makeText(SignIn.this, "Đăng  nhập thất bại", Toast.LENGTH_SHORT).show();
//
//                        }
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                mDialog.dismiss();
//                //error get and show error meesage
//                Toast.makeText(SignIn.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
//
//            }
//        });
//
//    }

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
