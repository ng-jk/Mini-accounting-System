package com.example.accounting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.androidnetworking.AndroidNetworking;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


public class MainSettingActivity extends AppCompatActivity {
     private Button LoginButton, BackUpButton, StuffRegisterButton;
    private ImageButton CashBookButton, ReportButton,CategoryButton, BudgetButton;
     private EditText UserName, UserBusiness;
     private TextView introTextView;
     public SharedPreferences sharedPreferencesLogin;
    private ViewModel ViewModel;
    private List<BookModel.Staff> listOfStaff = new ArrayList<>();

    private static final String TAG = "MyTag";
    private final static String IS_REGISTER = "register";
    private final static String CURRENT_STAFF = "staff";
    private final static String CURRENT_BUSINESS = "business";
    public static final String PREF_NAME = "login_pref";
    public static final String KEY_IS_LOGGED_IN = "is_logged_in";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MyTag", "mainSettingCreated:success ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_setting_view);
        AndroidNetworking.initialize(getApplicationContext());

        LoginButton = findViewById(R.id.LoginButton);
        BackUpButton = findViewById(R.id.backupButton);
        StuffRegisterButton = findViewById(R.id.stuffRegisterButton);
        CashBookButton = findViewById(R.id.cashBookButton);
        ReportButton = findViewById(R.id.reportButton);
        CategoryButton = findViewById(R.id.CategoryButton);
        BudgetButton = findViewById(R.id.budgetButton);
        UserName = findViewById(R.id.UserNameEditText);
        UserBusiness = findViewById(R.id.BusinessNameEditText);
        introTextView = findViewById(R.id.introTextView);

        sharedPreferencesLogin = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        if (sharedPreferencesLogin.getBoolean(KEY_IS_LOGGED_IN, false)) {
            LoginButton.setText("logout");
            LoginButton.setOnClickListener(V -> handleLogout());
            BackUpButton.setOnClickListener((V->navigateToBackupRestoreView()));
        }else{
            LoginButton.setText("login");
            LoginButton.setOnClickListener(V -> navigateToLoginView());
        }
        SharedPreferences sh = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        if(sh.getBoolean(IS_REGISTER,false)){
            introTextView.setText("you are "+sh.getString(CURRENT_STAFF,"unknown")+" from "+sh.getString(CURRENT_BUSINESS,"unknown"));
        }

        StuffRegisterButton.setOnClickListener(V -> staffRegister());
        CashBookButton.setOnClickListener((v -> navigateToMainCashBookView()));
        ReportButton.setOnClickListener((v ->navigateToMainReportView()));
        CategoryButton.setOnClickListener((V ->navigateToMainCategoryView()));
        BudgetButton.setOnClickListener((V->navigateToMainBudgetView()));
    }

    private void navigateToBackupRestoreView(){
        Intent intent = new Intent(MainSettingActivity.this, BackupRestoreActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void navigateToLoginView(){
        Intent intent = new Intent(MainSettingActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    private void handleLogout(){
        SharedPreferences.Editor editor = sharedPreferencesLogin.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.apply();

        // Sign out from Firebase
        FirebaseAuth.getInstance().signOut();

        // Navigate back to LoginActivity
        Intent intent = new Intent(MainSettingActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

        Log.d("logout debug", "handleLogout: logout success");
        LoginButton.setText("login");
        LoginButton.setOnClickListener(V -> navigateToLoginView());

    }

    private void staffRegister() {
        String Name = UserName.getText().toString();
        String Business = UserBusiness.getText().toString();

        SharedPreferences.Editor editor = sharedPreferencesLogin.edit();
        editor.putBoolean(IS_REGISTER, true);
        editor.putString(CURRENT_STAFF, Name);
        editor.putString(CURRENT_BUSINESS, Business);
        editor.apply();

        ViewModel = new ViewModelProvider(this).get(ViewModel.class);

        // Create a single Observer
        Observer<List<BookModel.Staff>> observer = new Observer<List<BookModel.Staff>>() {
            @Override
            public void onChanged(List<BookModel.Staff> staff) {
                listOfStaff = staff;

                boolean businessStatus = false;
                boolean staffStatus = false;
                Log.d(TAG, "onChanged: " + listOfStaff.size());

                // Check for matching business and staff
                for (BookModel.Staff theStaff : listOfStaff) {
                    if (theStaff.getBusinessName().equals(Business)) {
                        businessStatus = true;
                        if (theStaff.getStaffName().equals(Name)) {
                            staffStatus = true;
                            break;
                        }
                    }
                }

                // Remove the observer to prevent repeated triggering
                ViewModel.getAllStaff().removeObserver(this);

                // Register or login user based on status
                if (!businessStatus) {
                    Toast.makeText(MainSettingActivity.this, "You successfully registered a staff and a business named " + Business, Toast.LENGTH_SHORT).show();
                    BookModel.Staff newStaff = new BookModel.Staff(Name, Business);
                    ViewModel.insertStaff(newStaff);
                    introTextView.setText("You are " + Name + " from " + Business);
                } else if (!staffStatus) {
                    Toast.makeText(MainSettingActivity.this, "You successfully registered as a staff in business named " + Business, Toast.LENGTH_SHORT).show();
                    BookModel.Staff newStaff = new BookModel.Staff(Name, Business);
                    ViewModel.insertStaff(newStaff);
                } else {
                    Toast.makeText(MainSettingActivity.this, "You successfully logged in as a staff in business named " + Business, Toast.LENGTH_SHORT).show();
                }
            }
        };

        // Attach the observer
        ViewModel.getAllStaff().observe(this, observer);
    }



    private void navigateToMainCashBookView() {
        Intent intent = new Intent(MainSettingActivity.this, MainCashBookActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
    private void navigateToMainReportView() {
        Intent intent = new Intent(MainSettingActivity.this, MainReportActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
    private void navigateToMainBudgetView() {
        Intent intent = new Intent(MainSettingActivity.this, MainBudgetActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
    private void navigateToMainCategoryView() {
        Intent intent = new Intent(MainSettingActivity.this, MainCategoryActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
