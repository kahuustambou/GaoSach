package com.example.gaosach;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gaosach.Common.Common;
import com.example.gaosach.Database.Database;
import com.example.gaosach.Helper.RecycleItemTouchHelper;
import com.example.gaosach.Interface.RecycleItemTouchHelperListener;
import com.example.gaosach.Model.MyResponse;
import com.example.gaosach.Model.Notification;
import com.example.gaosach.Model.Order;
import com.example.gaosach.Model.Request;
import com.example.gaosach.Model.Sender;
import com.example.gaosach.Model.Token;
import com.example.gaosach.Remote.APIService;
import com.example.gaosach.ViewHolder.CartAdapter;
import com.example.gaosach.ViewHolder.CartViewHolder;
import com.google.android.gms.location.places.Place;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Cart extends AppCompatActivity implements RecycleItemTouchHelperListener {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    public TextView txtTotalPrice;
    Button btnPlaceOrder;
    List<Order> cart = new ArrayList<>();
    CartAdapter adapter;

    APIService mService;
    Place shippingAddress;
    private String address;

    RelativeLayout rootLayout;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //add code before setContentview method

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/BreeSerif.otf")
        .setFontAttrId(R.attr.fontPath)
        .build());


        setContentView(R.layout.activity_cart);

        //init service
        mService= Common.getFCMService();

        rootLayout= (RelativeLayout)findViewById(R.id.rootLayout);


        //firebase
        database= FirebaseDatabase.getInstance();
        requests= database.getReference("Requests");

        //init
        recyclerView= (RecyclerView) findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //swipe to delete
        ItemTouchHelper.SimpleCallback itemTouchHelperCalback= new RecycleItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(itemTouchHelperCalback).attachToRecyclerView(recyclerView);

        txtTotalPrice= (TextView)findViewById(R.id.txtTotalPrice);
        btnPlaceOrder=(Button) findViewById(R.id.btnPlaceOrder);
        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               if(cart.size()>0)
                   showAlerDialog();
               else
                   Toast.makeText(Cart.this,"Giỏ hàng của bạn trống",Toast.LENGTH_SHORT).show();

            }

        });
        
        loadListRice();



    }

//    @Override
//    public boolean onContextItemSelected(@NonNull MenuItem item) {
//        if(item.getTitle().equals(Common.DELETE))
//            deleteCart(item.getOrder());
//        return true;
//    }

//    private void deleteCart(int position) {
//        cart.remove(position);
//        new Database(this).cleanCart(Common.currentUser.getPhone());
//        for (Order item:cart)
//            new Database(this).addToCart(item);
//
//        loadListRice();
//
//    }

    private void showAlerDialog() {
        AlertDialog.Builder alertDialog= new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("Thêm một bước");
        alertDialog.setMessage("Nhập địa chỉ của bạn: ");

        LayoutInflater inflater= this.getLayoutInflater();
        View order_address_comment= inflater.inflate(R.layout.order_address_comment,null);
     final MaterialEditText edtAddress= (MaterialEditText)order_address_comment.findViewById(R.id.edtAddress);



        final MaterialEditText edtComment= (MaterialEditText)order_address_comment.findViewById(R.id.edtComment);

        //radio
        final RadioButton rdiHomeAddress=(RadioButton)order_address_comment.findViewById(R.id.rdiHomeAddress);

        //su kien cho radio
        rdiHomeAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b)
                {
                    if(Common.currentUser.getHomeAddress()!= null ||
                           !TextUtils.isEmpty(Common.currentUser.getHomeAddress()))
                    {
                        address = Common.currentUser.getHomeAddress();

                    }




                    
                }

            }
        });



        alertDialog.setView(order_address_comment);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("GỬI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // tao request moi

                Request request = new Request(
                        Common.currentUser.getPhone(),
                        Common.currentUser.getName(),
                        edtAddress.getText().toString(),
                        txtTotalPrice.getText().toString(),
                        edtComment.getText().toString(),
                        "0",//trang thái
                        cart

                );


                //submit den firebase
                // chung ta su dung systerm.current den kry
                String order_number= String.valueOf(System.currentTimeMillis());
                requests.child(order_number)
                        .setValue(request);

                //delete cart
                new Database(getBaseContext()).cleanCart(Common.currentUser.getPhone());

                sendNotification(order_number);
                Toast.makeText(Cart.this,"Cám ơn bạn đã đặt hàng thành công",Toast.LENGTH_SHORT).show();
                Intent move= new Intent(Cart.this,Home.class);
                startActivity(move);

//
            }
        });
        alertDialog.setNegativeButton("KHÔNG", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();


            }
        });
        alertDialog.show();



    }

    private void sendNotification(final String order_number) {

        DatabaseReference tokens= FirebaseDatabase.getInstance().getReference("Tokens");
        Query data= tokens.orderByChild("isServerToken").equalTo(true);//lay tat ca cac isServerToken là đúng
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot postSnapShot:dataSnapshot.getChildren())
                {
                    Token serverToken= postSnapShot.getValue(Token.class);

                    //create raw payload tosend
                    Notification notification= new Notification("Gạo Việt","Bạn có một đơn hàng mới "+order_number);
                    Sender content = new Sender(serverToken.getToken(),notification);
                    mService.sendNotification(content)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    //chi chay khi lay ket qua

                                 if(response.code()== 200) {
                                     if (response.body().success == 1) {
                                         Toast.makeText(Cart.this, "Cám ơn bạn đã đặt hàng", Toast.LENGTH_SHORT).show();
                                         finish();
                                     } else {
                                         Toast.makeText(Cart.this, "Thất bại!!!", Toast.LENGTH_SHORT).show();


                                     }
                                 }

                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Log.e("Lỗi",t.getMessage());

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadListRice() {
        cart= new Database(this).getCarts(Common.currentUser.getPhone());
        adapter= new CartAdapter(cart,this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        //tinh tong cong tien

        int total=0;

        for (Order order:cart)
            total+=(Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));
        Locale locale= new Locale("vie","VN");
//        NumberFormat fmt= NumberFormat.getCurrencyInstance();
        NumberFormat fmt= NumberFormat.getNumberInstance();


        txtTotalPrice.setText(fmt.format(total));


    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if(viewHolder instanceof CartViewHolder) {
            String name = ((CartAdapter) recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition()).getProductName();
            final Order deleteItem = ((CartAdapter) recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition());

            final int deleteIndex = viewHolder.getAdapterPosition();

            adapter.removeItem(deleteIndex);
            new Database(getBaseContext()).removeFromCart(deleteItem.getProductId(), Common.currentUser.getPhone());

            //update total
            //tinh tong cong tien

            int total = 0;
            List<Order> orders = new Database(getBaseContext()).getCarts(Common.currentUser.getPhone());
            for (Order item : orders)
                total += (Integer.parseInt(item.getPrice())) * (Integer.parseInt(item.getQuantity()));
            Locale locale = new Locale("vie", "VN");
            NumberFormat fmt = NumberFormat.getCurrencyInstance();


            txtTotalPrice.setText(fmt.format(total));

            //make snackbar
            Snackbar snackbar = Snackbar.make(rootLayout, name + "Xóa bỏ khỏi giỏ hàng", Snackbar.LENGTH_LONG);
            snackbar.setAction("Hoàn tác", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.restoreItem(deleteItem, deleteIndex);
                    new Database(getBaseContext()).addToCart(deleteItem);


                    //update total
                    //tinh tong cong tien

                    int total = 0;
                    List<Order> orders = new Database(getBaseContext()).getCarts(Common.currentUser.getPhone());
                    for (Order item : orders)
                        total += (Integer.parseInt(item.getPrice())) * (Integer.parseInt(item.getQuantity()));
                    Locale locale = new Locale("vie", "VN");
                    NumberFormat fmt = NumberFormat.getCurrencyInstance();


                    txtTotalPrice.setText(fmt.format(total));
                }

            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}


