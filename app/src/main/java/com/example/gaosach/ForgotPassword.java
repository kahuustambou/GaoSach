package com.example.gaosach;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gaosach.Common.Common;
import com.example.gaosach.Model.MyResponse;
import com.example.gaosach.Model.Notification;
import com.example.gaosach.Model.Sender;
import com.example.gaosach.Model.Token;
import com.example.gaosach.Model.User;
import com.example.gaosach.Remote.APIService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.example.gaosach.Common.Common.currentUser;
import static com.example.gaosach.Common.Common.nextIntent;
import static com.example.gaosach.Profile.updateUser;

public class ForgotPassword extends AppCompatActivity {
    private DatabaseReference userReference;
    private EditText edtPhoneNumber;
    private Button btnSendCode, btnBackToSignIn;
    private Context currentContext;
    private String code;
    public static boolean isSent;


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

        setContentView(R.layout.forgot_password);
        currentContext = this;
        isSent = false;
        userReference = FirebaseDatabase.getInstance().getReference("User");
        edtPhoneNumber = findViewById(R.id.edtPhoneNumberSend);
        btnSendCode = findViewById(R.id.btnSendCode);
        btnBackToSignIn = findViewById(R.id.btnBackToSignIn);

        btnSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCode("Gửi", edtPhoneNumber);
            }
        });

        btnBackToSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(currentContext, SignIn.class));
                finish();
            }
        });
    }

    private void sendCode(String title, final EditText edtPhoneNumber) {
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String phoneNumber = edtPhoneNumber.getText().toString();
                if (!dataSnapshot.child(phoneNumber).exists()) {
                    Toast.makeText(currentContext, "Người dùng không tồn tại", Toast.LENGTH_SHORT).show();
                    return;
                }

                User user = dataSnapshot.child(phoneNumber).getValue(User.class);
                try {
                    code = UUID.randomUUID().toString();
                    user.setCode(code);
                    currentUser = user;
                    updateUser(user, currentContext, "Đã gửi mã xác nhận.", "Đã xảy ra sự cố. Không thể gửi mã xác nhận.");
                    isSent = true;
                    Notification message = new Notification(
                            "GaoSach", "Mật khẩu tạm thời của bạn là: " + code
                    );

                    List<String> recipientIds = new ArrayList<String>();
                    recipientIds.add(currentUser.getPhone());

                    // Pass data to intent
                    nextIntent = new Intent(ForgotPassword.this, SignIn.class);
                    nextIntent.putExtra("AuthenticationCode", code);
                    sendNotification(currentContext, recipientIds, message, false);
                    btnSendCode.setText("Gửi lại");
                    userReference.removeEventListener(this);
                } catch (Exception exception) {
                    Log.d("--Message: error--", exception.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public static void sendNotification(final Context context, final List<String> recipientPhoneNumbers, final Notification notification, final boolean isSendToAdmin) {
        final APIService mService;
        mService = Common.getFCMService();
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");

        if (!isSendToAdmin) {
            tokens.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (int index = 0; index < recipientPhoneNumbers.size(); ++index) {
                        if (!dataSnapshot.hasChild(recipientPhoneNumbers.get(index))) {
                            continue;
                        }

                        Token tokenItem = dataSnapshot.child(recipientPhoneNumbers.get(index)).getValue(Token.class);
                        Sender content = new Sender(tokenItem.getToken(), notification);
                        mService.sendNotification(content)
                                .enqueue(new Callback<MyResponse>() {
                                    @Override
                                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                        if (response.code() == 200) {
                                            if (response.body().success == 1) {
                                                Log.e("--Message--", "Successful");
                                            } else {
                                                Log.e("--Message--", "Failed");
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<MyResponse> call, Throwable t) {
                                        Log.e("--Message--", t.getMessage());
                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("--Message--", databaseError.getMessage());
                }
            });
        } else {
            Query data = tokens.orderByChild("serverToken").equalTo(true);//lay tat ca cac isServerToken là đúng
            data.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                        Token serverToken = postSnapShot.getValue(Token.class);

                        //create raw payload to send
                        Sender content = new Sender(serverToken.getToken(), notification);
                        mService.sendNotification(content)
                                .enqueue(new Callback<MyResponse>() {
                                    @Override
                                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                        if (response.code() == 200) {
                                            if (response.body().success == 1) {
                                                Toast.makeText(context, "Cám ơn bạn đã đặt hàng", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(context, "Thất bại!!!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<MyResponse> call, Throwable t) {
                                        Log.e("--Message--", t.getMessage());

                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
