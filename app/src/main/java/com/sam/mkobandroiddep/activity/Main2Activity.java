package com.sam.mkobandroiddep.activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.sam.mkobandroiddep.singleappmode.R;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {

    private static final String TAG = "Main2Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        ListView userInstalledApps          = (ListView)findViewById(R.id.installed_app_list);
        final List<AppList> installedApps   = getInstalledApps();
        AppAdapter installedAppAdapter      = new AppAdapter(Main2Activity.this, installedApps);

        userInstalledApps.setAdapter(installedAppAdapter);
        userInstalledApps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String selectedItem = installedApps.get(position).getPackagename();
                final String selectedApp  = installedApps.get(position).getName();
                Log.d(TAG, "getView: "+selectedItem+" "+selectedApp);

                SharedPreferences.Editor editor = getSharedPreferences("SAMPref", MODE_PRIVATE).edit();
                editor.putString("SelectApp", selectedApp);
                editor.putString("SelectAppPackage", selectedItem);
                editor.apply();
                finish();

//                 Get the intent to launch the specified application
//                Intent intent = getPackageManager().getLaunchIntentForPackage(selectedItem);
//                if(intent != null){
//                    startActivity(intent);
//                }else {
//                    Toast.makeText(Main2Activity.this, " Launch Error.",Toast.LENGTH_SHORT).show();
//                }

            }
        });
    }

    private List<AppList> getInstalledApps() {
        List<AppList> res = new ArrayList<AppList>();
        List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if ((isSystemPackage(p) == false)) {
                String appName = p.applicationInfo.loadLabel(getPackageManager()).toString();
                Drawable icon = p.applicationInfo.loadIcon(getPackageManager());
                String packagename = p.applicationInfo.packageName;
                Log.d(TAG, "getInstalledApps: "+packagename);
                res.add(new AppList(appName, icon,packagename));
            }
        }
        return res;
    }

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true : false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences.Editor editor = getSharedPreferences("SAMPref", MODE_PRIVATE).edit();
        editor.putString("SelectApp", "No app is selected");
        editor.apply();
        finish();
    }
}
