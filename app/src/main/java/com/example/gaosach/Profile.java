package com.example.gaosach;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
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

import static com.example.gaosach.Common.Common.currentUser;
import static com.example.gaosach.Common.Validator.isEmail;
import static com.example.gaosach.Common.Validator.isEmpty;
import static com.example.gaosach.Common.Validator.isPassword;
import static com.example.gaosach.Common.Validator.isPhoneNumber;

public class Profile extends AppCompatActivity {
    EditText edtFullName, edtEmail, edtPhoneNumber;
    Button btnEdit;
    Typeface typeface;
    int contextCompat;
    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        database = FirebaseDatabase.getInstance().getReference();

        typeface = Typeface.create("arial", Typeface.NORMAL);
        contextCompat = ContextCompat.getColor(this, R.color.black);

        edtFullName = findViewById(R.id.edtFullName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        btnEdit = findViewById(R.id.btnEdit);

        setEditable(false);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnEdit.getText() == "Edit") {
                    btnEdit.setText("Update");
                    setEditable(true);
                } else {
//                    String email = edtEmail.getText().toString().trim();
                    String fullName = edtFullName.getText().toString().trim();
                    String phoneNumber = edtPhoneNumber.getText().toString().trim();

                    if (checkValidInputs(fullName, phoneNumber)) {
                        try {
                            User user = new User(
                                    fullName, phoneNumber, null, false
                            );

                            updateUser(user);
                        } catch (Exception exception) {
                            Log.d("loineh", exception.getMessage());
                        }

                        btnEdit.setText("Edit");
                        setEditable(false);
                    } else {
                        return;
                    }
                }
            }
        });
    }

    private void setTextView() {
        edtFullName.setText(currentUser.getName());
        edtPhoneNumber.setText(currentUser.getPhone());
    }

    private void setEditable(boolean isEnable) {
        edtFullName.setEnabled(isEnable);
        edtPhoneNumber.setEnabled(isEnable);
        //        edtEmail.setEnabled(isEnable);

        setTextStyle();
    }

    private void setTextStyle() {
        edtFullName.setTypeface(typeface);
        edtFullName.setTextColor(contextCompat);

//        edtEmail.setTypeface(typeface);
//        edtEmail.setTextColor(contextCompat);

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
            edtPhoneNumber.setError(getString(R.string.input_error_phone_invalid));
            edtPhoneNumber.requestFocus();
            return false;
        }

        return true;
    }

    public void updateUser(User user) {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/User/" + currentUser.getPhone(), user.toMap());
        database.child("User/" + currentUser.getPhone());
        database.updateChildren(childUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Profile.this, "Successful", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Profile.this, "Failed", Toast.LENGTH_LONG).show();
                    }
                });
    }
}
