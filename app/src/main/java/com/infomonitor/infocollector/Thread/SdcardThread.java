package com.infomonitor.infocollector.Thread;

import android.content.ContentValues;
import android.os.Environment;

import com.infomonitor.infocollector.InfoCollectorService;

import java.io.File;

/**
 * Created by Administrator on 2018/2/3.
 */
public class SdcardThread extends InfoCollectorThread {
    String mtime, mSdTotalSize, mSdFreeSize;
    public SdcardThread(int interval, InfoCollectorService service) {
        super(interval, service );
    }

    public void getSdcardSize() {
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {//
            File pathFile = Environment.getExternalStorageDirectory();

            android.os.StatFs statfs = new android.os.StatFs(pathFile.getPath());

            long nTotalBlocks = statfs.getBlockCount();

            long nBlocSize = statfs.getBlockSize();

            long nAvailaBlock = statfs.getAvailableBlocks();

            long nFreeBlock = statfs.getFreeBlocks();

            // 计算SDCard 总容量大小MB
            long nSDTotalSize = nTotalBlocks * nBlocSize / 1024 / 1024;

            // 计算 SDCard 剩余大小MB
            long nSDFreeSize = nAvailaBlock * nBlocSize / 1024 / 1024;

            String str="";
            if(nSDFreeSize>1024)
                mSdFreeSize = nSDFreeSize/1024 + "." + nSDFreeSize%1024/10 + "GB";
            else
                mSdFreeSize = nSDFreeSize + "MB";
            if(nSDTotalSize>1024)
                mSdTotalSize = nSDTotalSize/1024 + "." + nSDTotalSize%1024/10 + "GB";
            else
                mSdTotalSize = nSDTotalSize + "MB";

        }else {
            System.out.println("sdcard unmounted");
        }
    }

    @Override
    protected boolean writedata() {
        getSdcardSize();

        mtime = utils.getTime();

        ContentValues cv = new ContentValues();
        cv.put("time", mtime);
        cv.put("SdTotalSize", mSdTotalSize);
        cv.put("SdFreeSize", mSdFreeSize);
        values = cv;
        table_name = dbHelper.TABLE_SDCARD_INFO;
        return true;
    }
}
