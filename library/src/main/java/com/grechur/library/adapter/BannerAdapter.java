package com.grechur.library.adapter;

import android.view.View;

import java.util.List;

/**
 * Created by zz on 2018/8/10.
 * 对外的adapter
 */

public abstract class BannerAdapter<T> {
    private List<T> mData;
    public BannerAdapter(List<T> data){
        this.mData = data;
    }

    public int getCount(){
        return mData!=null?mData.size():0;
    }

    public abstract View getView(int position,View convertView);

    public String getDesc(int position){
        return "";
    }

    public Object getData(int index) {
        return mData.get(index);
    }
}
