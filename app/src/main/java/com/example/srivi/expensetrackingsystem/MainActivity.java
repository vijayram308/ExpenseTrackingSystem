package com.example.srivi.expensetrackingsystem;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //private static final int PERMISSIONS_REQUEST_CODE = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        int ch=0;
        if(getIntent().hasExtra("frgToLoad"))
            ch = getIntent().getExtras().getInt("frgToLoad");
        displaySelectedScreen(R.id.nav_upd, ch);

    }

  /*public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                start_activity();
            }
            else {
                finish();
            }
        }
    }
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            boolean showRationale = shouldShowRequestPermissionRationale( Manifest.permission.READ_CONTACTS);
            if (showRationale) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent,PERMISSIONS_REQUEST_CODE );                    }
                });
                builder1.setTitle("PERMISSION REQUIRED");
                builder1.setMessage("Expense Tracking System requires permission to access your Contacts for it to work properly.\n\nPlease allow this permission in the Settings page. You can click on the Settings button below or manually navigate to the App Settings and allow this permission.");
                AlertDialog alertDialog = builder1.create();
                alertDialog.show();
            }
            else if(!showRationale) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_CODE);
                    }
                });
                builder.setTitle("PERMISSION REQUIRED");
                builder.setMessage("Expense Tracking System requires permission to access your Contacts for it to work properly.\n\nPlease allow this permission in the next dialog box.");
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }

        }
        else
            start_activity();
    }

    public void start_activity()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        displaySelectedScreen(R.id.nav_upd);
    }*/

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
            case R.id.nav_share:
                break;
        }

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

