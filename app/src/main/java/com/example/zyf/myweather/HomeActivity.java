package com.example.zyf.myweather;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.example.zyf.myweather.util.HttpUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import com.example.zyf.myweather.util.*;
import com.example.zyf.myweather.gson.*;

public class HomeActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private static Weather weather;
    public static TextView[] textViews;
    public static TextView Moreinfo;
    public static View[] list;
    public static TextView[] otherList;
    public static int Count;
    public static String weatherId;
    public static HomeActivity instance;
    public static String CityName;//装在intent中传回来的用户搜索的城市名


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        instance=this;//全局使用
        weatherId=getIntent().getStringExtra("weather");
        CityName=getIntent().getStringExtra("cityName");

        //if(weatherId!=null)
        //Toast.makeText(HomeActivity.this,weatherId,Toast.LENGTH_LONG).show();
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        */
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        Count=mSectionsPagerAdapter.getCount();
        list=new View[Count];




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                        */
                Intent intent=new Intent(HomeActivity.this,SearchActivity.class);
                startActivity(intent);
                HomeActivity.this.finish();

            }
        });

    }
    //全局获取HomeActivity实例
    public static HomeActivity getInstance(){
        return instance;
    }
    //获取后四天VIEW，用于折叠面板
    public static void initTextViews(View view){
        textViews[0]=view.findViewById(R.id.textView5);
        textViews[1]=view.findViewById(R.id.textView6);
        textViews[2]=view.findViewById(R.id.line4);
        textViews[3]=view.findViewById(R.id.textView8);
        textViews[4]=view.findViewById(R.id.textView10);
        textViews[5]=view.findViewById(R.id.line5);
        textViews[6]=view.findViewById(R.id.textView11);
        textViews[7]=view.findViewById(R.id.textView12);
        textViews[8]=view.findViewById(R.id.line6);
        textViews[9]=view.findViewById(R.id.textView17);
        textViews[10]=view.findViewById(R.id.textView18);
        textViews[11]=view.findViewById(R.id.line7);
    }
    //获取当前碎片上其他textViews
    public static void initOtherTextViews(View view){
        otherList[0]=view.findViewById(R.id.temperature);//今天当前气温
        otherList[1]=view.findViewById(R.id.position);//用户选取的县名和城市名
        otherList[2]=view.findViewById(R.id.nowWeather);//当前天气状况
        otherList[3]=view.findViewById(R.id.air);//当前空气质量
        otherList[4]=view.findViewById(R.id.humidity);//当前湿度
        otherList[5]=view.findViewById(R.id.weather1);//今天天气
        otherList[6]=view.findViewById(R.id.weather2);
        otherList[7]=view.findViewById(R.id.wetaher3);//明天拼错了
        otherList[8]=view.findViewById(R.id.weather4);
        otherList[9]=view.findViewById(R.id.weather5);//后天
        otherList[10]=view.findViewById(R.id.weather6);

    }
    public static void requestWeatherById(String id){
        Log.d("HomeActivity",id);
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
                String responseText=response.body().string();
                if(!responseText.isEmpty()){
                    weather=resolveJsonWeather.resolveWeather(responseText);
                    if(weather!=null){
                        getInstance().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showInformation();
                                Toast.makeText(getInstance(),"GSON解析成功",Toast.LENGTH_SHORT).show();
                                Log.d("HomeActivity",String .valueOf(weather.cityid));
                                Log.d("HomeActivity",weather.update_time);
                                Log.d("HomeActivity",weather.city);
                                Log.d("HomeActivity",weather.country);
                                for(int i=0;i<weather.data.size();i++){
                                    Day day=weather.data.get(i);
                                    Log.d("HomeActivity",day.day);
                                    Log.d("HomeActivity",day.date);
                                    Log.d("HomeActivity",day.week);
                                    Log.d("HomeActivity",day.wea_img);
                                    Log.d("HomeActivity",String.valueOf(day.air));
                                    Log.d("HomeActivity",String.valueOf(day.humidity));
                                    Log.d("HomeActivity",day.tem);
                                    Log.d("HomeActivity",day.win.get(0));
                                    Log.d("HomeActivity",day.hours.get(1).win_speed);
                                    Log.d("HomeActivity",day.index.get(1).desc);
                                    Log.d("HomeActivity","\n\n");
                                }
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
    //根据网络请求更新界面消息
    public static void showInformation(){
        if(weather.data.get(0).tem.substring(0,1).equals("-")){
            otherList[0].setText(weather.data.get(0).tem.substring(0,2));
        }else{
            otherList[0].setText(weather.data.get(0).tem.substring(0,1));
        }
        otherList[1].setText(weather.city+" "+CityName);
        otherList[2].setText(weather.data.get(0).wea);
        if(!weather.data.get(0).air.equals("0")){
            otherList[3].setText("空气质量"+weather.data.get(0).air_level+" "+weather.data.get(0).air);
        }else{
            otherList[3].setText("空气质量 "+weather.data.get(0).air_level);
        }


        otherList[4].setText("湿度指数 "+weather.data.get(0).humidity);
        //前三天
        for(int i=0;i<3;i++){
            otherList[2*i+5].setText(weather.data.get(i).day+" - "+weather.data.get(i).wea);
            otherList[2*i+6].setText(weather.data.get(i).tem2+"~"+weather.data.get(i).tem1);
        }
        //后四天
        for(int i=0;i<4;i++){
            textViews[3*i].setText(weather.data.get(i+3).day+" - "+weather.data.get(i+3).wea);
            textViews[3*i+1].setText(weather.data.get(i+3).tem1+"~"+weather.data.get(i+3).tem2+"");
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_home, container, false);
            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            //Toast.makeText(rootView.getContext(),weatherId,Toast.LENGTH_LONG).show();

            //将每个页面的布局存到list数组中
            if(getArguments()!=null){
                list[getArguments().getInt(ARG_SECTION_NUMBER)-1]=rootView;//注意此处减1
            }
            //页面元素初始化
            //后四天
            textViews=new TextView[12];
            initTextViews(list[getArguments().getInt(ARG_SECTION_NUMBER)-1]);
            //前三天和其他
            otherList=new TextView[11];
            initOtherTextViews(list[getArguments().getInt(ARG_SECTION_NUMBER)-1]);
            Moreinfo=rootView.findViewById(R.id.more);
            if(weatherId==null){
                Intent intent=new Intent(instance,SearchActivity.class);
                startActivity(intent);
                instance.finish();
            }else{
                requestWeatherById(weatherId);//1.发起网络请求 2.同时里边又调用了showInformation()更新页面

            }
            Moreinfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Log.d("HomeActivity",weatherId);
                    for(int i=0;i<textViews.length;i++){
                        if(textViews[i].getVisibility()==View.GONE){
                            textViews[i].setVisibility(View.VISIBLE);
                        }else{
                            textViews[i].setVisibility(View.GONE);
                        }
                    }
                }
            });
            return rootView;
        }


    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 1 total pages.
            return 1;
        }
    }
}
