package com.example.accounting;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.AutoMigration;
import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {
        BookModel.Book.class,
        BookModel.Cash.class,
        BookModel.Staff.class,
        BookModel.Category.class
        }, version = 2, exportSchema = false)
public abstract class BookDatabase extends RoomDatabase {
    private static volatile BookDatabase instance;
    public abstract BookDao bookDao();
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    public static synchronized BookDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), BookDatabase.class, "book_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                BookDao bookDao = instance.bookDao();
                });
            }
    };
}
