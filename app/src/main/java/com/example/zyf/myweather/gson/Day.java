package com.example.zyf.myweather.gson;

import java.util.List;
//7天天气预报
//注意今天的天气结构和接下来的6天不同,缺少air,humidity,air_level,air_tips,alarm
public class Day {
    public String day;
    public String date;
    public String week;
    public String wea;//当前天气情况
    public String wea_img;
    public String air;
    public String humidity;
    public String air_level;
    public String air_tips;
    //public List<String> alarm;//7天接口里边此项为空,后来发现并不是每个城市都为空，进而引起了bug,导致有些城市查不出来
    public String tem1;//高温,白天温度
    public String tem2;//低温,晚上温度
    public String tem;//当前温度
    public List<String> win;//风向变化，如西南风->西北风，或者只有一个元素
    public String win_speed;
    public List<Hour> hours;//每2小时预报，共计4个
    public List<Tip> index;//生活指数
}
