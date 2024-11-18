package com.example.accounting;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class CashBookAddNewBookFrag extends Fragment implements AdapterView.OnItemSelectedListener {


    private ViewModel viewModel;
    private List<BookModel.Category> listOfCategory = new ArrayList<>();
    String[]categoryList;
    String category;

    private TextInputEditText BookName;
    private Button closeButton;
    private Button AddNewBookButton;
    private Spinner categorySpinner;


    public void closeFrag() {
        if (isAdded()) {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.remove(CashBookAddNewBookFrag.this);
            transaction.commit();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("myTag", "handleAddNewBook");

        View view = inflater.inflate(R.layout.fragment_add_new_book_frag, container, false);

        BookName = view.findViewById(R.id.BookNameEditText);
        closeButton = view.findViewById(R.id.closeButton);
        AddNewBookButton = view.findViewById(R.id.AddNewBookButton);
        categorySpinner = view.findViewById(R.id.categorySpinner);

        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        viewModel.getAllCategory().observe(getViewLifecycleOwner(), categories -> {
            listOfCategory = categories;

            // Populate the categoryList array with category names
            categoryList = new String[listOfCategory.size()];
            for (int i = 0; i < listOfCategory.size(); i++) {
                BookModel.Category category = listOfCategory.get(i);
                categoryList[i] = category.getCategory();
            }

            // Set up the adapter for the spinner with the populated categoryList
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, categoryList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categorySpinner.setAdapter(adapter);
        });
        categorySpinner.setOnItemSelectedListener(this);
        closeButton.setOnClickListener(v -> closeFrag());
        AddNewBookButton.setOnClickListener(v -> saveFrag());
        return view;
    }
    public void saveFrag(){
            String bookName = BookName.getText().toString();

            if (!bookName.isEmpty()) {
                BookModel.Book bookModel = new BookModel.Book(bookName, 0, category);
                viewModel.insertBook(bookModel);
                Toast.makeText(getActivity(), "Book save", Toast.LENGTH_SHORT).show();
                closeFrag();
            } else {
                Toast.makeText(getActivity(), "Please fill the field", Toast.LENGTH_SHORT).show();
            }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        category = categoryList[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        category = "unknown";
    }
}