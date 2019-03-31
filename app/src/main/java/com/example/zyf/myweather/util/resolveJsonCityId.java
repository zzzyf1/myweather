package com.example.zyf.myweather.util;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import com.example.zyf.myweather.db.*;

public class resolveJsonCityId
{
    //解析服务器传回来的城市数据
    public  static boolean getIdByCityName(String response,String searchCity)
    {
        if(!TextUtils.isEmpty(response))
        {
            try
            {
                JSONArray jsonArray=new JSONArray(response);
                for(int i=0;i<jsonArray.length();i++)
                {
                    JSONObject cityObject=jsonArray.getJSONObject(i);
                        if(cityObject!=null&&cityObject.getString("leaderZh").equals(searchCity))
                        {
                            //将查询结果缓存到数据库中
                            try{
                                City city=new City();
                                city.setId(cityObject.getInt("id"));
                                city.setWeather_id(String.valueOf(cityObject.getInt("id")));
                                //Log.d("resolveJsonCityId",cityObject.getInt("id"));
                                city.setCityZh(cityObject.getString("cityZh"));//县名
                                city.setLeaderZh(cityObject.getString("leaderZh"));//市名
                                city.setProvinceZh(cityObject.getString("provinceZh"));//省名
                                city.save();
                                Log.d("resolveJsonCityId",cityObject.getString("cityZh")+"   "+cityObject.getString("leaderZh"));
                                Log.d("resolveJsonCityId",city.getWeather_id());

                                //symbol=2;
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                }
                return true;
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return false;
    }
}
