package com.infomonitor.inforeader;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.infomonitor.R;

/**
 * Created by Administrator on 2018/2/4.
 */
public class CpuinfoFragment extends FragmentBase {
    String cpuinfo="";
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cpuinfo, container, false);
        getInfoBtn = (Button) view.findViewById(R.id.get_cpuinfo);
        textView = (TextView) view.findViewById(R.id.cpuinfoTv);
        getInfoBtn.setOnClickListener(this);
        Log.d(TAG, "Cpu onCreateView");
        sb.append("CPU: \r\n");

        return view;
    }
    @Override
    protected void getInfo() {
        try {
            GetCpuStateThread getCpuStateThread =new GetCpuStateThread("cpu");
            GetCpuStateThread getCpuStateThread1=new GetCpuStateThread("cpu0");
            GetCpuStateThread getCpuStateThread2=new GetCpuStateThread("cpu1");
            GetCpuStateThread getCpuStateThread3=new GetCpuStateThread("cpu2");
            GetCpuStateThread getCpuStateThread4=new GetCpuStateThread("cpu3");
            getCpuStateThread.start();
            getCpuStateThread1.start();
            getCpuStateThread2.start();
            getCpuStateThread3.start();
            getCpuStateThread4.start();
            getCpuStateThread.join();
            getCpuStateThread1.join();
            getCpuStateThread2.join();
            getCpuStateThread3.join();
            getCpuStateThread4.join();
            cpuinfo+="CPU "+getCpuStateThread.getCpuState();
            cpuinfo+="CPU 1 "+getCpuStateThread1.getCpuState();
            cpuinfo+="CPU 2 "+getCpuStateThread2.getCpuState();
            cpuinfo+="CPU 3 "+getCpuStateThread3.getCpuState();
            cpuinfo+="CPU 4 "+getCpuStateThread4.getCpuState();
            info=utils.getTime()+"\r\n"+cpuinfo+"\r\n";
            cpuinfo="";
            //sendMessage(2, "");
        }catch(Exception e)
        {
            e.printStackTrace();
        }

    }
}
