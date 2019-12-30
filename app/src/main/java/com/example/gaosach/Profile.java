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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.example.gaosach.Common.Common.USER_KEY;
import static com.example.gaosach.Common.Common.currentUser;
import static com.example.gaosach.Common.Validator.isEmpty;
import static com.example.gaosach.Common.Validator.isPhoneNumber;

public class Profile extends AppCompatActivity {
    EditText edtFullName, edtPhoneNumber;
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
        btnUpdate = findViewById(R.id.btnUpdate);

        setUserInfo();
//        setEditable(false);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = edtFullName.getText().toString().trim();
                String phoneNumber = edtPhoneNumber.getText().toString().trim();
                if (checkValidInputs(fullName, phoneNumber)) {
                    try {
                        User user = new User();
                        user.setIsStaff(currentUser.getIsStaff());
                        user.setCode(currentUser.getCode());
                        user.setPassword(currentUser.getPassword());
                        user.setName(fullName);
                        user.setPhone(phoneNumber);
                        updateUser(user, Profile.this);
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

    public static void updateUser(User user, final Context context) {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(user.getPhone(), user.toMap());
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("User");
        userReference.updateChildren(childUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Thay đổi thành công", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Thay đổi thất bại", Toast.LENGTH_LONG).show();
                    }
                });

        if (!user.getPhone().equals(currentUser.getPhone())) {
            userReference.child(currentUser.getPhone()).setValue(null);
            currentUser = user;
            Paper.init(context);
            Paper.book().write(USER_KEY, user.getPhone());
        }
    }
}
