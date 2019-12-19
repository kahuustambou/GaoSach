package com.example.gaosach.Remote;

import com.example.gaosach.Model.MyResponse;
import com.example.gaosach.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAV_SHPGk:APA91bEcXh0Rr0yRHeLTcK7GuZaKG0git_3IzxHIvipd8yTh4Qcuy5qZR8lBJZTP2G0s5v4Z3QMmXxKnXQvjhTOVDC1ePFeW-HdGTV9OF64VDFYp9OaDH-xvfpBHJ2dUwHj6BgW8Arwk"

            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}
