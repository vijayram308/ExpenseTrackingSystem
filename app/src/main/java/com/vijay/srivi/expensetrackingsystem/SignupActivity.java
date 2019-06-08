package com.vijay.srivi.expensetrackingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = database.getReference();
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_signup);
        this.getWindow().setStatusBarColor(this.getResources().getColor(R.color.StatsColor));
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        firebaseAuth = FirebaseAuth.getInstance();
        this.setTitle("Registration");
        final EditText emailId = findViewById(R.id.signup_email);
        final EditText passwd = findViewById(R.id.signup_pass);
        final EditText w_balance = findViewById(R.id.signup_wl);
        Button btnSignUp = findViewById(R.id.signup_btn);
        TextView signIn = findViewById(R.id.reg_login);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailID = emailId.getText().toString();
                String paswd = passwd.getText().toString();
                final String wBalance = w_balance.getText().toString();
                if (emailID.isEmpty()) {
                    emailId.setError("Please provide your Email ID!");
                    emailId.requestFocus();
                } else if (wBalance.isEmpty()) {
                    w_balance.setError("Please set the password");
                    w_balance.requestFocus();
                } else if (paswd.isEmpty()) {
                    passwd.setError("Please set the password");
                    passwd.requestFocus();
                } else if (emailID.isEmpty() && paswd.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Fields Empty!", Toast.LENGTH_SHORT).show();
                } else if (!(emailID.isEmpty() && paswd.isEmpty())) {
                    firebaseAuth.createUserWithEmailAndPassword(emailID, paswd).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (!task.isSuccessful()) {
                                Toast.makeText(SignupActivity.this.getApplicationContext(),
                                        "SignUp Unsuccessful: " + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                final String uid = user.getUid();
                                myRef.child(uid).child("Bank_details").child("Wallet").child("Balance").setValue(wBalance);
                                myRef.child(uid).child("Bank_details").child("Wallet").child("Name").setValue("Wallet");
                                myRef.push();
                                startActivity(new Intent(SignupActivity.this, MainActivity.class));
                            }
                        }
                    });
                } else {
                    Toast.makeText(SignupActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(I);
            }
        });
    }
}
