package com.example.gaosach.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.gaosach.Model.Favourites;
import com.example.gaosach.Model.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteAssetHelper {
    private  static final String DB_NAME="RiceDB.db";
    private static final  int DB_VER=2;

    public Database(Context context) {
        super(context, DB_NAME,null, DB_VER);
    }

    public boolean checkRiceList(String riceId,String userPhone)
    {
        boolean flag= false;
        SQLiteDatabase db= getReadableDatabase();
        Cursor cursor= null;
        String SQLQuery= String.format("SELECT * From OrderDetail WHERE UserPhone='%s' AND ProductId='%s'",userPhone,riceId);
        cursor= db.rawQuery(SQLQuery,null);
        if(cursor.getCount()>0)
            flag= true;
        else
            flag= false;
        cursor.close();
        return flag;
    }

    public List<Order> getCarts(String userPhone)
    {
        SQLiteDatabase db= getReadableDatabase();
        SQLiteQueryBuilder qb= new SQLiteQueryBuilder();

        String[] sqlSelect={"UserPhone","ProductName","ProductId","Quantity","Price","Discount","Image"};
        String sqlTable="OrderDetail";

        qb.setTables(sqlTable);
        Cursor c= qb.query(db,sqlSelect,"UserPhone=?",new String[]{userPhone},null,null,null );

        final List<Order> result = new ArrayList<>();
        if(c.moveToFirst())
        {
            do{
                result.add(new Order(
                        c.getString(c.getColumnIndex("UserPhone")),
                        c.getString(c.getColumnIndex("ProductId")),
                       c.getString(c.getColumnIndex("ProductName")),
                        c.getString(c.getColumnIndex("Quantity")),
                       c.getString(c.getColumnIndex("Price")),
                        c.getString(c.getColumnIndex("Discount")),
                        c.getString(c.getColumnIndex("Image"))

                ));
            } while (c.moveToNext());
        }

        return result;
    }

    public void addToCart(Order order)
    {
        SQLiteDatabase db= getReadableDatabase();
        String query= String.format("INSERT OR REPLACE INTO OrderDetail(UserPhone,ProductId,ProductName,Quantity,Price,Discount,Image) VALUES('%s','%s','%s','%s','%s','%s','%s');",
                order.getUserPhone(),
                order.getProductId(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice(),
                order.getDiscount(),
                order.getImage());
        db.execSQL(query);
    }

    public void cleanCart(String userPhone)
    {
        SQLiteDatabase db= getReadableDatabase();
        String query= String.format("DELETE FROM OrderDetail WHERE UserPhone='%s'",userPhone);
        db.execSQL(query);
    }

    public int getCountCart(String userPhone) {
        int count=0;
        SQLiteDatabase db= getReadableDatabase();
        String query= String.format("SELECT COUNT(*) FROM OrderDetail Where UserPhone='%s'",userPhone);
        Cursor cursor= db.rawQuery(query,null);
        if(cursor.moveToFirst())
        {
            do {
                count= cursor.getInt(0);
            }while (cursor.moveToNext());
        }
        return count;
    }

    public void UpdateCart(Order order) {
        SQLiteDatabase db= getReadableDatabase();
        String query= String.format("UPDATE OrderDetail SET Quantity= %s WHERE UserPhone= '%s' AND ProductId='%s'",order.getQuantity(),order.getUserPhone(),order.getProductId());
        db.execSQL(query);
    }

    public void increaseCart(String userPhone,String riceId) {
        SQLiteDatabase db= getReadableDatabase();
        String query= String.format("UPDATE OrderDetail SET Quantity= Quantity+1 WHERE UserPhone= '%s' AND ProductId='%s'",userPhone,riceId);
        db.execSQL(query);
    }

    public void removeFromCart(String productId, String phone) {

        SQLiteDatabase db= getReadableDatabase();
        String query= String.format("DELETE FROM OrderDetail WHERE UserPhone='%s' and ProductId='%s'",phone,productId);
        db.execSQL(query);
    }

//    public void updateUser(User user) {
//        SQLiteDatabase db= getReadableDatabase();
//        String query= String.format("UPDATE User SET (fullName, email, phoneNumber) VALUES('%s','%s','%s');",
//                user.Name,
//                user.Email,
//                user.Phone);
//
//        db.query();
//    }

    //yêu thích
    public void addToFavourites(Favourites rice)
    {
        SQLiteDatabase db= getReadableDatabase();
        String query= String.format("INSERT INTO Favourites("+
                "RiceId,RiceName,RicePrice,RiceMenuId,RiceImage,RiceDiscount,RiceDescription,UserPhone)"+
                "VALUES('%s','%s','%s','%s','%s','%s','%s','%s');",
                rice.getRiceId(),
                rice.getRiceName(),
                rice.getRicePrice(),
                rice.getRiceMenuId(),
                rice.getRiceImage(),
                rice.getRiceDiscount(),
                rice.getRiceDescription(),
                rice.getUserPhone());
        db.execSQL(query);

    }
    public void removeFavourites(String riceId,String userPhone)
    {
        SQLiteDatabase db= getReadableDatabase();
        String query= String.format("DELETE FROM Favourites WHERE RiceId='%s' and UserPhone='%s';", riceId,userPhone);
        db.execSQL(query);

    }
    public boolean isFavourite(String riceId,String userPhone)
    {
        SQLiteDatabase db= getReadableDatabase();
        String query= String.format("SELECT * FROM Favourites WHERE RiceId='%s' and UserPhone='%s';", riceId,userPhone);
        Cursor cursor= db.rawQuery(query,null);
        if(cursor.getCount()<=0)
        {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;

    }

    public List<Favourites> getAllFavourites (String userPhone)
    {
        SQLiteDatabase db= getReadableDatabase();
        SQLiteQueryBuilder qb= new SQLiteQueryBuilder();

        String[] sqlSelect={"UserPhone","RiceId","RiceName","RiceMenuId","RiceImage","RicePrice","RiceDiscount","RiceDescription"};
        String sqlTable="Favourites";

        qb.setTables(sqlTable);
        Cursor c= qb.query(db,sqlSelect,"UserPhone=?",new String[]{userPhone},null,null,null );

        final List<Favourites> result = new ArrayList<>();
        if(c.moveToFirst())
        {
            do{
                result.add(new Favourites(

                        c.getString(c.getColumnIndex("RiceId")),
                        c.getString(c.getColumnIndex("RiceName")),
                        c.getString(c.getColumnIndex("RicePrice")),
                        c.getString(c.getColumnIndex("RiceMenuId")),
                        c.getString(c.getColumnIndex("RiceImage")),
                        c.getString(c.getColumnIndex("RiceDiscount")),
                        c.getString(c.getColumnIndex("RiceDescription")),
                        c.getString(c.getColumnIndex("UserPhone"))


                ));
            } while (c.moveToNext());
        }

        return result;
    }


}
