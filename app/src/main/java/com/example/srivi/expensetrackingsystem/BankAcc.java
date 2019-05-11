package com.example.srivi.expensetrackingsystem;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by srivi on 28-04-2019.
 */

public class BankAcc extends Fragment {

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = database.getReference();

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    final String uid = user.getUid();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_bankacc, container, false);

        final EditText nm= (EditText) v.findViewById(R.id.bank_name);
        final EditText bl= (EditText) v.findViewById(R.id.bank_balance);
        Button b=(Button) v.findViewById(R.id.bank_sub);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Bank bnk = new Bank(nm.getText().toString(), bl.getText().toString());
                if (nm.getText().toString().matches(""))
                    Toast.makeText(getContext(), "Bank Name cannot be empty", Toast.LENGTH_SHORT).show();
                else if (bl.getText().toString().matches("")) {
                    myRef.child(uid).child("Bank_details").child(bnk.bank_name).child("Name").setValue(bnk.bank_name);
                    myRef.child(uid).child("Bank_details").child(bnk.bank_name).child("Balance").setValue("0");
                    Toast.makeText(getContext(), "Bank Account added with Balance set to 0", Toast.LENGTH_SHORT).show();
                    nm.getText().clear();
                } else {
                    myRef.child(uid).child("Bank_details").child(bnk.bank_name).child("Name").setValue(bnk.bank_name);
                    myRef.child(uid).child("Bank_details").child(bnk.bank_name).child("Balance").setValue(bnk.balance);
                    Toast.makeText(getContext(), "Bank Account Added", Toast.LENGTH_SHORT).show();
                    nm.getText().clear();
                    bl.getText().clear();
                }
            }
        });

        return v;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Add Bank Account");
    }
}
