package com.infomonitor.infocollector.Thread;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;

import com.infomonitor.infocollector.InfoCollectorService;

/**
 * Created by Administrator on 2018/2/3.
 */
public class GpsinfoThread extends InfoCollectorThread {
    private LocationManager lm;
    private static final String TAG = "GPSThread";
    private String Longitude, Latitude;


    public GpsinfoThread(int interval, InfoCollectorService service) {
        super(interval, service);
    }

    @Override
    protected boolean writedata() {
        set();

        String times = utils.getTime();
        ContentValues cv = new ContentValues();
        cv.put("time", times);
        cv.put("GpsLongitude", Longitude);
        cv.put("GpsLatitude", Latitude );
        values = cv;
        table_name = dbHelper.TABLE_GPS_INFO;
        return true;
    }

    private void set() {
        //获取定位管理器
        lm = (LocationManager) service.getSystemService(Context.LOCATION_SERVICE);
        //为获取地理位置信息时设置查询条件
        //如果不设置查询要求，getLastKnownLocaion方法传入的参数为LocationManager.GPS_PROVIDER
        String bestProvider = lm.getBestProvider(getCriteria(), true);
        //权限检查
        if (ActivityCompat.checkSelfPermission(service, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(service, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        //获取位置信息
        Location location = lm.getLastKnownLocation(bestProvider);
        //更新位置信息
        upLoadInfo(location);
        System.out.println("GPS结果：经度：" + Longitude + "纬度：" + Latitude);

    }

    private void upLoadInfo(Location location) {
        if (location != null) {
            Longitude = String.valueOf(location.getLongitude());
            Latitude = String.valueOf(location.getLatitude());
        }else {
            Longitude = "获取位置失败";
            Latitude = "获取位置失败";
        }
    }


    //返回查询条件
    private Criteria getCriteria()
    {
        Criteria criteria = new Criteria();
        //设置定位精确度
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        //设置是否要求速度
        criteria.setSpeedRequired(false);
        //设置是否允许运营商收费
        criteria.setCostAllowed(false);
        //设置是否需要方位信息
        criteria.setBearingAccuracy(0);
        //设置是否需要海拔信息
        criteria.setAltitudeRequired(false);
        //设置对电源的需求
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }
}
