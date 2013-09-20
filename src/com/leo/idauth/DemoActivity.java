package com.leo.idauth;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabWidget;

public class DemoActivity extends TabActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final TabHost tabHost = getTabHost();

        tabHost.addTab(tabHost.newTabSpec("Fingerprint Demo")
                .setIndicator("指纹demo")
                .setContent(new Intent(this, FingerprintDemo.class)));

        tabHost.addTab(tabHost.newTabSpec("IDCard Demo")
                .setIndicator("身份证demo")
                .setContent(new Intent(this, IDCardDemo.class)));

        tabHost.addTab(tabHost.newTabSpec("Qqc Demo").setIndicator("二维码demo")
                .setContent(new Intent(this, QqcDemo.class)));

        final TabWidget tabWidget = tabHost.getTabWidget();
        for (int i = 0; i < tabWidget.getChildCount(); i++) {
            tabWidget.getChildAt(i).getLayoutParams().height = 40;
        }
        
        
    }
}