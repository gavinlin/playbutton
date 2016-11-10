package com.gavincode.playbutton;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Property;

/**
 * Created by gavinlin on 7/11/2016.
 */

public class PlayDrawable extends Drawable {

    private static final Property<PlayDrawable, Float> PROGRESS =
            new Property<PlayDrawable, Float>(Float.class, "progress") {
                @Override
                public Float get(PlayDrawable object) {
                    return object.getProgress();
                }

                @Override
                public void set(PlayDrawable object, Float value) {
                    object.setProgress(value);
                }
            };

    private boolean isPlay;
    private float mProgress;
    private float mWidth;
    private float mHeight;

    private final Path mLeftBar = new Path();
    private final Path mRightBar = new Path();
    private final Path mRightStroke = new Path();
    private final Path mLeftStroke = new Path();
    private final Paint mPaint = new Paint();
    private final Paint mStrokePaint = new Paint();
    private final RectF mBounds = new RectF();
    private float mPauseBarWidth;
    private float mPauseBarHeight;
    private float mPauseBarDistance;

    private final float BAR_WIDTH_RATE = 5.4f;
    private final float BAR_HEIGHT_RATE = 1.8f;
    private final float BAR_DISTANCE_RATE = 6.5f;
    private final int STROKE_WIDTH = 2;


    public PlayDrawable(Context context) {
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeWidth(STROKE_WIDTH);
        mStrokePaint.setStrokeJoin(Paint.Join.ROUND);
        mStrokePaint.setColor(context.getResources().getColor(R.color.lightgrey));

    }

    @Override
    public void draw(Canvas canvas) {
        mLeftBar.rewind();
        mRightBar.rewind();
        mRightStroke.rewind();
        mLeftStroke.rewind();

        final float barDist = lerp(mPauseBarDistance, 0, mProgress);
        final float barWidth = lerp(mPauseBarWidth, mPauseBarHeight / 2f, mProgress);

        final float firstBarBottomRight = lerp(0, -mPauseBarHeight/4, mProgress);
        final float firstBarTopRight = lerp(-mPauseBarHeight, -mPauseBarHeight/4*3, mProgress);

        final float secondBarBottomLeft = lerp(0, -mPauseBarHeight/4, mProgress);
        final float secondBarTopLeft = lerp(-mPauseBarHeight, -mPauseBarHeight/4*3, mProgress);
        final float secondBarTopRight = lerp(-mPauseBarHeight, -mPauseBarHeight/2, mProgress);
        final float secondBarBottomRight = lerp (0, -mPauseBarHeight/2, mProgress);

        mLeftBar.moveTo(0, -mPauseBarHeight);
        mLeftBar.lineTo(0, 0);
        mLeftBar.lineTo(barWidth, firstBarBottomRight);
        mLeftBar.lineTo(barWidth, firstBarTopRight);
        mLeftBar.close();

        mRightBar.moveTo(barWidth + barDist, secondBarTopLeft);
        mRightBar.lineTo(barWidth + barDist, secondBarBottomLeft);
        mRightBar.lineTo(2 * barWidth + barDist, secondBarBottomRight);
        mRightBar.lineTo(2 * barWidth + barDist, secondBarTopRight);
        mRightBar.close();

        if (mProgress == 1) {
            mLeftStroke.moveTo(barWidth, firstBarTopRight);
            mLeftStroke.lineTo(0, -mPauseBarHeight );
            mLeftStroke.lineTo(0, 0);
            mLeftStroke.lineTo(barWidth, firstBarBottomRight);

            mRightStroke.moveTo(barWidth + barDist, secondBarBottomLeft);
            mRightStroke.lineTo(2 * barWidth + barDist, secondBarBottomRight);
            mRightStroke.lineTo(barWidth + barDist, secondBarTopLeft);
        }

        canvas.save();

        // Position the pause/play button in the center of the drawable's bounds.
        canvas.translate(mWidth / 2f - ((2 * barWidth + barDist) / 2f), mHeight / 2f + (mPauseBarHeight / 2f));

        canvas.drawPath(mLeftBar, mPaint);
        canvas.drawPath(mRightBar, mPaint);
        if (mProgress == 1) {
            canvas.drawPath(mLeftStroke, mStrokePaint);
            canvas.drawPath(mRightStroke, mStrokePaint);
        }
        else {
            canvas.drawPath(mRightBar, mStrokePaint);
            canvas.drawPath(mLeftBar, mStrokePaint);
        }
        canvas.restore();
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    public boolean isPlay() {
        return isPlay;
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public float getProgress() {
        return mProgress;
    }

    public void setProgress(float val) {
        mProgress = val;
        invalidateSelf();
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        mBounds.set(bounds);
        mWidth = mBounds.width();
        mHeight = mBounds.height();

        float base;
        if (mWidth <= mHeight) base = mWidth;
        else base = mHeight;

        mPauseBarWidth = base / BAR_WIDTH_RATE;
        mPauseBarHeight = base / BAR_HEIGHT_RATE;
        mPauseBarDistance = base / BAR_DISTANCE_RATE;
    }

    public Animator getPausePlayAnimator() {
        final Animator anim = ObjectAnimator.ofFloat(this, PROGRESS, isPlay ? 1 : 0, isPlay ? 0 : 1);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isPlay = !isPlay;
            }
        });
        return anim;
    }


    /**
     * Linear interpolate between a and b with parameter t.
     */
    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }
}
