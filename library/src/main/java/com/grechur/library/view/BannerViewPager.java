package com.grechur.library.view;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.grechur.library.R;
import com.grechur.library.SimpleActivityLifecycleCallbacks;
import com.grechur.library.adapter.BannerAdapter;
import com.grechur.library.listener.IBannerListener;
import com.grechur.library.scroller.BannerScroller;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Created by zz on 2018/8/10.
 */

public class BannerViewPager extends ViewPager{
    //设置消息标志位
    public final static int MSG_HANDLER_WHAT = 0x000011;
    //消息handler
    private RollHandler mHandler;
    //设置消息延迟时间
    private long mMsgTime = 2000;

    //对外的适配器
    private BannerAdapter mBannerAdapter;

    //复用view 使用set，是为了避免有重复的布局对象加入进来
    public List<View> mViews;

    private BannerScroller mBanScroller;

    private Activity mActivity;

    //设置监听
    private IBannerListener mBannerListener;

    public void setBannerListener(IBannerListener bannerListener) {
        this.mBannerListener = bannerListener;
    }

    public BannerViewPager(Context context) {
        this(context,null);
    }

    public BannerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        WeakReference<BannerViewPager> weakReference = new WeakReference<BannerViewPager>(this);
        mHandler = new RollHandler(weakReference);

        mViews = new ArrayList<>();

        mBanScroller = new BannerScroller(context);
        try {
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            field.set(this,mBanScroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mActivity = (Activity) context;
        mActivity.getApplication().registerActivityLifecycleCallbacks(mActivityLifecycle);
    }

    /**
     * 给viewpager设置适配器
     * @param adapter
     */
    public void setAdapter(BannerAdapter adapter) {
        mBannerAdapter = adapter;
        setAdapter(new BannerViewPagerAdapter());
//        startRoll();
    }



    public void startRoll(){
        //在开始之前，把前一个消息去掉，防止快速滑动时出现越来越快
        mHandler.removeMessages(MSG_HANDLER_WHAT);
        mHandler.sendEmptyMessageDelayed(MSG_HANDLER_WHAT,mMsgTime);
    }

    /**
     * 停止轮播
     */
    public void stopRoll(){
        mHandler.removeMessages(MSG_HANDLER_WHAT);
    }

    /**
     * 离开界面时停止轮播，销毁handler
     */
    @Override
    protected void onDetachedFromWindow() {
        mHandler.removeMessages(MSG_HANDLER_WHAT);
        mHandler = null;
        super.onDetachedFromWindow();
    }

    /**
     * 使用静态内部类，防止内存泄漏
     */
    public static class RollHandler extends Handler {
        WeakReference<BannerViewPager> mWeakReference;
        public  RollHandler(WeakReference<BannerViewPager> weakReference){
            this.mWeakReference = weakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            BannerViewPager banner = mWeakReference.get();
            if(banner!=null && msg.what == MSG_HANDLER_WHAT){
                banner.setCurrentItem(banner.getCurrentItem()+1);
                banner.startRoll();
            }
        }
    }

    /**
     * viewpager的适配器
     */
    public class BannerViewPagerAdapter extends PagerAdapter{

        /**
         * 将可滑动大小为最大（无限滑动）
         * @return
         */
        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        /**
         * 创建item
         * @param container
         * @param position
         * @return
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if(mBannerAdapter!=null&&mBannerAdapter.getCount()>0) {
                final int index = position % mBannerAdapter.getCount();
                View view = mBannerAdapter.getView(index,getConvertView(index));
                container.addView(view);
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mBannerListener!=null)
                        mBannerListener.setBannerListener(index);
                    }
                });
                return view;
            }
            return null;
        }



        /**
         * 销毁item
         * @param container
         * @param position
         * @param object
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
            mViews.add(view);
        }
    }


    private Application.ActivityLifecycleCallbacks mActivityLifecycle = new SimpleActivityLifecycleCallbacks(){
        @Override
        public void onActivityResumed(Activity activity) {
            if(activity == mActivity){
                startRoll();
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {
            if(activity == mActivity){
                //停止轮播
                stopRoll();
            }
        }
    };

    private View getConvertView(int index) {
        if(mViews.size() < mBannerAdapter.getCount())return  null;
        for (View mConvertView : mViews) {
            Log.e("TAG",mConvertView.toString());
            if(mConvertView.getParent() == null){
                if(mConvertView.getTag(R.id.image).equals(mBannerAdapter.getData(index))) {
                    return mConvertView;
                }
            }
        }
        return null;

    }
}
