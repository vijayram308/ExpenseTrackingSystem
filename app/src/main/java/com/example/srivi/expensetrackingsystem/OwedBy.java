package com.example.srivi.expensetrackingsystem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by srivi on 08-05-2019.
 */

public class OwedBy extends Fragment {

    private List<Debt> debtList = new ArrayList<>();

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = database.getReference();

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    final String uid = user.getUid();

    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private static final int RESULT_OK = -1;
    private Uri uriContact;
    private String contactID;
    String contactName = null;
    String contactNum = null;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v=inflater.inflate(R.layout.tab_owedby, container, false);
        Button b = (Button) v.findViewById(R.id.btn_owedby);

        createList(v);

        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_CONTACTS && resultCode == RESULT_OK) {
            uriContact = data.getData();
            retrieveContactNumber();
            retrieveContactName();
        }
    }

    private void retrieveContactNumber() {

        // getting contacts ID
        Cursor cursorID = getActivity().getContentResolver().query(uriContact,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);

        if (cursorID.moveToFirst()) {

            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }

        cursorID.close();

        Log.d("xxxx", "Contact ID: " + contactID);

        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

                new String[]{contactID},
                null);

        if (cursorPhone.moveToFirst()) {
            contactNum = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }

        cursorPhone.close();

        Log.d("xxxxx", "Contact Phone Number: " + contactNum);
    }

    private void retrieveContactName() {

        // querying contact data store
        Cursor cursor = getActivity().getContentResolver().query(uriContact, null, null, null, null);
        if (cursor.moveToFirst()) {

            // DISPLAY_NAME = The display name for the contact.
            // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.

            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            showInputDialog();
        }
        cursor.close();
    }

    protected void showInputDialog() {
        if(contactNum == null){
            showMessage("A contact without phone number cannot be added !!",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            },"OK");
        }
        else {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            View promptView = layoutInflater.inflate(R.layout.dialog, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
            alertDialogBuilder.setTitle("New Entry");
            alertDialogBuilder.setView(promptView);
            final EditText nm = promptView.findViewById(R.id.dialog_nm);
            final EditText ph = promptView.findViewById(R.id.dialog_ph);
            nm.setText(contactName);
            ph.setText(contactNum);
            nm.setEnabled(false);
            ph.setEnabled(false);
            final EditText amn = (EditText) promptView.findViewById(R.id.dialog_amn);

            // setup a dialog window
            alertDialogBuilder.setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            String amn_txt=amn.getText().toString();
                            if(amn_txt.isEmpty())
                                Toast.makeText(getContext(), "Amount field cannot be left empty", Toast.LENGTH_SHORT).show();
                            else {
                                final Debt d = new Debt(nm.getText().toString(), amn.getText().toString(), ph.getText().toString());
                                (myRef.child(uid).child("Debt")).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.child(d.ph_no).exists()) {
                                            float x = Float.parseFloat(dataSnapshot.child(d.ph_no).child("amount").getValue().toString());
                                            x -= Float.parseFloat(d.amount);
                                            myRef.child(uid).child("Debt").child(d.ph_no).child("amount").setValue(x);
                                            if (x > 0) {
                                                Toast.makeText(getContext(), d.name + " owes you Rs. " + x, Toast.LENGTH_SHORT).show();
                                            } else if (x < 0) {
                                                x *= -1;
                                                Toast.makeText(getContext(), "You owe " + d.name + " Rs. " + x, Toast.LENGTH_SHORT).show();
                                            } else {
                                                myRef.child(uid).child("Debt").child(d.ph_no).getRef().removeValue();
                                            }
                                        } else {
                                            int x = Integer.parseInt(d.amount) * (-1);
                                            d.amount = "" + x;
                                            myRef.child(uid).child("Debt").child(d.ph_no).setValue(d);
                                            myRef.push();
                                            x *= -1;
                                            Toast.makeText(getContext(), "You owe " + d.name + " Rs. " + x, Toast.LENGTH_SHORT).show();
                                        }
                                        Fragment fragment = new DebtManager();
                                        Bundle bundle = new Bundle();
                                        bundle.putInt("ch", 1);
                                        fragment.setArguments(bundle);
                                        FragmentTransaction ft = ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction();
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
    }

    private void showMessage(String message, DialogInterface.OnClickListener okListener, String btn) {
        new android.support.v7.app.AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .setTitle("MISSING CONTACT NUMBER")
                .setMessage(message)
                .setPositiveButton(btn, okListener)
                .create()
                .show();
    }

    private void callList(View v){
        RecyclerView rv = (RecyclerView) v.findViewById(R.id.owedby_list);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);
        DebtAdapter ca = new DebtAdapter(debtList,v.getContext(),1);
        rv.setAdapter(ca);
    }
    private void createList(final View v) {
        debtList.clear();
        (myRef.child(uid).child("Debt")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name, amount, ph;
                int amn;
                for (DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()) {
                    if(Integer.parseInt(uniqueKeySnapshot.child("amount").getValue().toString())<0) {
                        amn=Integer.parseInt(uniqueKeySnapshot.child("amount").getValue().toString())*(-1);
                        name = uniqueKeySnapshot.child("name").getValue().toString();
                        amount = "Rs." + amn;
                        ph = uniqueKeySnapshot.child("ph_no").getValue().toString();
                        debtList.add(new Debt(name, amount, ph));
                    }
                }
                if(!(debtList.isEmpty()))
                    callList(v);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
