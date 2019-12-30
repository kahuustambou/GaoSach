package com.example.gaosach;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gaosach.Common.Common;
import com.example.gaosach.Model.MyResponse;
import com.example.gaosach.Model.Notification;
import com.example.gaosach.Model.Sender;
import com.example.gaosach.Model.Token;
import com.example.gaosach.Model.User;
import com.example.gaosach.Remote.APIService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.gaosach.Common.Common.currentUser;

public class ForgotPassword extends AppCompatActivity {
    private DatabaseReference userReference;
    private EditText edtPhoneNumber;
    private TextView txtSignIn;
    private Button btnSendCode;
    private Context currentContext;
    public static boolean isSent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.forgot_password);
        currentContext = this;
        isSent = false;
        userReference = FirebaseDatabase.getInstance().getReference("User");
        edtPhoneNumber = findViewById(R.id.edtPhoneNumberSend);
        btnSendCode = findViewById(R.id.btnSendCode);
        txtSignIn = findViewById(R.id.txtSignIn);

        btnSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCode("Gửi", edtPhoneNumber);
            }
        });

        txtSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(currentContext, SignIn.class));
            }
        });

//        Notification message = new Notification(
//                "GaoSach", "Gao sach neh"
//        );
//
//        List<String> recipientIds = new ArrayList<String>();
//        recipientIds.add("0908431453");
//        sendNotification(recipientIds, message);
    }

    public static void updateUserCode(User user, final Context context) {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(user.getPhone(), user.toMap());
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("User");
        userReference.updateChildren(childUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Đã gửi mã xác nhận", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Đã xảy ra sự cố. Không thể gửi mã xác nhận.", Toast.LENGTH_LONG).show();
                    }
                });

        if (!user.getPhone().equals(currentUser.getPhone())) {
            userReference.child(currentUser.getPhone()).setValue(null);
        }
    }

    private void sendCode(String title, final EditText edtPhoneNumber) {
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String phoneNumber = edtPhoneNumber.getText().toString();
                if (!dataSnapshot.child(phoneNumber).exists()) {
                    Toast.makeText(currentContext, "Người dùng không tồn tại", Toast.LENGTH_SHORT).show();
                    return;
                }

                User user = dataSnapshot.child(phoneNumber).getValue(User.class);
                try {
                    String code = UUID.randomUUID().toString();
                    user.setCode(code);
                    updateUserCode(user, currentContext);
                    isSent = true;
                    btnSendCode.setText("Gửi lại");
                    userReference.removeEventListener(this);
                } catch (Exception exception) {
                    Log.d("loineh", exception.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

//    private void showConfirmCode(final AlertDialog.Builder builder) {
//        builder.setTitle("Quên mật khẩu");
//        builder.setMessage("Nhập mã xác thực của bạn");
//
//        LayoutInflater inflater = this.getLayoutInflater();
//        View forgot_view = inflater.inflate(R.layout.forgot_password, null);
//
//        builder.setIcon(R.drawable.ic_security_black_24dp);
//        final EditText edtCode = forgot_view.findViewById(R.id.edtCode);
//        edtCode.setHint("Nhập mã xác thực ...");
//        builder.setView(forgot_view);
//        builder.setPositiveButton("ĐÚNG", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                userReference.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        String code = edtPhone.getText().toString();
//
//                        if (!dataSnapshot.child(code).exists()) {
//                            Toast.makeText(currentContext, "Người dùng không tồn tại", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//
//                        User user = dataSnapshot.child(code)
//                                .getValue(User.class);
//
//                        if (user.getCode().equals(code))
//                            Toast.makeText(SignIn.this, "Mật khẩu của bạn: " + user.getPassword(), Toast.LENGTH_SHORT).show();
//                        else
//                            Toast.makeText(SignIn.this, "Sai mã xác thực", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//            }
//        });
//
//        builder.setNegativeButton("SAI", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface1, int i) {
//            }
//        });
//
//        builder.create();
//    }


    public static void sendNotification(final List<String> recipientIds, final Notification message) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query data = tokens.orderByChild("isServerToken").equalTo(true);//lay tat ca cac isServerToken là đúng
        final APIService mService;
        mService = Common.getFCMService();

        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Token token = data.getValue(Token.class);

                    Log.e("--truoc khi check--", "Successful");
                    if (!recipientIds.contains(token.getToken())) {
                        return;
                    }

                    Log.e("--Sau khi check--", "Successful");

                    //create raw payload tosend
//                    for (int index = 0; index < recipientIds.size(); ++index) {
                    Sender content = new Sender(token.getToken(), message);
                    mService.sendNotification(content)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    //chi chay khi lay ket qua
                                    if (response.code() == 200) {
                                        if (response.body().success == 1) {
//                                            Toast.makeText(ForgotPassword.this, "Sap co notification neh", Toast.LENGTH_SHORT).show();
//                                            this.finish();
                                            Log.e("--Message--", "Successful");
                                        } else {
                                            Log.e("--Message--", "Failed");
//                                            Toast.makeText(ForgotPassword.this, "Thất bại!!!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Log.e("Lỗi", t.getMessage());
                                }
                            });
//                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
