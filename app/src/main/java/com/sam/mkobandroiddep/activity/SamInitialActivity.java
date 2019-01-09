package com.sam.mkobandroiddep.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.sam.mkobandroiddep.singleappmode.R;

public class SamInitialActivity extends AppCompatActivity {

    AppCompatTextView tvTimer;
    SharedPreferences prefs;
    private int count = 0;
    private long startMillis=0;
    String strSelectAppPackage="";
    private static final String TAG = "SamInitialActivity";

    String password="";
    private boolean isCanceled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sam_exit);

        tvTimer             = findViewById(R.id.tv_time);
        prefs               = getApplicationContext().getSharedPreferences("SAMPref", MODE_PRIVATE);

    }

    private void otpTimerStart(){
        new CountDownTimer(12000, 1000) {

            public void onTick(long millisUntilFinished) {

                if (isCanceled){
                    cancel();
                }else {
                    tvTimer.setText("SAM is initializing for : " + millisUntilFinished / 1000+" Seconds");
                }

            }

            public void onFinish() {

                prefs = getApplicationContext().getSharedPreferences("SAMPref", MODE_PRIVATE);
                strSelectAppPackage = prefs.getString("SelectAppPackage", "");
                boolean saMactive = prefs.getBoolean("SAMactive", false);

                Log.d(TAG, "onClick:Active " + strSelectAppPackage + "SamActive " + saMactive);

                if (saMactive) {
                    Intent intent = getPackageManager().getLaunchIntentForPackage(strSelectAppPackage);
                    if (intent != null) {
                        SharedPreferences.Editor editor1 = getSharedPreferences("SAMPref", MODE_PRIVATE).edit();
                        editor1.putBoolean("SamStart", true);
                        editor1.apply();
                        try {
//                            stopLockTask();
                            Toast.makeText(SamInitialActivity.this, "SAM Active", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        Toast.makeText(SamInitialActivity.this, " Launch Error.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
//                        stopLockTask();
                        finish();
                        Intent intent = new Intent(SamInitialActivity.this, SamSettingActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int eventaction = event.getAction();
        if (eventaction == MotionEvent.ACTION_UP) {

            //get system current milliseconds
            long time= System.currentTimeMillis();

            //if it is the first time, or if it has been more than 3 seconds since the first tap ( so it is like a new try), we reset everything
            if (startMillis==0 || (time-startMillis> 5000) ) {
                startMillis=time;
                count=1;
            }
            //it is not the first, and it has been  less than 3 seconds since the first
            else{ //  time-startMillis< 3000
                count++;
            }
            if (count==5) {

                exitDialog();

            }
            return true;
        }
        return false;
    }

    public void exitDialog() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        final View View = layoutInflaterAndroid.inflate(R.layout.dialog_sign_out, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
        alertDialogBuilderUserInput.setView(View);

        final AppCompatEditText etOldPass   = (AppCompatEditText) View.findViewById(R.id.et_old_pass);
        AppCompatButton btnSave             = (AppCompatButton) View.findViewById(R.id.btn_save);

        final AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.setCancelable(false);
        alertDialogAndroid.show();

        etOldPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                Log.d(TAG, "onTextChanged: "+charSequence+" "+i+" "+i1+" "+i2);

                if (i==3){

                    String string = charSequence.toString();

                    alertDialogAndroid.cancel();
                    hideKeyboard(SamInitialActivity.this,View);
                    exitFromSam(string,alertDialogAndroid);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    void exitFromSam(String oldPass,AlertDialog alertDialog){
            alertDialog.cancel();
            prefs               = getApplicationContext().getSharedPreferences("SAMPref", MODE_PRIVATE);
            password            = prefs.getString("password", null);

            if (oldPass.equals(password)){

                try {
                    isCanceled=true;
//                    stopLockTask();
                    SharedPreferences.Editor editor1 = getSharedPreferences("SAMPref", MODE_PRIVATE).edit();
                    editor1.putBoolean("SamStart", false);
                    editor1.apply();
                    finish();
                    Intent intent=new Intent(SamInitialActivity.this,SamSettingActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }else {
                Toast.makeText(this, "Password not match.", Toast.LENGTH_SHORT).show();
            }
    }

    public void hideKeyboard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isCanceled=false;
        otpTimerStart();
//        startLockTask();
    }

    @Override
    public void onBackPressed() {

    }
}
