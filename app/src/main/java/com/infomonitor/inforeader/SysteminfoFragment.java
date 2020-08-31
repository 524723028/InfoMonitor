package com.infomonitor.inforeader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.infomonitor.R;

/**
 * Created by Administrator on 2018/3/19.
 */
public class SysteminfoFragment extends FragmentBase {
    int batteryLevel=-1;
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.systeminfo, container, false);
        getInfoBtn = (Button) view.findViewById(R.id.get_systeminfo);
        textView = (TextView) view.findViewById(R.id.systeminfoTv);
        getInfoBtn.setOnClickListener(this);
        sb.append("SystemInfo: \r\n");

        return view;
    }
    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        Log.d(TAG, "onstart bat");
        //自定义完广播接收器后，还需在需要接受广播的类中注册广播
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        getActivity().registerReceiver(batteryReceiver,
                intentFilter);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.d(TAG, "ondestroy bat");
        //因为是动态注册广播，所以不要忘了在生命周期的onDestroy()方法中取消注册广播
        getActivity().unregisterReceiver(batteryReceiver);
    }
//自定义广播接收器，用来处理广播中获得电量的逻辑代码
    BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub
            System.out.println("get");
            if (Intent.ACTION_BATTERY_CHANGED.equals(arg1.getAction())) {
                // 获取当前电量
                batteryLevel = arg1.getIntExtra("level", 0);
                Log.d(TAG, "battery changed level = "+batteryLevel);
            }
        }
    };

    /**
     * 获取手机IMEI号
     */
    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
        //String imei = telephonyManager.SubscriberId();

        return imei;
    }

    @Override
    protected void getInfo() {
        info=utils.getTime()+"\r\n"
                +"手机型号："+android.os.Build.MODEL+"\r\n"
                +"手机IMEI号："+getIMEI(getActivity())+"\r\n"
                //+"Android版本号："+utils.getAndroidVersion()+"\r\n"
                +"Android版本号："+android.os.Build.VERSION.RELEASE+"\r\n"
                +"电量："+batteryLevel+"\r\n"+"\r\n";
        System.out.println("手机IMEI号："+getIMEI(getActivity()));

    }
}
