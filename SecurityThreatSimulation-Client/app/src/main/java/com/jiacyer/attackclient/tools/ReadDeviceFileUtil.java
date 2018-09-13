package com.jiacyer.attackclient.tools;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 *  Created by Jiacy-PC on 2018/1/24.
 */

public class ReadDeviceFileUtil {
    //sys_path 为节点映射到的实际路径
    public static String readFile(String sys_path) {
        StringBuffer ret = new StringBuffer();// 默认值
        BufferedReader reader = null;
        String s;

        try {
            reader = new BufferedReader(new FileReader(sys_path));
            while ((s = reader.readLine())!=null) {
                ret.append(s);
//                ret.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.w("ReadDevice", " ***ERROR*** Here is what I know: " + e.getMessage());
        } finally {
            if(reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.w("ReadDevice", "readFile cmd from"+sys_path + "data"+" -> prop = "+ret.toString());
        return ret.toString();
    }
}
