package com.jiacyer.newpaymerchantclient.utils;

import java.io.*;

/**
 *  Created by Jiacy-PC on 2018/5/10.
 */

public class ConfigUtil {
    private static ConfigUtil instance = null;
    private String server = "http://127.0.0.1";
    private final String configFileName = "config.dat";

    public synchronized static ConfigUtil getInstance() {
        if (instance == null) {
            instance = new ConfigUtil();
        }
        return instance;
    }

    private ConfigUtil() {
        initConfig();
    }

    public String getDefaultServer() {
        if (server == null)
            return  "http://127.0.0.1";
        else
            return server;
    }

    public void setDefaultServer(String server) {
         this.server = server;
    }


    private void initConfig() {
//        StringBuffer projectPath = new StringBuffer(30);
//        projectPath.append(System.getProperty("user.dir"));
//        projectPath.append("//");
//        projectPath.append(configFileName);
//		System.out.println(projectPath);
//        File configFile = new File(projectPath.toString());
        File configFile = new File("./" + configFileName);
        if( configFile.exists() ) {
            System.out.println("configFile exists!");
            setConfig(configFile);
        } else {
            System.out.println("configFile doesn't exist!");
        }
    }

    private void setConfig(File configFile) {
        String s;
        try {
            FileReader fr = new FileReader(configFile);
            BufferedReader br = new BufferedReader(fr);
            while( (s = br.readLine()) != null ) {
                System.out.println(s);
                if (s.indexOf("<Server>")!=-1) {
                    server = s.substring(s.indexOf(">")+1, s.indexOf("</"));
                }
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveConfig() {
        StringBuffer sb = new StringBuffer();
        sb.append("<Server>");
        sb.append(server);
        sb.append("</Server>\r\n");
        System.out.println(sb.toString());

//        StringBuffer projectPath = new StringBuffer(30);
//        projectPath.append(System.getProperty("user.dir"));
//        projectPath.append("//");
//        projectPath.append(configFileName);
//		System.out.println(projectPath);
//        File configFile = new File(projectPath.toString());
        File configFile = new File("./" + configFileName);
        if(configFile.exists()) {
            configFile.delete();
        }
        FileWriter fw;
        try {
            fw = new FileWriter(configFile);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            pw.print(sb.toString());
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
