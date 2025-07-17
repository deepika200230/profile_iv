package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "UserDB";
    public static final String TABLE_NAME = "users";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(" +
                "userId TEXT PRIMARY KEY, " +
                "password TEXT, name TEXT, age INTEGER, dob TEXT, image TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("userId", user.userId);
        values.put("password", user.password);
        values.put("name", user.name);
        values.put("age", user.age);
        values.put("dob", user.dob);
        values.put("image", user.image);
        return db.insert(TABLE_NAME, null, values) != -1;
    }

    public User login(String userId, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE userId=? AND password=?",
                new String[]{userId, password});
        if (cursor.moveToFirst()) {
            return extractUser(cursor);
        }
        return null;
    }

    public User getUserById(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE userId=?",
                new String[]{userId});
        if (cursor.moveToFirst()) {
            return extractUser(cursor);
        }
        return null;
    }

    private User extractUser(Cursor cursor) {
        User user = new User();
        user.userId = cursor.getString(0);
        user.password = cursor.getString(1);
        user.name = cursor.getString(2);
        user.age = cursor.getInt(3);
        user.dob = cursor.getString(4);
        user.image = cursor.getString(5);
        return user;
    }
    public void printAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users", null);
        while (cursor.moveToNext()) {
            Log.d("DB_CHECK", "UserID: " + cursor.getString(0) +
                    ", Name: " + cursor.getString(2) +
                    ", ImgPath: " + cursor.getString(5));
        }
    }
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users", null);
        while (cursor.moveToNext()) {
            users.add(extractUser(cursor));
        }
        cursor.close();
        return users;
    }
    public boolean deleteUser(String userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "userId=?", new String[]{userId}) > 0;
    }


}
