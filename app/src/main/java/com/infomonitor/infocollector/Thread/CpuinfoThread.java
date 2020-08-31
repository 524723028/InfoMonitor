package com.infomonitor.infocollector.Thread;

import android.content.ContentValues;

import com.infomonitor.infocollector.InfoCollectorService;

/**
 * Created by Administrator on 2018/2/3.
 */
public class CpuinfoThread extends InfoCollectorThread {
    public CpuinfoThread(int interval, InfoCollectorService service) {
        super(interval, service);
    }

    @Override
    protected boolean writedata() {
        String times = utils.getTime();
        ContentValues cv = new ContentValues();
        cv.put("time", times);
        cv.put("CpuVersion", utils.getCpuVersion());
        cv.put("CpuNumCore", utils.getNumCores());
        cv.put("CpuUsagePer", utils.getCpuUsagePer("cpu"));
        cv.put("CpuMaxFreq", utils.getCpuFreqStr(utils.getCpuMaxFreq()));
        cv.put("CpuMinFreq", utils.getCpuFreqStr(utils.getCpuMinFreq()));
        values = cv;
        table_name = dbHelper.TABLE_CPU_INFO;
        System.out.println("8888888888看到我：" + utils.getCpuVersion());
        return true;
    }
}
