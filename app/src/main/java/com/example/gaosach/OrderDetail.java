package com.example.gaosach;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.example.gaosach.Common.Common;
import com.example.gaosach.ViewHolder.OrderDetailAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OrderDetail extends AppCompatActivity {

    TextView order_id,order_phone,order_address,order_total,order_comment;
    String order_id_value;
    RecyclerView lstRices;
    RecyclerView.LayoutManager layoutManager;

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
        setContentView(R.layout.activity_order_detail);


        order_id=(TextView)findViewById(R.id.order_id);
        order_phone=(TextView)findViewById(R.id.order_phone);
        order_address=(TextView)findViewById(R.id.order_address);
        order_total=(TextView)findViewById(R.id.order_total);
        order_comment=(TextView)findViewById(R.id.order_comment);

        lstRices= (RecyclerView)findViewById(R.id.lstRices);
        lstRices.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(this);
        lstRices.setLayoutManager(layoutManager);

        if(getIntent()!= null)
            order_id_value= getIntent().getStringExtra("OrderId");

        //set value
        order_id.setText(order_id_value);
        order_phone.setText(Common.currentRequest.getPhone());
        order_total.setText(Common.currentRequest.getTotal());
        order_address.setText(Common.currentRequest.getAddress());
        order_comment.setText(Common.currentRequest.getComment());

        OrderDetailAdapter adapter= new OrderDetailAdapter(Common.currentRequest.getRices());
        adapter.notifyDataSetChanged();
        lstRices.setAdapter(adapter);

    }
}
