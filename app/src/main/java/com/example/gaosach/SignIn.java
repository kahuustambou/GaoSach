package com.example.gaosach;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
    TextView notHaveAccount;
    Button btnSignIn;
    TextView txtRememberMe;
    private boolean isRemembered, isClickedSignIn;
    private boolean isValidPhoneNumber, isValidPassword;

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
        isRemembered = false;
        isClickedSignIn = false;
        isValidPhoneNumber = false;
        isValidPassword = false;

        // Init Paper
        Paper.init(this);

        activeSignInButton(false);
        edtPhone.addTextChangedListener(signInTextWatcher);
        edtPassword.addTextChangedListener(signInTextWatcher);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isClickedSignIn = true;
                String phoneNumber = edtPhone.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
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

    private void activeSignInButton(boolean isEnable) {
        btnSignIn.setEnabled(isEnable);
        if(!isEnable) {
            btnSignIn.setBackgroundResource(R.drawable.reg_btn_inactive);
        } else {
            btnSignIn.setBackgroundResource(R.drawable.reg_btnsignin);
        }
    }

    private TextWatcher signInTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String phoneNumber = edtPhone.getText().toString().trim();
            if (isEmpty(phoneNumber)) {
                edtPhone.setError("Vui lòng nhập số điện thoại");
                isValidPhoneNumber = false;
            }

            if (!isPhoneNumber(phoneNumber)) {
                edtPhone.setError("Số điện thoại bắt buộc là 10 số");
                isValidPhoneNumber = false;
            } else {
                isValidPhoneNumber = true;
            }

            String password = edtPassword.getText().toString().trim();
            if (isEmpty(password)) {
                edtPassword.setError("Vui lòng nhập mật khẩu của bạn");
                isValidPassword = false;
            }

            if (!isPassword(password)) {
                edtPassword.setError("Mật khẩu phải có ít nhất 6 kí tự");
                isValidPassword = false;
            } else {
                isValidPassword = true;
            }

            activeSignInButton(isValidPhoneNumber && isValidPassword);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }

    };

    private void signIn(final String phoneNumber, final String password, final Context context) {
        mDialog = new ProgressDialog(context);
        mDialog.setMessage("Vui lòng đợi...");
        mDialog.show();

        database = FirebaseDatabase.getInstance();
        userReference = database.getReference("User");

        signInEventListener = userReference.addValueEventListener(new ValueEventListener() {
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

                        if (isClickedSignIn) {
                            // Navigate to Home
                            userReference.removeEventListener(signInEventListener);
                            startActivity(new Intent(context, Home.class));
                            finish();
                        }
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
