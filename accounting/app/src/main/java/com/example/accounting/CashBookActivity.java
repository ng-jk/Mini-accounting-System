package com.example.accounting;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CashBookActivity extends AppCompatActivity {

    private final String IN_OR_OUT = "IN_OR_OUT";
    private final String IN = "IN";
    private final String OUT = "OUT";
    private final String THE_BOOK_ID = "THE_BOOK_ID";


    Button CloseButton, CashInButton, CashOutButton;
    TextView NetBalanceTextView, TotalInTextView, TotalOutTextView;
    ViewModel ViewModel;
    RecyclerView CashInRecyclerView, CashOutRecyclerView;
    CashInAdapter cashInAdapter;
    CashOutAdapter cashOutAdapter;
    int BookID;
    float totalIn=0, totalOut=0;
    String category;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cash_book_view);
        BookID = getIntent().getIntExtra(THE_BOOK_ID, 0);
        Log.d("MyTag1", "onCreate: "+BookID);

        CloseButton = findViewById(R.id.closeButton);
        CashInButton = findViewById(R.id.cashInButton);
        CashOutButton = findViewById(R.id.cashOutButton);
        NetBalanceTextView = findViewById(R.id.netBalanceTextView);
        TotalInTextView = findViewById(R.id.totalInTextView);
        TotalOutTextView = findViewById(R.id.totalOutTextView);

        CloseButton.setOnClickListener(v -> navigateToMainActivity());
        CashInButton.setOnClickListener(v -> navigateToCashInEntry());
        CashOutButton.setOnClickListener(v -> navigateToCashOutEntry());

        CashInRecyclerView = findViewById(R.id.cashInRecyclerView);
        CashInRecyclerView.setHasFixedSize(true);
        CashInRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cashInAdapter = new CashInAdapter(BookID, ViewModel, this);
        CashInRecyclerView.setAdapter(cashInAdapter);
        ViewModel = new ViewModelProvider(this).get(ViewModel.class);
        ViewModel.getAllCash().observe(this, cashList -> {
            cashInAdapter.setCash(cashList);
            Log.d("myTag2", "onCreate: "+cashList);
        });
        CashOutRecyclerView = findViewById(R.id.cashOutRecyclerView);
        CashOutRecyclerView.setHasFixedSize(true);
        CashOutRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cashOutAdapter = new CashOutAdapter(BookID, ViewModel, this);
        CashOutRecyclerView.setAdapter(cashOutAdapter);
        ViewModel = new ViewModelProvider(this).get(ViewModel.class);
        ViewModel.getAllCash().observe(this, cashList -> {
            cashOutAdapter.setCash(cashList);
        });

        ViewModel = new ViewModelProvider(this).get(ViewModel.class);
        ViewModel.getAllCash().observe(this, cashList -> {
            totalIn = 0;
            totalOut = 0;

            for (BookModel.Cash cash : cashList) {
                if (cash.getBookID() == BookID) {
                    if ("in".equalsIgnoreCase(cash.getInOrOut())) {
                        totalIn += cash.getAmount();
                    } else if ("out".equalsIgnoreCase(cash.getInOrOut())) {
                        totalOut += cash.getAmount();
                    }
                }
            }

            TotalInTextView.setText(String.format("%.2f", totalIn));
            TotalOutTextView.setText(String.format("%.2f", totalOut));
            NetBalanceTextView.setText(String.format("%.2f", totalIn - totalOut));
            Log.d("updateTag", "updateBookTotal: updateBook"+totalIn);

            updateBookTotal(totalIn,totalOut);

        });

    }

    private void updateBookTotal(float totalIn,float totalOut) {
        float netTotal = totalIn - totalOut;

        // Update the total in the BookModel.Book entry
        ViewModel.getAllBook().observe(this, book -> {
            double total = 0;
            for (int i = 0; i < book.size(); i++) {

                BookModel.Book theBook = book.get(i);
                if (theBook.getBookID()==BookID) {
                    theBook.setTotal(netTotal);
                    category = theBook.getCategory();
                    ViewModel.updateBook(theBook);
                }
                if(theBook.getCategory().equals(category)){
                    total = total+(double)theBook.getTotal();
                }
            }
            updateCategoryTotal(total,category);
        });
    }
    private void updateCategoryTotal(double total,String category){
        ViewModel.getAllCategory().observe(this, categories -> {
            if (categories != null) {
                for (int i = 0; i < categories.size(); i++) {

                    BookModel.Category theCategory = categories.get(i);
                    if (theCategory.getCategory().equals(category)) {
                        theCategory.setCategoryTotal(total);
                        ViewModel.updateCategory(theCategory); // Ensure updateCategory is implemented in ViewModel
                    }
                }
            }
            ViewModel.getAllCategory().removeObservers(this);
        });
    }


    private void navigateToMainActivity() {
        Intent intent = new Intent(CashBookActivity.this, MainCashBookActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToCashInEntry() {
        Intent intent = new Intent(CashBookActivity.this, CashInOutInputActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(IN_OR_OUT, IN);
        intent.putExtra(THE_BOOK_ID, BookID);
        startActivity(intent);
        finish();
    }

    private void navigateToCashOutEntry() {
        Intent intent = new Intent(CashBookActivity.this, CashInOutInputActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(IN_OR_OUT, OUT);
        intent.putExtra(THE_BOOK_ID, BookID);
        startActivity(intent);
        finish();
    }
}

