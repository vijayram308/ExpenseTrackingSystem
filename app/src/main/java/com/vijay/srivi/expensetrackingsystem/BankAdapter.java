package com.vijay.srivi.expensetrackingsystem;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class BankAdapter extends RecyclerView.Adapter<BankAdapter.MyViewHolder> {

    private List<Bank> bankList;

    public BankAdapter(List<Bank> bankList) {
        this.bankList = bankList;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        System.out.println("Bind [" + holder + "] - Pos [" + position + "]");
        Bank c = bankList.get(position);
        holder.bankText.setText(c.bank_name);
        holder.balanceText.setText(String.valueOf(c.balance));
    }

    @Override
    public int getItemCount() {
        Log.d("RV", "Item size [" + bankList.size() + "]");
        return bankList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view, parent, false);
        return new MyViewHolder(v);
    }

    /**
     * View holder class
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView bankText;
        public TextView balanceText;

        public MyViewHolder(View view) {
            super(view);
            bankText = view.findViewById(R.id.bankName);
            balanceText = view.findViewById(R.id.balance);
        }
    }
}