package com.vijay.srivi.expensetrackingsystem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.constraint.ConstraintLayout;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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

public class OwedTo extends Fragment {

    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private static final int RESULT_OK = -1;
    private static final int PERMISSION_REQUEST_CODE = 2;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = database.getReference();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    final String uid = user.getUid();
    String contactName = null;
    String contactNum = null;
    CheckBox cb;
    EditText nm;
    EditText ph;
    EditText amn;
    private List<Debt> debtList = new ArrayList<>();
    private Uri uriContact = null;
    private String contactID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.tab_owedto, container, false);
        final ConstraintLayout c = v.findViewById(R.id.owedto_main);
        c.setVisibility(View.GONE);
        Button b = v.findViewById(R.id.btn_owedto);
        createList(v, c);
        int ch = 0;
        if (getActivity().getIntent().hasExtra("openDialog"))
            ch = getActivity().getIntent().getExtras().getInt("openDialog");
        if (ch == 1) {
            showInputDialog();
        }
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
    }

    private void retrieveContactName() {
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

        if ((contactNum == null) && (!(getActivity().getIntent().hasExtra("nmVal")))) {
            showMessage("A contact without phone number cannot be added !!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }, "OK");
        } else {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            View promptView = layoutInflater.inflate(R.layout.dialog, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setView(promptView);
            cb = promptView.findViewById(R.id.sms_send);
            alertDialogBuilder.setTitle("New Entry");
            nm = promptView.findViewById(R.id.dialog_nm);
            ph = promptView.findViewById(R.id.dialog_ph);
            amn = promptView.findViewById(R.id.dialog_amn);
            /*if(getActivity().getIntent().hasExtra("nmVal")) {
                nm.setText(getActivity().getIntent().getExtras().getString("nmVal"));
                nm.setEnabled(false);
                ph.setText(getActivity().getIntent().getExtras().getString("phVal"));
                ph.setEnabled(false);
                amn.setText(getActivity().getIntent().getExtras().getString("amnVal"));
                cb.setChecked(true);
                getActivity().getIntent().removeExtra("nmVal");
                getActivity().getIntent().removeExtra("phVal");
                getActivity().getIntent().removeExtra("amnVal");
                getActivity().getIntent().removeExtra("openDialog");
            }
            else {*/
            nm.setText(contactName);
            ph.setText(contactNum);
            nm.setEnabled(false);
            ph.setEnabled(false);
            //}
            /*cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!checkPermission())
                        requestPermission();
                }
            });*/
            // setup a dialog window
            alertDialogBuilder.setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            String amn_txt = amn.getText().toString();
                            if (amn_txt.isEmpty())
                                Toast.makeText(getContext(), "Amount field cannot be left empty", Toast.LENGTH_SHORT).show();
                            else {
                                //final SmsManager smgr = SmsManager.getDefault();
                                final Debt d = new Debt(nm.getText().toString(), amn.getText().toString(), ph.getText().toString());
                                (myRef.child(uid).child("Debt")).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.child(d.ph_no).exists()) {
                                            float x = Float.parseFloat(dataSnapshot.child(d.ph_no).child("amount").getValue().toString());
                                            x += Float.parseFloat(d.amount);
                                            myRef.child(uid).child("Debt").child(d.ph_no).child("amount").setValue(x);
                                            if (x < 0) {
                                                x *= -1;
                                                if (cb.isChecked()) {
                                                    try {
                                                        //smgr.sendTextMessage(ph.getText().toString(), null, "I owe you Rs. " + x, null, null);
                                                        sendSMS("Hi "+nm.getText()+"\n\nI owe you Rs. " + x + "\n\nSent via MFlow", ph.getText().toString());
                                                        Toast.makeText(getContext(), "You owe " + d.name + " Rs. " + x + ". SMS sent", Toast.LENGTH_SHORT).show();
                                                    } catch (Exception e) {
                                                        Toast.makeText(getContext(), "SMS Failed to Send, Please try again", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else
                                                    Toast.makeText(getContext(), "You owe " + d.name + " Rs. " + x, Toast.LENGTH_SHORT).show();
                                            } else if (x > 0) {
                                                if (cb.isChecked()) {
                                                    try {
                                                        //smgr.sendTextMessage(ph.getText().toString(), null, "You owe me Rs. " + x, null, null);
                                                        sendSMS("Hi "+nm.getText()+"\n\nYou owe me Rs. " + x +"\n\nSent via MFlow", ph.getText().toString());
                                                        Toast.makeText(getContext(), d.name + " owes you Rs. " + x + ". SMS sent", Toast.LENGTH_SHORT).show();
                                                    } catch (Exception e) {
                                                        Toast.makeText(getContext(), "SMS Failed to Send, Please try again", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else
                                                    Toast.makeText(getContext(), d.name + " owes you Rs. " + x, Toast.LENGTH_SHORT).show();
                                            } else {
                                                myRef.child(uid).child("Debt").child(d.ph_no).getRef().removeValue();
                                            }
                                        } else {
                                            myRef.child(uid).child("Debt").child(d.ph_no).setValue(d);
                                            myRef.push();
                                            if (cb.isChecked()) {
                                                try {
                                                    //smgr.sendTextMessage(ph.getText().toString(), null, "You owe me Rs. " + d.amount, null, null);
                                                    sendSMS("Hi "+nm.getText()+"\n\nYou owe me Rs. " + d.amount + "\n\nSent via MFlow", ph.getText().toString());
                                                    Toast.makeText(getContext(), d.name + " owes you Rs. " + d.amount + ". SMS sent", Toast.LENGTH_SHORT).show();
                                                } catch (Exception e) {
                                                    Toast.makeText(getContext(), "SMS Failed to Send, Please try again", Toast.LENGTH_SHORT).show();
                                                }
                                            } else
                                                Toast.makeText(getContext(), d.name + " owes you Rs. " + d.amount, Toast.LENGTH_SHORT).show();
                                        }
                                        Fragment fragment = new DebtManager();
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

    public void sendSMS(String msg, String num) {
        Uri uri = Uri.parse("smsto:" + num);
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        it.putExtra("sms_body", msg);
        startActivity(it);
    }

    /*private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        requestPermissions(new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_CODE);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (accepted){
                    }
                    else {
                        Toast.makeText(getContext(),"Permission Denied",Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS)) {
                                showMessageOkCancel("To send SMS, you have to allow the SMS permission.\n\nPlease allow this permission for sending text messages.",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{Manifest.permission.SEND_SMS},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        },
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                cb.setChecked(false);
                                                dialogInterface.cancel();
                                            }
                                        }
                                        ,"OK");
                                return;
                            }
                            else
                            {
                                showMessageOkCancel("To send SMS, you have to allow the SMS permission.\n\nYou can click on the Settings button below or manually navigate to the App Settings and allow this permission. ",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    final Handler handler = new Handler();

                                                    Runnable checkSettingOn = new Runnable() {

                                                        @Override
                                                        //@TargetApi(23)
                                                        public void run() {
                                                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                                                                return;
                                                            }
                                                            if (checkPermission()) {
                                                                Intent i = new Intent(getActivity(), MainActivity.class);
                                                                i.putExtra("frgToLoad", 1);
                                                                i.putExtra("openDialog", 1);
                                                                i.putExtra("nmVal", nm.getText().toString());
                                                                i.putExtra("phVal", ph.getText().toString());
                                                                i.putExtra("amnVal", amn.getText().toString());
                                                                startActivity(i);
                                                                return;
                                                            }
                                                            handler.postDelayed(this, 200);
                                                        }
                                                    };
                                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                    Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                                                    intent.setData(uri);
                                                    handler.postDelayed(checkSettingOn, 1000);
                                                    startActivityForResult(intent, PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        },
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                cb.setChecked(false);
                                                dialogInterface.cancel();
                                            }
                                        }
                                        , "Settings");
                                return;
                            }
                        }

                    }
                }
                break;
        }
    }*/

    private void showMessageOkCancel(String message, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener, String btn) {
        new android.support.v7.app.AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .setTitle("PERMISSION REQUIRED")
                .setMessage(message)
                .setPositiveButton(btn, okListener)
                .setNegativeButton("Cancel", cancelListener)
                .create()
                .show();
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

    public void callList(View v) {
        RecyclerView rv = v.findViewById(R.id.owedto_list);
        TextView tv = v.findViewById(R.id.no_entry);
        if (!(debtList.isEmpty())) {
            LinearLayoutManager llm = new LinearLayoutManager(getContext());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            rv.setLayoutManager(llm);
            DebtAdapter ca = new DebtAdapter(debtList, v.getContext(), 0);
            rv.setAdapter(ca);
            rv.setVisibility(View.VISIBLE);
            tv.setVisibility(View.GONE);
        } else {
            rv.setVisibility(View.GONE);
            tv.setVisibility(View.VISIBLE);
        }
    }

    public void createList(final View v, final ConstraintLayout c) {
        debtList.clear();
        final ProgressBar pgsBar = v.findViewById(R.id.pBar_dto);
        (myRef.child(uid).child("Debt")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name, amount, ph;
                for (DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()) {
                    if (Integer.parseInt(uniqueKeySnapshot.child("amount").getValue().toString()) > 0) {
                        name = uniqueKeySnapshot.child("name").getValue().toString();
                        amount = uniqueKeySnapshot.child("amount").getValue().toString();
                        amount = "Rs." + amount;
                        ph = uniqueKeySnapshot.child("ph_no").getValue().toString();
                        debtList.add(new Debt(name, amount, ph));
                    }
                }
                callList(v);
                c.setVisibility(View.VISIBLE);
                pgsBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
