package com.example.gaosach;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gaosach.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.example.gaosach.Common.Validator.isEmpty;
import static com.example.gaosach.Common.Validator.isPassword;
import static com.example.gaosach.Common.Validator.isPhoneNumber;

public class SignUp extends AppCompatActivity {
    EditText edtPhoneNumber, edtPassword, edtFullName;
    Button btnSignUp;
    TextView mHaveAccount;

    private ProgressDialog mDialog;
    private FirebaseDatabase database;
    private DatabaseReference userReference;
    private ValueEventListener signUpEventListener;
    private boolean isValidPhoneNumber, isValidPassword, isValidFullName;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/BreeSerif.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_sign_up);

        edtPhoneNumber = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);
        edtFullName = findViewById(R.id.edtName);

        btnSignUp = findViewById(R.id.btnSignUp);
        mHaveAccount = findViewById(R.id.have_account);

        isValidFullName = isValidPhoneNumber = isValidPassword = false;
        activeSignUpButton(false);
        database = FirebaseDatabase.getInstance();
        userReference = database.getReference("User");

        edtFullName.addTextChangedListener(signUpTextWatcher);
        edtPhoneNumber.addTextChangedListener(signUpTextWatcher);
        edtPassword.addTextChangedListener(signUpTextWatcher);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog = new ProgressDialog(SignUp.this);
                mDialog.setMessage("Vui lòng đợi...");
                mDialog.show();

                signUpEventListener = userReference.addValueEventListener(new ValueEventListener() {
                    String phoneNumber = edtPhoneNumber.getText().toString().trim();
                    String fullName = edtFullName.getText().toString().trim();
                    String password = edtPassword.getText().toString().trim();

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(phoneNumber).exists()) {
                            mDialog.dismiss();
                            Toast.makeText(SignUp.this, "Số điện thoại đã được đăng ký", Toast.LENGTH_SHORT).show();
                        } else {
                            mDialog.dismiss();
                            User user = new User(fullName, phoneNumber, password, false, "", "");
                            userReference.child(phoneNumber).setValue(user);
                            Toast.makeText(SignUp.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();

                            userReference.removeEventListener(signUpEventListener);
                            startActivity(new Intent(SignUp.this, SignIn.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        //handle signin text view
        mHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUp.this, SignIn.class));
            }
        });
    }

    private void activeSignUpButton(boolean isEnable) {
        btnSignUp.setEnabled(isEnable);
        if (!isEnable) {
            btnSignUp.setBackgroundResource(R.drawable.reg_btn_inactive);
        } else {
            btnSignUp.setBackgroundResource(R.drawable.reg_btnsignup);
        }
    }

    private TextWatcher signUpTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Check if full name is valid
            String fullName = edtFullName.getText().toString().trim();
            if (isEmpty(fullName)) {
                edtFullName.setError("Vui lòng nhập tên người dùng");
                isValidFullName = false;
            } else {
                isValidFullName = true;
            }

            // Check if phone number is valid
            String phoneNumber = edtPhoneNumber.getText().toString().trim();
            if (isEmpty(phoneNumber)) {
                edtPhoneNumber.setError("Nhập sai số điện thoại");
                isValidPhoneNumber = false;
            }

            if (!isPhoneNumber(phoneNumber)) {
                edtPhoneNumber.setError("Số điện thoại mặc định là 10 số");
                isValidPhoneNumber = false;
            } else {
                isValidPhoneNumber = true;
            }

            // Check if password is valid
            String password = edtPassword.getText().toString().trim();
            if (isEmpty(password)) {
                edtPassword.setError(getString(R.string.input_error_password));
                isValidPassword = false;
            }

            if (!isPassword(password)) {
                edtPassword.setError("Mật khẩu phải có ít nhất 6 kí tự");
                isValidPassword = false;
            } else {
                isValidPassword = true;
            }

            activeSignUpButton(isValidFullName && isValidPhoneNumber && isValidPassword);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
