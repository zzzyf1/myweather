package com.example.zyf.myweather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
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

import com.example.zyf.myweather.db.User;
import com.example.zyf.myweather.util.HttpUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import com.example.zyf.myweather.util.*;
import com.example.zyf.myweather.gson.*;

import org.litepal.crud.DataSupport;
public class HomeActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private ViewPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private static Weather weather;
    public static TextView[] textViews;
    public static TextView Moreinfo;
    public static View[] list;
    public static TextView[] otherList;
    public static String weatherId;
    public static HomeActivity instance;//全局获取HomeActivity实例
    public static String CityName;//装在intent中传回来的用户搜索的城市名
    public static List<User> userList;//用户存储的城市id ,用于活动启动时的初始化
    public List<Fragment> fragmentList=new ArrayList<>();//界面初始化
    public static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sharedPreferences=getSharedPreferences("com.example.zyf.myweather_preferences",Context.MODE_PRIVATE);
        instance=this;//全局使用


        mSectionsPagerAdapter = new ViewPagerAdapter(this,getSupportFragmentManager(),fragmentList);

        // Set up the ViewPager with the sections adapter.
        //Count=mSectionsPagerAdapter.getCount();
        //list=new View[Count];
        initView();
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(fragmentList.size());
        mViewPager.setCurrentItem(0);


        for(int i=0;i<userList.size();i++){
            if(sharedPreferences.getString(userList.get(i).getWeatherId(),null)!=null){
                Log.d("HomeActivitys",userList.get(i).getWeatherId()+"成功读取");
            }

        }
        Log.d("HomeActivity","onCreate");

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

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("HomeActivity","onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("HomeActivity","onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("HomeActivity","onPause");
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.d("HomeActivity","onStop");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("HomeActivity","onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("HomeActivity","onRestart");
    }



    public static HomeActivity getInstance(){
        return instance;
    }
    //初始化用户页面
    public boolean initView(){
        if(queryUser()){
            list=new View[userList.size()];
            initFragment();
        }else{
            Intent intent=new Intent(HomeActivity.this,SearchActivity.class);
            startActivity(intent);
            HomeActivity.this.finish();
        }

        return false;
    }
    //查询用户表是否为空
    public boolean queryUser(){
        //先清空
        if(userList!=null&&userList.size()>0){
            userList.clear();
        }
        userList=DataSupport.findAll(User.class);
        if(userList!=null&&userList.size()>0){
            return true;
        }
        return false;
    }
    //根据queryUser查询的数据指定frament的个数
    private void initFragment(){
        //先清空
        if(fragmentList!=null&&fragmentList.size()>0){
            fragmentList.clear();
        }
        if(userList!=null&&userList.size()>0){
            for(int i=0;i<userList.size();i++){
                //每个frament根据userlist中的weatherId初始化
                Fragment fragment=PlaceholderFragment.newInstance(i+1);
                fragmentList.add(fragment);
            }
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
        private String TAG="Fragment";
        private View rootView=null;
        private String string;
        private SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(getInstance());
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
            rootView = inflater.inflate(R.layout.fragment_home, container, false);


            //将每个页面的布局存到list数组中
            /*if(getArguments()!=null){
                list[getArguments().getInt(ARG_SECTION_NUMBER)-1]=rootView;//注意此处减1
            }
             */
            //根据当前frament的下标获取userlist中的weatherId来初始化
            if(userList.size()>0){
                //requestAndShowData();
                string=sharedPreferences.getString(String.valueOf(userList.get(getArguments().getInt(ARG_SECTION_NUMBER)-1).getWeatherId()),null);
                if(string!=null){
                    weather=resolveJsonWeather.resolveWeather(string);
                }
                if(weather!=null){
                    Log.d(TAG,String.valueOf(getArguments().getInt(ARG_SECTION_NUMBER)-1)+">>>>>>>"+weather.cityid+"请求数据");
                }
            }else{
                Intent intent=new Intent(instance,SearchActivity.class);
                startActivity(intent);
                instance.finish();
            }
            /*Moreinfo=rootView.findViewById(R.id.more);

            Moreinfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    for(int i=0;i<textViews.length;i++){
                        if(textViews[i].getVisibility()==View.GONE){
                            textViews[i].setVisibility(View.VISIBLE);
                        }else{
                            textViews[i].setVisibility(View.GONE);
                        }
                    }

                }
            });
            */
            Log.d("HomeActivityF","onCreateView");
            return rootView;

        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            String weatherString;
            weatherString=sharedPreferences.getString(userList.get(getArguments().getInt(ARG_SECTION_NUMBER)-1).getWeatherId(),null);
            if(weatherString!=null){
                weather=resolveJsonWeather.resolveWeather(weatherString);
                ShowData();
            }
            Log.d("HomeActivityF","onActivityCreated");
        }

        @Override
        public void onStart() {
            super.onStart();
            Log.d("HomeActivityF","onStart");
        }

        @Override
        public void onResume() {
            super.onResume();
            Log.d("HomeActivityF","onResume");
        }

        @Override
        public void onStop() {
            super.onStop();
            Log.d("HomeActivityF","onStop");
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            Log.d("HomeActivityF","onDestroyView");
        }

        @Override
        public void onDetach() {
            super.onDetach();
            Log.d("HomeActivityF","onDetach");
        }

        public void ShowData(){
            textViews=new TextView[12];
            initTextViews(rootView);
            otherList=new TextView[11];
            initOtherTextViews(rootView);
            showInformation();

        }
        //获取后四天VIEW，用于折叠面板
        public  void initTextViews(View view){
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
        public  void initOtherTextViews(View view){
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

        //根据网络请求更新界面消息
        public  void showInformation(){
            otherList[0].setText(weather.data.get(0).tem.substring(0,weather.data.get(0).tem.indexOf("℃")));
            otherList[1].setText(weather.city+" "+"中国");
            otherList[2].setText(weather.data.get(0).wea);
            if(!weather.data.get(0).air.equals("0")){//有些城市没有返回空气质量数据
                otherList[3].setText("空气质量"+weather.data.get(0).air_level+" "+weather.data.get(0).air);
            }else{
                otherList[3].setText("空气质量 "+weather.data.get(0).air_level);
            }
            otherList[4].setText("湿度指数 "+weather.data.get(0).humidity);
            //前三天
            for(int i=0;i<3;i++){
                otherList[2*i+5].setText(weather.data.get(i).day.substring(weather.data.get(i).day.length()-3,weather.data.get(i).day.length()-1)+" - "+weather.data.get(i).wea);
                otherList[2*i+6].setText(weather.data.get(i).tem2+"~"+weather.data.get(i).tem1);
            }
            //后四天
            for(int i=0;i<4;i++){
                textViews[3*i].setText(weather.data.get(i+3).day.substring(0,weather.data.get(i).day.indexOf("日")+1)+" - "+weather.data.get(i+3).wea);
                textViews[3*i+1].setText(weather.data.get(i+3).tem2+"~"+weather.data.get(i+3).tem1+"");
            }

        }

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
}
