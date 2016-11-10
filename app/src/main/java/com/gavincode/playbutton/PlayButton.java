package com.gavincode.playbutton;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

/**
 * Created by gavinlin on 7/11/2016.
 */

public class PlayButton extends FrameLayout {

    private PlayDrawable mPlayDrawable;
    private final Paint mPaint = new Paint();

    private AnimatorSet mAnimatorSet;
    private int mWidth;
    private int mHeight;

    public PlayButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPlayDrawable = new PlayDrawable(context);
        mPlayDrawable.setCallback(this);
        setClickable(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            setAttr();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setAttr() {
        int [] attrs = new int[] {android.R.attr.selectableItemBackground};
        TypedArray ta = getContext().obtainStyledAttributes(attrs);
        Drawable drawableFromTheme = ta.getDrawable(0);
        ta.recycle();
        setForeground(drawableFromTheme);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int size = Math.min(getMeasuredWidth(), getMeasuredHeight());
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPlayDrawable.draw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPlayDrawable.setBounds(0, 0, w, h);
        mWidth = w;
        mHeight = h;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setOutlineProvider(new ViewOutlineProvider() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, view.getWidth(), view.getHeight());
                }
            });
            setClipToOutline(true);
        }
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return who == mPlayDrawable || super.verifyDrawable(who);
    }

    @Override
    public boolean performClick() {
        toggle();
        return super.performClick();
    }

    private void toggle() {
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
        }

        mAnimatorSet = new AnimatorSet();
        final Animator pausePlayAnim = mPlayDrawable.getPausePlayAnimator();
        mAnimatorSet.setInterpolator(new DecelerateInterpolator());
        mAnimatorSet.setDuration(200);
        mAnimatorSet.playSequentially(pausePlayAnim);
        mAnimatorSet.start();
    }
}
