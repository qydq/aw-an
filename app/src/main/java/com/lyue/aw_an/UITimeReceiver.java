package com.lyue.aw_an;

/**********************************************************
 * @文件名称：UITimeReceiver
 * @文件作者：staryumou@163.com
 * @创建时间：2016/10/10
 * @文件描述：null
 * @修改历史：2016/10/10
 **********************************************************/

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class UITimeReceiver extends BroadcastReceiver {

    private String stringTime;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(MainActivity.TIME_CHANGED_ACTION)) {
            Bundle bundle = intent.getExtras();
            stringTime = intent.getStringExtra("time");
//            System.out.println(getClass().getName() + "---qydq--time变化--" + stringTime);
            MainActivity.getTime().setText(stringTime);
            MainActivity.getmProgress().setMessage(stringTime);
        }
    }
}
