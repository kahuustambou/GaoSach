package com.example.gaosach;

import android.app.ProgressDialog;
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

import static com.example.gaosach.Common.Validator.isEmpty;
import static com.example.gaosach.Common.Validator.isPassword;
import static com.example.gaosach.Common.Validator.isPhoneNumber;

public class SignUp extends AppCompatActivity {
    EditText edtPhoneNumber, edtPassword, edtFullName,edtsourceCode;
    Button btnSignUp;
    TextView mHaveAccount;

    private ProgressDialog mDialog;
    private FirebaseDatabase database;
    private DatabaseReference userReference;
    private ValueEventListener signUpEventListener;
    private boolean isValidPhoneNumber, isValidPassword, isValidFullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtPhoneNumber = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);
        edtFullName = findViewById(R.id.edtName);
        edtsourceCode= findViewById(R.id.edtsourceCode);

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
                    String sourceCode= edtsourceCode.getText().toString().trim();

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(phoneNumber).exists()) {
                            mDialog.dismiss();
                            Toast.makeText(SignUp.this, "Số điện thoại đã được đăng ký", Toast.LENGTH_SHORT).show();
                        } else {
                            mDialog.dismiss();
                            User user = new User(fullName, phoneNumber, password, false,sourceCode);
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
        if(!isEnable) {
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
                edtFullName.setError(getString(R.string.input_error_name));
                isValidFullName = false;
            } else {
                isValidFullName = true;
            }

            // Check if phone number is valid
            String phoneNumber = edtPhoneNumber.getText().toString().trim();
            if (isEmpty(phoneNumber)) {
                edtPhoneNumber.setError(getString(R.string.input_error_phone));
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

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        if(mAuth.getCurrentUser() !=null){
//
//
//        }
//    }
//    private void SignUpUser(){
//        final String Name= edtName.getText().toString().trim();
//        final String Email= edtEmail.getText().toString().trim();
//        final String Phone= edtPhoneNumber.getText().toString().trim();
//        String Password= edtPassword.getText().toString().trim();
//
//        if(Name.isEmpty()){
//            edtName.setError(getString(R.string.input_error_name));
//            edtName.requestFocus();
//        }
//        if (Email.isEmpty()) {
//            edtEmail.setError(getString(R.string.input_error_email));
//            edtEmail.requestFocus();
//            return;
//        }
//        if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
//            edtEmail.setError(getString(R.string.input_error_email_invalid));
//            edtEmail.requestFocus();
//            return;
//        }
//
//        if (Password.isEmpty()) {
//            edtPassword.setError(getString(R.string.input_error_password));
//            edtPassword.requestFocus();
//            return;
//        }
//
//        if (Password.length() < 6) {
//            edtPassword.setError(getString(R.string.input_error_password_length));
//            edtPassword.requestFocus();
//            return;
//        }
//
//        if (Phone.isEmpty()) {
//            edtPhoneNumber.setError(getString(R.string.input_error_phone));
//            edtPhoneNumber.requestFocus();
//            return;
//        }
//
//        if (Phone.length() != 10) {
//            edtPhoneNumber.setError(getString(R.string.input_error_phone_invalid));
//            edtPhoneNumber.requestFocus();
//            return;
//        }
//
//        mAuth.createUserWithEmailAndPassword(Email,Password)
//                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//
//                        if(task.isSuccessful()){
//                            User user= new User(
//                                    Name,
//                                    Email
//
//                            );
//                            FirebaseDatabase.getInstance().getReference("User")
//                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if(task.isSuccessful())
//                                    {
//                                        Toast.makeText(SignUp.this,"Đăng kí thành công",Toast.LENGTH_SHORT).show();
//                                        Intent signup= new Intent(SignUp.this,Home.class);
//                                        startActivity(signup);
//
//
//                                    }else {
//                                        Toast.makeText(SignUp.this, "Xác thực thất bại", Toast.LENGTH_SHORT).show();
//
//                                    }
//                                }
//                            });
//
//                        }else {
//                            Toast.makeText(SignUp.this, task.getException().getMessage(),Toast.LENGTH_SHORT).show();
//
//                        }
//
//                    }
//                });
//
//
//    }

//    @Override
//    public boolean onSupportNavigateUp() {
//        onBackPressed();
//
//        return super.onSupportNavigateUp();
//    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.btnSignUp:
//                SignUpUser();
//                break;
//
//        }
//
//    }
}
