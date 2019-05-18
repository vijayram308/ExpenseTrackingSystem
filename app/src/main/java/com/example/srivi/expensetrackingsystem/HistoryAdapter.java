package com.example.srivi.expensetrackingsystem;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {

    private List<Transaction> historyList;
    int d[]=new int[1000];
    Context con;

    /**
     * View holder class
     * */
    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView amountText;
        public TextView typeText;
        public TextView payModeText;
        public TextView descText;
        public TextView dtText;

        public MyViewHolder(View view) {
            super(view);
            amountText = (TextView) view.findViewById(R.id.amn1);
            typeText = (TextView) view.findViewById(R.id.typ1);
            payModeText = (TextView) view.findViewById(R.id.pay_mode1);
            descText = (TextView) view.findViewById(R.id.desc1);
            dtText = (TextView) view.findViewById(R.id.dt1);
        }
    }

    public HistoryAdapter(List<Transaction> historyList, int d[], Context con) {
        this.con = con;
        this.historyList = historyList;
        this.d = d;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        System.out.println("Bind ["+holder+"] - Pos ["+position+"]");
        Transaction c = historyList.get(position);
        holder.amountText.setText(c.amn);
        holder.typeText.setText(c.type);
        holder.typeText.setBackgroundResource(d[position]);

        SpannableStringBuilder builder = new SpannableStringBuilder();
        if(c.pay_mode.equals("Wallet")) {
            builder.append(" ").append(" ", new ImageSpan(con, R.drawable.ic_wallet1), 0).append("  " + c.pay_mode);
        }
        else {
            builder.append(" ").append(" ", new ImageSpan(con, R.drawable.ic_menu_bank), 0).append("  " + c.pay_mode);
        }
        holder.payModeText.setText(builder);
        holder.descText.setText(c.desc);
        holder.dtText.setText(c.d);
    }

    @Override
    public int getItemCount() {
        Log.d("RV", "Item size ["+ historyList.size()+"]");
        return historyList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_card_list,parent, false);
        return new MyViewHolder(v);
    }
}