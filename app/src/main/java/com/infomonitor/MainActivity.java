package com.infomonitor;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.TabHost;

import com.infomonitor.infocollector.InfoCollectorActivity;
import com.infomonitor.inforeader.InfoReaderActivity;

public class MainActivity extends TabActivity {
    TabHost tabHost;
    Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        utils = Utils.getInstance();
    }

    private void initUI() {
        tabHost = this.getTabHost();
        TabHost.TabSpec spec;
        Intent intent;
        //前面的InfoCollector跟页面布局有关，后面的InfoCollector是一个标签
        intent = new Intent().setClass(this, InfoCollectorActivity.class);
        spec = tabHost.newTabSpec("InfoCollector").setIndicator("InfoCollector")
                .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, InfoReaderActivity.class);
        spec = tabHost.newTabSpec("InfoReader").setIndicator("InfoReader")
                .setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);
        RadioGroup radioGroup = (RadioGroup) this.findViewById(R.id.main_tab_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged (RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.main_tab_infocollector:
                        tabHost.setCurrentTabByTag("InfoCollector");
                        break;
                    case R.id.main_tab_inforeader:
                        tabHost.setCurrentTabByTag("InfoReader");
                        break;
                    default:
                        break;
                }

            }
        });

    }
}
