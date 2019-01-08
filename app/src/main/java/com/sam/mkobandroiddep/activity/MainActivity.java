package com.sam.mkobandroiddep.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.sam.mkobandroiddep.BasicDeviceAdminReceiver;
import com.sam.mkobandroiddep.singleappmode.R;

import in.arjsna.passcodeview.PassCodeView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private PassCodeView passCodeView;
    AppCompatTextView promptView;
    SharedPreferences prefs;
    private static final String TAG = "LockingMainActivity";
    boolean userFirstLock=false;
    String password;
    static final int ACTIVATION_REQUEST = 1;
    ComponentName deviceAdminComponentName;
    boolean firstTimePass=true,SAMactiveBoolean=false,SAMConfLock=false;
    public static String strFirstPass="",strConfPass="";
    LinearLayoutCompat llMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        String name = BasicDeviceAdminReceiver.class.getName();
//        if(name.startsWith(BuildConfig.APPLICATION_ID)) {
//            name = name.substring(BuildConfig.APPLICATION_ID.length());
//        }
//        final String command = "dpm set-device-owner " + BuildConfig.APPLICATION_ID + '/' + name;
//
//
//        Log.d(TAG, "onCreate: "+command);
//
//        try {
//            Runtime.getRuntime().exec(command);
//        } catch (Exception e) {
//            Log.e(TAG, "device owner not set");
//            Log.e(TAG, e.toString());
//            e.printStackTrace();
//        }

        passCodeView        = (PassCodeView)        findViewById(R.id.pass_code_view);
        promptView          = (AppCompatTextView)   findViewById(R.id.promptview);
        llMain              = (LinearLayoutCompat)  findViewById(R.id.ll_main);

        llMain.setOnClickListener(this);

        prefs               = getApplicationContext().getSharedPreferences("SAMPref", MODE_PRIVATE);
        userFirstLock       = prefs.getBoolean("Lock", false);  // getting boolean
        password            = prefs.getString("password", null);
        SAMactiveBoolean    = prefs.getBoolean("SAMactive", false);
        SAMConfLock         = prefs.getBoolean("ConfLock", false);

        Log.d(TAG, "onCreateSAMActive: "+SAMactiveBoolean);

        if (!userFirstLock){
            promptView.setText(getText(R.string.enter_pass));
            startLockTask();
        }
        else {
//            startLockTask();
            promptView.setText(getText(R.string.verify_pass));
            if (SAMConfLock){

                try {
//                    stopLockTask();
                    Intent intent= new Intent(MainActivity.this,SamSettingActivity.class);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finish();
            }else {

            }


        }

        Log.d(TAG, "onCreate: "+userFirstLock+" "+password);
        Typeface typeFace   = Typeface.createFromAsset(this.getAssets(), "fonts/Raleway-ExtraLight.ttf");

        passCodeView.setTypeFace(typeFace);
        passCodeView.setKeyTextColor(R.color.black_shade);
        passCodeView.setEmptyDrawable(R.drawable.empty_dot);
        passCodeView.setFilledDrawable(R.drawable.filled_dot);
        promptView.setTypeface(typeFace);

        bindEvents();
    }

    @Override
    protected void onPostCreate(Bundle bundle) {
        super.onPostCreate(bundle);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

//        provisionOwner();
    }

    private void provisionOwner() {
        DevicePolicyManager manager =
                (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        deviceAdminComponentName = new ComponentName(this,BasicDeviceAdminReceiver.class);

        if(!manager.isAdminActive(deviceAdminComponentName)) {
            Intent intent=new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,deviceAdminComponentName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"You Should Enable the app!");
            startActivityForResult(intent, ACTIVATION_REQUEST);
            return;
        }
        else {
//            Toast.makeText(this, "You are already Admin", Toast.LENGTH_SHORT).show();
            String mPackageName = this.getPackageName();

            try{
                if (manager.isDeviceOwnerApp(mPackageName)) {
                    manager.setLockTaskPackages(deviceAdminComponentName, new String[]{mPackageName});
                }
                else {
                    Toast.makeText(this, "You are Not Admin", Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception e){
                Log.d(TAG, "provisionOwner: "+e);
            }

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (manager.isDeviceOwnerApp(getPackageName()))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    manager.setLockTaskPackages(deviceAdminComponentName, new String [] {getPackageName()});
                }
        }
    }

    private void bindEvents() {
        passCodeView.setOnTextChangeListener(new PassCodeView.TextChangeListener() {
            @Override
            public void onTextChanged(String text) {
                if (text.length() == 4) {
                    verifyCredentials(text);
                }
            }
        });
    }

    void verifyCredentials(String text) {
        prefs                           = getApplicationContext().getSharedPreferences("SAMPref", MODE_PRIVATE);
        userFirstLock                   = prefs.getBoolean("Lock", false);  // getting boolean
        password                        = prefs.getString("password", null);

        SharedPreferences.Editor editor = getSharedPreferences("SAMPref", MODE_PRIVATE).edit();

        if (!userFirstLock) {
            if (firstTimePass) {
                strFirstPass = text;
                firstTimePass = false;
                passCodeView.reset();
                promptView.setText("Confirm Password");
            } else {
                promptView.setText("Confirm Password");
                strConfPass = text;
                if (strFirstPass.equals(strConfPass)) {

                    editor.putString("password", text);
                    editor.putBoolean("Lock", true);
                    editor.putBoolean("ConfLock", true);
                    editor.apply();
//                    Toast.makeText(MainActivity.this, "Your device is password enabled by SAM.", Toast.LENGTH_LONG).show();


                    Intent intent= null;
                    try {
                        stopLockTask();
                        intent = new Intent(MainActivity.this,SamSettingActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }



                } else {
                    passCodeView.setError(true);
                    Toast.makeText(this, "Password not same.", Toast.LENGTH_SHORT).show();
                }

            }
        } else {
            if (text.equals(password)) {
//                Toast.makeText(this, "SAM Deactivated.", Toast.LENGTH_SHORT).show();

                SharedPreferences.Editor editor1 = getSharedPreferences("SAMPref", MODE_PRIVATE).edit();
                editor1.putBoolean("verified", true);
//                editor1.putBoolean("SAMactive", false);
                editor1.apply();

                Intent intent= null;
                try {
                    stopLockTask();
                    intent = new Intent(MainActivity.this,SamSettingActivity.class);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }

//

            } else {
                passCodeView.setError(true);
                Toast.makeText(MainActivity.this, "Wrong password.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onPause() {
        super.onPause();

        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.moveTaskToFront(getTaskId(), 0);

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTIVATION_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    Log.i("MainActivity", "Administration enabled!");

                    DevicePolicyManager myDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
                    String mPackageName = this.getPackageName();

                    if (myDevicePolicyManager.isDeviceOwnerApp(mPackageName)) {
                        myDevicePolicyManager.setLockTaskPackages(deviceAdminComponentName, new String[]{mPackageName});
                    }

                    if (myDevicePolicyManager.isLockTaskPermitted(mPackageName)) {
                        startLockTask();
                    }
                } else {
                    Log.i("MainActivity", "Administration enable FAILED!");
                    finish();
                }

                return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        if (v==llMain){
            hideKeyboard(this,llMain);
        }
    }

    public void hideKeyboard(Activity activity,View view) {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
