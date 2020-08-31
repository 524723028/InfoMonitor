package com.infomonitor.infocollector.Thread;

import android.content.ContentValues;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.infomonitor.Utils;
import com.infomonitor.infocollector.InfoCollectorService;

/**
 * Created by Administrator on 2018/3/19.
 */
public class SysteminfoThread extends InfoCollectorThread {
    public SysteminfoThread(int interval, InfoCollectorService service) {
        super(interval, service);
    }

    /**
     * 获取手机IMEI号
     */
    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();

        return imei;
    }

    @Override
    protected boolean writedata() {
        for (int i = 0; i < 5; i++) {
            if(batteryLevel==-1) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
        if(batteryLevel==-1) {
            Log.e(Utils.DEBUG_TAG, "get batterylevel failed");
            return false;
        }

        String times = utils.getTime();
        //获取手机型号
        String mPhoneModel = android.os.Build.MODEL;
        String mImei = getIMEI(service);
        String mAndroidVersion = android.os.Build.VERSION.RELEASE;

        ContentValues cv = new ContentValues();
        cv.put("time", times);
        cv.put("PhoneModel", mPhoneModel);
        cv.put("PhoneImei", mImei );
        cv.put("AndroidVersion", mAndroidVersion);
        cv.put("battery", batteryLevel);
        values = cv;
        table_name = dbHelper.TABLE_SYSTEM_INFO;
        System.out.println("777777777看到我：" + mImei) ;
        return true;
    }
}
