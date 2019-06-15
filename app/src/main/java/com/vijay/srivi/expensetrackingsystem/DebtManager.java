package com.vijay.srivi.expensetrackingsystem;

import android.Manifest;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.IntentCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import static android.app.Activity.RESULT_CANCELED;

/**
 * Created by srivi on 08-05-2019.
 */

public class DebtManager extends Fragment {


    private static final int PERMISSION_REQUEST_CODE = 0;
    private static final int RESULT_OK = 1;
    int opt=0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.frag_debt, container, false);
        if (checkPermission()) {
            TabLayout tabLayout = v.findViewById(R.id.tab_layout);
            tabLayout.addTab(tabLayout.newTab().setText("OWED TO ME"));
            tabLayout.addTab(tabLayout.newTab().setText("OWED BY ME"));
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

            final ViewPager viewPager = v.findViewById(R.id.pager);
            final PageAdapter adapter = new PageAdapter
                    (getActivity().getSupportFragmentManager(), tabLayout.getTabCount());
            viewPager.setAdapter(adapter);
            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
            Bundle bundle = this.getArguments();
            if (bundle != null) {
                int i = bundle.getInt("ch", 0);
                TabLayout.Tab tab = tabLayout.getTabAt(i);
                tab.select();
            }

        } else {
            requestPermission();
        }
        return v;
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_CODE);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    Fragment fragment = new DebtManager();
                    FragmentTransaction ft = ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction();

                    if (accepted) {
                        ft.replace(R.id.content_frame, fragment);
                        ft.commit();
                    } else {
                        Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                                showMessage("Debt Manager feature requires permission to access your contacts.\n\nPlease allow the permission to use this feature.",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Fragment fragment = new DashBoard();
                                                if (fragment != null) {
                                                    FragmentTransaction ft = ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction();
                                                    ft.replace(R.id.content_frame, fragment);
                                                    ft.commit();
                                                }
                                            }
                                        }, "OK");
                                return;
                            } else {
                                showMessageOkCancel("The Debt Manager feature requires permission to access your contacts. Please allow the permission to use this feature.\n\nYou can click on the Settings button below or manually navigate to the App Settings and allow this permission. ",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    final Handler handler = new Handler();

                                                    Runnable checkSettingOn = new Runnable() {

                                                        @Override
                                                        public void run() {
                                                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                                                                return;
                                                            }
                                                            if (checkPermission()) {
                                                                Intent i = new Intent(getActivity(), MainActivity.class);
                                                                i.putExtra("frgToLoad", 1);
                                                                startActivity(i);
                                                                opt=1;
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
                                            public void onClick(DialogInterface dialog, int which) {

                                                Fragment fragment = new DashBoard();
                                                if (fragment != null) {
                                                    FragmentTransaction ft = ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction();
                                                    ft.replace(R.id.content_frame, fragment);
                                                    ft.commit();
                                                }
                                            }
                                        }, "Settings");
                                return;
                            }
                        }

                    }
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (resultCode == RESULT_CANCELED) {
                Log.d("TESTINGOFF","XYZER");
                PackageManager packageManager = getContext().getPackageManager();
                Intent intent = packageManager.getLaunchIntentForPackage(getContext().getPackageName());
                ComponentName componentName = intent.getComponent();
                Intent mainIntent = IntentCompat.makeRestartActivityTask(componentName);
                getContext().startActivity(mainIntent);
                System.exit(0);
            }
        }
    }

    private void showMessage(String message, DialogInterface.OnClickListener okListener, String btn) {
        new AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .setTitle("PERMISSION REQUIRED")
                .setMessage(message)
                .setPositiveButton(btn, okListener)
                .create()
                .show();
    }

    private void showMessageOkCancel(String message, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener, String btn) {
        new AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .setTitle("PERMISSION REQUIRED")
                .setMessage(message)
                .setPositiveButton(btn, okListener)
                .setNegativeButton("Cancel", cancelListener)
                .create()
                .show();
    }
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Debt Manager");
    }
}
