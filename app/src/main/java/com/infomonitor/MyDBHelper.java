package com.infomonitor;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2018/1/20.
 */
public class MyDBHelper extends SQLiteOpenHelper {

    Context context;
    public MyDBHelper(Context context) {
        super(context, MyDBHelper.DB_name, null, 3);
        this.context = context;
    }
    public final String ID="_id";

    //数据库名字
    static String DB_name="MY.db";

    //系统信息表
    public String TABLE_SYSTEM_INFO = "SystemInfo";
    public String SYSTEM_TIME = "time";
    public String PHONE_MODEL = "PhoneModel";
    public String PHONE_IMEI = "PhoneImei";
    public String ANDROID_VERSION = "AndroidVersion";
    public String BATTERY = "battery";


    public final String create_table_system_info = "create table " + TABLE_SYSTEM_INFO + "(" + ID + " integer primary key autoincrement,"
            + SYSTEM_TIME + " text,"
            + PHONE_MODEL + " text,"
            + PHONE_IMEI + " text,"
            + ANDROID_VERSION + " text,"
            + BATTERY + " text" + ")";

    //cpu信息表
    public String TABLE_CPU_INFO = "cpuInfo";
    public String CPU_TIME = "time";
    public String CPU_VERSION = "CpuVersion";
    public String CPU_NUM_CORES = "CpuNumCore";
    public String CPU_USAGE_PER = "CpuUsagePer";
    public String CPU_MAX_FREQ = "CpuMaxFreq";
    public String CPU_MIN_FREQ = "CpuMinFreq";

    public final String create_table_cpu_info = "create table " + TABLE_CPU_INFO + "(" + ID + " integer primary key autoincrement,"
            + CPU_TIME + " text,"
            + CPU_VERSION + " text,"
            + CPU_NUM_CORES + " text,"
            + CPU_USAGE_PER + " text,"
            + CPU_MAX_FREQ + " text,"
            + CPU_MIN_FREQ + " text" + ")";

    //手机内存信息表
    public String TABLE_SDCARD_INFO = "sdcardInfo";
    public String SDCARD_TIME = "time";
    public String SDCARD_TOTAL_SIZE = "SdTotalSize";
    public String SDCARD_FREE_SIAE = "SdFreeSize";

    public final String create_table_sdcard_info = "create table " + TABLE_SDCARD_INFO + "(" + ID + " integer primary key autoincrement,"
            + SDCARD_TIME + " text,"
            + SDCARD_TOTAL_SIZE + " text,"
            + SDCARD_FREE_SIAE + " text" + ")";

    //手机运行内存信息表
    public String TABLE_RAM_INFO = "ramInfo";
    public String RAM_TIME = "time";
    public String RAM_TOTAL_SIZE = "RamTotalSize";
    public String RAM_USED_SIZE = "RamUsedSize";
    public String RAM_FREE_SIZE = "RamFreeSize";
    public String RAM_AVERAGE_FREE = "RamAverageUsed";

    public final String create_table_ram_info = "create table " + TABLE_RAM_INFO + "(" + ID + " integer primary key autoincrement,"
            + RAM_TIME + " text,"
            + RAM_TOTAL_SIZE + " text,"
            + RAM_USED_SIZE + " text,"
            + RAM_FREE_SIZE + " text,"
            + RAM_AVERAGE_FREE + " text" + ")";

    //GPS信息表
    public String TABLE_GPS_INFO = "GpsInfo";
    public String GPS_TIME = "time";
    public String GPS_LONGITUDE = "GpsLongitude";
    public String GPS_LATITUDE = "GpsLatitude";

    public final String create_table_gps_info = "create table " + TABLE_GPS_INFO + "(" + ID + " integer primary key autoincrement,"
            + GPS_TIME + " text,"
            + GPS_LONGITUDE + " text,"
            + GPS_LATITUDE + " text" + ")";

    //带宽和当前流量信息表
    public String TABLE_NETINFO_INFO = "NetInfo";
    public String NETINFO_TIME = "time";
    public String NETTYPE = "NetType";
    public String TOTAL_RX_BYTES = "TotalRxBytes";
    //public String NET_SPEED = "NetSpeed";

    public final String create_table_net_info = "create table " + TABLE_NETINFO_INFO + "(" + ID + " integer primary key autoincrement,"
            + NETINFO_TIME + " text,"
            + NETTYPE + " text,"
            + TOTAL_RX_BYTES + " text" + ")";



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //创建数据库中的表格
        sqLiteDatabase.execSQL(create_table_system_info);
        sqLiteDatabase.execSQL(create_table_cpu_info);
        sqLiteDatabase.execSQL(create_table_sdcard_info);
        sqLiteDatabase.execSQL(create_table_ram_info);
        sqLiteDatabase.execSQL(create_table_gps_info);
        sqLiteDatabase.execSQL(create_table_net_info);
        System.out.println("数据库创建成功了");

    }

    /**
     * 用于保存新数据时，设置ID的值，防止ID重复出现
     * 返回值为整型
     * **/
/*
    public int Get_New_ID(SQLiteDatabase db, String table_name){
        int result=1;
        Cursor cursor=db.rawQuery("Select max(_id) as _id from "+table_name,null);
        if(cursor!=null){
            cursor.moveToNext();
            result=cursor.getInt(cursor.getColumnIndex(ID))+1;
        }
        return result;
    }
*/
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        System.out.println("数据库升级成功了");

    }
}
