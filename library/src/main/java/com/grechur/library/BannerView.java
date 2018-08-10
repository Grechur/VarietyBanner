package com.grechur.library;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.grechur.library.adapter.BannerAdapter;
import com.grechur.library.listener.IBannerListener;
import com.grechur.library.transformer.CoverModeTransformer;
import com.grechur.library.transformer.DepthTransformer;
import com.grechur.library.view.BannerViewPager;
import com.grechur.library.view.IndicationDotView;

/**
 * Created by zz on 2018/8/10.
 */

public class BannerView extends RelativeLayout{

    private BannerViewPager mBannerVp;
    private TextView mBannerDesc;
    private LinearLayout mDotContainer;
    
    //自定义的适配器
    private BannerAdapter mBanAdapter;
    //上下文对象
    private Context mContext;

    //当前的位置
    private int mCurrentPosition = 0;

    //自定义属性
    private int mDotSize = 10;
    private int mDotDistance = 10;
    private Drawable mDotNormalDrawable ;
    private Drawable mDotSelectDrawable;
    private int mDotLocation;
    private boolean mCanLooper = true;
    private int mMode;

    public BannerView(Context context) {
        this(context,null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context,attrs);
        //加载界面
        View view = null;
        if(mMode == 1){
            view = inflate(context,R.layout.banner_view,this);
        }else{
            view = inflate(context,R.layout.banner_view,this);
        }


        mContext = context;
        initView(view);
    }

    /**
     * 初始化自定义属性
     * @param context
     * @param attrs
     */
    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs,R.styleable.BannerView);
        mCanLooper = array.getBoolean(R.styleable.BannerView_CanLooper,mCanLooper);
        mMode = array.getInt(R.styleable.BannerView_Mode,mMode);
        mDotSize = array.getDimensionPixelOffset(R.styleable.BannerView_DotSize,dp2px(mDotSize));
        mDotDistance = array.getDimensionPixelOffset(R.styleable.BannerView_DotDistance,dp2px(mDotDistance));
        mDotLocation = array.getInt(R.styleable.BannerView_DotLocation,0);
        int normal = array.getResourceId(R.styleable.BannerView_DotNormalDrawable,R.drawable.banner_normal_shape);
        int select = array.getResourceId(R.styleable.BannerView_DotSelectDrawable,R.drawable.banner_select_shape);
        mDotNormalDrawable = getResources().getDrawable(normal);
        mDotSelectDrawable = getResources().getDrawable(select);
        array.recycle();
    }

    /**
     * 界面初始化
     */
    private void initView(View view) {
        mBannerVp = view.findViewById(R.id.banner_vp);
        mBannerDesc = view.findViewById(R.id.tv_banner_desc);
        mDotContainer = view.findViewById(R.id.ll_dot_container);

        mBannerVp.setBannerListener(new IBannerListener() {
            @Override
            public void setBannerListener(int position) {
                if(mBannerListener!=null)
                    mBannerListener.setBannerListener(position);
            }
        });
    }
    //设置监听
    private IBannerListener mBannerListener;

    public void setBannerListener(IBannerListener bannerListener) {
        this.mBannerListener = bannerListener;
    }
    /**
     * 设置适配器
     * @param bannerAdapter
     */
    public void setAdapter(BannerAdapter bannerAdapter) {
        if(bannerAdapter == null) {
            throw new RuntimeException("you don't set BannerAdapter");
        }
        if(bannerAdapter.getCount() == 0){
            return;
        }
        mBanAdapter = bannerAdapter;
        mBannerVp.setAdapter(bannerAdapter);
        if(mCanLooper) mBannerVp.startRoll();
        if(mMode == 1) {
            mBannerVp.setPageTransformer(true, new CoverModeTransformer());
        }else if(mMode == 2){
            mBannerVp.setPageTransformer(true, new DepthTransformer());
        }

        initDot();

        mBannerVp.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                pagerSelected(position);

            }
        });


        //第一条广告介绍
        String desc = mBanAdapter.getDesc(0);
        mBannerDesc.setText(desc);
    }

    /**
     * 添加拦截时间，按下时停止切换，抬起时开始切换
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        switch (ev.getAction()){
            // 按住Banner的时候，停止自动轮播
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_DOWN:
                int paddingLeft = mBannerVp.getLeft();
                float touchX = ev.getRawX();
                // 如果是魅族模式，去除两边的区域
                if(touchX >= paddingLeft && touchX < getScreenWidth(getContext()) - paddingLeft){
                    mBannerVp.stopRoll();
                }
                break;
            case MotionEvent.ACTION_UP:
                //抬起重新开始切换
                mBannerVp.startRoll();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }



    /**
     * 页面切换的回调
     */
    private void pagerSelected(int position) {
        IndicationDotView oldDot = (IndicationDotView) mDotContainer.getChildAt(mCurrentPosition);
        oldDot.setDrawable(mDotNormalDrawable);
        mCurrentPosition = position%mBanAdapter.getCount();
        IndicationDotView newDot = (IndicationDotView) mDotContainer.getChildAt(mCurrentPosition);
        newDot.setDrawable(mDotSelectDrawable);

        mBannerDesc.setText(mBanAdapter.getDesc(mCurrentPosition));
    }

    /**
     * 初始化指示点
     */
    private void initDot() {
        int count  = mBanAdapter.getCount();

        RelativeLayout.LayoutParams containerParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        containerParams.addRule(getDotGravity(mDotLocation));
        mDotContainer.setLayoutParams(containerParams);

        for (int i = 0; i < count; i++) {
            IndicationDotView dotView = new IndicationDotView(mContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mDotSize,mDotSize);
            params.rightMargin = params.leftMargin = mDotDistance;
            dotView.setLayoutParams(params);
            //第一个位置为选中位置
            if(i==0){
                dotView.setDrawable(mDotSelectDrawable);
            }else{
                dotView.setDrawable(mDotNormalDrawable);
            }
            //添加到布局中
            mDotContainer.addView(dotView);
        }
    }

    /**
     * 根据自定义属性值来显示不同的位置
     * @param mDotLocation
     * @return
     */
    private int getDotGravity(int mDotLocation) {
        switch (mDotLocation){
            case -1:
                return RelativeLayout.ALIGN_PARENT_LEFT;
            case 0:
                return RelativeLayout.CENTER_IN_PARENT;
            case 1:
                return RelativeLayout.ALIGN_PARENT_RIGHT;
        }
        return RelativeLayout.CENTER_IN_PARENT;
    }

    /**
     * dip转px
     * @param dip
     * @return
     */
    public int dp2px(int dip){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dip,getResources().getDisplayMetrics());
    }

    private int getScreenWidth(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        int width = dm.widthPixels;
        return width;
    }
}
