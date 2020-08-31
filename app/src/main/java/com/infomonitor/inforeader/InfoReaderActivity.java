package com.infomonitor.inforeader;



import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
//import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.ToxicBakery.viewpager.transforms.RotateUpTransformer;

import com.infomonitor.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/2/2.
 */
public class InfoReaderActivity extends AppCompatActivity {

    // 导航栏的条目数
    public static int BAR_NUMBER = 6;
    // 导航栏到父容器的偏移值
    public static int SCREEN_OFFSET = 20;
    // 导航条与导航栏的每一项的偏移值
    public static int BAR_OFFSET = 10;
    // 导航条的高度
    public static int BAR_HEIGHT = 8;
    // 默认选中的项
    public static int BAR_DEFAULT_POSITION = 0;

    //滚动条的宽度，动态决定
    private int bar_width = 0;
    //当前选中项
    private int bar_current_position = BAR_DEFAULT_POSITION;
    //滚动条的图片
    private Bitmap bitmap = null;

    //是否是第一次调用
    private boolean isFirst = true;




    ImageView imageView; // 指示器
    private LinearLayout linearLayout = null;
    int bitWidth; // 图片宽度
    int offset = 0; // 偏移量
    int tabIndex = 0; // 全局index,用以标示当前的index
    boolean isScroll = false;
    TextView systemLabel, cpuLabel,sdLabel,ramLabel, gpsLabel, netinfoLabel;

    FragmentBase systeminfoFragment;
    FragmentBase cpuinfoFragment;
    FragmentBase sdinfoFragment;
    FragmentBase raminfoFragment;
    FragmentBase gpsinfoFragment;
    FragmentBase netinfoFragment;

    FragmentManager fragmentManager;

    ViewPager viewPager;
    private ArrayList<Fragment> fragmentList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inforeader_activity);

       // InitImageView();
        InitTextView();
        initialParams();

        systeminfoFragment = new SysteminfoFragment();
        cpuinfoFragment = new CpuinfoFragment();
        sdinfoFragment = new SdinfoFragment();
        raminfoFragment = new RaminfoFragment();
        gpsinfoFragment = new GpsinfoFragment();
        netinfoFragment = new NetinfoFragment();

        // 开启事物，添加第一个fragment
        fragmentManager = getSupportFragmentManager();
        fragmentList = new ArrayList<Fragment>();//将要分页显示的View装入数组中
        fragmentList.add(systeminfoFragment);
        fragmentList.add(cpuinfoFragment);
        fragmentList.add(sdinfoFragment);
        fragmentList.add(raminfoFragment);
        fragmentList.add(gpsinfoFragment);
        fragmentList.add(netinfoFragment);
        
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        new MyFragmentPagerAdapter(getSupportFragmentManager(), viewPager, fragmentList);
        viewPager.setCurrentItem(0);
        viewPager.setPageTransformer(true, new RotateUpTransformer());
        fragmentManager.removeOnBackStackChangedListener(new OnBackStackChangedListener() {

            @Override
            public void onBackStackChanged() {
                // TODO Auto-generated method stub

            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();

        if (isFirst) {
            isFirst = false;
            //这个方法不能在onCreate中调用，因为在onCreate中ImageView还没绘制完
            initialBarPosition();
        }
    }

    private void initialParams() {
        //初始化屏幕参数
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //得到屏幕的宽度
        int screen_width = displayMetrics.widthPixels;

        //设置导航栏的宽度
        ViewGroup.LayoutParams param_ll = linearLayout.getLayoutParams();
        param_ll.width = screen_width - 2 * SCREEN_OFFSET ;
        linearLayout.setLayoutParams(param_ll);

        //设置导航条图片的宽高
        bar_width = param_ll.width / 6 - 2 * BAR_OFFSET;
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cursor);
        bitmap = resizeImage(bitmap, bar_width, BAR_HEIGHT);
        imageView.setImageBitmap(bitmap);
    }

    /**
     * 缩放图片
     * @param bitmap 原图
     * @param newWidth 新宽度
     * @param newHeight 新高度
     * @return 新图
     * @author chenchen  2014-11-20
     */
    public Bitmap resizeImage(Bitmap bitmap, int newWidth, int newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        if (scaleWidth <= 0 || scaleHeight <= 0) {
            return bitmap;
        }
        // 缩放
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 创建新图
        Bitmap resizedBitmap = null;
        try {
            resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        } catch (OutOfMemoryError e) {
            System.gc();
            resizedBitmap = null;
            return bitmap;
        }
        return resizedBitmap;
    }

    /**
     * 初始化bar的位置，将bar移动到默认项的正中间
     * @author chenchen  2014-11-20
     */
    private void initialBarPosition() {
        Matrix matrix = new Matrix();
        int offset = BAR_OFFSET + BAR_DEFAULT_POSITION * (2 * BAR_OFFSET + bar_width);
        Log.i("MainActivity", "offset="+offset);
        matrix.postTranslate(offset , 0);
        imageView.setImageMatrix(matrix);
    }

    private void InitTextView() {
        imageView = (ImageView) findViewById(R.id.cursor);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout1);

        systemLabel = (TextView) findViewById(R.id.systemlabel);
        cpuLabel = (TextView) findViewById(R.id.cpulabel);
        sdLabel = (TextView) findViewById(R.id.sdlabel);
        ramLabel = (TextView) findViewById(R.id.ramlabel);
        gpsLabel = (TextView) findViewById(R.id.gpslabel);
        netinfoLabel = (TextView) findViewById(R.id.netinfolabel);

        systemLabel.setOnClickListener(new MyOnClickListener(0));
        cpuLabel.setOnClickListener(new MyOnClickListener(1));
        sdLabel.setOnClickListener(new MyOnClickListener(2));
        ramLabel.setOnClickListener(new MyOnClickListener(3));
        gpsLabel.setOnClickListener(new MyOnClickListener(4));
        netinfoLabel.setOnClickListener(new MyOnClickListener(5));
    }

    class MyOnClickListener implements View.OnClickListener {

        int index; // 保存点击时传入的index

        public MyOnClickListener(int index) {
            this.index = index;
        }

        @Override
        public void onClick(View v) {

            isScroll = false;
            //使页面的index做出相应改变
            viewPager.setCurrentItem(index);
            scrollCursor(index);

        }
    }
    public void scrollCursor(int index) {
        int click_position = index;
        //如果当前项不等于点击的项，则移动
        if(bar_current_position != click_position){
            //移动bar
            moveAnimation(bar_current_position, click_position);
            bar_current_position = click_position;
            //Toast.makeText(this, "第"+click_position+"项", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 移动动画
     * @param start 移动的起点按钮
     * @param end 移动的终点按钮
     * @author chenchen  2014-11-20
     */
    private void moveAnimation(int start, int end) {
        // 要移动的距离
        int length = (2 * BAR_OFFSET + bar_width) * (end - start);
        // 初始位置，默认的ImageView在默认项的位置
        int offset = (2 * BAR_OFFSET + bar_width) * (start - BAR_DEFAULT_POSITION );
        Animation animation = new TranslateAnimation(offset, offset + length, 0, 0);
        // 动画结束后，View停留在结束的位置
        animation.setFillAfter(true);
        animation.setDuration(300);
        imageView.startAnimation(animation);
    }



    public class MyFragmentPagerAdapter extends PagerAdapter implements
            ViewPager.OnPageChangeListener {
        ArrayList<Fragment> list;
        private int currentPageIndex = 0;
        private FragmentManager fragmentManager;
        private OnExtraPageChangeListener onExtraPageChangeListener;
        private ViewPager viewPager;

        public MyFragmentPagerAdapter(FragmentManager fm, ViewPager viewPager,
                                      ArrayList<Fragment> list) {
            super();
            this.list = list;
            this.fragmentManager = fm;
            this.viewPager = viewPager;
            this.viewPager.setAdapter(this);
            this.viewPager.setOnPageChangeListener(this);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = list.get(position);
            if (!fragment.isAdded()) { // 如果fragment还没有added
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.add(fragment, fragment.getClass().getSimpleName());
                ft.commit();
                /**
                 * 在用FragmentTransaction.commit()方法提交FragmentTransaction对象后
                 * 会在进程的主线程中，用异步的方式来执行。 如果想要立即执行这个等待中的操作，就要调用这个方法（只能在主线程中调用）。
                 * 要注意的是，所有的回调和相关的行为都会在这个调用中被执行完成，因此要仔细确认这个方法的调用位置。
                 */
                fragmentManager.executePendingTransactions();
            }

            if (fragment.getView().getParent() == null) {
                container.addView(fragment.getView()); // 为viewpager增加布局
            }

            return fragment.getView();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //container.removeView(list.get(position).getView()); //
            // 移出viewpager两边之外的page布局
            Log.d(FragmentBase.TAG, "destroy item");
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0 == arg1;
        }

        /**
         * 当前page索引（切换之前）
         *
         * @return
         */
        public int getCurrentPageIndex() {
            return currentPageIndex;
        }

        public OnExtraPageChangeListener getOnExtraPageChangeListener() {
            return onExtraPageChangeListener;
        }

        /**
         * 设置页面切换额外功能监听器
         *
         * @param onExtraPageChangeListener
         */
        public void setOnExtraPageChangeListener(
                OnExtraPageChangeListener onExtraPageChangeListener) {
            this.onExtraPageChangeListener = onExtraPageChangeListener;
        }


        public void onPageScrolled(int i, float v, int i2) {
            isScroll = true;
            //Log.d(FragmentBase.TAG, "page scrolled");
            if (null != onExtraPageChangeListener) { // 如果设置了额外功能接口
                onExtraPageChangeListener.onExtraPageScrolled(i, v, i2);
            }
        }

        public void onPageSelected(int i) {
            list.get(currentPageIndex).onPause(); // 调用切换前Fargment的onPause()
            // fragments.get(currentPageIndex).onStop(); //
            // 调用切换前Fargment的onStop()
            Log.d(FragmentBase.TAG, "page selected");
            if (list.get(i).isAdded()) {
                // fragments.get(i).onStart(); // 调用切换后Fargment的onStart()
                list.get(i).onResume(); // 调用切换后Fargment的onResume()
            }
            currentPageIndex = i;
            if (isScroll)
                //使导航栏游标的index做出相应改变。
                scrollCursor(currentPageIndex);
            if (null != onExtraPageChangeListener) { // 如果设置了额外功能接口
                onExtraPageChangeListener.onExtraPageSelected(i);
            }

        }

        public void onPageScrollStateChanged(int i) {
            if (null != onExtraPageChangeListener) { // 如果设置了额外功能接口
                onExtraPageChangeListener.onExtraPageScrollStateChanged(i);
            }
        }

        /**
         * page切换额外功能接口
         */
    }

    static class OnExtraPageChangeListener {
        public void onExtraPageScrolled(int i, float v, int i2) {
        }

        public void onExtraPageSelected(int i) {
        }

        public void onExtraPageScrollStateChanged(int i) {
        }
    }
}
