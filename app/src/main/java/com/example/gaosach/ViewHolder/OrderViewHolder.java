package com.example.gaosach.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.gaosach.Interface.ItemClickListener;
import com.example.gaosach.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtOrderId,txtOrderStatus,txtOrderPhone,txtOrderAddress,txtOrderDate,btn_cancel;

    private ItemClickListener itemClickListener;


    public Button btnDetail;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        txtOrderAddress=(TextView)itemView.findViewById(R.id.order_address);
        txtOrderId=(TextView)itemView.findViewById(R.id.order_id);
        txtOrderStatus=(TextView)itemView.findViewById(R.id.order_status);
        txtOrderPhone=(TextView)itemView.findViewById(R.id.order_phone);
        txtOrderDate=(TextView)itemView.findViewById(R.id.order_date);
        btn_cancel=(TextView) itemView.findViewById(R.id.btn_cancel);
        btnDetail = (Button)itemView.findViewById(R.id.btnDetail);


        itemView.setOnClickListener(this);


    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
//        itemClickListener.onClick(view,getAdapterPosition(),false);

    }
}
