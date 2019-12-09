package com.example.gaosach;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.gaosach.Database.Database;
import com.example.gaosach.Model.Order;
import com.example.gaosach.Model.Rice;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class RiceDetail extends AppCompatActivity {

    TextView rice_name,rice_price,rice_description;
    ImageView rice_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart;
    ElegantNumberButton numberButton;

    String riceId="";
    FirebaseDatabase database;
    DatabaseReference rices;
    Rice currentRice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rice_detail);
        //firebase
        database= FirebaseDatabase.getInstance();
        rices= database.getReference("Rices");

        //Init view
        numberButton=(ElegantNumberButton)findViewById(R.id.number_button);
        btnCart=(FloatingActionButton)findViewById(R.id.btnCart);
        rice_description= (TextView)findViewById(R.id.rice_description);
        rice_price= (TextView)findViewById(R.id.rice_price);
        rice_name= (TextView)findViewById(R.id.rice_name);
        rice_image= (ImageView)findViewById(R.id.rice_image);

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
        }




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
}
