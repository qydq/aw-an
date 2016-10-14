package com.lyue.aw_an;

/**********************************************************
 * @文件名称：MyService
 * @文件作者：staryumou@163.com
 * @创建时间：2016/10/10
 * @文件描述：null
 * @修改历史：2016/10/10
 **********************************************************/

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

public class MyService extends Service {
    private String TAG = "MyService";
    private Timer timer = null;
    private SimpleDateFormat sdf = null;
    private Intent timeIntent = null;
    private Bundle bundle = null;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        init();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                sendTimeChangedBroadcast();
            }
        }, 1000, 1000);
    }

    private void init() {
        timer = new Timer();
        sdf = new SimpleDateFormat("yyyy年MM月dd日 " + "hh:mm:ss");
        timeIntent = new Intent();
        bundle = new Bundle();
    }

    private String getTime() {
        return sdf.format(new Date());
    }

    private void sendTimeChangedBroadcast() {
        bundle.putString("time", getTime());
//        System.out.println(getClass().getName() + "---qydq--time变化--" + getTime());
        timeIntent.setAction(MainActivity.TIME_CHANGED_ACTION);
        timeIntent.putExtras(bundle);
        sendBroadcast(timeIntent);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);
    }
}

