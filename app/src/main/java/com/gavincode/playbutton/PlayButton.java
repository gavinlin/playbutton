package com.gavincode.playbutton;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

/**
 * Created by gavinlin on 7/11/2016.
 */

public class PlayButton extends FrameLayout {

    private static final Property<PlayButton, Integer> COLOR =
            new Property<PlayButton, Integer>(Integer.class, "color") {
                @Override
                public Integer get(PlayButton v) {
                    return v.getColor();
                }

                @Override
                public void set(PlayButton v, Integer value) {
                    v.setColor(value);
                }
            };

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
        mPaint.setColor(getContext().getResources().getColor(android.R.color.black));
        final float radius = Math.min(mWidth, mHeight) / 2f;
        canvas.drawCircle(mWidth / 2f, mHeight / 2f, radius, mPaint);
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

    private int getColor() {
        return getContext().getResources().getColor(android.R.color.black);
    }

    private void setColor(int color) {

    }

    public void toggle() {
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
        }

        mAnimatorSet = new AnimatorSet();
        final ObjectAnimator colorAnim = ObjectAnimator.ofInt(this, COLOR, getContext().getResources().getColor(android.R.color.black));
        colorAnim.setEvaluator(new ArgbEvaluator());
        final Animator pausePlayAnim = mPlayDrawable.getPausePlayAnimator();
        mAnimatorSet.setInterpolator(new DecelerateInterpolator());
        mAnimatorSet.setDuration(200);
        mAnimatorSet.playTogether(colorAnim, pausePlayAnim);
        mAnimatorSet.start();
    }

}
