package com.example.zyf.myweather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.renderscript.Sampler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.view.PointerIcon;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zyf.myweather.db.User;
import com.example.zyf.myweather.util.HttpUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
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
    public static View[] list;
    public static TextView[] otherList;
    public static String weatherId;
    public static HomeActivity instance;//全局获取HomeActivity实例
    public static String CityName;//装在intent中传回来的用户搜索的城市名
    public static List<User> userList;//用户存储的城市id ,用于活动启动时的初始化
    public List<Fragment> fragmentList=new ArrayList<>();//界面初始化
    public static SharedPreferences sharedPreferences;
    private int number;//HomeActivity启动后默认显示哪一个页面

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
        Intent intent=getIntent();//获得本活动的intent
        number=intent.getIntExtra("current_number",0);
        //CityName=intent.getStringExtra("searchCity");
        Log.d("HomeActivity",String.valueOf(number));
        mViewPager.setCurrentItem(number-1);


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
        private String string;//读取的天气数据
        private TextView MoreInfo;
        private SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(getInstance());
        private SwipeRefreshLayout swipeRefresh;
        public static SharedPreferences.Editor editor;
        private boolean isViewcreated;
        private boolean isfirstVisible=true;//?
        private boolean isFragmentVisible;
        private ProgressBar progressBar;
        private List<PointValue> mPoint=new ArrayList<>();//点的数据
        private List<String> LabelX=new ArrayList<>();//暂存X轴标签，用来初始化AxisValue（X轴标签类）
        private List<AxisValue> mAxisXValue=new ArrayList<>();
        private Line line;//曲线
        private List<Line> lines=new ArrayList<>();
        private Axis axisX=new Axis();//X 轴
        private Axis axisY=new Axis();//Y轴
        private LineChartView mChartView;
        private LineChartData mdata;
        private Viewport viewport;
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
        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);
            isFragmentVisible=isVisibleToUser;

            if(isVisibleToUser&&isViewcreated&&isfirstVisible){
                progressBar=rootView.findViewById(R.id.progressBar);
                if(progressBar.getVisibility()==View.GONE){
                    progressBar.setVisibility(View.VISIBLE);
                }
                Log.d("HomeActivityF","只在第一次可见时自动更新一次数据");
                refreshWeather(userList.get(getArguments().getInt(ARG_SECTION_NUMBER)-1).getWeatherId());
                if(progressBar.getVisibility()==View.VISIBLE){
                    progressBar.setVisibility(View.GONE);
                }
                isfirstVisible=false;
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_home, container, false);
            MoreInfo=rootView.findViewById(R.id.more);
            swipeRefresh=rootView.findViewById(R.id.swipeRefresh);
            isViewcreated=true;
            if(isFragmentVisible&&isfirstVisible){
                //refreshWeather(userList.get(getArguments().getInt(ARG_SECTION_NUMBER)-1).getWeatherId());
                //SyncHttpUtil.sendOkHttpRequest("https://www.tianqiapi.com/api/?version=v1&cityid="+userList.get(getArguments().getInt(ARG_SECTION_NUMBER)-1).getWeatherId())
                if(userList.get(getArguments().getInt(ARG_SECTION_NUMBER)-1).getWeatherId()!=null){
                    //同步请求数据
                    String responseString=SyncHttpUtil.sendOkHttpRequest("https://www.tianqiapi.com/api/?version=v1&cityid="+userList.get(getArguments().getInt(ARG_SECTION_NUMBER)-1).getWeatherId());
                    weather=resolveJsonWeather.resolveWeather(responseString);
                    if(weather!=null){
                        editor=PreferenceManager.getDefaultSharedPreferences(getInstance()).edit();
                        editor.remove(String.valueOf(weather.cityid));
                        if(editor.commit()){
                            editor.putString(String.valueOf(weather.cityid),responseString);
                            editor.commit();
                            ShowData();
                            swipeRefresh.setRefreshing(false);

                        }else{
                            //此处还得添加用来增强健壮性
                        }
                    }
                }
                progressBar=rootView.findViewById(R.id.progressBar);
                if(progressBar.getVisibility()==View.VISIBLE){
                    progressBar.setVisibility(View.GONE);
                }
                Log.d("HomeActivityF","默认展示的fragment");
                isfirstVisible=false;
            }
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
            MoreInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textViews=new TextView[12];
                    initTextViews(rootView);
                    for(int i=0;i<textViews.length;i++){
                        if(textViews[i].getVisibility()==View.GONE){
                            textViews[i].setVisibility(View.VISIBLE);
                        }else{
                            textViews[i].setVisibility(View.GONE);
                        }
                    }
                }
            });
            swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshWeather(userList.get(getArguments().getInt(ARG_SECTION_NUMBER)-1).getWeatherId());

                }
            });
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
        public boolean initPoint(){
            if(weather!=null){
                float X;
                float Y;
                //赋值前先清空，避免重复数据
                if(LabelX!=null&&LabelX.size()>0){
                    LabelX.clear();
                }
                if(mPoint!=null&&mPoint.size()>0){
                    mPoint.clear();
                }
                //获取第一天实时天气信息，X轴坐标从0起
                for(int i=0;i<weather.data.get(0).hours.size();i++){
                    X=i;
                    Y=Float.parseFloat(weather.data.get(0).hours.get(i).tem.substring(0,weather.data.get(0).hours.get(i).tem.indexOf("℃")));
                    PointValue pointValue=new PointValue(X,Y);
                    LabelX.add(weather.data.get(0).hours.get(i).day);
                    mPoint.add(pointValue);

                }
                //接着获取第二天的实时天气信息,注意X轴坐标不是从0起
                int sum=mPoint.size();
                int start=0;
                for(int j=sum;j<sum+weather.data.get(1).hours.size();j++){
                    X=j;
                    Y=Float.parseFloat(weather.data.get(1).hours.get(start).tem.substring(0,weather.data.get(1).hours.get(start).tem.indexOf("℃")));
                    PointValue pointValue=new PointValue(X,Y);
                    mPoint.add(pointValue);
                    LabelX.add(weather.data.get(1).hours.get(start).day);
                    start++;
                }
                //点对应的X轴标签
                /**
                 * bug点
                 * X轴标签刷新后会日期信息会出现重叠，当刷新前和刷新后的信息不一样时
                 * 赋值前先清空，避免重复数据
                 */

                if(mAxisXValue!=null&&mAxisXValue.size()>0){
                    mAxisXValue.clear();
                }
                for(int i=0;i<weather.data.get(0).hours.size()+weather.data.get(1).hours.size();i++){

                    mAxisXValue.add(new AxisValue(i).setLabel(LabelX.get(i)));
                }
                return  true;
            }else{
                return false;
            }
        }
        public void initChart(){
            //设置曲线
            line=new Line(mPoint).setColor(Color.YELLOW);
            line.setCubic(true);
            line.setHasLabels(true);
            line.setStrokeWidth(1);
            line.setFilled(true);
            line.setPointRadius(5);
            line.setShape(ValueShape.DIAMOND);
            //必须清空，不然每次下拉刷新会造成重复绘制前一条曲线
            if(lines!=null&&lines.size()>0){
                lines.clear();
                Log.d("HomeActivity","初始化lines");
            }
            lines.add(line);
            //设置X轴Y轴
            axisX.setMaxLabelChars(0);
            axisX.setValues(mAxisXValue);
            //设置ChartVIew视图缩放方式
            mChartView=rootView.findViewById(R.id.chartView);
            mChartView.setInteractive(true);
            mChartView.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
            //设置LineChartData()
            mdata=new LineChartData();
            mdata.setAxisXBottom(axisX);
            mdata.setAxisYLeft(axisY);
            mdata.setLines(lines);
            //mdata.setBaseValue(40);
            mdata.setValueLabelBackgroundEnabled(false);
            mdata.setValueLabelsTextColor(Color.WHITE);
            mChartView.setLineChartData(mdata);
            //把Y轴数值固定，X轴滑动
            viewport=new Viewport(mChartView.getMaximumViewport());
            if(getYMinValue()!=-1){
                viewport.bottom=(int)mPoint.get(getYMinValue()).getY()-5;
                if(viewport.bottom<-10){
                    //当最低温度低于-10时Y轴颜色设置为蓝色
                    axisY.setTextColor(Color.BLUE);
                    //axisY.setLineColor(Color.BLUE);//此属性要起作用，必须设置字体颜色
                }
            }else{
                viewport.bottom=-40;
            }
            if(getYMaxValue()!=-1){
                viewport.top=(int)mPoint.get(getYMaxValue()).getY()+5;
                if(viewport.top>35){
                    axisY.setTextColor(Color.RED);
                }
            }else{
                viewport.top=40;
            }
            mChartView.setMaximumViewport(viewport);
            /**
             * 此处不能先设置left right 的值不然图标无法滑动
             * 同理，如果想要x轴固定也不能先设置bottom和top的值
             */
            viewport.left=0;
            viewport.right=5;
            mChartView.setCurrentViewport(viewport);
            mChartView.moveTo(0,0);
        }
        public int getYMaxValue(){
            int max=0;
            if(mPoint!=null&&mPoint.size()>0){
                for(int i=0;i<mPoint.size();i++){
                    if(mPoint.get(i).getY()>mPoint.get(max).getY()){
                        max=i;
                    }
                }
                return max;
            }
            return -1;
        }
        public int getYMinValue(){
            int min=0;
            if(mPoint!=null&&mPoint.size()>0){
                for(int i=0;i<mPoint.size();i++){
                    if(mPoint.get(i).getY()<mPoint.get(min).getY()){
                        min=i;
                    }
                }
                return min;
            }
            return -1;
        }
        public void refreshWeather(String id){

            HttpUtil.sendOkHttpRequest("https://www.tianqiapi.com/api/?version=v1&cityid="+id, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    getInstance().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getInstance(),"无法获得json数据",Toast.LENGTH_SHORT).show();
                            swipeRefresh.setRefreshing(false);
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
                                    editor.remove(String.valueOf(weather.cityid));
                                    if(editor.commit()){
                                        editor.putString(String.valueOf(weather.cityid),responseText);
                                        editor.commit();
                                        ShowData();
                                        swipeRefresh.setRefreshing(false);

                                    }else{
                                        //此处还得添加用来增强健壮性
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
        public void ShowData(){
            textViews=new TextView[12];
            initTextViews(rootView);
            otherList=new TextView[11];
            initOtherTextViews(rootView);
            CityName=userList.get(getArguments().getInt(ARG_SECTION_NUMBER)-1).getCity();
            showInformation();
            initPoint();
            initChart();

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
            otherList[1].setText(weather.city+" "+CityName);
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
                Log.d("HomeActivity",weather.data.get(i+3).day.substring(0,weather.data.get(i).day.indexOf("日")+1));
                textViews[3*i+1].setText(weather.data.get(i+3).tem2+"~"+weather.data.get(i+3).tem1+"");
            }

        }

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
}
