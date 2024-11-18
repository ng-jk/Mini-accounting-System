package com.example.accounting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class CashBookAddNewCategoryFrag extends Fragment {


    private ViewModel viewModel;
    private TextInputEditText CategoryName;
    private Button closeButton, AddNewBookButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_new_category_frag, container, false);

        CategoryName = view.findViewById(R.id.BookCategoryEditText);
        closeButton = view.findViewById(R.id.closeButton);
        AddNewBookButton = view.findViewById(R.id.AddNewBookButton);
        viewModel =new ViewModelProvider(this).get(ViewModel.class);

        closeButton.setOnClickListener(v -> closeFrag());
        AddNewBookButton.setOnClickListener(v -> saveFrag());
        return view;
    }

    public void saveFrag(){
        String categoryName = CategoryName.getText().toString();

        if (!categoryName.isEmpty()) {
            BookModel.Category category = new BookModel.Category(categoryName,0,0);
            viewModel.insertCategory(category);
            Toast.makeText(getActivity(), "Category save", Toast.LENGTH_SHORT).show();
            closeFrag();
        } else {
            Toast.makeText(getActivity(), "Please fill the field", Toast.LENGTH_SHORT).show();
        }
    }
    public void closeFrag() {
        if (isAdded()) {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.remove(this);
            transaction.commit();
        }
    }
}