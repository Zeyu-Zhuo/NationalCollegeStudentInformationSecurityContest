package com.jiacyer.newpaydemo.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 *  Created by Jiacy-PC on 2018/5/29.
 */

public class ProtectUtil {

    public static String getForegroundApp() {
        return getForegroundAppAction(null);
    }

    public static String getForegroundApp(String phoneMaker) {
        return getForegroundAppAction(phoneMaker);
    }

    public static boolean isRunningForeground(String appName, String phoneMaker) {
        String foregroundProcessName = getForegroundAppAction(phoneMaker);
//        Log.i("TopActivity", foregroundProcessName);
        return foregroundProcessName!=null && appName.equals(foregroundProcessName);
    }

    public static boolean isRunningForeground(String appName) {
        String foregroundProcessName = getForegroundAppAction(null);
        return foregroundProcessName!=null && appName.equals(foregroundProcessName);
    }

    /***********************************************************************************************
    /** first app user */
    private static final int AID_APP = 10000;
    /** offset for uid ranges for each user */
    private static final int AID_USER = 100000;
    private static String getForegroundAppAction(String phoneMaker) {
        File[] files = new File("/proc").listFiles();
        int lowestOomScore = Integer.MAX_VALUE;
        String foregroundProcess = null;
        for (File file : files) {
            if (!file.isDirectory()) {
                continue;
            }
            int pid;
            try {
                pid = Integer.parseInt(file.getName());
            } catch (NumberFormatException e) {
                continue;
            }
            try {
                String cgroup = read(String.format("/proc/%d/cgroup", pid));
                String[] lines = cgroup.split("\n");
                String cpuSubsystem;
                String cpuaccctSubsystem;

//                System.out.println("lines length:"+lines.length);
                if (lines.length == 2) {//有的手机里cgroup包含2行或者3行，我们取cpu和cpuacct两行数据
                    cpuSubsystem = lines[0];
                    cpuaccctSubsystem = lines[1];
                }else if(lines.length==3){
                    cpuSubsystem = lines[0];
                    cpuaccctSubsystem = lines[2];
                } else if(lines.length == 4){
                    cpuSubsystem = lines[0];
                    cpuaccctSubsystem = lines[3];
                } else if(lines.length == 5){
                    cpuSubsystem = lines[0];
                    cpuaccctSubsystem = lines[4];
                } else {
                    continue;
                }
                if (!cpuaccctSubsystem.endsWith(Integer.toString(pid))) {
                    // not an application process
                    continue;
                }
                if (cpuSubsystem.endsWith("bg_non_interactive")) {
                    // background policy
                    continue;
                }
                String cmdline = read(String.format("/proc/%d/cmdline", pid));
                if (cmdline.contains("com.android.systemui")) {
                    continue;
                }
                if (phoneMaker != null && cmdline.contains(phoneMaker.toLowerCase())) {
                    continue;
                }
                int uid = Integer.parseInt(
                        cpuaccctSubsystem.split(":")[2].split("/")[1].replace("uid_", ""));
                if (uid >= 1000 && uid <= 1038) {
                    // system process
                    continue;
                }
                int appId = uid - AID_APP;
                int userId = 0;
                // loop until we get the correct user id.
                // 100000 is the offset for each user.
                while (appId > AID_USER) {
                    appId -= AID_USER;
                    userId++;
                }
                if (appId < 0) {
                    continue;
                }
                // u{user_id}_a{app_id} is used on API 17+ for multiple user account support.
                // String uidName = String.format("u%d_a%d", userId, appId);
                File oomScoreAdj = new File(String.format("/proc/%d/oom_score_adj", pid));
                if (oomScoreAdj.canRead()) {
                    int oomAdj = Integer.parseInt(read(oomScoreAdj.getAbsolutePath()));
                    if (oomAdj != 0) {
                        continue;
                    }
                }
                int oomscore = Integer.parseInt(read(String.format("/proc/%d/oom_score", pid)));
                if (oomscore < lowestOomScore) {
                    lowestOomScore = oomscore;
                    foregroundProcess = cmdline;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return foregroundProcess;
    }

    private static String read(String path) throws IOException {
        StringBuilder output = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(path));
        output.append(reader.readLine());
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            output.append('\n').append(line);
        }
        reader.close();
        return output.toString().trim();//不调用trim()，包名后面会带有乱码
    }
}
