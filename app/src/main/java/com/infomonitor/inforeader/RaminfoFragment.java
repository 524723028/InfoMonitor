package com.infomonitor.inforeader;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.infomonitor.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Administrator on 2018/3/19.
 */
public class RaminfoFragment extends FragmentBase {
    String line,free,cached,memTotal,ramSize,ramFreeSize,ramUsedSize;
    String mtime,  mRamTotalSize, mRamUsedSize, mRamFreeSize, mRamAverageFree;
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.raminfo, container, false);
        getInfoBtn = (Button) view.findViewById(R.id.get_raminfo);
        textView = (TextView) view.findViewById(R.id.raminfoTv);
        getInfoBtn.setOnClickListener(this);
        sb.append("RAM: \r\n");

        return view;
    }

    @Override
    protected void getInfo() {
        GetRamData();
        info=utils.getTime()+"\r\n"
                +"总运行内存:"+mRamTotalSize+"\r\n"
                +"已使用运行内存:"+mRamUsedSize+"\r\n"
                +"剩余运行内存:"+mRamFreeSize+"\r\n"
                +"运行内存使用率:"+mRamAverageFree+"\r\n"+"\r\n";

    }

    private void GetRamData() {
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

            mRamAverageFree= utils.parseDoubletoPer(memUsed/total);

            mtime = utils.getTime();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
