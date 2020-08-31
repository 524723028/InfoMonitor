package com.infomonitor.infocollector.Thread;

import android.content.ContentValues;
import android.util.Log;

import com.infomonitor.infocollector.InfoCollectorService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Administrator on 2018/3/19.
 */
public class RaminfoThread extends InfoCollectorThread {
    String line,free,cached,memTotal,ramSize,ramFreeSize,ramUsedSize;
    String mtime,  mRamTotalSize, mRamUsedSize, mRamFreeSize, mRamAverageUsed;
    public static final String TAG = "InfoCollector";

    public RaminfoThread(int interval, InfoCollectorService service) {
        super(interval, service );
    }

    @Override
    protected boolean writedata() {
        GetMemData();

        ContentValues cv = new ContentValues();
        //cv.put("_id", dbHelper.Get_New_ID(db, "memInfo"));
        cv.put("time", mtime);
        cv.put("RamTotalSize", mRamTotalSize);
        cv.put("RamUsedSize", mRamUsedSize);
        cv.put("RamFreeSize", mRamFreeSize);
        cv.put("RamAverageUsed", mRamAverageUsed);
        values = cv;
        table_name = dbHelper.TABLE_RAM_INFO;
        return true;

    }

    private void GetMemData() {
        Process p;
        //StringBuffer sb=new StringBuffer();
        try {
            p = Runtime.getRuntime().exec("cat /proc/meminfo");
            BufferedReader br=new BufferedReader(new InputStreamReader(p.getInputStream ()));
            while((line=br.readLine())!=null) {
                if(line.trim().length()<1){
                    continue;
                }else if(line.contains("MemFree"))
                {
                    System.out.println(line);
                    free=line.split(":")[1].split("kB")[0].trim();
                    System.out.println(free);
                }
                else if (line.contains("Cached"))
                {
                    System.out.println(line);
                    cached=line.split(":")[1].split("kB")[0].trim();
                    System.out.println(cached);
                    break;
                }else if (line.contains("MemTotal"))
                {
                    System.out.println(line);
                    memTotal=line.split(":")[1].split("kB")[0].trim();
                    System.out.println(memTotal);
                }
            }
            //MemFreeSize
            double memFree=Double.parseDouble(free)+Double.parseDouble(cached);
            memFree = memFree/1024;
            ramFreeSize = utils.parseDouble(memFree);
            //MemTotalSize
            double total = Double.parseDouble(memTotal)/1024;
            Log.d(TAG, "free "+memFree+" total "+total);
            ramSize = utils.parseDouble(total);
            //MemUsedSize
            double memUsed = total - memFree;
            ramUsedSize = utils.parseDouble(memUsed);

            if(total>1024)
                mRamTotalSize = utils.parseDouble(total/1024.0) + "GB";
            else
                mRamTotalSize = ramSize + "MB";

            if(memFree > 1024)
                mRamFreeSize = utils.parseDouble(memFree/1024.0) + "GB";
            else
                mRamFreeSize = ramFreeSize + "MB";

            if(memUsed > 1024)
                mRamUsedSize = utils.parseDouble(memUsed/1024.0) + "GB";
            else
                mRamUsedSize = ramUsedSize + "MB";

            mRamAverageUsed= utils.parseDouble((memUsed/total)*100);

            mtime = utils.getTime();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
