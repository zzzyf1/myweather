package com.example.zyf.myweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.zyf.myweather.db.City;
import com.example.zyf.myweather.db.User;
import com.example.zyf.myweather.gson.Weather;
import com.example.zyf.myweather.util.*;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity {
    private String searchCity;
    private List<City> cityList;//数据库查询返回的结果
    private ArrayAdapter<String> adapter;
    private List<String> dataList=new ArrayList<>();
    private ListView listView;
    private EditText myEditText;
    private ImageButton myImageButton;
    private ProgressBar progressBar;
    public  String WeatherID;
    private static String CityRequest;
    private Weather weather;
    public static SharedPreferences.Editor editor;
    public static SearchActivity instance;//全局获取HomeActivity实例
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        intent=new Intent(SearchActivity.this,HomeActivity.class);
        instance=this;
        try {
            CityRequest=GetJsonDataUtil.getJson(SearchActivity.this,"city.json");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        myImageButton=findViewById(R.id.imageButton);
        myEditText=findViewById(R.id.editText2);
        listView=findViewById(R.id.listview);
        progressBar=findViewById(R.id.progressBar2);
        adapter=new ArrayAdapter<>(SearchActivity.this,android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        /**
        * EditText的getText()方法只能在监听事件中才能够实现
        * searchCity=myEditText.getText().toString();
        * */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WeatherID=cityList.get(position).getWeather_id();
                if(!queryRepeatID()){
                    /**非重复添加城市
                     * 1、保存进用户表中
                     * 2、请求数据，写进Preferences*/
                    User user=new User();
                    user.setWeatherId(WeatherID);
                    user.save();
                    if(progressBar.getVisibility()==View.GONE){
                        progressBar.setVisibility(View.VISIBLE);
                    }
                    requestWeatherById(WeatherID);
                }else{
                    //添加重复城市，直接返回HomeActivity
                    intent.putExtra("weather",WeatherID);
                    intent.putExtra("cityName",searchCity);
                    startActivity(intent);
                    SearchActivity.this.finish();
                }


            }
        });
        myImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchCity=myEditText.getText().toString();
                if(!searchCity.isEmpty()){
                    cityList=DataSupport.where("leaderZh=?",searchCity).find(City.class);
                    if(cityList!=null&&cityList.size()>0){
                        /**
                         * 加载到listView,一定要先清空
                         * */
                        dataList.clear();
                        /**
                         * 装载listItem数据
                         * */
                        for(City city : cityList){
                            dataList.add(" "+city.getCityZh()+"--"+city.getLeaderZh()+"--"+city.getProvinceZh());
                        }
                        adapter.notifyDataSetChanged();
                        listView.setSelection(0);
                    }else{
                        /**
                         * 2019-4-9    zyf
                         * 由于接口网站的更新不得不把原来向网络请求得到的city.json缓存到assets目录下的city.json中去。
                         */
                        if(progressBar.getVisibility()==View.GONE){
                            progressBar.setVisibility(View.VISIBLE);
                        }
                        if(resolveJsonCityId.getIdByCityName(CityRequest,searchCity)){
                            queryCityFromDatabase();
                            if(progressBar.getVisibility()==View.VISIBLE){
                                progressBar.setVisibility(View.GONE);
                            }
                        }

                        /**HttpUtil.sendOkHttpRequest("https://cdn.huyahaha.com/tianqiapi/city.json", new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                //注意此处一定要切换到主进程，否则在子进程结束后应用会关闭
                                //切换回主线程此处没有写到碎片里，所以不能使用getActivity
                                SearchActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(SearchActivity.this,"OkHttp回调失败,请检查网络连接",Toast.LENGTH_LONG).show();
                                    }
                                });

                            }
                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String responseText=response.body().string();
                                if(!responseText.isEmpty()){
                                    if(resolveJsonCityId.getIdByCityName(responseText,searchCity)){
                                        //注意此处一定要切换到主进程，否则在子进程结束后应用会关闭
                                        //切换到主线程更相信UI
                                        SearchActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //Toast.makeText(SearchActivity.this,"OkHttp回调和json解析成功",Toast.LENGTH_LONG).show();
                                                //从数据库查询并更新UI
                                                queryCityFromDatabase();
                                                if(progressBar.getVisibility()==View.VISIBLE){
                                                    progressBar.setVisibility(View.GONE);
                                                }
                                            }
                                        });
                                    }
                                }else{
                                    ////注意此处一定要切换到主进程，否则在子进程结束后应用会关闭
                                    SearchActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(SearchActivity.this,"JSON数据返回为空",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        });*/
                    }
                }else{
                    Toast.makeText(SearchActivity.this,"城市名为空",Toast.LENGTH_LONG).show();
                }

            }
        });
    }
    //查询该用户要添加的城市是否已经存在
    private boolean queryRepeatID(){
        List<User> userList=DataSupport.where("weatherId=?",WeatherID).find(User.class);
        if(userList!=null&&userList.size()>0){
            return true;//表明该城市已经添加
        }
        return false;
    }
    //如果要搜索的城市数据库里有就不再去解析city.json
    private void queryCityFromDatabase(){
        cityList=DataSupport.where("leaderZh=?",searchCity).find(City.class);
        if(cityList!=null&&cityList.size()>0){
            //加载到listView
            //一定要先清空
            dataList.clear();
            //装载listItem数据
            for(City city : cityList){
                dataList.add(" "+city.getCityZh()+"--"+city.getLeaderZh()+"--"+city.getProvinceZh());
                //Log.d("SearchActivity",String.valueOf(city.getId()));
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
        }else{
            //没有搜索到该城市
            dataList.clear();
            adapter.notifyDataSetChanged();
            Toast.makeText(SearchActivity.this,"未搜索到该城市",Toast.LENGTH_LONG).show();

        }
    }
    private SearchActivity getInstance(){
        return instance;
    }
    public void requestWeatherById(String id){

        HttpUtil.sendOkHttpRequest("https://www.tianqiapi.com/api/?version=v1&cityid="+id, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getInstance().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getInstance(),"无法获得json数据",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText=response.body().string();
                if(!responseText.isEmpty()){
                    weather=resolveJsonWeather.resolveWeather(responseText);
                    if(weather!=null){
                        getInstance().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                editor=PreferenceManager.getDefaultSharedPreferences(getInstance()).edit();
                                editor.putString(String.valueOf(weather.cityid),responseText);
                                if(editor.commit()){
                                    if(progressBar.getVisibility()==View.VISIBLE){
                                        progressBar.setVisibility(View.GONE);
                                    }
                                    intent.putExtra("weather",WeatherID);
                                    intent.putExtra("cityName",searchCity);
                                    startActivity(intent);
                                    SearchActivity.this.finish();
                                }
                                //Toast.makeText(getInstance(),"GSON解析成功",Toast.LENGTH_SHORT).show();
                                Log.d("HomeActivityRequest-->",String.valueOf(weather.cityid));
                            }
                        });

                    }else{
                        getInstance().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getInstance(),"GSON解析失败",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }else{
                    getInstance().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getInstance(),"json数据返回为空值",Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });

    }
}
