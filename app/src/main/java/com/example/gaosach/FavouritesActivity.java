package com.example.gaosach;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.gaosach.Common.Common;
import com.example.gaosach.Database.Database;
import com.example.gaosach.Helper.RecycleItemTouchHelper;
import com.example.gaosach.Interface.RecycleItemTouchHelperListener;
import com.example.gaosach.Model.Favourites;
import com.example.gaosach.ViewHolder.FavouritesAdapter;
import com.example.gaosach.ViewHolder.FavouritesViewHolder;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FavouritesActivity extends AppCompatActivity implements RecycleItemTouchHelperListener {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FavouritesAdapter adapter;
    RelativeLayout rootLayout;




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

        setContentView(R.layout.activity_favourites);

        rootLayout=(RelativeLayout) findViewById(R.id.root_layout);

        recyclerView=(RecyclerView)findViewById(R.id.recycle_fav);
        layoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //swipe to delete
        ItemTouchHelper.SimpleCallback itemTouchHelperCalback= new RecycleItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(itemTouchHelperCalback).attachToRecyclerView(recyclerView);

        loadFavourites();

    }

    private void loadFavourites() {
        adapter= new FavouritesAdapter(this,new Database(this).getAllFavourites(Common.currentUser.getPhone()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if(viewHolder instanceof FavouritesViewHolder)
        {
            String name=((FavouritesAdapter)recyclerView.getAdapter()).getItem(position).getRiceName();
            final Favourites deleteItem=((FavouritesAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition());
            final int deleteIndex= viewHolder.getAdapterPosition();

            adapter.removeItem(viewHolder.getAdapterPosition());
            new Database(getBaseContext()).removeFavourites(deleteItem.getRiceId(), Common.currentUser.getPhone());

            //make snackbar
            Snackbar snackbar = Snackbar.make(rootLayout, name + "Xóa bỏ khỏi yêu thích", Snackbar.LENGTH_LONG);
            snackbar.setAction("Hoàn tác", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.restoreItem(deleteItem, deleteIndex);
                    new Database(getBaseContext()).addToFavourites(deleteItem);



                }

            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();

        }

    }
}
