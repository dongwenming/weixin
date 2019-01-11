package com.example.administrator.message;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class Guide extends Activity implements ViewPager.OnPageChangeListener{
    private View first,second,third;
    private ViewPager viewPager;//对应 <android.support.v4.view.ViewPager/>控件
    private List<View> viewList;//View数组

    private ViewGroup vg;//放置圆点
    //实例化原点View
    private ImageView iv_point;
    private ImageView []ivPointArray;
    //最后一页的按钮
    private ImageButton ib_start;
    private LinearLayout.LayoutParams layoutParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide_page);
        ib_start = (ImageButton) findViewById(R.id.guide_ib_start);
        ib_start.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(Guide.this, startActivity.class));
                finish();
            }

        });

        /*初始化*/
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        LayoutInflater inflater = getLayoutInflater();
        first = inflater.inflate(R.layout.guide_page1,null);
        second = inflater.inflate(R.layout.guide_page2,null);
//        third = inflater.inflate(R.layout.third_page,null);



        viewList = new ArrayList<View>();// 将要分页显示的View装入数组中
        viewList.add(first);
        viewList.add(second);
//        viewList.add(third);

        /*适配器部分*/
        NewPagerAdapter pagerAdapter = new NewPagerAdapter(viewList);
        viewPager.setAdapter(pagerAdapter);

        viewPager.setOnPageChangeListener(this);
        initPoint();


    }

    private void initPoint() {
        //这里实例化LinearLayout
        vg = (ViewGroup) findViewById(R.id.guide_point);
        //根据ViewPager的item数量实例化数组
        ivPointArray = new ImageView[viewList.size()];
        //循环新建底部圆点ImageView，将生成的ImageView保存到数组中
        int size = viewList.size();
        for (int i = 0;i<size;i++){
            iv_point = new ImageView(this);
            layoutParams = new LinearLayout.LayoutParams(15,15);
            //第一个页面需要设置为选中状态，这里采用两张不同的图片
            if (i == 0){
                iv_point.setBackgroundResource(R.drawable.shape_bg_point_enable);
            }else{
                layoutParams.leftMargin=20;
                iv_point.setBackgroundResource(R.drawable.shape_bg_point_disable);
            }

            iv_point.setLayoutParams(layoutParams);

            iv_point.setPadding(30,0,30,0);//left,top,right,bottom

            ivPointArray[i] = iv_point;

            //将数组中的ImageView加入到ViewGroup

            vg.addView(ivPointArray[i]);

        }
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }
    /**
     * 滑动后的监听
     * @param position
     */
    @Override
    public void onPageSelected(int position){
        //循环设置当前页的标记图
        int length = viewList.size();
        for (int i = 0; i < length; i++) {
            ivPointArray[position].setBackgroundResource(R.drawable.shape_bg_point_enable);
            if (position != i) {
                ivPointArray[i].setBackgroundResource(R.drawable.shape_bg_point_disable);
            }
        }
        //判断是否是最后一页，若是则显示按钮
        if (position == viewList.size() - 1) {
            ib_start.setVisibility(View.VISIBLE);
        } else {
            ib_start.setVisibility(View.GONE);
        }
    }
    @Override
    public void onPageScrollStateChanged(int state) {
    }



}

