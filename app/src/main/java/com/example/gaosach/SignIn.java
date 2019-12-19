package com.example.gaosach;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
    TextView notHaveAccount, txtForgetpw;
    Button btnSignIn;
    CheckBox txtRememberMe;

    private boolean isRemembered, isClickedSignIn;

    //declare an instance of firebase
    private FirebaseDatabase database;
    private DatabaseReference userReference;
    private ValueEventListener signInEventListener;
    private ProgressDialog mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtPassword = findViewById(R.id.edtPassword);
        edtPhone = findViewById(R.id.edtPhone);

        notHaveAccount = findViewById(R.id.nothave_account);
        btnSignIn = findViewById(R.id.btnSignIn);
        txtRememberMe = findViewById(R.id.rememberMe);
        txtForgetpw= findViewById(R.id.txtForgetpw);
        isRemembered = false;
        isClickedSignIn = false;


        // Init Paper
        Paper.init(this);
        database = FirebaseDatabase.getInstance();
        userReference = database.getReference("User");



        txtForgetpw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(SignIn.this, SignUp.class));
                showForgotPwdDialog();
            }
        });



        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isClickedSignIn = true;
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

        // Navigate to Sign Up screen if user does not have account
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

        // Init dialog
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Đăng Nhập ...");

    }



    private void signIn(final String phoneNumber, final String password, final Context context) {
        mDialog = new ProgressDialog(context);
        mDialog.setMessage("Vui lòng đợi...");
        mDialog.show();






        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
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
                            Paper.book().write(USER_KEY, edtPhone.getText().toString());
                            Paper.book().write(PASSWORD_KEY, edtPassword.getText().toString());
                        }

                        if(isClickedSignIn) {
                            // Navigate to Home
//                            userReference.removeEventListener(signInEventListener);
                            startActivity(new Intent(context, Home.class));
                            finish();

                        }
                        userReference.removeEventListener(this);

                    } else {
                        Toast.makeText(context, "Sai mật khẩu", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mDialog.dismiss();
                    Toast.makeText(context, "Người dùng không tồn tại", Toast.LENGTH_SHORT).show();
                }

                isClickedSignIn = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void showForgotPwdDialog() {

        final AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Quên mật khẩu");
        builder.setMessage("Nhập mã nguồn của bạn");


        LayoutInflater inflater= this.getLayoutInflater();
        View forgot_view= inflater.inflate(R.layout.forgot_password,null);

        builder.setView(forgot_view);
        builder.setIcon(R.drawable.ic_security_black_24dp);

        final EditText edtPhone= (EditText)forgot_view.findViewById(R.id.edtNumberPhone);
        final EditText edtSourceCode= (EditText)forgot_view.findViewById(R.id.edtsourceCode);

        builder.setPositiveButton("ĐÚNG", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                userReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        User user= dataSnapshot.child(edtPhone.getText().toString())
                                .getValue(User.class);

                        if(user.getSourceCode().equals(edtSourceCode.getText().toString()))
                            Toast.makeText(SignIn.this,"Mật khẩu của bạn: "+ user.getPassword(),Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(SignIn.this,"Sai mã nguồn",Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });
        builder.setNegativeButton("SAI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface1, int i) {

            }
        });
        builder.show();


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();

        return super.onSupportNavigateUp();
    }
}
