package com.infomonitor.infocollector.utils;

/**
 * Created by Administrator on 2018/2/3.
 */
public interface ServiceObserver {
    public void onUpdate(int time);
    //显示传送/更新数据成功
    public void ShowSendSuccess(String successMsg);
    //显示传送数据成功
    public void ShowSendFail(String failCode, String failMsg);
    //显示请求发送失败
    public void RequestSendFail();
    //显示请求接收失败
    public void RequestReceiveFail();
}
