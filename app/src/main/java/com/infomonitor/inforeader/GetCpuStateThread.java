package com.infomonitor.inforeader;

import com.infomonitor.Utils;

/**
 * Created by Administrator on 2018/2/5.
 */
public class GetCpuStateThread extends Thread {
    private String cpuName,cpuState;
    private Utils utils;

    public GetCpuStateThread(String cpuName)
    {
        this.cpuName=cpuName;
        utils=Utils.getInstance();
    }
    @Override
    public void run() {
        // TODO Auto-generated method stub
        super.run();
        if(cpuName.equals("cpu"))
            cpuState="型号："+ utils.getCpuVersion()+"\r\n"+"使用率："+utils.parseDoubletoPer(utils.getCpuUsagePerNum(cpuName))+"\r\n"+"最大频率："+utils.getCpuFreqStr(utils.getCpuMaxFreq())
                    +" 最小频率："+utils.getCpuFreqStr(utils.getCpuMinFreq())+"\r\n";
        else
            cpuState="使用率："+utils.parseDoubletoPer(utils.getCpuUsagePerNum(cpuName))+" 频率："+utils.getCpuFreqStr(utils.getCpuFreq(cpuName))+"\r\n";
    }
    public String getCpuState() {
        return this.cpuState;
    }

}
