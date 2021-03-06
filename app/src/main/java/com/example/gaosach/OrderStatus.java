package com.example.gaosach;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.gaosach.Common.Common;
import com.example.gaosach.Model.Notification;
import com.example.gaosach.Model.Request;
import com.example.gaosach.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.example.gaosach.Common.Common.convertCodeToColorStatus;
import static com.example.gaosach.Common.Common.nextIntent;
import static com.example.gaosach.Common.Common.currentContext;
import static com.example.gaosach.ForgotPassword.sendNotification;

public class OrderStatus extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference requests;

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
        setContentView(R.layout.activity_order_status);

        currentContext = this;

        //firebase
        database= FirebaseDatabase.getInstance();
        requests=database.getReference("Requests");
        
        recyclerView=(RecyclerView)findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        
        loadOrders(Common.currentUser.getPhone());
    }


    private void loadOrders(final String phone) {
        adapter= new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                requests.orderByChild("phone")
                .equalTo(phone)

        ) {
            @Override
            protected void populateViewHolder(final OrderViewHolder viewHolder, final Request model, final int position) {
                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderStatus.setTextColor(convertCodeToColorStatus(model.getStatus()));
                viewHolder.txtOrderAddress.setText(model.getAddress());
//                viewHolder.txtOrderPhone.setText(model.getPhone());
                viewHolder.txtOrderPhone.setText(model.getPhone());
                viewHolder.txtOrderDate.setText(Common.getDate(Long.parseLong(adapter.getRef(position).getKey())));
                viewHolder.btn_cancel.setOnClickListener(new View.OnClickListener() {


                    @Override
                    public void onClick(View v) {
                        if(adapter.getItem(position).getStatus().equals("0")) {
//                            deleteOrder(adapter.getRef(position).getKey());
                            nextIntent = new Intent(OrderStatus.this, OrderStatus.class);
//                            nextIntent.putExtra("status", "Bi huy");
                            Notification notification = new Notification("Gạo Việt", "Đơn hàng " + viewHolder.txtOrderId.getText().toString() + " đã bị hủy.");
                            sendNotification(OrderStatus.this, null, notification, true);
                            Toast.makeText(OrderStatus.this,"Đơn hàng đã bị hủy",Toast.LENGTH_SHORT).show();

                        }


                        else
                            Toast.makeText(OrderStatus.this,"Bạn không thể hủy đơn hàng này",Toast.LENGTH_SHORT).show();

                    }

                });
                viewHolder.btnDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent orderDetail= new Intent(OrderStatus.this,OrderDetail.class);
                        Common.currentRequest= model;
                        orderDetail.putExtra("OrderId",adapter.getRef(position).getKey());
                        startActivity(orderDetail);

                    }
                });

            }

        };

        recyclerView.setAdapter(adapter);

    }



//    private void deleteOrder(final String key) {
//        requests.child(key).setValue(key).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Toast.makeText(OrderStatus.this,new StringBuilder("Đơn hàng ")
//                .append(key)
//                .append("đã bị hủy").toString(),Toast.LENGTH_SHORT).show();
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(OrderStatus.this,e.getMessage(),Toast.LENGTH_SHORT).show();
//
//            }
//        });
//
//    }




}
