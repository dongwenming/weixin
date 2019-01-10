package com.example.administrator.message;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class NewPagerAdapter extends PagerAdapter {
    public List<View> viewList;

    public NewPagerAdapter(List<View> viewList){
        this.viewList = viewList;
    }

    /*下面四个函数是一定要重写的*/
    @Override
    public boolean isViewFromObject(View arg0, Object arg1){
        //判断instantiateItem(ViewGroup, int)函数所返回来的Key与一个页面视图是否是代表的同一个视图(判断key）
        // TODO Auto-generated method stub
        return arg0 == arg1;
    }

    @Override
    public int getCount() {//返回要滑动的VIew的个数
        // TODO Auto-generated method stub
        return viewList.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position,
                            Object object) {//从当前container中删除指定位置（position）的View;
        // TODO Auto-generated method stub
        container.removeView(viewList.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //实例化：将当前视图添加到container中，并返回当前View（传送key）
        // TODO Auto-generated method stub
        container.addView(viewList.get(position));

        return viewList.get(position);
    }
}
