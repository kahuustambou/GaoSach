package com.example.gaosach;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class Contact extends AppCompatActivity {
    ImageView ImgMessage, ImgCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        ImgMessage = (ImageView) findViewById(R.id.imageViewMessage);
        ImgCall = (ImageView) findViewById(R.id.imageViewCall);

        ImgMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SENDTO);
                intent.putExtra("sms_body", "Chào mừng bạn đến với Gạo Việt");
                intent.setData(Uri.parse("sms:0982446871"));
                startActivity(intent);

            }
        });
        ImgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:0982446871"));
                startActivity(intent);
            }
        });


    }
}
