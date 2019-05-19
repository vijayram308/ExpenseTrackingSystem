package com.example.srivi.expensetrackingsystem;

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
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setStatusBarColor(this.getResources().getColor(R.color.StatsColor));
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
                            finish();
                            moveTaskToBack(true);
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

