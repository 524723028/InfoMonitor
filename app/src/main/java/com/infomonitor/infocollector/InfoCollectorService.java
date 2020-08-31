package com.infomonitor.infocollector;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.infomonitor.MyDBHelper;
import com.infomonitor.Utils;
import com.infomonitor.infocollector.Thread.CpuinfoThread;
import com.infomonitor.infocollector.Thread.GpsinfoThread;
import com.infomonitor.infocollector.Thread.InfoCollectorThread;
import com.infomonitor.infocollector.Thread.NetinfoThread;
import com.infomonitor.infocollector.Thread.RaminfoThread;
import com.infomonitor.infocollector.Thread.SdcardThread;
import com.infomonitor.infocollector.Thread.SysteminfoThread;
import com.infomonitor.myServlet.CommonRequest;
import com.infomonitor.myServlet.Constant;
import com.infomonitor.myServlet.HttpPostTask;
import com.infomonitor.myServlet.utils.ResponseHandler;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Created by Administrator on 2018/2/3.
 */
public class InfoCollectorService extends Service {
    InfoCollectorThread systeminfoThread;
    InfoCollectorThread cpuinfoThread;
    InfoCollectorThread sdcardThread;
    InfoCollectorThread raminfoThread;
    InfoCollectorThread gpsinfoThread;
    InfoCollectorThread netinfoThread;

    UpdateStatusThread updateStatusThread;
    SendDataTread sendDataTread;

    HashMap<String, String> map = new HashMap<String, String>();

    InfoCollectorService service;
    Utils utils;

    //与传送数据有关
    private MyDBHelper dbHelper;
    private SQLiteDatabase db;

    private String mPhoneModel, mPhoneImei, mAndroidVersion,mbattery;
    private String mtime, mCpuVersion, mCpuNumCore, mCpuUsagePer, mCpuMaxFreq, mCpuMinFreq;
    private String mSdTotalSize, mSdFreeSize;
    private String mRamTotalSize, mRamUsedSize, mRamFreeSize, mRamAverageUsed;
    private String mGpsLongitude, mGpsLatitude;
    private String mNetType, mTotalRxBytes ;

    private String URL_SEND_DATA = "http://192.168.1.102:8080/InfoMonitor_web/infomoniter_servlet";
    //String MobileName = "小米";

    private void init() {
        Log.d(Utils.DEBUG_TAG, "LogCollectorService.init()");
        //通知
        Notification notification = new Notification();
        startForeground(1, notification);
        utils = Utils.getInstance();

        service = this;

        dbHelper = new MyDBHelper(service);

        //把logcollector.cfg里面的信息进行了拆分
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    openFileInput("logcollector.cfg")));

            String line = "";
            String temp = "";
            while ((line = br.readLine()) != null) {
                temp = temp.concat(line);
            }
            String strtemp[] = temp.split(",");
            for (String cfg : strtemp) {
                String keyValue[] = cfg.split(" ");
                map.put(keyValue[0], keyValue[1]);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startDump() {
        updateStatusThread = new UpdateStatusThread(handler);
        updateStatusThread.start();

        //开始传送数据的线程
        sendDataTread = new SendDataTread(handler2);
        sendDataTread.start();

        //Systeminfo线程
        if (map.get("SYSTEMINFO-ENABLE").equals("true")) {
            systeminfoThread = new SysteminfoThread(Integer.parseInt(map
                    .get("INTERVAL")), service);
            systeminfoThread.start();
        }

        // Cpuinfo线程
        if (map.get("CPUINFO-ENABLE").equals("true")) {
            cpuinfoThread = new CpuinfoThread(Integer.parseInt(map
                    .get("INTERVAL")), service);
            cpuinfoThread.start();
        }
        // Sdcard容量线程
        if (map.get("SDCARDINFO-ENABLE").equals("true")) {
            sdcardThread = new SdcardThread(Integer.parseInt(map
                    .get("INTERVAL")), service);
            sdcardThread.start();
        }

        //Ram线程
        if (map.get("RAMINFO-ENABLE").equals("true")) {
            raminfoThread = new RaminfoThread(Integer.parseInt(map
                    .get("INTERVAL")), service);
            raminfoThread.start();
        }
        //GPS线程
        if (map.get("GPSINFO-ENABLE").equals("true")) {
            gpsinfoThread = new GpsinfoThread(Integer.parseInt(map
                    .get("INTERVAL")), service);
            gpsinfoThread.start();
        }
        //netinfo线程
        if (map.get("NETINFO-ENABLE").equals("true")) {
            netinfoThread = new NetinfoThread(Integer.parseInt(map
                    .get("INTERVAL")), service);
            netinfoThread.start();
        }
    }

    //跟UpdateStatusThread有关的Handler
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    utils.log("notify update");
                    utils.notifyUpdate(msg.arg1);
                    break;
                default:
                    break;

            }
        }

    };

    //子线程通过Handler机制实现主线程的UI更新
    class UpdateStatusThread extends Thread {
        private boolean isRunning = false;
        private int count = 1;

        private Handler mHandler;
        public UpdateStatusThread(Handler handler) {
            isRunning=true;
            this.mHandler=handler;
        }
        @Override
        public void run() {
            super.run();
            while (isRunning) {
                notifyUpdate(count);
                count++;
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        public void notifyUpdate(int time) {
            Message msg = new Message();
            msg.what = 0;
            msg.arg1 = time;
            mHandler.sendMessage(msg);
        }

        public void stopThread() {
            isRunning = false;
            utils.unregisterObserver();
            Utils.log("stop status thread");
        }
    }

    //跟SendDataTread有关的Handler
    Handler handler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == Constant.HANDLER_HTTP_SEND_FAIL){
                utils.RequestSendFail();
            }else if (msg.what == Constant.HANDLER_HTTP_RECEIVE_FAIL) {
                utils.RequestReceiveFail();
            }
        }
    };


    //用来传送数据的子线程
    class SendDataTread extends Thread {
        private boolean isRunning = false;

        private Handler dHandler;
        public SendDataTread(Handler handler2) {
            isRunning = true;
            this.dHandler = handler2;
        }

        @Override
        public void run() {
            super.run();
            while (isRunning) {
                sendTestData();
                try {
                    db.close();
                    Thread.sleep(180000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void sendTestData() {
            db = dbHelper.getReadableDatabase();

            Cursor cursor = db.query("SystemInfo", new String[]{"PhoneModel","PhoneImei", "AndroidVersion", "battery"}, null, null, null, null, null);
            if (cursor.moveToLast()) {
                mPhoneModel = cursor.getString(cursor.getColumnIndex("PhoneModel"));
                mPhoneImei = cursor.getString(cursor.getColumnIndex("PhoneImei"));
                mAndroidVersion = cursor.getString(cursor.getColumnIndex("AndroidVersion"));
                mbattery = cursor.getString(cursor.getColumnIndex("battery"));
            }
            cursor.close();

            Cursor cursor1 = db.query("cpuInfo", new String[]{"time", "CpuVersion", "CpuNumCore",  "CpuUsagePer", "CpuMaxFreq", "CpuMinFreq"}, null, null, null,null, null);
            if (cursor1.moveToLast()) {
                mtime = cursor1.getString(cursor1.getColumnIndex("time"));
                mCpuVersion = cursor1.getString(cursor1.getColumnIndex("CpuVersion"));
                mCpuNumCore = cursor1.getString(cursor1.getColumnIndex("CpuNumCore"));
                mCpuUsagePer = cursor1.getString(cursor1.getColumnIndex("CpuUsagePer"));
                mCpuMaxFreq = cursor1.getString(cursor1.getColumnIndex("CpuMaxFreq"));
                mCpuMinFreq = cursor1.getString(cursor1.getColumnIndex("CpuMinFreq"));
            }
            cursor1.close();


            //db.close();

            Cursor cursor2 = db.query("sdcardInfo", new String[]{"SdTotalSize", "SdFreeSize"}, null, null, null, null, null);
            if (cursor2.moveToLast()) {
                mSdTotalSize = cursor2.getString(cursor2.getColumnIndex("SdTotalSize"));
                mSdFreeSize = cursor2.getString(cursor2.getColumnIndex("SdFreeSize"));
            }
            cursor2.close();

            Cursor cursor3 = db.query("ramInfo", new String[]{"RamTotalSize", "RamUsedSize", "RamFreeSize", "RamAverageUsed"}, null, null, null, null, null);
            if (cursor3.moveToLast()) {
                mRamTotalSize = cursor3.getString(cursor3.getColumnIndex("RamTotalSize"));
                mRamUsedSize = cursor3.getString(cursor3.getColumnIndex("RamUsedSize"));
                mRamFreeSize = cursor3.getString(cursor3.getColumnIndex("RamFreeSize"));
                mRamAverageUsed = cursor3.getString(cursor3.getColumnIndex("RamAverageUsed"));
            }
            cursor3.close();

            Cursor cursor4 = db.query("GpsInfo", new String[]{"GpsLongitude", "GpsLatitude"}, null, null, null, null, null);
            if (cursor4.moveToLast()) {
                mGpsLongitude = cursor4.getString(cursor4.getColumnIndex("GpsLongitude"));
                mGpsLatitude = cursor4.getString(cursor4.getColumnIndex("GpsLatitude"));
            }
            cursor4.close();

            Cursor cursor5 = db.query("NetInfo", new String[]{"NetType", "TotalRxBytes"}, null, null, null, null, null);
            if (cursor5.moveToLast()) {
                mNetType = cursor5.getString(cursor5.getColumnIndex("NetType"));
                mTotalRxBytes = cursor5.getString(cursor5.getColumnIndex("TotalRxBytes"));
                //mNetSpeed = cursor5.getString(cursor5.getColumnIndex("NetSpeed"));
            }
            cursor5.close();

            System.out.println("看看我查到了什么："+mCpuUsagePer+""+mbattery);
            if (mPhoneModel != null) {
                final CommonRequest request = new CommonRequest();
                request.addRequestParam("PhoneModel", mPhoneModel);
                //注意一下这个地方
                request.addRequestParam("PhoneImei", mPhoneImei);
                request.addRequestParam("time",mtime );
                request.addRequestParam("CpuVersion", mCpuVersion);
                request.addRequestParam("CpuNumCore", mCpuNumCore);
                request.addRequestParam("CpuUsagePer", mCpuUsagePer);
                request.addRequestParam("CpuMaxFreq", mCpuMaxFreq);
                request.addRequestParam("CpuMinFreq", mCpuMinFreq);

                request.addRequestParam("AndroidVersion", mAndroidVersion);
                request.addRequestParam("battery", mbattery);

                request.addRequestParam("SdTotalSize", mSdTotalSize);
                request.addRequestParam("SdFreeSize", mSdFreeSize);

                request.addRequestParam("RamTotalSize", mRamTotalSize);
                request.addRequestParam("RamUsedSize", mRamUsedSize);
                request.addRequestParam("RamFreeSize", mRamFreeSize);
                request.addRequestParam("RamAverageUsed", mRamAverageUsed);

                request.addRequestParam("GpsLongitude", mGpsLongitude);
                request.addRequestParam("GpsLatitude", mGpsLatitude);

                request.addRequestParam("NetType", mNetType);
                request.addRequestParam("TotalRxBytes", mTotalRxBytes);
                //request.addRequestParam("NetSpeed", "mNetSpeed");



                sendHttpPostRequest(URL_SEND_DATA, request, new ResponseHandler() {
                    @Override
                    public void success(String successMsg) {
                        utils.ShowSendSuccess(successMsg);
                    }

                    @Override
                    public void fail(String failCode, String failMsg) {
                        utils.ShowSendFail(failCode, failMsg);
                    }
                });
            }
        }


        protected void sendHttpPostRequest(String url, CommonRequest request, ResponseHandler responseHandler){
            new HttpPostTask(request,dHandler , responseHandler).execute(url);
            //if (showLoadingDialog) {
            //  utils.ShowLoadingDialog();
            // }
        }

        public void stopThread() {
            isRunning = false;
            //utils.unregisterObserver();
            Utils.log("stop sendData thread");
        }


    }




    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init();
        startDump();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d(Utils.DEBUG_TAG, "LogCollectorService.onDestroy()");
        if (systeminfoThread != null) {
            systeminfoThread.stopThread();
        }
        if (cpuinfoThread != null) {
            cpuinfoThread.stopThread();
        }
        if (sdcardThread != null) {
            sdcardThread.stopThread();
        }

        if (raminfoThread != null) {
            raminfoThread.stopThread();
        }
        if(gpsinfoThread!=null) {
            gpsinfoThread.stopThread();
        }
        if(netinfoThread!=null) {
            netinfoThread.stopThread();
        }
        if(updateStatusThread!=null) {
            updateStatusThread.stopThread();
        }
        if(sendDataTread != null) {
            sendDataTread.stopThread();
        }
        super.onDestroy();
    }
}
