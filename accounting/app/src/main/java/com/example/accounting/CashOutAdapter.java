package com.example.accounting;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CashOutAdapter extends RecyclerView.Adapter<CashOutAdapter.ViewHolder> {
    private List<BookModel.Cash> listOfCash = new ArrayList<>();
    private int intendedBookID; // Add a field to hold the intended Book ID
    private ViewModel viewModel;
    private Context context;
    private final String OUT = "OUT";
    public static final String IN_OR_OUT = "IN_OR_OUT";
    private final String THE_BOOK_ID = "THE_BOOK_ID";


    public CashOutAdapter(int intendedBookID, ViewModel viewModel, Context context) {
        this.intendedBookID = intendedBookID; // Initialize it in the constructor
        this.context = context;
        this.viewModel = viewModel;
    }

    // Method to set cash entries in the adapter
    public void setCash(List<BookModel.Cash> cash) {
        // Filter the list to only include items with InOrOut = "in" and matching Book ID
        for (BookModel.Cash cashEntry : cash) {
            Log.d("myTag3", "setCash: "+" "+intendedBookID+" "+cashEntry.getInOrOut());
            if ("out".equalsIgnoreCase(cashEntry.getInOrOut()) && cashEntry.getBookID() == intendedBookID) {
                listOfCash.add(cashEntry);
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CashOutAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_of_cash, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookModel.Cash theCash = listOfCash.get(position);
        holder.CashAmount.setText(String.format("%.2f", (theCash.getAmount())));
        holder.Date.setText(theCash.getDate().toString());
        holder.Time.setText(theCash.getTime().toString());
        holder.paymentMethod.setText(theCash.getPaymentMethod());
        holder.NoteTextView.setText(theCash.getNote());

        holder.MenuButton.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), holder.MenuButton);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.common_menu, popup.getMenu());

            // Handle menu item clicks
            popup.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId()==R.id.edit) {
                    viewModel.deleteCash(theCash);
                    Intent intent = new Intent(context, CashInOutInputActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra(IN_OR_OUT, OUT);
                    intent.putExtra(THE_BOOK_ID,intendedBookID);
                    context.startActivity(intent);
                    return true;
                }else if (menuItem.getItemId()==R.id.delete){
                    viewModel.deleteCash(theCash);
                    Intent intent = new Intent(context, CashBookActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                    return true;
                }
                return false;
            });
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return listOfCash.size(); // Return the size of the filtered list
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView CashAmount, Date, paymentMethod, Time, NoteTextView;
        ImageButton MenuButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            CashAmount = itemView.findViewById(R.id.CashAmount);
            Date = itemView.findViewById(R.id.dateTextView);
            Time = itemView.findViewById(R.id.timeTextView);
            paymentMethod = itemView.findViewById(R.id.paymentMethodTextView);
            NoteTextView = itemView.findViewById(R.id.noteTextView);
            MenuButton = itemView.findViewById(R.id.menuButton);
        }
    }
}