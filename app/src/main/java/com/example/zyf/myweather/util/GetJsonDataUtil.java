package com.example.zyf.myweather.util;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public  class GetJsonDataUtil {
    public  static String getJson(Context context, String fileName) throws UnsupportedEncodingException {

        StringBuilder stringBuilder = new StringBuilder();
        try {
            //assets目录管理器
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //return new String(stringBuilder.toString().getBytes("GBK"),"GBK");
        //此处不用考虑city.json中中文unicode编码的问题因为在json解析时jsonObject会自动转换为中文
        return stringBuilder.toString();
    }

}
