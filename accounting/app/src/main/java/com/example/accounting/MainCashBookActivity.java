package com.example.accounting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainCashBookActivity extends AppCompatActivity {

    private ViewModel viewModel;
    private CashBookAdapter adapter;

    private RecyclerView recyclerView;
    private FloatingActionButton AddNewBook_obj;
    private ImageButton SettingButton, ReportButton,CategoryButton, BudgetButton;

    private final static String THE_BOOK_ID = "THE_BOOK_ID";
    public static final String PREF_NAME = "login_pref";
    public static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private final static String IS_REGISTER = "register";
    private final static String CURRENT_STAFF = "staff";
    private final static String CURRENT_BUSINESS = "business";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CashBookAdapter();
        recyclerView.setAdapter(adapter);
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        viewModel.getAllBook().observe(this, books -> {
            viewModel.getAllCategory().observe(this,categories ->{
                adapter.setBooks(categories,books,this,viewModel);
            });
        });

        AddNewBook_obj = findViewById(R.id.addNewBook);
        SettingButton = findViewById(R.id.settingButton);
        ReportButton = findViewById(R.id.reportButton);
        CategoryButton = findViewById(R.id.categoryButton);
        BudgetButton = findViewById(R.id.budgetButton);

        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        if( sharedPreferences.getBoolean(IS_REGISTER,false)){
            AddNewBook_obj.setOnClickListener(v -> handleAddNewBook());
        }

        SettingButton.setOnClickListener((v -> navigateToMainSettingView()));
        ReportButton.setOnClickListener((v ->navigateToMainReportView()));
        CategoryButton.setOnClickListener((V ->navigateToMainCategoryView()));
        BudgetButton.setOnClickListener((V->navigateToMainBudgetView()));

        adapter.setItemClickListener(position -> {
            BookModel.Book clickedBook = adapter.getBookAt(position);
            // Perform any action, such as opening details
            Intent intent = new Intent(MainCashBookActivity.this, CashBookActivity.class);
            if (clickedBook != null) {
                intent.putExtra(THE_BOOK_ID, clickedBook.getBookID());
                Log.d("myTag", "onCreate: "+clickedBook.getBookID());
            }
            startActivity(intent);
            finish();
        });
    }

    private void handleAddNewBook() {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                    .add(R.id.AddNewBook_container, new CashBookAddNewBookFrag())
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .commit();
    }
    private void navigateToMainSettingView() {
        Log.d("MyTag", "navigateToMainSettingView:success ");
        Intent intent = new Intent(getApplicationContext(), MainSettingActivity.class);
        startActivity(intent);
        finish();
    }
    private void navigateToMainReportView() {
        Log.d("MyTag", "navigateToMainReportView:success ");
        Intent intent = new Intent(getApplicationContext(), MainReportActivity.class);
        startActivity(intent);
        finish();
    }
    private void navigateToMainBudgetView() {
        Log.d("MyTag", "navigateToMainBudgetView:success ");
        Intent intent = new Intent(getApplicationContext(), MainBudgetActivity.class);
        startActivity(intent);
        finish();
    }
    private void navigateToMainCategoryView() {
        Log.d("MyTag", "navigateToMainCategoryView:success ");
        Intent intent = new Intent(getApplicationContext(), MainCategoryActivity.class);
        startActivity(intent);
        finish();
    }
}
