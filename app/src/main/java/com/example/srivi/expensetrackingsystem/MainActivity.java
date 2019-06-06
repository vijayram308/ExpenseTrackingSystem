package com.example.srivi.expensetrackingsystem;

import android.app.KeyguardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int INTENT_AUTHENTICATE=10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setStatusBarColor(this.getResources().getColor(R.color.StatsColor));
        int ch1=0;
        if(getIntent().hasExtra("test"))
            ch1 = getIntent().getExtras().getInt("test");
        if(ch1!=1) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

                if (km.isKeyguardSecure()) {
                    Intent authIntent = km.createConfirmDeviceCredentialIntent("Unlock MFlow", "Confirm your screen lock pattern, PIN or password");
                    startActivityForResult(authIntent, INTENT_AUTHENTICATE);
                }
            }
        }
        else{
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            int ch=0;
            if(getIntent().hasExtra("frgToLoad"))
                ch = getIntent().getExtras().getInt("frgToLoad");
            displaySelectedScreen(R.id.nav_upd, ch);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTENT_AUTHENTICATE) {
            if (resultCode == RESULT_OK) {
                Intent i = new Intent(this, MainActivity.class);
                i.putExtra("test", 1);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(i);
            }
            else {

                    finish();
                    moveTaskToBack(true);

            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            showMessageOkCancel("Are you sure you want to Exit?",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(android.os.Build.VERSION.SDK_INT >= 21)
                            {
                                finishAndRemoveTask();
                            }
                            else
                            {
                                finish();
                            }
                        }
                    },
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
        }
    }

    private void showMessageOkCancel(String message, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        new android.support.v7.app.AlertDialog.Builder(MainActivity.this)
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton("Yes", okListener)
                .setNegativeButton("Cancel",cancelListener)
                .create()
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_settings) {
            return true;
        }*/
        if(id == R.id.logout){
            FirebaseAuth.getInstance().signOut();
            Intent I=new Intent(MainActivity.this,LoginActivity.class);
            startActivity(I);
        }

        return super.onOptionsItemSelected(item);
    }

    private void displaySelectedScreen(int itemId, int ch) {

        //creating fragment object
        Fragment fragment = null;
        if(ch==1)
            itemId=R.id.nav_dues;
        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.nav_upd:
                fragment = new UpdateBalance();
                break;
            case R.id.nav_bank:
                fragment = new BankAcc();
                break;
            case R.id.nav_history:
                fragment = new History();
                break;
            case R.id.nav_dues:
                fragment= new DebtManager();
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                Intent I=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(I);
            /*case R.id.nav_share:
                break;*/
        }

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        displaySelectedScreen(id,0);
        return true;
    }
}

