package com.example.accounting;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BookDao {

    //create
    @Insert
    void insertBook(BookModel.Book book);
    @Insert
    void insertCash(BookModel.Cash cash);
    @Insert
    void insertStaff(BookModel.Staff staff);
    @Insert
    void insertCategory(BookModel.Category category);

    //read
    @Query("SELECT * FROM Book ORDER BY BookID")
    LiveData<List<BookModel.Book>> getAllBook();
    @Query("SELECT * FROM Cash ORDER BY CashID")
    LiveData<List<BookModel.Cash>> getAllCash();
    @Query("SELECT * FROM Book WHERE BookID = :bookId")
     LiveData<BookModel.Book> getBookById(int bookId);
    @Query("SELECT * FROM Cash WHERE CashID = :cashId")
    LiveData<BookModel.Cash> getCashById(int cashId);
    @Query("SELECT * FROM Staff ORDER BY StaffID")
    LiveData<List<BookModel.Staff>> getAllStaff();
    @Query("SELECT * FROM Category ORDER BY CategoryID")
    LiveData<List<BookModel.Category>> getAllCategory();

    //update
    @Update
    void updateBook(BookModel.Book book);
    @Update
    void updateCash(BookModel.Cash cash);
    @Update
    void updateStaff(BookModel.Staff staff);
    @Update
    void updateCategory(BookModel.Category category);

    //delete
    @Query("DELETE FROM Book")
    void deleteAllBook();
    @Query("DELETE FROM Cash")
    void deleteAllCash();
    @Query("DELETE FROM Staff")
    void deleteAllStaff();
    @Query("DELETE FROM Category")
    void deleteAllCategory();

    @Delete
    void deleteBook(BookModel.Book book);
    @Delete
    void deleteCash(BookModel.Cash cash);
    @Delete
    void deleteStaff(BookModel.Staff staff);
    @Delete
    void deleteCategory(BookModel.Category category);
}