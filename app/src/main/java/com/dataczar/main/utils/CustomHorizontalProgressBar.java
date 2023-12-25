package com.dataczar.main.utils;

import static android.graphics.drawable.GradientDrawable.Orientation.LEFT_RIGHT;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import androidx.core.view.ViewCompat;

import com.dataczar.R;

public class CustomHorizontalProgressBar extends FrameLayout {

    private static final int DEFAULT_ANIMATION_DURATION = 2000;
    private static final int DEFAULT_START_COLOR = Color.parseColor("#FF2D55");
    private static final int DEFAULT_END_COLOR = Color.parseColor("#0074FF");
    private static final int DEFAULT_cnt_COLOR = Color.parseColor("#4CD964");

    private final View one;
    private final View two;

    private int animationDuration;
    private int startColor;
    private int endColor;
    private int ceter;

    private int laidOutWidth;

    public CustomHorizontalProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        inflate(context, R.layout.my_horizontal_progress, this);
        readAttributes(attrs);

        one = findViewById(R.id.one);
        two = findViewById(R.id.two);

        ViewCompat.setBackground(one, new GradientDrawable(LEFT_RIGHT, new int[]{ startColor, endColor, ceter}));
        ViewCompat.setBackground(two, new GradientDrawable(LEFT_RIGHT, new int[]{ endColor, startColor ,ceter}));

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                laidOutWidth = CustomHorizontalProgressBar.this.getWidth();
                ValueAnimator animator = ValueAnimator.ofInt(0, 2 * laidOutWidth);
                animator.setInterpolator(new LinearInterpolator());
                animator.setRepeatCount(ValueAnimator.INFINITE);
                animator.setRepeatMode(ValueAnimator.RESTART);
                animator.setDuration(animationDuration);
                animator.addUpdateListener(updateListener);
                animator.start();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                else {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    private void readAttributes(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MyHorizontalProgress);
        animationDuration = a.getInt(R.styleable.MyHorizontalProgress_animationDuration, DEFAULT_ANIMATION_DURATION);
        startColor = a.getColor(R.styleable.MyHorizontalProgress_gradientStartColor, DEFAULT_START_COLOR);
        endColor = a.getColor(R.styleable.MyHorizontalProgress_gradientEndColor, DEFAULT_END_COLOR);
        ceter= a.getColor(R.styleable.MyHorizontalProgress_gradientCeterColor, DEFAULT_cnt_COLOR);
        a.recycle();
    }

    private ValueAnimator.AnimatorUpdateListener updateListener = new ValueAnimator.AnimatorUpdateListener() {

        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            int offset = (int) valueAnimator.getAnimatedValue();
            one.setTranslationX(calculateOneTranslationX(laidOutWidth, offset));
            two.setTranslationX(calculateTwoTranslationX(laidOutWidth, offset));
        }
    };

    private int calculateOneTranslationX(int width, int offset) {
        return (-1 * width) + offset;
    }

    private int calculateTwoTranslationX(int width, int offset) {
        if (offset <= width) {
            return offset;
        }
        else {
            return (-2 * width) + offset;
        }
    }
}
