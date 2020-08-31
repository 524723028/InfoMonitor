package com.infomonitor.infocollector.Thread;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.infomonitor.MyDBHelper;
import com.infomonitor.Utils;
import com.infomonitor.infocollector.InfoCollectorService;

/**
 * Created by Administrator on 2018/2/3.
 */
public abstract class InfoCollectorThread extends Thread {
    protected boolean isRunning;
    private int interval = 5000;
    protected InfoCollectorService service;
    protected int batteryLevel = -1;
    protected String table_name;
    ContentValues values;

    protected Utils utils;

    MyDBHelper dbHelper;
    SQLiteDatabase db;

    public InfoCollectorThread(int interval, InfoCollectorService service) {
        isRunning = true;
        this.interval = interval;
        this.service = service;
        utils = Utils.getInstance();
    }

    @Override
    public void run() {
        super.run();
        while (isRunning) {
            dbHelper = new MyDBHelper(service);
            db = dbHelper.getReadableDatabase();
            //跟电量获取有关系
            IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            service.registerReceiver(batteryReceiver,
                    intentFilter);

            if(writedata()){
                Cursor cursor = db.query(table_name, null, null, null, null, null, null);
                int count = cursor.getCount();
                System.out.println("数据库中的记录数量：" + cursor.getCount());
                if (count > 19) {
                    //删除表中的数据，表还是要保留的（官方推荐方法）
                    db.delete(table_name, null, null);
                    //设置id从1开始（sqlite默认id从1开始），若没有这一句，id将会延续删除之前的id
                    db.execSQL("update sqlite_sequence set seq=0 where name=" + "'" + table_name + "'");
                }
                db.insert(table_name, null, values);
            }

            try {
                db.close();
                sleep(interval*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public void stopThread()
    {
        isRunning=false;

    }

    protected abstract boolean writedata();

    BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub
            if (Intent.ACTION_BATTERY_CHANGED.equals(arg1.getAction())) {
                // 获取当前电量
                batteryLevel = arg1.getIntExtra("level", 0);
                service.unregisterReceiver(batteryReceiver);
            }
        }
    };

}
