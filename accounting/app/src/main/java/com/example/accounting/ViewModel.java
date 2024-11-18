package com.example.accounting;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ViewModel extends AndroidViewModel {

    private static BookRepository repository;
    private LiveData<List<BookModel.Book>> allBook;
    private LiveData<List<BookModel.Cash>> allCash;
    private LiveData<List<BookModel.Staff>> allStaff;
    private LiveData<List<BookModel.Category>> allCategory;

    public ViewModel (@NonNull Application application) {
        super(application);
        repository = new BookRepository(application);
        allBook = repository.getAllBook();
        allCash = repository.getAllCash();
        allStaff = repository.getAllStaff();
        allCategory = repository.getAllCategory();
    }

    // below method is use to insert the data to our repository.
    public void insertBook(BookModel.Book book) {
        repository.insertBook(book);
    }
    public void insertCash (BookModel.Cash cash) {
        repository.insertCash(cash);
    }
    public void insertStaff (BookModel.Staff staff) {
        repository.insertStaff(staff);
    }
    public void insertCategory (BookModel.Category category) {
        repository.insertCategory(category);
    }

    // below line is to update data in our repository.
    public void updateBook(BookModel.Book book) {
        repository.updateBook(book);
    }
    public void updateCash(BookModel.Cash cash) {
        repository.updateCash(cash);
    }
    public void updateStaff(BookModel.Staff staff) {
        repository.updateStaff(staff);
    }
    public void updateCategory(BookModel.Category category){
        repository.updateCategory(category);
    }


    // below line is to delete the data in our repository.
    public void deleteBook(BookModel.Book book) {
        repository.deleteBook(book);
    }
    public void deleteCash(BookModel.Cash cash) {
        repository.deleteCash(cash);
    }
    public void deleteStaff(BookModel.Staff staff) {
        repository.deleteStaff(staff);
    }
    public void deleteCategory(BookModel.Category category) {
        repository.deleteCategory(category);
    }

    public void deleteAllBook(){repository.deleteAllBook();}
    public void deleteAllCash(){repository.deleteAllCash();}
    public void deleteAllStaff(){repository.deleteAllStaff();}
    public void deleteAllCategory(){repository.deleteAllCategory();}

    // below method is to get all the courses in our list.
    public LiveData<List<BookModel.Book>> getAllBook() {
        return allBook;
    }
    public LiveData<List<BookModel.Cash>> getAllCash() {
        return allCash;
    }
    public LiveData<BookModel.Book> getBookById(int bookId) {
        return repository.getBookById(bookId);
    }
    public LiveData<BookModel.Cash> getCashById(int cashId) {
        return repository.getCashById(cashId);
    }
    public LiveData<List<BookModel.Staff>> getAllStaff() {
        return allStaff;
    }
    public LiveData<List<BookModel.Category>> getAllCategory() {
        return allCategory;
    }
}