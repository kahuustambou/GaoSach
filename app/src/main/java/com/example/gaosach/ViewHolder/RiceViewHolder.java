package com.example.gaosach.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gaosach.Interface.ItemClickListener;
import com.example.gaosach.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RiceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView rice_name, rice_price;
    public ImageView rice_image, fav_image;
    private ItemClickListener itemClickListener;




    public RiceViewHolder(@NonNull View itemView) {
        super(itemView);
        rice_name = (TextView)itemView.findViewById(R.id.rice_name);
        rice_image= (ImageView)itemView.findViewById(R.id.rice_image);
        rice_price= (TextView)itemView.findViewById(R.id.rice_price);
        fav_image= (ImageView)itemView.findViewById(R.id.fav);




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
