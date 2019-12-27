package com.example.gaosach;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.gaosach.Common.Common;
import com.example.gaosach.Model.MyResponse;
import com.example.gaosach.Model.Notification;
import com.example.gaosach.Model.Sender;
import com.example.gaosach.Model.Token;
import com.example.gaosach.Remote.APIService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPassword extends AppCompatActivity {
    APIService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //init service
        mService= Common.getFCMService();
    }


    private void sendNotification(final Notification message, List<String> recipientIds) {

        DatabaseReference tokens= FirebaseDatabase.getInstance().getReference("Tokens");
        Query data= tokens.orderByChild("isServerToken").equalTo(true);//lay tat ca cac isServerToken là đúng
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot postSnapShot:dataSnapshot.getChildren())
                {
                    Token serverToken= postSnapShot.getValue(Token.class);

                    //create raw payload tosend

                    Sender content = new Sender(serverToken.getToken(), message);
                    mService.sendNotification(content)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    //chi chay khi lay ket qua
                                    if(response.code()== 200) {
                                        if (response.body().success == 1) {
                                            Toast.makeText(ForgotPassword.this, "Sap co notification neh", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(ForgotPassword.this, "Thất bại!!!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Log.e("Lỗi",t.getMessage());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }}

}
