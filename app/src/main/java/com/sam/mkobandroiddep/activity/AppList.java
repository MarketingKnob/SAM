package com.sam.mkobandroiddep.activity;

import android.graphics.drawable.Drawable;

public class AppList {

    private String name;
    private String packagename;
    Drawable icon;

    public AppList(String name, Drawable icon, String packagename) {
        this.name = name;
        this.icon = icon;
        this.packagename = packagename;
    }

    public String getName() {
        return name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getPackagename(){
        return packagename;
    }
}
