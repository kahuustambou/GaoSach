package com.example.gaosach;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.gaosach.Common.Common;
import com.example.gaosach.Database.Database;
import com.example.gaosach.Model.Order;
import com.example.gaosach.Model.Rating;
import com.example.gaosach.Model.Rice;
import com.example.gaosach.ViewHolder.RiceViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.example.gaosach.Common.Common.INSERT_RICE_ID;
import static com.example.gaosach.Common.Common.currentUser;
import static com.example.gaosach.Common.Validator.isEmpty;

public class RiceDetail extends AppCompatActivity implements RatingDialogListener {

    TextView rice_name, rice_price, rice_description;
    ImageView rice_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnRating;
    CounterFab btnCart;
    ElegantNumberButton numberButton;
    RatingBar ratingBar;

    String riceId = "";
    FirebaseDatabase database;
    DatabaseReference rices;
    DatabaseReference ratingTbl;
    Rice currentRice;
    TextView txtShowComment, txtFeedBackAmount, txtReviews, txtGrade;
    CounterFab fab;

    FirebaseRecyclerAdapter<Rice, RiceViewHolder> adapter;


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

        txtFeedBackAmount = findViewById(R.id.txtFeedBackAmount);
        txtReviews = findViewById(R.id.txtReviews);
        txtReviews.setText("Đánh giá");
        txtGrade = findViewById(R.id.txtGrade);
        txtShowComment = findViewById(R.id.txtShowComment);
        txtShowComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RiceDetail.this, ShowComment.class);
                intent.putExtra(INSERT_RICE_ID, riceId);
                startActivity(intent);
            }
        });
        //firebase
        database = FirebaseDatabase.getInstance();
        rices = database.getReference("Rices");
        ratingTbl = database.getReference("Rating");

        //Init view
        numberButton = findViewById(R.id.number_button);
        btnCart = findViewById(R.id.btnCart);
        btnRating = findViewById(R.id.btn_rating);
        ratingBar = findViewById(R.id.ratingBar);

        rice_description = findViewById(R.id.rice_description);
        rice_price = findViewById(R.id.rice_price);
        rice_name = findViewById(R.id.rice_name);
        rice_image = findViewById(R.id.rice_image);

        //Init Paper
        Paper.init(this);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(RiceDetail.this, Cart.class);
                startActivity(cartIntent);
            }
        });

        fab.setCount(new Database(this).getCountCart(currentUser.getPhone()));

        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRatingDialog();
            }
        });

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isExits = new Database(getBaseContext()).checkRiceDetail(riceId, Common.currentUser.getPhone());
                if (!isExits) {
                    new Database(getBaseContext()).addToCart(new Order(
                            Common.currentUser.getPhone(),
                            riceId,
                            currentRice.getName(),
                            "1",
                            currentRice.getPrice(),
                            currentRice.getDiscount(),
                            currentRice.getImage()
                    ));
                } else {

                    new Database(getBaseContext()).increaseCart(Common.currentUser.getPhone(), riceId);


                }
            }
        });


        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandeAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);
        //lay riceid tu intent

        if (getIntent() != null)
            riceId = getIntent().getStringExtra("RiceId");
        if (!isEmpty(riceId)) {
            getDetailFood(riceId);
            getRatingRice(riceId);
        }
    }

    private void getRatingRice(String riceId) {

        Query riceRating = ratingTbl.orderByChild("riceId").equalTo(riceId);
        riceRating.addValueEventListener(new ValueEventListener() {
            float count = 0, sum = 0;

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    com.example.gaosach.Model.Rating item = postSnapShot.getValue(com.example.gaosach.Model.Rating.class);
                    sum += Integer.parseInt(item.getRateValue());
                    count++;
                }

                if (count != 0) {
                    float grade = (5 * sum) / (5 * count);
                    txtFeedBackAmount.setText(String.valueOf(count));
                    txtGrade.setText(String.valueOf(grade));
                    ratingBar.setRating(grade);
                }

                count = sum = 0;
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
                .setNoteDescriptions(Arrays.asList("Rất tệ", "Không tốt", "Khá tốt", "Rất tốt", "Xuất sắc"))
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

                //lay image
                Picasso.with(getBaseContext()).load(currentRice.getImage())
                        .into(rice_image);

                collapsingToolbarLayout.setTitle(currentRice.getName());
                rice_price.setText(currentRice.getPrice());
                rice_name.setText(currentRice.getName());
                rice_description.setText(currentRice.getDescription());
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
    protected void onResume() {
        super.onResume();
        fab.setCount(new Database(this).getCountCart(currentUser.getPhone()));
        if (adapter != null)
            adapter.startListening();
    }

    @Override
    public void onPositiveButtonClicked(int value, String comments) {
        //lay đanh gia ve firebase
        final com.example.gaosach.Model.Rating rating = new Rating(currentUser.getPhone(),
                riceId,
                String.valueOf(value),
                comments);

        //fix user can rating multiple time
        ratingTbl.push()
                .setValue(rating)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Toast.makeText(RiceDetail.this, "Cám ơn bạn đã gửi đánh giá", Toast.LENGTH_SHORT).show();

                    }
                });
    }
}
