package com.grechur.varietybanner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.grechur.library.BannerView;
import com.grechur.library.adapter.BannerAdapter;
import com.grechur.library.listener.IBannerListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private BannerView banner_view;
    private List<Integer> mList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        banner_view = findViewById(R.id.banner_view);

        mList = new ArrayList<>();
        mList.add(R.mipmap.banner);
        mList.add(R.mipmap.banner1);
        mList.add(R.mipmap.banner2);
        banner_view.setAdapter(new BannerAdapter(mList) {
            @Override
            public View getView(int position,View convertView) {
                ImageView imageView = null;
                if(convertView != null){
                   imageView = (ImageView) convertView;
//                    Log.e("TAG","界面复用");
                }else {
                    imageView = new ImageView(MainActivity.this);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageView.setTag(R.id.image,mList.get(position));
                    imageView.setImageResource(mList.get(position));
                }
                return imageView;
            }

            @Override
            public String getDesc(int position) {
                return "这是banner"+position;
            }
        });
        banner_view.setBannerListener(new IBannerListener() {
            @Override
            public void setBannerListener(int position) {
                Toast.makeText(MainActivity.this,"这是第"+position+"个",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onClick(View v){


    }
}
