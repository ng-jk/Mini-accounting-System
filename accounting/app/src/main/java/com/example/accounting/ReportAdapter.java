package com.example.accounting;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder>{

    private List<BookModel.Category> listOfCategory = new ArrayList<>();

    // Method to set books in the adapter
    public void setCategory(List<BookModel.Category> category) {
        this.listOfCategory = category;
        notifyDataSetChanged(); // Use DiffUtil for more efficiency with large data
    }

    @NonNull
    @Override
    public ReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_of_report, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportAdapter.ViewHolder holder, int position) {
        BookModel.Category category = listOfCategory.get(position);
        double remainBudget = category.getCategoryTotal()-category.getCategoryBudget();
        holder.CategoryName.setText(category.getCategory());
        holder.CategoryTotal.setText(String.format("%.2f", category.getCategoryTotal()));
        holder.CategoryBudget.setText(String.format("%.2f", category.getCategoryBudget()));

        if (category.getCategoryTotal()<=0 || remainBudget<=0){
            holder.Status.setTextColor(Color.RED);
            holder.Status.setText("overbudget");
            holder.CategoryRemain.setText(String.format("%.2f", 0.00));
        }else{
            holder.Status.setTextColor(Color.parseColor("#0dfc63"));
            holder.Status.setText("underbudget");
            holder.CategoryRemain.setText(String.format("%.2f", remainBudget));
        }
    }

    @Override
    public int getItemCount() {
        return listOfCategory.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView CategoryName;
        TextView CategoryTotal;
        TextView CategoryBudget;
        TextView CategoryRemain;
        TextView Status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            CategoryName = itemView.findViewById(R.id.CategoryName);
            CategoryTotal = itemView.findViewById(R.id.spentTextView);
            CategoryBudget = itemView.findViewById(R.id.budgetTextView);
            CategoryRemain = itemView.findViewById(R.id.remainTextView);
            Status = itemView.findViewById(R.id.StatusTextView);
        }
    }
}
