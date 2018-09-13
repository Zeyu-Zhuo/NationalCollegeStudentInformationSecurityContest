package com.jiacyer.attackclient.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 *  Created by Jiacy-PC on 2018/4/29.
 */

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("BootReceiver", "Start receive broadcast.");
        Intent intent1 = new Intent(context, DetectService.class);
        context.startService(intent1);
        Log.i("BootReceiver", "End receive broadcast.");
    }
}
