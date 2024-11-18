package com.example.accounting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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

public class MainBudgetActivity extends AppCompatActivity {

    private ViewModel ViewModel;
    private RecyclerView recyclerView;
    private BudgetAdapter adapter;
    private ImageButton SettingButton, ReportButton,CategoryButton, CashBookButton;

    private final static String THE_BOOK_ID = "THE_BOOK_ID";
    public static final String PREF_NAME = "login_pref";
    public static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private final static String IS_REGISTER = "register";
    private final static String CURRENT_STAFF = "staff";
    private final static String CURRENT_BUSINESS = "business";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.budget_view);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BudgetAdapter();
        recyclerView.setAdapter(adapter);
        ViewModel = new ViewModelProvider(this).get(ViewModel.class);
        ViewModel.getAllCategory().observe(this, category -> {
            adapter.setCategory(this,category,ViewModel);
        });

        SettingButton = findViewById(R.id.SettingButton);
        ReportButton = findViewById(R.id.ReportButton);
        CategoryButton = findViewById(R.id.CategoryButton);
        CashBookButton = findViewById(R.id.CashBookButton);

        SettingButton.setOnClickListener((v -> navigateToMainSettingView()));
        ReportButton.setOnClickListener((v ->navigateToMainReportView()));
        CategoryButton.setOnClickListener((V ->navigateToMainCategoryView()));
        CashBookButton.setOnClickListener((V->navigateToMainCashBookView()));
    }

    private void navigateToMainSettingView() {
        Intent intent = new Intent(this, MainCashBookActivity.class);
        startActivity(intent);
        finish();
    }
    private void navigateToMainReportView() {
        Intent intent = new Intent(this, MainReportActivity.class);
        startActivity(intent);
        finish();
    }
    private void navigateToMainCashBookView() {
        Intent intent = new Intent(this, MainCashBookActivity.class);
        startActivity(intent);
        finish();
    }
    private void navigateToMainCategoryView() {
        Intent intent = new Intent(this, MainCategoryActivity.class);
        startActivity(intent);
        finish();
    }
}
