package com.example.gaosach;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.example.gaosach.Interface.ItemClickListener;
import com.example.gaosach.Model.Rice;
import com.example.gaosach.ViewHolder.RiceViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RiceList extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference riceList;



    String categoryId="";
   DatabaseReference databaseReference;

    FirebaseRecyclerAdapter<Rice, RiceViewHolder> adapter;

    //Search Function
    FirebaseRecyclerAdapter<Rice, RiceViewHolder> searchAdapter;
    List<String> suggesList= new ArrayList<>();
    MaterialSearchBar materialSearchBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rice_list);

        //firebase
        database=FirebaseDatabase.getInstance();
        riceList= database.getReference("Rices");

        recyclerView=(RecyclerView)findViewById(R.id.recycle_rice);
        recyclerView.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // lay intent
        if(getIntent() != null)
            categoryId= getIntent().getStringExtra("CategoryId");
        if(!categoryId.isEmpty() && categoryId != null)
        {
            loadListRice(categoryId);
        }
        // search
        materialSearchBar= (MaterialSearchBar) findViewById(R.id.searchBar);
        materialSearchBar.setHint("Tìm kiếm sản phẩm...");
        loadSuggest(); //viet functiom để load sugest tu firebase

        materialSearchBar.setLastSuggestions(suggesList);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                // khi user type their text. ho se thay do suggest list

                List<String> suggest= new ArrayList<String>();
                for (String search:suggesList) //loop in suggest list
                {
                    if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                materialSearchBar.setLastSuggestions(suggest);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //khi tim kiem se close
                //restore original adapter
                if(!enabled)
                    recyclerView.setAdapter(adapter);

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {

                //khi tim kiem hoan thanh
                //show ket qua
                startSearch(text);

            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });
    }

    private void startSearch(CharSequence text) {
        searchAdapter= new FirebaseRecyclerAdapter<Rice, RiceViewHolder>(
                Rice.class,
                R.layout.rice_item,
                RiceViewHolder.class,
                riceList.orderByChild("name").equalTo(text.toString()) //compare tên
        ) {
            @Override
            protected void populateViewHolder(RiceViewHolder viewHolder, Rice model, int position) {
                viewHolder.rice_name.setText(model.getName());
                viewHolder.rice_price.setText(model.getPrice());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.rice_image);

                final Rice local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        // bat dau activity moi
                        Intent riceDetail= new Intent(RiceList.this, RiceDetail.class);
                        riceDetail.putExtra("RiceId",searchAdapter.getRef(position).getKey());
                        startActivity(riceDetail);

                    }
                });

            }
        };
        recyclerView.setAdapter(searchAdapter);
    }

    private void loadSuggest() {
        riceList.orderByChild("menuId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                        {
                            Rice item= postSnapshot.getValue(Rice.class);
                            suggesList.add(item.getName()); //add ten rice
                            databaseReference = FirebaseDatabase.getInstance().getReference().child("Rices");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadListRice(String categoryId) {
        adapter = new FirebaseRecyclerAdapter<Rice, RiceViewHolder>(Rice.class,
                R.layout.rice_item,
                RiceViewHolder.class,
                riceList.orderByChild("menuId").equalTo(categoryId) //like: select* from rice
                ) {
            @Override
            protected void populateViewHolder(RiceViewHolder viewHolder, Rice model, int position) {
                viewHolder.rice_name.setText(model.getName());
                viewHolder.rice_price.setText(model.getPrice());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.rice_image);

                final Rice local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        // bat dau activity moi
                        Intent riceDetail= new Intent(RiceList.this, RiceDetail.class);
                        riceDetail.putExtra("RiceId",adapter.getRef(position).getKey());
                        startActivity(riceDetail);
//                        Toast.makeText(RiceList.this,""+local.getName(),Toast.LENGTH_SHORT).show();

                    }
                });





            }
        };

        //set adapter
        Log.d("TAG",""+adapter.getItemCount());
        recyclerView.setAdapter(adapter);
    }
}
