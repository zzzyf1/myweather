package com.example.zyf.myweather.db;

import org.litepal.crud.DataSupport;

public class City extends DataSupport {
    //缓存的数据较少，就不考虑冗余了
    private int id;//县的id,用来请求天气
    private String cityZh;//县的名字
    private String leaderZh;//市的名字
    private String provinceZh;//省的名字

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityZh() {
        return cityZh;
    }

    public void setCityZh(String cityZh) {
        this.cityZh = cityZh;
    }

    public String getLeaderZh() {
        return leaderZh;
    }

    public void setLeaderZh(String leaderZh) {
        this.leaderZh = leaderZh;
    }

    public String getProvinceZh() {
        return provinceZh;
    }

    public void setProvinceZh(String provinceZh) {
        this.provinceZh = provinceZh;
    }
}
