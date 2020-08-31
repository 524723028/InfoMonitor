package com.infomonitor.infocollector.Thread;

import android.content.ContentValues;
import android.net.TrafficStats;

import com.infomonitor.infocollector.InfoCollectorService;
import com.infomonitor.infocollector.utils.NetWorkUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Administrator on 2018/2/3.
 */
public class NetinfoThread extends InfoCollectorThread {
    private long nowTotalRxBytes = 0;
    public NetinfoThread(int interval, InfoCollectorService service) {
        super(interval, service);
    }

    @Override
    protected boolean writedata() {
        //获取网络连接类型及数据网带宽
        String NetType = NetWorkUtil.getCurrentNetwork(service);
        // 获取当前数据总量
        getTotalRxBytes();
        //long nowTotalRxBytes = getTotalRxBytes();
        System.out.println("网络类型为：" + NetType);

        /*
        //获取当前网速
        long nowTimeStamp = System.currentTimeMillis();// 当前时间
        // kb/s
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp == lastTimeStamp ? nowTimeStamp : nowTimeStamp
                - lastTimeStamp));// 毫秒转换
        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;
        */

        String times = utils.getTime();
        ContentValues cv = new ContentValues();
        //cv.put("_id", dbHelper.Get_New_ID(db, "BandwidthInfo"));
        cv.put("time", times);
        cv.put("NetType", NetType);
        cv.put("TotalRxBytes", String.valueOf(nowTotalRxBytes) + "B");
        //cv.put("NetSpeed", String.valueOf(speed) + "KB/s");
        values = cv;
        table_name = dbHelper.TABLE_NETINFO_INFO;
        return true;
    }


    //获取当前数据总量
    public void getTotalRxBytes() {
        // 得到整个手机的流量值
        //return TrafficStats.getUidRxBytes(getActivity().getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 :
        //        (TrafficStats.getTotalRxBytes()/1024);//转为KB

        //获取总的接收和发送的字节数，包括Mobile和Wifi等
        nowTotalRxBytes =  TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes();

        // 获取某个网络UID接收和发送字节的总和,即当前进程
        int uID = service.getApplicationInfo().uid;
        //nowTotalRxBytes =  TrafficStats.getUidRxBytes(uID) + TrafficStats.getUidTxBytes(uID);
        System.out.println("看看流量值："+ nowTotalRxBytes);
        if (nowTotalRxBytes == 0 || (TrafficStats.getUidRxBytes(uID) == -1) && (TrafficStats.getUidTxBytes(uID) == -1)) {
            nowTotalRxBytes = getTotalBytesManual(uID);
        }
    }
    /**
     * 通过uid查询文件夹中的数据
     * @param localUid
     * @return
     */
    private Long getTotalBytesManual(int localUid) {
//        Log.e("BytesManual*****", "localUid:" + localUid);
        File dir = new File("/proc/uid_stat/");
        String[] children = dir.list();
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < children.length; i++) {
            stringBuffer.append(children[i]);
            stringBuffer.append("   ");
        }
//        Log.e("children*****", children.length + "");
//        Log.e("children22*****", stringBuffer.toString());
        if (!Arrays.asList(children).contains(String.valueOf(localUid))) {
            return 0L;
        }
        File uidFileDir = new File("/proc/uid_stat/" + String.valueOf(localUid));
        File uidActualFileReceived = new File(uidFileDir, "tcp_rcv");
        File uidActualFileSent = new File(uidFileDir, "tcp_snd");
        String textReceived = "0";
        String textSent = "0";
        try {
            BufferedReader brReceived = new BufferedReader(new FileReader(uidActualFileReceived));
            BufferedReader brSent = new BufferedReader(new FileReader(uidActualFileSent));
            String receivedLine;
            String sentLine;

            if ((receivedLine = brReceived.readLine()) != null) {
                textReceived = receivedLine;
//                Log.e("receivedLine*****", "receivedLine:" + receivedLine);
            }
            if ((sentLine = brSent.readLine()) != null) {
                textSent = sentLine;
//                Log.e("sentLine*****", "sentLine:" + sentLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
//            Log.e("IOException*****", e.toString());
        }
//        Log.e("BytesManualEnd*****", "localUid:" + localUid);
        return Long.valueOf(textReceived).longValue() + Long.valueOf(textSent).longValue();
    }

}
