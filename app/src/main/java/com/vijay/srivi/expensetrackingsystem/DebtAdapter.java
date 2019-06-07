package com.vijay.srivi.expensetrackingsystem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by srivi on 08-05-2019.
 */

public class DebtAdapter extends RecyclerView.Adapter<DebtAdapter.MyViewHolder> {

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = database.getReference();
    Context c1;
    int ch;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    final String uid = user.getUid();
    private List<Debt> debtList;

    public DebtAdapter(List<Debt> debtList, Context c1, int ch) {

        this.c1 = c1;
        this.debtList = debtList;
        this.ch = ch;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        System.out.println("Bind [" + holder + "] - Pos [" + position + "]");
        final Debt c = debtList.get(position);
        holder.nameText.setText(c.name);
        holder.amountText.setText(String.valueOf(c.amount));
        holder.doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog(c);
            }
        });
    }

    protected void showInputDialog(final Debt c) {
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(c1);
        View promptView = layoutInflater.inflate(R.layout.dialog_done, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c1);
        alertDialogBuilder.setView(promptView);
        final EditText amn = promptView.findViewById(R.id.dialog_done_amn);
        final Button btn = promptView.findViewById(R.id.fullpay_btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRef.child(uid).child("Debt").child(c.ph_no).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String amount = dataSnapshot.child("amount").getValue().toString();
                        final int x = Integer.parseInt(amount) * -1;
                        if (Integer.parseInt(amount) < 0)
                            amn.setText(Integer.toString(x));
                        else
                            amn.setText(amount);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String amn_txt = amn.getText().toString();
                        if (amn_txt.isEmpty()) {
                            Toast.makeText(c1, "Amount field cannot be left empty", Toast.LENGTH_SHORT).show();
                        } else {
                            myRef.child(uid).child("Debt").child(c.ph_no).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    final String amount = dataSnapshot.child("amount").getValue().toString();
                                    int x = 0;
                                    if (Integer.parseInt(amount) > 0) {
                                        x = Integer.parseInt(amount) - Integer.parseInt(amn.getText().toString());
                                        if (x > 0)
                                            Toast.makeText(c1, c.name + " owes you Rs." + x, Toast.LENGTH_SHORT).show();
                                        else if (x < 0)
                                            Toast.makeText(c1, "You owe " + c.name + " Rs." + (x * (-1)), Toast.LENGTH_SHORT).show();

                                    } else if (Integer.parseInt(amount) < 0) {
                                        x = Integer.parseInt(amount) + Integer.parseInt(amn.getText().toString());
                                        if (x > 0)
                                            Toast.makeText(c1, c.name + " owes you Rs." + x, Toast.LENGTH_SHORT).show();
                                        else if (x < 0)
                                            Toast.makeText(c1, "You owe " + c.name + " Rs." + (x * (-1)), Toast.LENGTH_SHORT).show();
                                    }
                                    myRef.child(uid).child("Debt").child(c.ph_no).child("amount").setValue(x);
                                    Fragment fragment = new DebtManager();
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("ch", ch);
                                    fragment.setArguments(bundle);
                                    FragmentTransaction ft = ((AppCompatActivity) c1).getSupportFragmentManager().beginTransaction();
                                    ft.replace(R.id.content_frame, fragment);
                                    ft.commit();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public int getItemCount() {
        Log.d("RV", "Item size [" + debtList.size() + "]");
        return debtList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.debt_card_list, parent, false);
        return new MyViewHolder(v);
    }

    /**
     * View holder class
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView nameText;
        public TextView amountText;
        //public TextView phText;
        Button doneBtn;

        public MyViewHolder(View view) {
            super(view);
            nameText = view.findViewById(R.id.name_debt);
            amountText = view.findViewById(R.id.amn_debt);
            //phText = (TextView) view.findViewById(R.id.ph_debt);
            this.doneBtn = view.findViewById(R.id.done_btn);
        }
    }
}