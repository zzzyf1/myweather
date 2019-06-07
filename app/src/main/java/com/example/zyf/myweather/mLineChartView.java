package com.example.zyf.myweather;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import lecho.lib.hellocharts.view.LineChartView;

public class mLineChartView extends LineChartView {
    public mLineChartView(Context context) {
        super(context);
    }

    public mLineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public mLineChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:{
                break;
            }
            case MotionEvent.ACTION_MOVE:{
                /**
                 *此处并没有根据X或Y方向上的距离进行比较来确定是否滑动，原因有
                 * （1）保留LineChart的缩放功能
                 * （2）因为图表有一个上标签可以满足scrollView的滑动需求而且特意在X轴坐标下增加了一个空白的textView，总之不是特别影响用户体验
                 * 关于滑动冲突此处并没有详细深入了解，之后有时间参考以下网站
                 * https://blog.csdn.net/a568478312/article/details/79142663
                 * https://www.jianshu.com/p/057832528bdd
                 */
                getParent().requestDisallowInterceptTouchEvent(true);//通知父View不拦截该滑动事件，并交给此控件处理
                break;
            }
            case MotionEvent.ACTION_UP:{
                break;
            }
            default:
                break;
        }

        return super.dispatchTouchEvent(event);
    }
}
