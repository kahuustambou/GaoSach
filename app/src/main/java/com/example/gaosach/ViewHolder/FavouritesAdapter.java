package com.example.gaosach.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.gaosach.Common.Common;
import com.example.gaosach.Database.Database;
import com.example.gaosach.Interface.ItemClickListener;
import com.example.gaosach.Model.Favourites;
import com.example.gaosach.Model.Order;
import com.example.gaosach.R;
import com.example.gaosach.RiceDetail;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesViewHolder> {

    private Context context;
    private List<Favourites> favouritesList;

    public FavouritesAdapter(Context context,List<Favourites> favouritesList){
        this.context= context;
        this.favouritesList= favouritesList;

    }

    @NonNull
    @Override
    public FavouritesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(context)
                .inflate(R.layout.favourites_item,parent,false);
        return new FavouritesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouritesViewHolder viewHolder, final int position) {
        viewHolder.rice_name.setText(favouritesList.get(position).getRiceName());
        viewHolder.rice_price.setText(String.format("%s /kg",favouritesList.get(position).getRicePrice().toString()));
        Picasso.with(context).load(favouritesList.get(position).getRiceImage())
                .into(viewHolder.rice_image);

        //quick cart

        viewHolder.quick_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isExits = new Database(context).checkRiceList(favouritesList.get(position).getRicePrice(), Common.currentUser.getPhone());
                if (!isExits) {
                    new Database(context).addToCart(new Order(
                            Common.currentUser.getPhone(),
                            favouritesList.get(position).getRiceId(),
                            favouritesList.get(position).getRiceName(),
                            "1",
                            favouritesList.get(position).getRicePrice(),
                            favouritesList.get(position).getRiceDiscount(),
                            favouritesList.get(position).getRiceImage()
                    ));
                } else {
                    new Database(context).increaseCart(Common.currentUser.getPhone(),
                            favouritesList.get(position).getRiceId());


                }
                Toast.makeText(context, "Thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            }


        });

        final Favourites local = favouritesList.get(position);
        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                // bat dau activity moi
                Intent riceDetail= new Intent(context, RiceDetail.class);
                riceDetail.putExtra("RiceId",favouritesList.get(position).getRiceId());
                context.startActivity(riceDetail);
//                        Toast.makeText(RiceList.this,""+local.getName(),Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return favouritesList.size();
    }
    public void removeItem(int position)
    {
        favouritesList.remove(position);
        notifyItemRemoved(position);
    }
    public void restoreItem(Favourites item,int position)
    {
        favouritesList.add(position,item);
        notifyItemInserted(position);
    }

    public Favourites getItem(int position)
    {
        return favouritesList.get(position);
    }
}
