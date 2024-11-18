package com.example.accounting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainCategoryActivity extends AppCompatActivity {

    private ViewModel ViewModel;
    private CategoryAdapter adapter;

    private RecyclerView recyclerView;
    private FloatingActionButton AddNewCategory;
    private ImageButton SettingButton, ReportButton,CashBookButton, BudgetButton;

    private final static String THE_BOOK_ID = "THE_BOOK_ID";
    public static final String PREF_NAME = "login_pref";
    public static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private final static String IS_REGISTER = "register";
    private final static String CURRENT_STAFF = "staff";
    private final static String CURRENT_BUSINESS = "business";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_view);

        AddNewCategory = findViewById(R.id.addNewCategory);
        SettingButton = findViewById(R.id.settingButton);
        ReportButton = findViewById(R.id.reportButton);
        CashBookButton = findViewById(R.id.cashBookButton);
        BudgetButton = findViewById(R.id.budgetButton);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CategoryAdapter();
        recyclerView.setAdapter(adapter);
        ViewModel = new ViewModelProvider(this).get(ViewModel.class);
        ViewModel.getAllCategory().observe(this, category -> {
            ViewModel.getAllBook().observe(this,book->{
                adapter.setCategory(book,category,this,ViewModel);
            });
        });

        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        if( sharedPreferences.getBoolean(IS_REGISTER,false)){
            AddNewCategory.setOnClickListener(v -> handleAddNewCategory());
        }

        SettingButton.setOnClickListener((v -> navigateToMainSettingView()));
        ReportButton.setOnClickListener((v ->navigateToMainReportView()));
        CashBookButton.setOnClickListener((V ->navigateToMainCashBookView()));
        BudgetButton.setOnClickListener((V->navigateToMainBudgetView()));
    }

    private void handleAddNewCategory() {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                    .add(R.id.AddNewCategory_container, new CashBookAddNewCategoryFrag())
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .commit();
    }

    private void navigateToMainSettingView() {
        Intent intent = new Intent(this, MainSettingActivity.class);
        startActivity(intent);
        finish();
    }
    private void navigateToMainReportView() {
        Intent intent = new Intent(this, MainReportActivity.class);
        startActivity(intent);
        finish();
    }
    private void navigateToMainBudgetView() {
        Intent intent = new Intent(this, MainBudgetActivity.class);
        startActivity(intent);
        finish();
    }
    private void navigateToMainCashBookView() {
        Intent intent = new Intent(this, MainCashBookActivity.class);
        startActivity(intent);
        finish();
    }
}