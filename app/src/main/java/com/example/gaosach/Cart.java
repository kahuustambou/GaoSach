package com.example.gaosach;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gaosach.Common.Common;
import com.example.gaosach.Database.Database;
import com.example.gaosach.Model.MyResponse;
import com.example.gaosach.Model.Notification;
import com.example.gaosach.Model.Order;
import com.example.gaosach.Model.Request;
import com.example.gaosach.Model.Sender;
import com.example.gaosach.Model.Token;
import com.example.gaosach.Remote.APIService;
import com.example.gaosach.ViewHolder.CartAdapter;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Cart extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    TextView txtTotalPrice;
    Button btnPlaceOrder;
    List<Order> cart = new ArrayList<>();
    CartAdapter adapter;

    APIService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //init service
        mService= Common.getFCMService();

        //firebase
        database= FirebaseDatabase.getInstance();
        requests= database.getReference("Requests");

        //init
        recyclerView= (RecyclerView) findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        
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
    private void showAlerDialog() {
        AlertDialog.Builder alertDialog= new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("Thêm một bước");
        alertDialog.setMessage("Nhập địa chỉ của bạn: ");

        LayoutInflater inflater= this.getLayoutInflater();
        View order_address_comment= inflater.inflate(R.layout.order_address_comment,null);
        final MaterialEditText edtAddress= (MaterialEditText)order_address_comment.findViewById(R.id.edtAddress);
        final MaterialEditText edtComment= (MaterialEditText)order_address_comment.findViewById(R.id.edtComment);


        alertDialog.setView(order_address_comment);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("CÓ", new DialogInterface.OnClickListener() {
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
                new Database(getBaseContext()).cleanCart();

                sendNotification(order_number);
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
                    Notification notification= new Notification("KDMT Dev","Bạn có một đơn hàng mới "+order_number);
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
        cart= new Database(this).getCarts();
        adapter= new CartAdapter(cart,this);
        recyclerView.setAdapter(adapter);

        //tinh tong cong tien

        int total=0;
        for (Order order:cart)
            total+=(Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));
        Locale locale= new Locale("en","US");
        NumberFormat fmt= NumberFormat.getCurrencyInstance();


        txtTotalPrice.setText(fmt.format(total));


    }
}


