package com.example.srivi.expensetrackingsystem;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class History extends Fragment {

    private List<Transaction> historyList = new ArrayList<>();

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = database.getReference();

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    final String uid = user.getUid();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View v=inflater.inflate(R.layout.frag_history, container, false);
        //TextView tv = v.findViewById(R.id.typ1);
        //tv.setBackgroundColor(Color.parseColor("#696969"));
        createList(v);
        return v;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("History");
    }
    private String month(int m)
    {
        switch(m)
        {
            case 0: return "Jan";
            case 1: return "Feb";
            case 2: return "Mar";
            case 3: return "Apr";
            case 4: return "May";
            case 5: return "Jun";
            case 6: return "Jul";
            case 7: return "Aug";
            case 8: return "Sep";
            case 9: return "Oct";
            case 10: return "Nov";
            case 11: return "Dec";
        }
        return "";
    }
    private void callList(View v, int d[]){
        RecyclerView rv = (RecyclerView) v.findViewById(R.id.cards);
        TextView tv = (TextView) v.findViewById(R.id.no_history);
        if(!(historyList.isEmpty())) {
            LinearLayoutManager llm = new LinearLayoutManager(getContext());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            rv.setLayoutManager(llm);
            HistoryAdapter ca = new HistoryAdapter(historyList, d, getContext());
            rv.setAdapter(ca);
            rv.setVisibility(View.VISIBLE);
            tv.setVisibility(View.GONE);
        }
        else{
            rv.setVisibility(View.GONE);
            tv.setVisibility(View.VISIBLE);
        }
    }
    private void createList(final View v) {
        (myRef.child(uid).child("history")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String pay_md, typ, des,amn, str_m, d, dat, yr;
                int m, y, i=0;
                String s[]=new String[1000];
                int d1[] = new int[1000];
                for (DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()) {
                    pay_md = uniqueKeySnapshot.child("pay_mode").getValue().toString();
                    typ = uniqueKeySnapshot.child("type").getValue().toString();
                    if(typ.equals("Expenditure"))
                        d1[i]=R.drawable.round_corners_red;
                    else
                        d1[i]=R.drawable.round_corners;
                    i++;
                    amn="Rs." + uniqueKeySnapshot.child("amount").getValue().toString();
                    des = uniqueKeySnapshot.child("desc").getValue().toString();
                    dat = uniqueKeySnapshot.child("dt").child("date").getValue().toString();
                    m = Integer.parseInt(uniqueKeySnapshot.child("dt").child("month").getValue().toString());
                    y = Integer.parseInt(uniqueKeySnapshot.child("dt").child("year").getValue().toString());
                    y+=1900;
                    yr = new Integer(y).toString() ;
                    str_m = month(m);
                    d = dat + " " + str_m + " " + yr;
                    historyList.add(new Transaction(pay_md, typ, amn, des, d));
                }
                callList(v, d1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}