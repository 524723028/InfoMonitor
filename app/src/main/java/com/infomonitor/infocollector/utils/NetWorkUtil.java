package com.infomonitor.infocollector.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

/**跟网络带宽有关的工具类
 * Created by Administrator on 2018/1/25.
 */
public class NetWorkUtil {
    private final static String TAG = "NetworkUtil";
    /**得到当前网络类型
     * @param context 上下文
     * @return 返回当前网络
     */
    public static String getCurrentNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if(cm == null) {
            Log.w(TAG, "ConnectivityManager is null");
            return NetConstant.NT_NONE;
        }

        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if(networkInfo != null) {
            int type = networkInfo.getType();
            int subtype = networkInfo.getSubtype();
            return isConnectionFast(type, subtype);
        }else {
            Log.w(TAG, "networkInfo is null");
            return NetConstant.NT_NONE;
        }
    }

    private static String isConnectionFast(int type, int subtype) {
        if (type == ConnectivityManager.TYPE_WIFI) {
            return NetConstant.NT_WIFI;
        }else if (type == ConnectivityManager.TYPE_MOBILE) {
            switch (subtype) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return NetConstant.NT1_2G;// ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return NetConstant.NT2_2G; // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return NetConstant.NT3_2G; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return NetConstant.NT4_2G; // ~ 100 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return NetConstant.NT1_3G; // ~25 kbps
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return NetConstant.NT_4G; // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return NetConstant.NT2_3G; // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return NetConstant.NT3_3G; // ~ 2-14 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return NetConstant.NT4_3G; // ~ 700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return NetConstant.NT5_3G; // ~ 1-23 Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return NetConstant.NT6_3G; // ~ 400-7000 kbps
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                    return NetConstant.NT7_3G; // ~ 1-2 Mbps
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    return NetConstant.NT8_3G; // ~ 5 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return NetConstant.NT9_3G; // ~ 10-20 Mbps
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return NetConstant.NT5_2G; // ~ 10+ Mbps
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                    default:
                        return NetConstant.NT_NONE;
            }
        }else {
            return NetConstant.NT_NONE;
        }
    }
}
