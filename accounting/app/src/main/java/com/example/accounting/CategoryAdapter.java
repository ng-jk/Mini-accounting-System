package com.example.accounting;

import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<BookModel.Category> listOfCategory = new ArrayList<>();
    private List<BookModel.Book> listOfBook = new ArrayList<>();

    private Context context;
    public  final String THE_CASH_ID = "THE_CASH_ID";
    private final String OUT = "OUT";
    public  final String IN_OR_OUT = "IN_OR_OUT";
    private final String UPDATE = "update";
    private ViewModel viewModel;

    // Method to set books in the adapter
    public void setCategory(List<BookModel.Book> book, List<BookModel.Category> category, Context context,ViewModel viewModel) {
        this.listOfBook = book;
        this.listOfCategory = category;
        this.context = context;
        this.viewModel = viewModel;
        notifyDataSetChanged(); // Use DiffUtil for more efficiency with large data
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_of_category, parent, false);
        return new ViewHolder(view); // No need to pass the listener here since it's now a part of the adapter
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookModel.Category category = listOfCategory.get(position);
        holder.CategoryName.setText(category.getCategory());
        holder.MenuButton.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), holder.MenuButton);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.common_menu, popup.getMenu());

            // Handle menu item clicks
            popup.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.edit) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("edit category");
                    final EditText input = new EditText(context);
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);
                    builder.setPositiveButton("Save", (dialog, which) -> {
                        category.setCategory(input.getText().toString());
                        viewModel.updateCategory(category);
                    });
                    builder.setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                    });

                    // Show the dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                } else if (menuItem.getItemId() == R.id.delete) {
                    for (BookModel.Book theBook:listOfBook){
                        if(theBook.getCategory().equals(category.getCategory())){
                            viewModel.deleteBook(theBook);
                        }
                    }
                    viewModel.deleteCategory(category);

                    return true;
                }
                return false;
            });
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return listOfCategory.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView CategoryName;
        ImageButton MenuButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            CategoryName = itemView.findViewById(R.id.CategoryName);
            MenuButton = itemView.findViewById(R.id.menuButton);
        }
    }
}

