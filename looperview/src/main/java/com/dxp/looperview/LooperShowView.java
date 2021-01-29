package com.dxp.looperview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @Author: dangzp
 * @CreateTime: 2021/1/28 13:10
 * @Description:
 */
public class LooperShowView extends LinearLayout {
    private Handler mHandler;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private int switchInterval = 5000;  //切换间隔
    private int showTime = 4000;        //显示时间
    private List<String> messageList;   //消息列表
    private int position = -1;
    private int textColor = Color.parseColor("#ffffff");
    private int textSize = 12;
    private OnItemClickListener onItemClickListener;
    TextView textView = null;
    private int offset = 30; //偏移量
    private int animaterDuration = 500;  //动画时长

    public LooperShowView(Context context) {
        this(context,null,0);
    }

    public LooperShowView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LooperShowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs,defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr){
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LooperShowView, defStyleAttr, 0);
        showTime = typedArray.getInteger(R.styleable.LooperShowView_ls_show_time,showTime);
        switchInterval = typedArray.getInteger(R.styleable.LooperShowView_ls_switch_interval,switchInterval);
        textColor = typedArray.getColor(R.styleable.LooperShowView_ls_textcolor,textColor);
        textSize = typedArray.getDimensionPixelSize(R.styleable.LooperShowView_ls_textsize,textSize);
        typedArray.recycle();

        setClipChildren(false);
        setAlpha(0.0f);
        addView(createTextView());
        mHandler = new Handler();
        mTimer = new Timer();
        mTimerTask = new MyTimerTask();
    }

    private class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            //显示view
            postVisibility();
            postGone();
        }
    }

    //显示view
    private void postVisibility(){
        post(new Runnable() {
            @Override
            public void run() {
                if (position!=messageList.size()-1){
                    position ++;
                }else {
                    position = 0;
                }

                textView.setText(messageList.get(position));

                ValueAnimator animator = ValueAnimator.ofFloat(0.0f,1.0f);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        setAlpha((Float) valueAnimator.getAnimatedValue());
                        postInvalidate();
                    }
                });

                ValueAnimator animator1 = ValueAnimator.ofFloat(getY()-offset,getY());
                animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        setY((Float) valueAnimator.getAnimatedValue());
                        postInvalidate();
                    }
                });

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.setDuration(animaterDuration);
                animatorSet.playTogether(animator,animator1);
                animatorSet.start();
            }
        });
    }

    //隐藏view
    private void postGone(){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ValueAnimator animator = ValueAnimator.ofFloat(1.0f,0.0f);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        setAlpha((Float) valueAnimator.getAnimatedValue());
                        postInvalidate();
                    }
                });

                ValueAnimator animator1 = ValueAnimator.ofFloat(getY(),getY()-offset);
                animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        setY((Float) valueAnimator.getAnimatedValue());
                        postInvalidate();
                    }
                });

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.setDuration(animaterDuration);
                animatorSet.playTogether(animator,animator1);
                animatorSet.start();
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        //执行结束 回到之前的位置
                        setY(getY()+offset);
                        postInvalidate();
                    }
                });
            }
        },showTime);
    }

    /**
     * 外部调用，传入list
     * @param message
     */
    public void startWithList(List<String> message){
        this.messageList = message;
        if (messageList == null || messageList.isEmpty()) {
            throw new RuntimeException("The messages cannot be empty!");
        }
        position = -1;
        mTimer.schedule(mTimerTask,switchInterval,switchInterval+showTime);
    }

    /**
     * 数据源改变时，用于刷新数据
     */
    public void notifity(){
        if (mTimer!=null){
            mTimer.cancel();
            mTimerTask.cancel();
            mTimer = null;
            mTimerTask = null;
        }

        if (mHandler!=null){
            mHandler.removeCallbacksAndMessages(this);
        }

        position = -1;
        mTimer = new Timer();
        mTimerTask = new MyTimerTask();
        mTimer.schedule(mTimerTask,switchInterval,switchInterval+showTime);
    }

    private TextView createTextView(){
        setGravity(Gravity.CENTER_VERTICAL);
        if (textView == null) {
            textView = new TextView(getContext());
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setTextColor(textColor);
            textView.setTextSize(textSize);
            textView.setIncludeFontPadding(true);
            textView.setSingleLine(true);
            if (true) {
                textView.setMaxLines(1);
                textView.setEllipsize(TextUtils.TruncateAt.END);
            }

            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null && getAlpha()==1.0f) {
                        onItemClickListener.onItemClick(position, (TextView) v);
                    }
                }
            });
        }

        return textView;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, TextView textView);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mHandler!=null){
            mHandler.removeCallbacksAndMessages(this);
        }
        if (mTimer!=null){
            mTimer.cancel();
            mTimer = null;
        }
    }
}
