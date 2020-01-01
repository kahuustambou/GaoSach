package com.example.gaosach.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.gaosach.Interface.ItemClickListener;
import com.example.gaosach.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FavouritesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView rice_name, rice_price;
    public ImageView rice_image, fav_image,quick_cart;
    private ItemClickListener itemClickListener;

    public RelativeLayout view_background;
    public LinearLayout view_foreground;




    public FavouritesViewHolder(@NonNull View itemView) {
        super(itemView);
        rice_name = (TextView)itemView.findViewById(R.id.rice_name);
        rice_image= (ImageView)itemView.findViewById(R.id.rice_image);
        rice_price= (TextView)itemView.findViewById(R.id.rice_price);
        fav_image= (ImageView)itemView.findViewById(R.id.fav);
        quick_cart=(ImageView)itemView.findViewById(R.id.btn_quick_cart);
        view_background=(RelativeLayout)itemView.findViewById(R.id.view_background);
        view_foreground=(LinearLayout)itemView.findViewById(R.id.view_foreground);



        itemView.setOnClickListener(this);
    }
    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);

    }


}
