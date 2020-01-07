package com.example.gaosach;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.example.gaosach.Common.Common;
import com.example.gaosach.Database.Database;
import com.example.gaosach.Interface.ItemClickListener;
import com.example.gaosach.Model.Favourites;
import com.example.gaosach.Model.Order;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RiceList extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference riceList;

    CounterFab fab;



    String categoryId="";
   DatabaseReference databaseReference;

    FirebaseRecyclerAdapter<Rice, RiceViewHolder> adapter;

    //Search Function
    FirebaseRecyclerAdapter<Rice, RiceViewHolder> searchAdapter;
    List<String> suggesList= new ArrayList<>();
    MaterialSearchBar materialSearchBar;
    //yêu thích

    Database locaDB;

    SwipeRefreshLayout swipeRefreshLayout;



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


        setContentView(R.layout.activity_rice_list);

        //firebase
        database=FirebaseDatabase.getInstance();
        riceList= database.getReference("Rices");
        //local DB
        locaDB= new Database(this);

         //Init Paper
        Paper.init(this);

        fab =(CounterFab) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(RiceList.this, Cart.class);
                startActivity(cartIntent);
            }
        });

//        fab.setCount(new Database(this).getCountCart(Common.currentUser.getPhone()));

        swipeRefreshLayout= (SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // lay intent
                if(getIntent() != null)
                    categoryId= getIntent().getStringExtra("CategoryId");
                if(!categoryId.isEmpty() && categoryId != null)
                {
                    loadListRice(categoryId);
                }

            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                // lay intent
                if(getIntent() != null)
                    categoryId= getIntent().getStringExtra("CategoryId");
                if(!categoryId.isEmpty() && categoryId != null)
                {
                    loadListRice(categoryId);
                }

                //after getIntent categoryId
                //search

                // search
                materialSearchBar= (MaterialSearchBar) findViewById(R.id.searchBar);
                materialSearchBar.setHint("Tìm kiếm sản phẩm...");
                loadSuggest(); //viet functiom để load sugest tu firebase

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
        });


        recyclerView=(RecyclerView)findViewById(R.id.recycle_rice);
        recyclerView.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


    }
    @Override
    protected void onResume() {
        super.onResume();
        fab.setCount(new Database(this).getCountCart(Common.currentUser.getPhone()));
        if(adapter!=null)
            adapter.startListening();
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        //fix
//        if(adapter!= null)
//            adapter.startListening();
//    }

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

                        materialSearchBar.setLastSuggestions(suggesList);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public static String getDotPrice(String oldPrice) {
        int count = 0;
        double integer, surplus;
        double currentPrice = Double.valueOf(oldPrice);
        String price;
        do {
            count ++;
            integer = currentPrice / 1000;
            surplus = currentPrice % 1000;
            currentPrice = integer;
        } while(currentPrice / 1000 > 1000);

        if(surplus > 0) {
            while(count != 1) {
                surplus *= 1000;
                count --;
            }

            price = (int)integer + "." + (int)surplus;
        } else {
            price = String.valueOf((int)integer);
            while(count > 0) {
                count --;
                price += ".000";
            }
        }

        return price;
    }

    private void loadListRice(String categoryId) {
        adapter = new FirebaseRecyclerAdapter<Rice, RiceViewHolder>(
                Rice.class,
                R.layout.rice_item,
                RiceViewHolder.class,
                riceList.orderByChild("menuId").equalTo(categoryId) //like: select* from rice
                ) {
            @Override
            protected void populateViewHolder(final RiceViewHolder viewHolder, final Rice model, final int position) {
                viewHolder.rice_name.setText(model.getName());
                viewHolder.rice_price.setText(String.format("%s /kg", getDotPrice(model.getPrice())));
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.rice_image);

                //quick cart

                    viewHolder.quick_cart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            boolean isExits = new Database(getBaseContext()).checkRiceList(adapter.getRef(position).getKey(), Common.currentUser.getPhone());
                            if (!isExits) {
                                new Database(getBaseContext()).addToCart(new Order(
                                        Common.currentUser.getPhone(),
                                        adapter.getRef(position).getKey(),
                                        model.getName(),
                                        "1",
                                        model.getPrice(),
                                        model.getDiscount(),
                                        model.getImage()
                                ));
                            } else {

                                new Database(getBaseContext()).increaseCart(Common.currentUser.getPhone(), adapter.getRef(position).getKey());


                            }

                            Toast.makeText(RiceList.this, "Thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                        }

                        });
                fab.setCount(new Database(getBaseContext()).getCountCart(Common.currentUser.getPhone()));





                // add yêu thích

                if(locaDB.isFavourite(adapter.getRef(position).getKey(),Common.currentUser.getPhone()))
                    viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);

                //chọn thay doi state yeu thich
                viewHolder.fav_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Favourites favourites= new Favourites();
                        favourites.setRiceId(adapter.getRef(position).getKey());
                        favourites.setRiceName(model.getName());
                        favourites.setRiceDescription(model.getDescription());
                        favourites.setRiceDiscount(model.getDiscount());
                        favourites.setRiceImage(model.getImage());
                        favourites.setRiceMenuId(model.getMenuId());
                        favourites.setUserPhone(Common.currentUser.getPhone());
                        favourites.setRicePrice(model.getPrice());


                        if(!locaDB.isFavourite(adapter.getRef(position).getKey(),Common.currentUser.getPhone()))
                        {
                            locaDB.addToFavourites(favourites);
                            viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(RiceList.this,"Đã thích",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            locaDB.removeFavourites(adapter.getRef(position).getKey(),Common.currentUser.getPhone());
                            viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(RiceList.this,"Bỏ thích",Toast.LENGTH_SHORT).show();
                        }


                    }
                });


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
        swipeRefreshLayout.setRefreshing(false);
    }
}
