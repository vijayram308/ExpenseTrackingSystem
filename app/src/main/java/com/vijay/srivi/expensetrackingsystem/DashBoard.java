package com.vijay.srivi.expensetrackingsystem;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DashBoard extends Fragment {
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = database.getReference();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    final String uid = user.getUid();
    private List<Bank> bankList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.frag_dash, container, false);
        final ConstraintLayout c = v.findViewById(R.id.dsh_main);
        c.setVisibility(View.GONE);
        createList(v, c);
        TextView dbt = v.findViewById(R.id.debt_lnk);
        TextView bl = v.findViewById(R.id.bal_lnk);
        dbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new DebtManager();
                FragmentTransaction ft = ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.commit();;
            }
        });
        bl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new UpdateBalance();
                FragmentTransaction ft = ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.commit();;
            }
        });
        return v;
    }

    private void callList(View v) {
        RecyclerView rv = v.findViewById(R.id.list);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);
        BankAdapter ca = new BankAdapter(bankList);
        rv.setAdapter(ca);
    }

    private void createList(final View v, final ConstraintLayout c) {
        bankList.clear();
        final ProgressBar pgsBar = v.findViewById(R.id.pBar_dsh);
        final ConstraintLayout cd = v.findViewById(R.id.card_dbt);
        (myRef.child(uid)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("Debt")==false)
                {
                    cd.setVisibility(View.GONE);
                }
                else {
                    float tot_to = 0, tot_by = 0;
                    for (DataSnapshot ds : dataSnapshot.child("Debt").getChildren()) {
                        float amn = Float.parseFloat(ds.child("amount").getValue().toString());
                        if (amn > 0)
                            tot_to += amn;
                        else if (amn < 0)
                            tot_by += amn;
                    }
                    if (tot_by < 0)
                        tot_by *= -1;
                    TextView to = (TextView) v.findViewById(R.id.to_bl);
                    TextView by = (TextView) v.findViewById(R.id.by_bl);
                    to.setText("Rs. " + tot_to);
                    by.setText("Rs. " + tot_by);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        (myRef.child(uid).child("Bank_details")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nm = dataSnapshot.child("Wallet").child("Name").getValue().toString();
                String bl = "Rs." + dataSnapshot.child("Wallet").child("Balance").getValue().toString();
                bankList.add(new Bank(nm, bl));
                callList(v);
                for (DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()) {
                    if ((uniqueKeySnapshot.child("Name").getValue().toString()).equals("Wallet"))
                        continue;
                    nm = uniqueKeySnapshot.child("Name").getValue().toString();
                    bl = "Rs." + uniqueKeySnapshot.child("Balance").getValue().toString();
                    bankList.add(new Bank(nm, bl));
                }
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
        getActivity().setTitle("Dashboard");
    }
}
