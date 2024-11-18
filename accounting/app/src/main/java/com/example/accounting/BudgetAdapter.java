package com.example.accounting;

import static android.app.PendingIntent.getActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.ViewHolder>{

    private List<BookModel.Category> listOfCategory = new ArrayList<>();
    private Context context;
    private ViewModel ViewModel;


    // Method to set books in the adapter
    public void setCategory(Context context,List<BookModel.Category> category,ViewModel ViewModel) {
        this.ViewModel = ViewModel;
        this.context = context;
        this.listOfCategory = category;
        notifyDataSetChanged(); // Use DiffUtil for more efficiency with large data
    }

    @NonNull
    @Override
    public BudgetAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_of_budget, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookModel.Category category = listOfCategory.get(position);
        holder.CategoryName.setText(category.getCategory());
        holder.CategoryTotal.setText(String.format("%.2f", category.getCategoryTotal()));
        holder.CategoryBudget.setText(String.format("%.2f", category.getCategoryBudget()));
        holder.SetBudgetButton.setOnClickListener(v->handleSetBudget(category));
    }

    @Override
    public int getItemCount() {
        return listOfCategory.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView CategoryName,CategoryTotal,CategoryBudget;
        Button SetBudgetButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            CategoryName = itemView.findViewById(R.id.CategoryName);
            CategoryTotal = itemView.findViewById(R.id.spentTextView);
            CategoryBudget = itemView.findViewById(R.id.limitTextView);
            SetBudgetButton = itemView.findViewById(R.id.SetBudgetButton);
        }
    }

    public void handleSetBudget(BookModel.Category category){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Budget");
        builder.setMessage("How much budget you wish to set?");

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL );
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            BookModel.Category newCategory =category;
            newCategory.setCategoryBudget(Double.parseDouble(input.getText().toString()));
            ViewModel.updateCategory(newCategory);
            Toast.makeText(context, "Item Save", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }
}
