package com.infomonitor.inforeader;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.infomonitor.R;
import com.infomonitor.infocollector.utils.NetWorkUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2018/2/4.
 */
public class NetinfoFragment extends FragmentBase {
    private long nowTotalRxBytes = 0;
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.netinfo, container, false);
        getInfoBtn = (Button) view.findViewById(R.id.get_netinfo);
        textView = (TextView) view.findViewById(R.id.netinfoTv);
        getInfoBtn.setOnClickListener(this);
        sb.append("NetInfo: \r\n");

        return view;
    }

    @Override
    protected void getInfo() {
        //获取网络连接类型
        String NetType = NetWorkUtil.getCurrentNetwork(getActivity());
        // 获取当前数据总量
        //long nowTotalRxBytes =getTotalRxBytes() ;
        getTotalRxBytes();

        info=utils.getTime()+"\r\n"
        +"网络连接类型："+NetType+"\r\n"
        +"当前总流量："+String.valueOf(nowTotalRxBytes) + "B"+"\r\n\r\n";
        //+"网速："+String.valueOf(speed) + "KB/s"+"\r\n"+"\r\n";

    }
    //获取当前数据总量
    public void getTotalRxBytes() {
        // 得到整个手机的流量值
        //return TrafficStats.getUidRxBytes(getActivity().getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 :
        //        (TrafficStats.getTotalRxBytes()/1024);//转为KB

        //获取总的接收和发送的字节数，包括Mobile和Wifi等
        nowTotalRxBytes =  TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes();
        // 获取某个网络UID接收和发送字节的总和,即当前进程
        int uID = getActivity().getApplicationInfo().uid;
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
