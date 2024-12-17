package com.example.accounting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.paypal.android.corepayments.CoreConfig;
import com.paypal.android.corepayments.Environment;
import com.paypal.android.corepayments.PayPalSDKError;
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutClient;
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutFundingSource;
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutListener;
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutRequest;
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BackupRestoreActivity extends AppCompatActivity {
    public final static String USERNAME = "USERNAME";
    public final static String USER_BUSINESS = "USER_BUSINESS";
    public static final String KEY_PAYPAL_ACCESS_KEY= "PaypalAccessKey";
    public static final String PREF_NAME = "login_pref";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private String ClintId = "";
    private String SecretKey = "";
    private String ReturnUrl = "com.example.accounting://demoapp";
    private String accessToken = "";
    private String uniqueId;
    private String orderid = "";
    private static final String TAG = "MyTag";

    ViewModel viewModel;
    private List<BookModel.Book> listOfBook = new ArrayList<>();
    private List<BookModel.Cash> listOfCash = new ArrayList<>();
    private List<BookModel.Staff> listOfStaff = new ArrayList<>();
    private List<BookModel.Category> listOfCategory = new ArrayList<>();

    ProgressBar progressbar;
    Button backUpButton, closeButton, restoreButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    String uid,email;

    protected void onStart(){
        super.onStart();
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.back_up_view);
        AndroidNetworking.initialize(getApplicationContext());

        backUpButton = findViewById(R.id.BackUpButton);
        closeButton = findViewById(R.id.closeButton);
        restoreButton = findViewById(R.id.RestoreButton);
        progressbar = findViewById(R.id.progress_bar);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        SharedPreferences SH = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        email = user.getEmail();
        Log.d(TAG, "onCreate: "+uid);

        if (user != null) {
            backUpButton.setOnClickListener(v -> fetchAccessToken());
            restoreButton.setOnClickListener(v -> restore());
        }
        closeButton.setOnClickListener(v -> navigateToMainSettingActivity());
    }

    private void showLoading() {
        progressbar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE); // Disables interaction
    }

    private void hideLoading() {
        progressbar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE); // Re-enables interaction
    }

    private void fetchAccessToken() {
        showLoading();
        String authString = ClintId + ":" + SecretKey;
        String encodedAuthString = Base64.encodeToString(authString.getBytes(), Base64.NO_WRAP);
        AndroidNetworking.post("https://api-m.sandbox.paypal.com/v1/oauth2/token")
                .addHeaders("Authorization", "Basic " + encodedAuthString)
                .addHeaders("Content-Type", "application/x-www-form-urlencoded")
                .addBodyParameter("grant_type", "client_credentials")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "fetchToken: success");
                        accessToken = response.optString("access_token");
                        Log.d(TAG, accessToken);
                        uniqueId = UUID.randomUUID().toString();
                        hideLoading();
                        payment();
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.d(TAG, error.getErrorBody());
                        Toast.makeText(BackupRestoreActivity.this, "Error Occurred!", Toast.LENGTH_SHORT).show();
                        hideLoading();
                    }
                });
    }

        private void payment () {

            try {
                JSONObject orderRequestJson = new JSONObject()
                        .put("intent", "CAPTURE")
                        .put("purchase_units", new JSONArray().put(new JSONObject()
                                .put("reference_id", uniqueId)
                                .put("amount", new JSONObject()
                                        .put("currency_code", "MYR")
                                        .put("value", "5.00")
                                )
                        ))
                        .put("payment_source", new JSONObject()
                                .put("paypal", new JSONObject()
                                        .put("experience_context", new JSONObject()
                                                .put("payment_method_preference", "IMMEDIATE_PAYMENT_REQUIRED")
                                                .put("brand_name", "SH Developer")
                                                .put("locale", "en-US")
                                                .put("landing_page", "LOGIN")
                                                .put("shipping_preference", "NO_SHIPPING")
                                                .put("user_action", "PAY_NOW")
                                                .put("return_url", ReturnUrl)
                                                .put("cancel_url", "https://example.com/cancelUrl")
                                        )
                                )
                        );

                AndroidNetworking.post("https://api-m.sandbox.paypal.com/v2/checkout/orders")
                        .addHeaders("Authorization", "Bearer " + accessToken)
                        .addHeaders("Content-Type", "application/json")
                        .addHeaders("PayPal-Request-Id", uniqueId)
                        .addJSONObjectBody(orderRequestJson)
                        .setPriority(Priority.HIGH)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, "Order Response: " + response.toString());
                                handleOrderID(response.optString("id"));
                            }

                            @Override
                            public void onError(ANError error) {
                                Log.d(TAG, "Order Error: " + error.getMessage() + " || " + error.getErrorBody() + " || " + error.getResponse());
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    private void handleOrderID(String orderID) {
        Environment environment = Environment.SANDBOX;
        CoreConfig config = new CoreConfig(ClintId, environment);
        PayPalWebCheckoutClient payPalWebCheckoutClient = new PayPalWebCheckoutClient(this, config, ReturnUrl);
        Log.d(TAG, "handleOrderID: success"+orderID);
        orderid = orderID;
        PayPalWebCheckoutRequest payPalWebCheckoutRequest = new PayPalWebCheckoutRequest(orderID, PayPalWebCheckoutFundingSource.PAYPAL);
        payPalWebCheckoutClient.start(payPalWebCheckoutRequest);
        payPalWebCheckoutClient.setListener(new PayPalWebCheckoutListener() {
            @Override
            public void onPayPalWebSuccess(PayPalWebCheckoutResult result) {
                Log.d(TAG, "onPayPalWebSuccess: " + result);
            }

            @Override
            public void onPayPalWebFailure(PayPalSDKError error) {
                Log.d(TAG, "onPayPalWebFailure: " + error);
            }

            @Override
            public void onPayPalWebCanceled() {
                Log.d(TAG, "onPayPalWebCanceled: ");
            }
        });


    }


    private void captureOrder(String orderID) {
        AndroidNetworking.post("https://api-m.sandbox.paypal.com/v2/checkout/orders/" + orderID + "/capture")
                .addHeaders("Authorization", "Bearer " + accessToken)
                .addHeaders("Content-Type", "application/json")
                .addJSONObjectBody(new JSONObject()) // Empty body
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Capture Response: " + response.toString());
                        Toast.makeText(BackupRestoreActivity.this, "Payment Successful", Toast.LENGTH_SHORT).show();
                        backup();
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.e(TAG, "Capture Error: " + error.getErrorDetail());
                    }
                });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent: " + intent);

        if ("payment".equals(intent.getData().getQueryParameter("opType"))) {
            captureOrder(orderid);
        } else if ("cancel".equals(intent.getData().getQueryParameter("opType"))) {
            Toast.makeText(this, "Payment Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    public void backup(){
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;
        showLoading();

        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        viewModel.getAllBook().observe(this, bookList -> {
            listOfBook  = bookList;
            viewModel.getAllCash().observe(this, cashList -> {
                listOfCash = cashList;
                viewModel.getAllStaff().observe(this, staffList -> {
                    listOfStaff = staffList;
                    viewModel.getAllCategory().observe(this, categoryList -> {
                        listOfCategory = categoryList;
                        Map<String, Object> backupMap = new HashMap<>();
                        backupMap.put("listOfBook", listOfBook);
                        backupMap.put("listOfCash",listOfCash);
                        backupMap.put("listOfCategory", listOfCategory);
                        backupMap.put("listOfStaff", listOfStaff);
                        DocumentReference documentReference = db.collection("data").document(uid);
                        documentReference.set(backupMap).addOnSuccessListener(aVoid -> {
                                    Toast.makeText(BackupRestoreActivity.this, "User information saved", Toast.LENGTH_SHORT).show();
                                    Log.d("storeTag", "storeUserInfo: success"+uid);
                                    hideLoading();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(BackupRestoreActivity.this, "Failed to save user info: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.d("storeTag", "storeUserInfo: "+e.getMessage());
                                    hideLoading();
                                });
                    });
                });
            });
        });
    }

    public void restore(){
        viewModel = new ViewModelProvider(this).get(ViewModel.class);

        DocumentReference docRef =  db.collection("data").document(uid);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    Map<String, Object> backupMap = document.getData();
                    Log.d(TAG, "restore: " + backupMap.size());

                    List<Map<String, Object>> rBook = (List<Map<String, Object>>) backupMap.get("listOfBook");
                    List<Map<String, Object>> rCash = (List<Map<String, Object>>) backupMap.get("listOfCash");
                    List<Map<String, Object>> rCategory = (List<Map<String, Object>>) backupMap.get("listOfCategory");
                    List<Map<String, Object>> rStaff = (List<Map<String, Object>>) backupMap.get("listOfStaff");

                    Log.d(TAG, "restore: " + rStaff);

                    viewModel.deleteAllBook();
                    viewModel.deleteAllCash();
                    viewModel.deleteAllStaff();
                    viewModel.deleteAllCategory();

                    for (int i = 0; i < rCategory.size(); i++) {
                        Map<String, Object> categoryData = rCategory.get(i);

                        // Retrieve each field from the map
                        String categoryName = (String) categoryData.get("category");
                        double categoryTotal = (double)categoryData.get("categoryTotal");
                        double categoryBudget = (double)categoryData.get("categoryBudget");

                        // Create a Category object without the auto-generated categoryID
                        BookModel.Category category = new BookModel.Category(categoryName, categoryTotal, categoryBudget);

                        // Insert into the database using the ViewModel
                        viewModel.insertCategory(category);
                    }
                    Log.d(TAG, "restore: Category success");

                    for (int i = 0; i < rBook.size(); i++) {
                        Map<String, Object> bookData = rBook.get(i);
                        Log.d(TAG, "restore: " + bookData.get("total").getClass());
                        long bookIDLong = (long)bookData.get("bookID");
                        int bookID = (int) bookIDLong;
                        Float total = bookData.get("total") != null ? ((Number) bookData.get("total")).floatValue():2f;
                        String bookName = (String) bookData.get("bookName");
                        String category = (String) bookData.get("category");

                        BookModel.Book book = new BookModel.Book(bookID, bookName, total,category);
                        viewModel.insertBook(book);
                    }
                    Log.d(TAG, "restore: book success");

                    for (int i = 0; i < rCash.size(); i++) {
                        Map<String, Object> cashData = rCash.get(i);

                        // Retrieve each field from the map
                        String inOrOut = (String) cashData.getOrDefault("inOrOut", "");
                        Float amount = cashData.get("amount") != null ? ((Number) cashData.get("amount")).floatValue() : 2f;
                        long bookIDLong = (long)cashData.get("bookID");
                        int bookID = (int) bookIDLong;
                        Log.d(TAG, "restore: bookid "+bookID);
                        String date = (String) cashData.getOrDefault("date", "");
                        String time = (String) cashData.getOrDefault("time", "");
                        String paymentMethod = (String) cashData.getOrDefault("paymentMethod", "");
                        String note = (String) cashData.getOrDefault("note", "");

                        // Create a Cash object without the auto-generated cashID
                        BookModel.Cash cash = new BookModel.Cash(inOrOut, amount, bookID, date, time, paymentMethod, note);

                        // Insert into the database using the ViewModel
                        viewModel.insertCash(cash);
                    }
                    Log.d(TAG, "restore: Cash success");

                    for (int i = 0; i < rStaff.size(); i++) {
                        Map<String, Object> staffData = rStaff.get(i);

                        String staffName = (String) staffData.get("staffName");
                        String businessName = (String) staffData.get("businessName");

                        BookModel.Staff staff = new BookModel.Staff(staffName, businessName);
                        viewModel.insertStaff(staff);
                    }
                    Log.d(TAG, "staff: Category success");


                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        Toast.makeText(this, "Restore complete", Toast.LENGTH_SHORT).show();
    }

    public void handleFailure(Exception e){
            Toast.makeText(this, "Failed to save user info: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
    public void navigateToMainSettingActivity(){
        Intent i = new Intent();
        Intent intent = new Intent(BackupRestoreActivity.this, MainSettingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
