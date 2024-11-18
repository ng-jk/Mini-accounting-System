package com.example.accounting;

import android.content.Context;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class CashBookAdapter extends RecyclerView.Adapter<CashBookAdapter.ViewHolder> {

    private List<BookModel.Book> listOfBook = new ArrayList<>();
    private List<BookModel.Category> listOfCategory = new ArrayList<>();
    private ItemClickListener itemClickListener;
    private Context context;
    private ViewModel viewModel;

    // Method to set books in the adapter
    public void setBooks(List<BookModel.Category> categories,List<BookModel.Book> books,Context context, ViewModel viewModel) {
        this.listOfCategory = categories;
        this.listOfBook = books;
        this.context = context;
        this.viewModel = viewModel;
        notifyDataSetChanged(); // Use DiffUtil for more efficiency with large data
    }

    // Setter for the ItemClickListener
    public void setItemClickListener(ItemClickListener listener) {
        this.itemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_of_book, parent, false);
        return new ViewHolder(view); // No need to pass the listener here since it's now a part of the adapter
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookModel.Book theBook = listOfBook.get(position);
        holder.BookName.setText(theBook.getBookName());
        holder.BookTotal.setText(String.format("%.2f", theBook.getTotal()));
        String categoryName = theBook.getCategory();
        holder.CategoryName.setText(categoryName);
        Log.d("BookTotal", "onBindViewHolder: "+(String.format("%.2f", theBook.getTotal())));
        holder.menuButton.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), holder.menuButton);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.common_menu, popup.getMenu());

            // Handle menu item clicks
            popup.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.edit) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("edit book name");
                    final EditText input = new EditText(context);
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);

                    builder.setPositiveButton("Save", (dialog, which) -> {
                        theBook.setBookName(input.getText().toString());
                        viewModel.updateBook(theBook);
                    });
                    builder.setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                    });

                    // Show the dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                } else if (menuItem.getItemId() == R.id.delete) {
                    for (BookModel.Category theCategory:listOfCategory){
                        if(theCategory.getCategory().equals(categoryName)){
                            double categoryTotal = theCategory.getCategoryTotal()-theBook.getTotal();
                            theCategory.setCategoryTotal(categoryTotal);
                            viewModel.updateCategory(theCategory);
                        }
                    }
                    viewModel.deleteBook(theBook);
                    return true;
                }
                return false;
            });
            popup.show();
        });
        // Set the click listener
        holder.itemView.setOnClickListener(view -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(position); // Trigger the click event
            }
        });
    }

    @Override
    public int getItemCount() {
        return listOfBook.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView BookName,BookTotal,CategoryName;
        ImageButton menuButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            BookName = itemView.findViewById(R.id.BookName);
            BookTotal = itemView.findViewById(R.id.BookTotal);
            CategoryName = itemView.findViewById(R.id.CategoryName);
            menuButton = itemView.findViewById(R.id.menuButton);
        }
    }

    // Embedded interface for item clicks
    public interface ItemClickListener {
        void onItemClick(int position);
    }

    // Method to get a Book at a given position
    public BookModel.Book getBookAt(int position) {
        return listOfBook.get(position);
    }
}
