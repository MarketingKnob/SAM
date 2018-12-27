package com.sam.mkobandroiddep.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.sam.mkobandroiddep.singleappmode.R;

public class SamSettingActivity extends AppCompatActivity implements View.OnClickListener {

    AppCompatTextView tvSelectSam,tvChangePin,tvActivateSam,tvExit;
    public TextView tvAppName;
    View mView;
    SharedPreferences prefs;
    boolean userFirstLock=false;
    String password="",strSelectedApp="",strSelectAppPackage="";

    private int count = 0;
    private long startMillis=0;
    private static final String TAG = "SamSettingActivity";
    boolean verifyBoolean=false,SAMactiveBoolean=false,SAMPassVerifyBoolean=false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sam_setting);

        tvAppName           = (TextView)findViewById(R.id.tv_app_name);
        tvSelectSam         = findViewById(R.id.tv_select_app);
        tvChangePin         = findViewById(R.id.tv_change_pin);
        tvActivateSam       = findViewById(R.id.tv_activate_sam);
        tvExit              = findViewById(R.id.tv_exit_sam);

        prefs               = getApplicationContext().getSharedPreferences("SAMPref", MODE_PRIVATE);
        userFirstLock       = prefs.getBoolean("Lock", false);  // getting boolean
        verifyBoolean       = prefs.getBoolean("verified", false);  // getting boolean
        SAMactiveBoolean    = prefs.getBoolean("SAMactive", false);  // getting boolean
        password            = prefs.getString("password", null);

        Log.d(TAG, "onCreate: "+verifyBoolean+SAMactiveBoolean);

        tvSelectSam.setOnClickListener(this);
        tvChangePin.setOnClickListener(this);
        tvActivateSam.setOnClickListener(this);
        tvExit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if (v==tvSelectSam){
            startActivity(new Intent(SamSettingActivity.this,Main2Activity.class));
//            stopLockTask();
        }else if (v==tvChangePin){
            showChangePassDialog();
        }else if (v==tvActivateSam){

            if (strSelectedApp.equalsIgnoreCase("No app is selected")){
                Toast.makeText(this, "No App Selected", Toast.LENGTH_SHORT).show();
            }else {
//                startLockTask();
                SharedPreferences.Editor editor = getSharedPreferences("SAMPref", MODE_PRIVATE).edit();
                editor.putBoolean("SAMactive", true);
                editor.apply();
                strSelectAppPackage            = prefs.getString("SelectAppPackage", "");
                boolean s                       = prefs.getBoolean("SAMactive", false);

                Log.d(TAG, "onClick:Active "+strSelectAppPackage+"SamActive "+s);
                Intent intent = getPackageManager().getLaunchIntentForPackage(strSelectAppPackage);
                if(intent != null){

                    SharedPreferences.Editor editor1 = getSharedPreferences("SAMPref", MODE_PRIVATE).edit();
                    editor1.putBoolean("SamStart", true);
                    editor1.apply();

                    startActivity(intent);
                    Toast.makeText(this, strSelectedApp+" is Activate", Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(SamSettingActivity.this, " Launch Error.",Toast.LENGTH_SHORT).show();
                }
            }


        } else if (v == tvExit) {
            SharedPreferences.Editor editor1 = getSharedPreferences("SAMPref", MODE_PRIVATE).edit();
            editor1.putBoolean("SAMactive", false);
            editor1.putBoolean("SamStart", false);
            editor1.putBoolean("SAMPassVerify", false);
            editor1.apply();
            SamSettingActivity.this.finish();
        }
    }

    /*Dialog for Change Password*/
    public void showChangePassDialog() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        mView = layoutInflaterAndroid.inflate(R.layout.dialog_conf_pass, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
        alertDialogBuilderUserInput.setView(mView);

        final AppCompatEditText etOldPass   = (AppCompatEditText) mView.findViewById(R.id.et_old_pass);
        final AppCompatEditText etNewPass   = (AppCompatEditText) mView.findViewById(R.id.et_new_pass);
        final AppCompatEditText etConfPass  = (AppCompatEditText) mView.findViewById(R.id.et_conf_pass);
        AppCompatButton btnSave             = (AppCompatButton) mView.findViewById(R.id.btn_save);
        AppCompatButton btnCancel           = (AppCompatButton) mView.findViewById(R.id.btn_cancel);

        final AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.setCancelable(false);
        alertDialogAndroid.show();


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String strOldPass   = etOldPass.getText().toString();
                String strNewPass   = etNewPass.getText().toString();
                String strConfPass  = etConfPass.getText().toString();

                changePass(strOldPass, strNewPass, strConfPass, alertDialogAndroid);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogAndroid.cancel();
            }
        });

    }

    /*Change Password SAM*/
    void changePass(String oldPass,String newPass,String confPass,AlertDialog alertDialog){
        if (!userFirstLock){
            Toast.makeText(this, "Please insert password before change.", Toast.LENGTH_LONG).show();
            alertDialog.cancel();
            hideKeyboard(this,mView);
        }
        else {
            password            = prefs.getString("password", null);
            if (password.equals(oldPass)){
                if (!newPass.equals("")&&newPass.length()==4) {
                    if (newPass.equals(confPass)) {
                        SharedPreferences.Editor editor = getSharedPreferences("SAMPref", MODE_PRIVATE).edit();
                        editor.putString("password", newPass);
                        editor.putBoolean("Lock", true);
                        editor.apply();

                        hideKeyboard(this,mView);
                        alertDialog.cancel();

                        Toast.makeText(this, "Password change.", Toast.LENGTH_SHORT).show();

                    }else {
                        Toast.makeText(this, "Password not same.", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(this, "Please enter 4 digit pin.", Toast.LENGTH_LONG).show();
                }

            }
            else {
                Toast.makeText(this, "Please enter old password correctly.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void hideKeyboard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        prefs               = getApplicationContext().getSharedPreferences("SAMPref", MODE_PRIVATE);
        SAMactiveBoolean    = prefs.getBoolean("SAMactive", false);
        Log.d(TAG, "onBackPressed: "+SAMactiveBoolean);

//        if (SAMactiveBoolean) {
//            startLockTask();
//        } else {
//            super.onBackPressed();
//            finish();
//        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        prefs               = getApplicationContext().getSharedPreferences("SAMPref", MODE_PRIVATE);
        strSelectedApp      = prefs.getString("SelectApp", "No app is selected");
        verifyBoolean       = prefs.getBoolean("verified", false);
        SAMactiveBoolean    = prefs.getBoolean("SAMactive", false);
        SAMPassVerifyBoolean= prefs.getBoolean("SAMPassVerify", false);

        Log.d(TAG, "onCreate: "+strSelectedApp+" "+verifyBoolean+SAMactiveBoolean);
        tvAppName.setText("Single App = "+strSelectedApp);

        boolean booleanSamStart=false;
        booleanSamStart   = prefs.getBoolean("SamStart", false);
        Log.d(TAG, "onCreateSamStrt: "+booleanSamStart);

        if (booleanSamStart){
            Toast.makeText(this, "Sam Starts", Toast.LENGTH_SHORT).show();
            strSelectAppPackage            = prefs.getString("SelectAppPackage", "");
            Intent activityIntent = new Intent(this, SamInitialActivity.class);
            activityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(activityIntent);
//            Intent intent = getPackageManager().getLaunchIntentForPackage(strSelectAppPackage);
//            if(intent != null){
//                startActivity(intent);
//            }else {
//                Toast.makeText(SamSettingActivity.this, " Launch Error.",Toast.LENGTH_SHORT).show();
//            }
        }else {
//            Toast.makeText(this, "Sam Not Starts", Toast.LENGTH_SHORT).show();
        }

//        if (SAMactiveBoolean) {
//            startLockTask();
//        }  else {
//
//        }

    }

}
