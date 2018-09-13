package com.jiacyer.attackclient.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.ByteArrayOutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 *  Created by Jiacy-PC on 2018/2/4.
 */

public class HttpUtil {

    private static OkHttpClient client = new OkHttpClient();
//    private static final String server_url = "https://test.jiacyer.com/";
    private static final String upload_url = "pic/";
//    private static final String local_url = "http://10.133.23.178:8888/"; // 有线
//    private static final String local_url = "http://192.168.43.130:8888/"; // 有线
//    private static final String local_url = "http://10.135.31.49:8888/"; // wifi

    public static final int NotWork = 0;
    public static final int Work = 1;

    /**
     * 获取当前的网络状态 ：没有网络-0：有网络-1
     * @param context
     * @return 网络状态
     */
    public static int getNetworkState(Context context) {
        int netType;
        //获取手机所有连接管理对象
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取NetworkInfo对象
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        //NetworkInfo对象为空 则代表没有网络
        if (networkInfo == null)
            netType = NotWork;
        else
            netType = Work;

        return netType;
    }

    public static boolean uploadBitmap(String attackID, int picIndex, Bitmap bitmap) {
        Response response;
        String responseData;
        ConfigUtil configUtil = ConfigUtil.getInstance(null);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

        try {
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), bos.toByteArray());
            MultipartBody body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("picFile", attackID+picIndex+".jpg", requestBody)
                    .build();

            Request request = new Request.Builder().url(configUtil.getDefaultServer()+"/"+upload_url+attackID+"/"+picIndex).post(body).build();
            response = client.newCall(request).execute();
            responseData = response.body().string();
            Log.i("HttpUtil", "Response Data : "+responseData);
            Log.i("HttpUtil", "Response Code : "+response.code());

            if (responseData.contains("Upload succeed"))
                return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
