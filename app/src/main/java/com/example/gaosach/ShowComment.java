package com.example.gaosach;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gaosach.Common.Common;
import com.example.gaosach.Model.Rating;
import com.example.gaosach.ViewHolder.ShowCommentViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ShowComment extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference ratingTbl;

    SwipeRefreshLayout mSwipeRefreshLayout;
    FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder> adapter;

    String riceId="";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter!= null)
            adapter.startListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/BreeSerif.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_show_comment);

        //Firebase

        database= FirebaseDatabase.getInstance();
        ratingTbl= database.getReference("Rating");

        recyclerView=(RecyclerView)findViewById(R.id.recyclerComment);
        layoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //swipe layout
        mSwipeRefreshLayout= (SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(getIntent()!= null)
                    riceId= getIntent().getStringExtra(Common.INSERT_RICE_ID);
                if(!riceId.isEmpty() && riceId!= null)
                {
                    //tao request query
                    Query query= ratingTbl.orderByChild("riceId").equalTo(riceId);

//                    FirebaseRecyclerOptions<Rating> options= new FirebaseRecyclerOptions.Builder<Rating>()
//                            .setQuery(query,Rating.class)
//                            .build();

                    adapter= new FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder>( Rating.class,
                            R.layout.show_comment_layout,
                            ShowCommentViewHolder.class,
                            ratingTbl.orderByChild("riceId").equalTo(riceId)) {
//                        @Override
//                        protected void onBindViewHolder(@NonNull ShowCommentViewHolder holder, int position, @NonNull Rating model) {
//                            holder.ratingBar.setRating(Float.parseFloat(model.getRateValue()));
//                            holder.txtComment.setText(model.getComment());
//                            holder.txtUserPhone.setText(model.getUserPhone());
//
//
//                        }

                        @NonNull
                        @Override
                        public ShowCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view= LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.show_comment_layout,parent,false);
                            return new ShowCommentViewHolder(view);
                        }

                        @Override
                        protected void populateViewHolder(ShowCommentViewHolder holder, Rating model, int position) {
                            holder.ratingBar.setRating(Float.parseFloat(model.getRateValue()));
                            holder.txtComment.setText(model.getComment());
                            holder.txtUserPhone.setText(model.getUserPhone());

                        }
                    };
                    loadComment(riceId);


                }
            }
        });
        //
        //chủ đề để tải bình luận về lần khởi chạy đầu tiên

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);

                if(getIntent()!= null)
                    riceId= getIntent().getStringExtra(Common.INSERT_RICE_ID);
                if(!riceId.isEmpty() && riceId!= null)
                {
                    //tao request query
                    Query query= ratingTbl.orderByChild("riceId").equalTo(riceId);

//                    FirebaseRecyclerOptions<Rating> options= new FirebaseRecyclerOptions.Builder<Rating>()
//                            .setQuery(query,Rating.class)
//                            .build();

                    adapter= new FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder>(
                            Rating.class,
                            R.layout.show_comment_layout,
                            ShowCommentViewHolder.class,
                            ratingTbl.orderByChild("riceId").equalTo(riceId)

                    ) {
//                        @Override
//                        protected void onBindViewHolder(@NonNull ShowCommentViewHolder holder, int position, @NonNull Rating model) {
//                            holder.ratingBar.setRating(Float.parseFloat(model.getRateValue()));
//                            holder.txtComment.setText(model.getComment());
//                            holder.txtUserPhone.setText(model.getUserPhone());
//
//
//                        }

//                        @NonNull
//                        @Override
//                        public ShowCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                            View view= LayoutInflater.from(parent.getContext())
//                                    .inflate(R.layout.show_comment_layout,parent,false);
//                            return new ShowCommentViewHolder(view);
//                        }

                        @Override
                        protected void populateViewHolder(ShowCommentViewHolder holder, Rating model, int position) {
                            holder.ratingBar.setRating(Float.parseFloat(model.getRateValue()));
                            holder.txtComment.setText(model.getComment());
                            holder.txtUserPhone.setText(model.getUserPhone());

                        }
                    };
                    loadComment(riceId);


                }

            }
        });


    }

    private void loadComment(String riceId) {

        adapter.startListening();

        recyclerView.setAdapter(adapter);
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
