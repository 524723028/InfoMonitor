package com.infomonitor.infocollector;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.infomonitor.R;
import com.infomonitor.Utils;
import com.infomonitor.infocollector.utils.ServiceObserver;
import com.infomonitor.myServlet.utils.DialogUtil;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by Administrator on 2018/2/2.
 */
public class InfoCollectorActivity extends AppCompatActivity implements ServiceObserver {
    private int interval = 10000;
    private Utils utils;
    public Context context;
    public static int isServiceRunning = 0;
    private TextView statusView;
    private EditText et_interval;
    CheckBox systeminfo, cpuinfo,sdcardinfo,raminfo,netinfo, gpsinfo;

    private LocationManager lm;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.infocollector_activity);
        utils = Utils.getInstance();
        initUI();
        context = this;
        //用来请求允许定位权限
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    private void initUI() {
        statusView = (TextView) (this.findViewById(R.id.textView3));
        et_interval = (EditText)(this.findViewById(R.id.editText1));

        systeminfo = (CheckBox) this.findViewById(R.id.systeminfoCheckBox);
        cpuinfo = (CheckBox) this.findViewById(R.id.cpuinfoCheckBox);
        sdcardinfo = (CheckBox) this.findViewById(R.id.sdcardCheckBox);
        raminfo = (CheckBox) this.findViewById(R.id.raminfoCheckBox);
        gpsinfo = (CheckBox) this.findViewById(R.id.GPSCheckBox);
        netinfo = (CheckBox) this.findViewById(R.id.netinfoCheckBox);
    }

    private void StartDump() {
        //这里完成了files目录下的logcollector的内容
        interval = Integer.parseInt(et_interval.getText().toString());
        try {
            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(context.openFileOutput("logcollector.cfg", Context.MODE_WORLD_WRITEABLE)));
            output.write("START-TIME " + utils.getTime() + ",");
            output.write("INTERVAL " + interval + ",");
            output.write("SYSTEMINFO-ENABLE " + systeminfo.isChecked() + ",");
            output.write("CPUINFO-ENABLE " + cpuinfo.isChecked() + ",");
            output.write("SDCARDINFO-ENABLE " + sdcardinfo.isChecked() + ",");
            output.write("RAMINFO-ENABLE " + raminfo.isChecked() + ",");
            output.write("GPSINFO-ENABLE " + gpsinfo.isChecked() + ",");
            output.write("NETINFO-ENABLE " + netinfo.isChecked() + ",");

            output.flush();
            output.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent i = new Intent(InfoCollectorActivity.this,
                InfoCollectorService.class);
        i.putExtra("isMonite", true);
        Log.d("LogCollector",
                "activity.StartDump : "
                        + "systeminfo>" + systeminfo.isChecked()
                        + "cpuinfo>" + cpuinfo.isChecked()
                        + "sdcardinfo>" +sdcardinfo.isChecked()
                        + "raminfo>" + raminfo.isChecked()
                        + "gpsinfo>" + gpsinfo.isChecked()
                        + "netinfo>" + netinfo.isChecked());

        startService(i);
    }

    private void StopDump() {
        Intent i = new Intent(InfoCollectorActivity.this,
                InfoCollectorService.class);
        stopService(i);
    }

    private boolean isItemCkecked() {
        if(systeminfo.isChecked()||cpuinfo.isChecked()||sdcardinfo.isChecked() ||raminfo.isChecked() ||gpsinfo.isChecked() ||netinfo.isChecked()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.set, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.subitem1:
                if(!isItemCkecked()) {
                    Toast.makeText(this, "Please choose info type", Toast.LENGTH_LONG).show();
                    isServiceRunning=0;
                    return false;
                }
                //需要获取GPS信息就需要打开GPS
                //判断GPS是否正常启动
                if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) && gpsinfo.isChecked()) {
                    Toast.makeText(this, "请开启定位...", Toast.LENGTH_SHORT).show();

                    //返回开启GPS设置界面
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent, 0);
                    return false;


                }

                Toast.makeText(this, "Start Monitor.....", Toast.LENGTH_LONG).show();
                StartDump();
                statusView.setText("Start Monitor.....");
                isServiceRunning=1;
                return true;

            case R.id.subitem2:
                Toast.makeText(this, "Stop Monitor", Toast.LENGTH_LONG).show();
                StopDump();
                statusView.setText("Stop Monitor(Data can not be sent to server)");
                isServiceRunning=0;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    @Override
    public void onUpdate(int time) {
        // TODO Auto-generated method stub
        statusView.setText("service is running" + time + "mins");
        isServiceRunning = 1;

    }

    @Override
    public void ShowSendSuccess(String successMsg) {
        System.out.println("到底传过来的文字是什么："+ successMsg);
        final AlertDialog alert = new AlertDialog.Builder(InfoCollectorActivity.this).create();
        alert.setTitle("温馨提示：");
        if (successMsg.equals("上传数据成功")) {
            alert.setMessage("上传数据成功");
        } else {
            alert.setMessage("更新数据成功");
        }

        alert.show();
        //实现弹框的2s自动消失
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                alert.dismiss();
            }
        }, 2000);

    }

    @Override
    public void ShowSendFail(String failCode, String failMsg) {
        DialogUtil.showHintDialog(InfoCollectorActivity.this, true, "传送数据失败", failCode + " : " + failMsg, "关闭对话框", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.dismissDialog();
            }
        });

    }

    @Override
    public void RequestSendFail() {
        DialogUtil.showHintDialog(InfoCollectorActivity.this, "请求发送失败，请重试", false);

    }

    @Override
    public void RequestReceiveFail() {
        DialogUtil.showHintDialog(InfoCollectorActivity.this, "请求接受失败，请重试", false);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        initListener();
        super.onResume();
    }

    private void initListener() {
        Utils utils = Utils.getInstance();
        if(utils!=null)
            utils.registerObserver(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
