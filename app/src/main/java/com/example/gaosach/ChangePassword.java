package com.example.gaosach;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.example.gaosach.Common.Common.currentUser;
import static com.example.gaosach.Common.Validator.isEmpty;
import static com.example.gaosach.Profile.updateUser;

public class ChangePassword extends AppCompatActivity {
    private EditText edtCurrentPassword, edtNewPassword, edtRepeatedPassword;
    private boolean isValidCurrentPassword, isValidNewPassword, isValidRepeatedPassword;
    private Button btnChange;




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
        setContentView(R.layout.change_password_layout);

        edtCurrentPassword = findViewById(R.id.edtCurrentPassword);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtRepeatedPassword = findViewById(R.id.edtRepeatedPassword);
        btnChange = findViewById(R.id.btnChange);
        isValidCurrentPassword = isValidNewPassword = isValidRepeatedPassword = false;
        onNewIntent(getIntent());

        edtCurrentPassword.addTextChangedListener(textWatcher);
        edtNewPassword.addTextChangedListener(textWatcher);
        edtRepeatedPassword.addTextChangedListener(textWatcher);

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentUser.setPassword(edtNewPassword.getText().toString());
                updateUser(currentUser, ChangePassword.this, null, null);
                Toast.makeText(ChangePassword.this, "Đã cập nhật", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ChangePassword.this, Home.class));
            }
        });
    }

    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("AuthenticationCode")) {
                String authenticationCode = extras.getString("AuthenticationCode");
                Log.d("--Message--", "onNewIntent: co neh " + authenticationCode);
                edtCurrentPassword.setText(authenticationCode);
            }
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String currentPassword = edtCurrentPassword.getText().toString().trim();
            if (isEmpty(currentPassword) || !currentPassword.equals(currentUser.getPassword()) && !currentPassword.equals(currentUser.getCode())) {
                edtCurrentPassword.setError("Vui lòng nhập mật khẩu gần nhất");
                isValidCurrentPassword = false;
            } else {
                isValidCurrentPassword = true;
            }

            String newPassword = edtNewPassword.getText().toString().trim();
            if (isEmpty(newPassword)) {
                edtNewPassword.setError("Vui lòng nhập mật khẩu mới");
                isValidNewPassword = false;
            } else {
                isValidNewPassword = true;
            }

            String repeatedPassword = edtRepeatedPassword.getText().toString().trim();
            if (isEmpty(repeatedPassword)) {
                edtRepeatedPassword.setError("Vui lòng nhập mật khẩu mới");
                isValidRepeatedPassword = false;
            }

            if (!newPassword.equals(repeatedPassword)) {
                edtRepeatedPassword.setError("Vui lòng nhập lại cùng mật khẩu");
                isValidRepeatedPassword = false;
            } else {
                isValidRepeatedPassword = true;
            }

            activeChangeButton(isValidCurrentPassword && isValidNewPassword && isValidRepeatedPassword);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private void activeChangeButton(boolean isEnable) {
        btnChange.setEnabled(isEnable);
        if (!isEnable) {
            btnChange.setBackgroundResource(R.drawable.reg_btn_inactive);
        } else {
            btnChange.setBackgroundResource(R.drawable.reg_btnsignin);
        }
    }
}
