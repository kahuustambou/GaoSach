package com.example.gaosach;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.gaosach.Common.Common;
import com.example.gaosach.Database.Database;
import com.example.gaosach.Model.Order;
import com.example.gaosach.Model.Rating;
import com.example.gaosach.Model.Rice;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RiceDetail extends AppCompatActivity implements RatingDialogListener {

    TextView rice_name,rice_price,rice_description;
    ImageView rice_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart,btnRating;
    ElegantNumberButton numberButton;
    RatingBar ratingBar;

    String riceId="";
    FirebaseDatabase database;
    DatabaseReference rices;
    DatabaseReference ratingTbl;
    Rice currentRice;



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


        setContentView(R.layout.activity_rice_detail);
        //firebase
        database= FirebaseDatabase.getInstance();
        rices= database.getReference("Rices");
        ratingTbl= database.getReference("Rating");

        //Init view
        numberButton=(ElegantNumberButton)findViewById(R.id.number_button);
        btnCart=(FloatingActionButton)findViewById(R.id.btnCart);
        btnRating= (FloatingActionButton)findViewById(R.id.btn_rating);
        ratingBar=(RatingBar)findViewById(R.id.ratingBar);

        rice_description= (TextView)findViewById(R.id.rice_description);
        rice_price= (TextView)findViewById(R.id.rice_price);
        rice_name= (TextView)findViewById(R.id.rice_name);
        rice_image= (ImageView)findViewById(R.id.rice_image);
        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRatingDialog();


            }
        });

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Database(getBaseContext()).addToCart(new Order(
                        riceId,
                        currentRice.getName(),
                        numberButton.getNumber(),
                        currentRice.getPrice(),
                        currentRice.getDiscount()
                ));
                Toast.makeText(RiceDetail.this,"Thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        });


        collapsingToolbarLayout= (CollapsingToolbarLayout)findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandeAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);
        //lay riceid tu intent

        if(getIntent()!= null)
            riceId= getIntent().getStringExtra("RiceId");
        if(!riceId.isEmpty())
        {
            getDetailFood(riceId);
            getRatingRice(riceId);
        }




    }

    private void getRatingRice(String riceId) {

        Query riceRating= ratingTbl.orderByChild("riceId").equalTo(riceId);
        riceRating.addValueEventListener(new ValueEventListener() {
            int count=0,sum=0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapShot:dataSnapshot.getChildren())
                {
                    com.example.gaosach.Model.Rating item= postSnapShot.getValue(com.example.gaosach.Model.Rating.class);
                    sum+=Integer.parseInt(item.getRateValue());
                    count++;

                }
                if(count!=0)
                {
                    float average= sum/count;
                    ratingBar.setRating(average);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Gửi đi")
                .setNegativeButtonText("Hủy bỏ")
                .setNoteDescriptions(Arrays.asList("Rất tệ","Không tốt","Khá tốt","Rất tốt","Xuất sắc"))
                .setDefaultRating(1)
                .setTitle("Đánh giá sản phẩm này")
                .setDescription("Vui lòng chọn ngôi sao đánh giá và gửi phản hồi của bạn")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Vui lòng viết bình luận của bạn ở đây")
                .setHintTextColor(R.color.green)
                .setCommentTextColor(android.R.color.black)
                .setCommentBackgroundColor(R.color.white)
                .setWindowAnimation(R.style.RatingDialogFadeAmin)
                .create(RiceDetail.this)
                .show();


    }

    private void getDetailFood(String riceId) {
        rices.child(riceId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentRice = dataSnapshot.getValue(Rice.class);


//
                //lay image
                Picasso.with(getBaseContext()).load(currentRice .getImage())
                        .into(rice_image);

                collapsingToolbarLayout.setTitle(currentRice.getName());
                rice_price.setText(currentRice .getPrice());
                rice_name.setText(currentRice .getName());
                rice_description.setText(currentRice .getDescription());



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int value,  String comments) {

        //lay đanh gia ve firebase

         final com.example.gaosach.Model.Rating rating= new Rating(Common.currentUser.getPhone(),
                riceId,
                String.valueOf(value),
                comments);

        ratingTbl.child(Common.currentUser.getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(Common.currentUser.getPhone()).exists())
                {
                    // remove old value
                    ratingTbl.child(Common.currentUser.getPhone()).removeValue();
                    //update new rating
                    ratingTbl.child(Common.currentUser.getPhone()).setValue(rating);

                }
                else {
                    ratingTbl.child(Common.currentUser.getPhone()).setValue(rating);

                }
                Toast.makeText(RiceDetail.this,"Cám ơn bạn đã gửi đánh giá",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
