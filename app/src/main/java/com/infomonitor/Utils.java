package com.infomonitor;

import android.util.Log;

import com.infomonitor.infocollector.utils.ServiceObserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018/2/2.
 */
public class Utils {
    public static final String DEBUG_TAG="InfoMonitor";
    public static final boolean DEBUG=true;
    private static Utils utils;
    private static String time="";
    private static SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");

    private ServiceObserver observer;

    public static Utils getInstance() {
        if (utils == null)
            utils = new Utils();
        return utils;
    }

    //两种不同格式的获取当前时间的方法
    public String getTime() {
        time =simpleDateFormat.format(new Date());
        return time;
    }

    public String getCurTime() {
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMddHHmmss");
        String curTime =simpleDateFormat.format(new Date());
        return curTime;
    }

    //与界面显示有关的方法
    public void registerObserver(ServiceObserver observer) {
        this.observer = observer;
        log("register observer");
    }
    public void unregisterObserver() {
        this.observer=null;
        log("unregister observer");
    }
    public void notifyUpdate(int time) {
        if (observer!=null) {
            observer.onUpdate(time);
        }
    }

    public static void log(String content) {
        if(DEBUG)
            Log.d(DEBUG_TAG, content);
    }

    //获取Android版本号
    public String getAndroidVersion() {
        return getProp("ro.build.version.release");
    }

    public String getProp(String prop) {
        String value = "";
        try {
            Process p = Runtime.getRuntime().exec("getprop "+prop);
            BufferedReader br=new BufferedReader(new InputStreamReader(p.getInputStream ()));
            value=br.readLine();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return value.trim();
    }



    /**
     * 和获取CPU信息有关
     * @return
     */

    //获取CPU型号
    public String getCpuVersion() {
        String str1 = "/proc/cpuinfo";
        String str2 = "";
        String CpuVersion = "";
        String[] arrayOfString;
        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            for (int i = 2; i < arrayOfString.length; i++) {
                CpuVersion = CpuVersion + arrayOfString[i] + " ";
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return CpuVersion;
    }

    //获取cpu的内核数
    public int getNumCores() {
        //Private Class to display only CPU devices in the directory listing
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                //Check if filename is "cpu", followed by a single digit number
                if(Pattern.matches("cpu[0-9]", pathname.getName())) {
                    return true;
                }
                return false;
            }
        }

        try {
            //Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            //Filter to only list the devices we care about
            File[] files = dir.listFiles(new CpuFilter());
            Log.d(DEBUG_TAG, "CPU Count: "+files.length);
            //Return the number of cores (virtual CPU devices)
            return files.length;
        } catch(Exception e) {
            //Print exception
            Log.d(DEBUG_TAG, "CPU Count: Failed.");
            e.printStackTrace();
            //Default to return 1 core
            return 1;
        }
    }

    //获取cpu的使用率，读取/proc/stat 所有CPU活动的信息来计算CPU使用率
    public String getCpuUsagePer(String cpuName)
    {
        double per=getCpuUsagePerNum(cpuName);
        if(per==0)
            return "offline";
        return parseDouble(per*100);
        //return String.valueOf(per);
    }

    public double getCpuUsagePerNum(String cpuName)
    {
        int [] time1=getCpuUsage(cpuName);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int [] time2=getCpuUsage(cpuName);
        int totaltime=time2[0]-time1[0];
        int idle=time2[1]-time1[1];
        if(totaltime==0)
            return 0;
        Log.d(DEBUG_TAG,"totaltime "+totaltime+" idle "+idle);
        double per=1-(double)idle/(totaltime*1.0);
        return per;
    }
    public int[] getCpuUsage(String cpuName)
    {
        int total=0,idle=0;
        String line="";
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(new File("/proc/stat")));
            while((line=reader.readLine())!=null)
            {
                String []cpus=line.split(" ");
                if(cpus[0].trim().equals(cpuName))
                {
                    for (int i = 2; i < cpus.length; i++) {
                        //if(DEBUG)
//  	                	Log.d(DEBUG_TAG,"cpus "+i+"   "+cpus[i].trim());
                        total+=Integer.parseInt(cpus[i].trim());
                    }
                    if(cpuName.equals("cpu"))
                        idle=Integer.parseInt(cpus[5].trim());
                    else
                        idle=Integer.parseInt(cpus[4].trim());
//  	                if(DEBUG)
//  	                Log.d(DEBUG_TAG,"total "+total+" idle "+idle);
                    break;
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int times[] = {0,0};
        times[0]=total;
        times[1]=idle;
        return times;
    }

    //获取cpu的频率


    public String getCpuFreqStr(String cpuFreq)
    {
        if(cpuFreq.equals("-"))
            return cpuFreq;
        long freq=Integer.parseInt(cpuFreq);
        if(freq>1000)
        {

            if(freq>1000000)
            {
                double f=(double)freq;
                return parseDouble(f/1000000)+"GHz";
            }
            else
                return String.valueOf(freq/1000)+"MHz";
        }
        return freq+"KHz";

    }
    public String getCpuFreq(String cpuName) {
        String cpuFreq="";
        BufferedReader reader;
        File file =new File("/sys/devices/system/cpu/"+cpuName+"/cpufreq/scaling_cur_freq");
        if(!file.exists())
            return "-";
        try {
            reader = new BufferedReader(new FileReader(file));
            cpuFreq=reader.readLine();
            System.out.println("cpuFreq "+cpuFreq);
            reader.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return cpuFreq;

    }

    public String getCpuMaxFreq()
    {
        String cpuFreq="";
        BufferedReader reader;
        File file =new File("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq");
        if(!file.exists())
            return "-";
        try {
            reader = new BufferedReader(new FileReader(file));
            cpuFreq=reader.readLine();
            reader.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return cpuFreq;
    }

    public String getCpuMinFreq()
    {
        String cpuFreq="";
        BufferedReader reader;
        File file =new File("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq");
        if(!file.exists())
            return "-";
        try {
            reader = new BufferedReader(new FileReader(file));
            cpuFreq=reader.readLine();
            reader.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return cpuFreq;
    }



    //设置百分比的表现形式
    public String parseDoubletoPer(double num) {
        DecimalFormat df = new DecimalFormat("0.00%");
        String numStr= df.format(num);
        return numStr;
    }

    public String parseDouble(double num) {
        DecimalFormat df = new DecimalFormat("#.00");
        String numStr= df.format(num);
        return numStr;
    }


    //显示上传数据成功
    public void ShowSendSuccess(String successMsg) {
        if (observer!=null) {
            observer.ShowSendSuccess(successMsg);
        }
    }
    //显示传送数据失败
    public void ShowSendFail(String failCode, String failMsg) {
        if (observer!=null) {
            observer.ShowSendFail(failCode,failMsg );
        }
    }
    //显示请求发送失败
    public void RequestSendFail() {
        if (observer!=null) {
            observer.RequestSendFail();
        }
    }
    //显示请求接收失败
    public void RequestReceiveFail() {
        if (observer!=null) {
            observer.RequestReceiveFail();
        }
    }



}
