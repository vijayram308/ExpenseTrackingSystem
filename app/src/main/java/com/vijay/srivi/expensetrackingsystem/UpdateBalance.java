package com.vijay.srivi.expensetrackingsystem;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class UpdateBalance extends Fragment {

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = database.getReference();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    final String uid = user.getUid();
    private List<String> pay_spinnerList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.frag_upd, container, false);
        final ConstraintLayout c = v.findViewById(R.id.upd_main);
        c.setVisibility(View.GONE);
        final Spinner pm = v.findViewById(R.id.pay_mode);
        createList(pm, v, c);

        final Spinner typ = v.findViewById(R.id.type);
        final EditText as = v.findViewById(R.id.amount);
        final EditText dsc = v.findViewById(R.id.desc);
        Button b = v.findViewById(R.id.sub);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.type, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        typ.setAdapter(adapter1);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Transaction t;
                String amn = as.getText().toString();
                final String des = dsc.getText().toString();
                if (amn.matches("")) {
                    as.setError("Please enter the amount");
                    as.requestFocus();
                    return;
                } else {
                    t = new Transaction(pm.getSelectedItem().toString(), typ.getSelectedItem().toString(), Float.toString(Float.parseFloat(as.getText().toString())), des, new Date());
                    if((t.pay_mode.equals("Wallet")) && (amn.indexOf('.')>=0)) {
                        showMessageOk(
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                    }
                    else {
                        if ((t.type).equals("Expenditure")) {
                            (myRef.child(uid).child("Bank_details")).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    float x = Float.parseFloat(dataSnapshot.child(t.pay_mode).child("Balance").getValue().toString());
                                    x -= Float.parseFloat(t.amount);
                                    if (x < 0) {
                                        String msg = "Insufficient " + t.pay_mode + " balance";
                                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                                        as.getText().clear();
                                        dsc.getText().clear();
                                    } else {
                                        myRef.child(uid).child("history").push().setValue(t);
                                        myRef.push();
                                        myRef.child(uid).child("Bank_details").child(t.pay_mode).child("Balance").setValue(Float.toString(x));
                                        Toast.makeText(getContext(), "Expenditure updated", Toast.LENGTH_SHORT).show();
                                        as.getText().clear();
                                        dsc.getText().clear();
                                        createList(pm, v, c);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        } else if ((t.type).equals("Income")) {
                            myRef.child(uid).child("history").push().setValue(t);
                            myRef.push();
                            (myRef.child(uid).child("Bank_details")).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    float x = Float.parseFloat(dataSnapshot.child(t.pay_mode).child("Balance").getValue().toString());
                                    x += Float.parseFloat(t.amount);
                                    myRef.child(uid).child("Bank_details").child(t.pay_mode).child("Balance").setValue(Float.toString(x));
                                    Toast.makeText(getContext(), "Income updated", Toast.LENGTH_SHORT).show();
                                    as.getText().clear();
                                    dsc.getText().clear();
                                    createList(pm, v, c);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }
            }
        });
        return v;
    }

    private void showMessageOk(DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .setTitle("Wallet Entry Alert")
                .setMessage("Your physical wallet amount cannot be a decimal value. Please enter a valid amount.")
                .setPositiveButton("OK", okListener)
                .create()
                .show();
    }


    private void createList(final Spinner pm, final View v, final ConstraintLayout c) {
        pay_spinnerList.clear();
        final ProgressBar pgsBar = v.findViewById(R.id.pBar_upd);
        (myRef.child(uid).child("Bank_details")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nm = dataSnapshot.child("Wallet").child("Name").getValue().toString();
                pay_spinnerList.add(nm);
                for (DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()) {
                    if ((uniqueKeySnapshot.child("Name").getValue().toString()).equals("Wallet"))
                        continue;
                    nm = uniqueKeySnapshot.child("Name").getValue().toString();
                    pay_spinnerList.add(nm);
                }
                ArrayAdapter<String> payAdapter = new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_spinner_item, pay_spinnerList);
                payAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                pm.setAdapter(payAdapter);
                c.setVisibility(View.VISIBLE);
                pgsBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Update Balance");
    }
}

class Transaction {
    public String type;
    public String pay_mode;
    public String amount;
    public String desc = "";
    public Date dt;
    public String d;


    public Transaction(String Pay_mode, String Type, String Amn, String Desc, String D) {
        pay_mode = Pay_mode;
        type = Type;
        amount = Amn;
        desc = Desc;
        d = D;
    }

    public Transaction(String Pay_mode, String Type, String Amn, String Desc, Date D) {
        pay_mode = Pay_mode;
        type = Type;
        amount = Amn;
        desc = Desc;
        dt = D;
    }
/*
    public Transaction(String Pay_mode, String Type, float Amount, String Desc, Date Dt) {
        pay_mode = Pay_mode;
        type = Type;
        amount = Amount;
        desc = Desc;
        dt = Dt;
    }*/
}
