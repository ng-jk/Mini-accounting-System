package com.example.accounting;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

public class BookModel {
    @Entity(tableName = "Staff")
    public static class Staff {
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "StaffID")
        private int staffID;

        @ColumnInfo(name = "StaffName")
        private String staffName;

        @ColumnInfo(name = "BusinessName")
        private String businessName;

        public Staff() {
        }

        public Staff(int staffID, String staffName, String businessName) {
            this.staffID = staffID;
            this.staffName = staffName;
            this.businessName = businessName;
        }

        public Staff(String staffName, String businessName) {
            this.staffName = staffName;
            this.businessName = businessName;
        }

        public int getStaffID() {
            return staffID;
        }

        public void setStaffID(int staffID) {
            this.staffID = staffID;
        }

        public String getStaffName() {
            return staffName;
        }

        public void setStaffName(String staffName) {
            this.staffName = staffName;
        }

        public String getBusinessName() {
            return businessName;
        }

        public void setBusinessName(String businessName) {
            this.businessName = businessName;
        }
    }

    @Entity(tableName = "Category",
            indices = {@Index(value = "Category", unique = true)}
)
    public static class Category {
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "CategoryID")
        private int categoryID;

        @ColumnInfo(name = "Category")
        private String category;

        @ColumnInfo(name = "CategoryTotal")
        private double categoryTotal;

        @ColumnInfo(name = "CategoryBudget")
        private double categoryBudget;

        public Category() {
        }

        public Category(int categoryID, String category, double categoryTotal, double categoryBudget) {
            this.categoryID = categoryID;
            this.category = category;
            this.categoryTotal = categoryTotal;
            this.categoryBudget = categoryBudget;
        }

        public Category(String category, double categoryTotal, double categoryBudget) {
            this.category = category;
            this.categoryTotal = categoryTotal;
            this.categoryBudget = categoryBudget;
        }

        public int getCategoryID() {
            return categoryID;
        }

        public void setCategoryID(int categoryID) {
            this.categoryID = categoryID;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public double getCategoryTotal() {
            return categoryTotal;
        }

        public void setCategoryTotal(double categoryTotal) {
            this.categoryTotal = categoryTotal;
        }

        public double getCategoryBudget() {
            return categoryBudget;
        }

        public void setCategoryBudget(double categoryBudget) {
            this.categoryBudget = categoryBudget;
        }
    }

    @Entity(
            tableName = "Book",
            foreignKeys = @ForeignKey(
                    entity = Category.class,
                    parentColumns = "Category",
                    childColumns = "Category",
                    onDelete = ForeignKey.CASCADE,
                    onUpdate = ForeignKey.CASCADE
            ),
            indices = {@Index("Category")}
    )
    public static class Book {
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "BookID")
        private int bookID;

        @ColumnInfo(name = "BookName")
        private String bookName;

        @ColumnInfo(name = "Total")
        private float total;

        @ColumnInfo(name = "Category")  // Foreign key referencing Category's category field
        private String category;

        public Book() {
        }

        public Book(int bookID, String bookName, float total, String category) {
            this.bookID = bookID;
            this.bookName = bookName;
            this.total = total;
            this.category = category;
        }

        public Book(String bookName, float total, String category) {
            this.bookName = bookName;
            this.total = total;
            this.category = category;
        }

        public int getBookID() {
            return bookID;
        }

        public void setBookID(int bookID) {
            this.bookID = bookID;
        }

        public String getBookName() {
            return bookName;
        }

        public void setBookName(String bookName) {
            this.bookName = bookName;
        }

        public float getTotal() {
            return total;
        }

        public void setTotal(float total) {
            this.total = total;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }
    }

    @Entity(
            tableName = "Cash",
            foreignKeys = @ForeignKey(
                    entity = Book.class,
                    parentColumns = "BookID",
                    childColumns = "BookID",
                    onDelete = ForeignKey.CASCADE,
                    onUpdate = ForeignKey.CASCADE
            ),
            indices = {@Index("BookID")}
    )
    public static class Cash {
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "CashID")
        private int cashID;

        @ColumnInfo(name = "CashType")
        private String inOrOut;

        @ColumnInfo(name = "Amount")
        private float amount;

        @ColumnInfo(name = "BookID")
        private int bookID;

        @ColumnInfo(name = "Date")
        private String date;

        @ColumnInfo(name = "Time")
        private String time;

        @ColumnInfo(name = "PaymentMethod")
        private String paymentMethod;

        @ColumnInfo(name = "Note")
        private String note;

        // No-argument constructor
        public Cash() {
        }

        // Constructor with all fields
        public Cash(int cashID, String inOrOut, float amount, int bookID, String date, String time, String paymentMethod, String note) {
            this.cashID = cashID;
            this.inOrOut = inOrOut;
            this.amount = amount;
            this.bookID = bookID;
            this.date = date;
            this.time = time;
            this.paymentMethod = paymentMethod;
            this.note = note;
        }

        // Constructor without cashID (auto-generated by Room)
        public Cash(String inOrOut, float amount, int bookID, String date, String time, String paymentMethod, String note) {
            this.inOrOut = inOrOut;
            this.amount = amount;
            this.bookID = bookID;
            this.date = date;
            this.time = time;
            this.paymentMethod = paymentMethod;
            this.note = note;
        }

        public int getCashID() {
            return cashID;
        }

        public void setCashID(int cashID) {
            this.cashID = cashID;
        }

        public String getInOrOut() {
            return inOrOut;
        }

        public void setInOrOut(String inOrOut) {
            this.inOrOut = inOrOut;
        }

        public float getAmount() {
            return amount;
        }

        public void setAmount(float amount) {
            this.amount = amount;
        }

        public int getBookID() {
            return bookID;
        }

        public void setBookID(int bookID) {
            this.bookID = bookID;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public void setPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }
    }

}
