package com.example.accounting;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.Calendar;

public class CashInOutInputActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final String THE_BOOK_ID = "THE_BOOK_ID";
    public static final String IN_OR_OUT = "IN_OR_OUT";
    private final String[] paths = {"Online", "Cash", "Other"};
    private final String UPDATE = "update";
    public static final String THE_CASH_ID = "THE_CASH_ID";

    Spinner PaymentMethod;
    Button SaveAndAddNewButton, SaveButton,CloseButton;
    EditText DateEditText, TimeEditText, AmountEditText, RemarkEditText;
    ViewModel CashViewModel;
    int BookID,CashID;
    String InOrOut,paymentMethod;
    Boolean Update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.in_out_input_view);

        // Retrieve the intent extras
        BookID = getIntent().getIntExtra(THE_BOOK_ID, 0);
        InOrOut = getIntent().getStringExtra(IN_OR_OUT);

        // Initialize UI elements
        DateEditText = findViewById(R.id.dateEditText);
        TimeEditText = findViewById(R.id.timeEditText);
        AmountEditText = findViewById(R.id.AmountEditText);
        RemarkEditText = findViewById(R.id.noteEditText);
        SaveAndAddNewButton = findViewById(R.id.saveAndAddNewButton);
        SaveButton = findViewById(R.id.saveButton);
        CloseButton = findViewById(R.id.closeButton);
        PaymentMethod = findViewById(R.id.paymentMethod);

        // Set listeners
        DateEditText.setOnClickListener(v -> showDatePicker());
        TimeEditText.setOnClickListener(v -> showTimePicker());
        SaveButton.setOnClickListener(v -> saveHandle());
        SaveAndAddNewButton.setOnClickListener(v -> addNewHandle());
        CloseButton.setOnClickListener(v -> navigateToCashBookView());

        // Set up the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, paths);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        PaymentMethod.setAdapter(adapter);
        PaymentMethod.setOnItemSelectedListener(this);

        // Initialize ViewModel
        CashViewModel = new ViewModelProvider(this).get(ViewModel.class);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        paymentMethod = paths[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        paymentMethod = "unknown";
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) ->
                        DateEditText.setText(String.format("%02d-%02d-%04d", dayOfMonth, monthOfYear + 1, year1)),
                year, month, day);
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minuteOfHour) ->
                        TimeEditText.setText(String.format("%02d:%02d", hourOfDay, minuteOfHour)),
                hour, minute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }

    private void saveHandle() {
        Intent intent = new Intent(CashInOutInputActivity.this, CashBookActivity.class);
        intent.putExtra(THE_BOOK_ID, BookID); // Passing the BookID back
        try {
            float amount = Float.parseFloat(AmountEditText.getText().toString());
            String remark = RemarkEditText.getText().toString();
            String date = DateEditText.getText().toString();
            String time = TimeEditText.getText().toString();

            // Create a Cash object to save
            BookModel.Cash cashModel = new BookModel.Cash(InOrOut, amount, BookID, date, time, paymentMethod, remark);
            CashViewModel.insertCash(cashModel);

            Toast.makeText(this, "Entry saved successfully", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Error saving entry: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void addNewHandle() {
        saveHandle();

        // Clear fields for a new entry
        AmountEditText.setText("");
        RemarkEditText.setText("");
        DateEditText.setText("");
        TimeEditText.setText("");
        PaymentMethod.setSelection(0);
    }

    private void navigateToCashBookView() {
        Intent intent = new Intent(CashInOutInputActivity.this, CashBookActivity.class);
        intent.putExtra(THE_BOOK_ID, BookID); // Passing the BookID back
        startActivity(intent);
        finish();
    }
}
