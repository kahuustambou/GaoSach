package com.example.gaosach;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gaosach.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.example.gaosach.Common.Common.PASSWORD_KEY;
import static com.example.gaosach.Common.Common.USER_KEY;
import static com.example.gaosach.Common.Common.currentUser;
import static com.example.gaosach.Common.Validator.isEmpty;
import static com.example.gaosach.Common.Validator.isPhoneNumber;

public class Profile extends AppCompatActivity {
    EditText edtFullName, edtPhoneNumber, edtAddress;
    Button btnUpdate;
    Typeface typeface;
    int contextCompat;
    DatabaseReference database;


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
        setContentView(R.layout.activity_profile);

        database = FirebaseDatabase.getInstance().getReference("User");

        typeface = Typeface.create("arial", Typeface.NORMAL);
        contextCompat = ContextCompat.getColor(this, R.color.black);

        edtFullName = findViewById(R.id.edtFullName);
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        edtAddress = findViewById(R.id.edtAddress);
        btnUpdate = findViewById(R.id.btnUpdate);

        setUserInfo();
//        setEditable(false);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = edtFullName.getText().toString().trim();
                String phoneNumber = edtPhoneNumber.getText().toString().trim();
                String address = edtAddress.getText().toString().trim();
                if (checkValidInputs(fullName, phoneNumber)) {
                    try {
                        User user = new User();
                        user.setIsStaff(currentUser.getIsStaff());
                        user.setCode(currentUser.getCode());
                        user.setPassword(currentUser.getPassword());
                        user.setName(fullName);
                        user.setPhone(phoneNumber);
                        user.setAddress(address);
                        updateUser(user, Profile.this, null, null);
                    } catch (Exception exception) {
                        Log.d("loineh", exception.getMessage());
                    }
                }
            }
        });
    }

    private void setUserInfo() {
        edtFullName.setText(currentUser.getName());
        edtPhoneNumber.setText(currentUser.getPhone());
        edtAddress.setText(currentUser.getAddress());
    }

    private void setEditable(boolean isEnable) {
        edtFullName.setEnabled(isEnable);
        edtPhoneNumber.setEnabled(isEnable);

        setTextStyle();
    }

    private void setTextStyle() {
        edtFullName.setTypeface(typeface);
        edtFullName.setTextColor(contextCompat);

        edtPhoneNumber.setTypeface(typeface);
        edtPhoneNumber.setTextColor(contextCompat);
    }

    private boolean checkValidInputs(String fullName, String phoneNumber) {

        if (isEmpty(phoneNumber)) {
            edtPhoneNumber.setError(getString(R.string.input_error_phone));
            edtPhoneNumber.requestFocus();
            return false;
        }

        if (!isPhoneNumber(phoneNumber)) {
            edtPhoneNumber.setError("Số điện thoại mặc định phải có 10 số");
            edtPhoneNumber.requestFocus();
            return false;
        }

        return true;
    }

    public static void updateUser(User user, final Context context, final String successMessage, final String failureMessage) {
        final Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(user.getPhone(), user.toMap());
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("User");
        userReference.updateChildren(childUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, isEmpty(successMessage) ? "Thay đổi thành công." : successMessage, Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, isEmpty(failureMessage) ? "Thay đổi thất bại." : failureMessage, Toast.LENGTH_LONG).show();
                    }
                });

        if(!isEmpty(currentUser.getPhone())) {
            if (!user.getPhone().equals(currentUser.getPhone())) {
                userReference.child(currentUser.getPhone()).setValue(null);
                currentUser = user;
                Paper.init(context);
                Paper.book().write(USER_KEY, currentUser.getPhone());
                Paper.book().write(PASSWORD_KEY, currentUser.getPassword());
            }
        } else {
            currentUser = user;
            Paper.init(context);
            Paper.book().write(USER_KEY, currentUser.getPhone());
            Paper.book().write(PASSWORD_KEY, currentUser.getPassword());
        }
    }
}
