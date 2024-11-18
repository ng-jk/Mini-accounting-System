package com.example.accounting;
import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class BookRepository {

    // Variables for DAO and LiveData for different tables
    private final BookDao bookDao;
    private final LiveData<List<BookModel.Book>> allBooks;
    private final LiveData<List<BookModel.Cash>> allCash;
    private final LiveData<List<BookModel.Staff>> allStaff;
    private final LiveData<List<BookModel.Category>> allCategory;

    // ExecutorService for handling background operations
    private final Executor executor;

    // Constructor initializing DAO, LiveData, and Executor
    public BookRepository(Application application) {
        BookDatabase database = BookDatabase.getInstance(application);
        bookDao = database.bookDao();
        allBooks = bookDao.getAllBook(); // Ensure this method exists in DAO
        allCash = bookDao.getAllCash();
        allStaff = bookDao.getAllStaff();
        allCategory = bookDao.getAllCategory();
        executor = Executors.newSingleThreadExecutor(); // Single thread executor
    }

    // Insert method for Book
    public void insertBook(BookModel.Book book) {
        executor.execute(() -> bookDao.insertBook(book));
    }
    public void insertCash(BookModel.Cash cash) {
        executor.execute(() -> bookDao.insertCash(cash));
    }
    public void insertStaff(BookModel.Staff staff) {
        executor.execute(() -> bookDao.insertStaff(staff));
    }
    public void insertCategory(BookModel.Category category) {
        executor.execute(() -> bookDao.insertCategory(category));
    }


    // Update method for Book
    public void updateBook(BookModel.Book book) {
        executor.execute(() -> bookDao.updateBook(book));
    }
    public void updateCash(BookModel.Cash cash) {
        executor.execute(() -> bookDao.updateCash(cash));
    }
    public void updateStaff(BookModel.Staff staff) {
        executor.execute(() -> bookDao.updateStaff(staff));
    }
    public void updateCategory(BookModel.Category category) {
        executor.execute(() -> bookDao.updateCategory(category));
    }


    // Delete method for Book
    public void deleteBook(BookModel.Book book) {
        executor.execute(() -> bookDao.deleteBook(book));
    }
    public void deleteCash(BookModel.Cash cash) {
        executor.execute(() -> bookDao.deleteCash(cash));
    }
    public void deleteStaff(BookModel.Staff staff) {
        executor.execute(() -> bookDao.deleteStaff(staff));
    }
    public void deleteCategory(BookModel.Category category) {
        executor.execute(() -> bookDao.deleteCategory(category));
    }

    public void deleteAllBook() {
        executor.execute(() -> bookDao.deleteAllBook());
    }
    public void deleteAllCash() {
        executor.execute(() -> bookDao.deleteAllCash());
    }
    public void deleteAllStaff() {
        executor.execute(() -> bookDao.deleteAllStaff());
    }
    public void deleteAllCategory() {
        executor.execute(() -> bookDao.deleteAllCategory());
    }

    // Method to get all Books
    public LiveData<List<BookModel.Book>> getAllBook() {
        return allBooks;
    }
    public LiveData<BookModel.Book> getBookById(int bookId) {
        return bookDao.getBookById(bookId);
    }
    public LiveData<BookModel.Cash> getCashById(int cashId) {
        return bookDao.getCashById(cashId);
    }
    // Method to get all Cash entries
    public LiveData<List<BookModel.Cash>> getAllCash() {
        return allCash;
    }
    public LiveData<List<BookModel.Staff>> getAllStaff() {
        return allStaff;
    }
    public LiveData<List<BookModel.Category>> getAllCategory() {
        return allCategory;
    }
}
