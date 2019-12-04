package com.example.speedometer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;


public class SpeedometerView extends View {

    private static final String TAG ="Speedometer";
    private static final Paint ARC_PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint TEXT_MIN_VELOCITY_PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint TEXT_MAX_VELOCITY_PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint CENTRAL_NET = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint TEXT_VELOCITY = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint ARROW = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final float STROKE_WIDTH = 50f;
    private static final float START_ANGLE = 160f;
    private static float RADIUS = 300f;
//    private  float radius = 300f;

    private static final float FONT_MIN_SIZE = 36f;
    private static final float FONT_MAX_SIZE = 72f;
    private static final float NET_RADIUS = 40f;
    private static final float MAGIC_MULTIPLIER = 1.1f;
    private Shader shaderGradient;
//    = new LinearGradient(0, RADIUS, 2f *RADIUS, RADIUS,
//            new int[] { Color.GREEN, Color.YELLOW, Color.YELLOW, Color.RED }, null, Shader.TileMode.CLAMP);

    private static float END_ANGLE = 360f - ((START_ANGLE - 90f) * 2);
    private Rect mTextBounds = new Rect();
    private int mProgress;
    private RectF mArcRect;


    public int getProgress() {
        return mProgress;
    }

    public void setProgress(int progress) {
        mProgress = progress;
        invalidate();
    }

    public SpeedometerView(Context context) {
        this(context, null, 0);
    }

    public SpeedometerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SpeedometerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawArc(mArcRect, START_ANGLE, END_ANGLE, false, ARC_PAINT);
        canvas.drawCircle(mArcRect.width()/2, mArcRect.height()/2, NET_RADIUS, CENTRAL_NET);
        drawText(canvas);
        drawLine(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "onMeasure() called with: widthMeasureSpec = [" + MeasureSpec.toString(widthMeasureSpec) + "], heightMeasureSpec = [" + MeasureSpec.toString(heightMeasureSpec) + "]");
        float desiredWidth = Math.max(mTextBounds.width() + 2 * STROKE_WIDTH, getSuggestedMinimumWidth()) + getPaddingLeft() + getPaddingRight();
        float desiredHeight = Math.max(mTextBounds.height() + 2 * STROKE_WIDTH, getSuggestedMinimumWidth()) + getPaddingTop() + getPaddingBottom();
        int desiredSize = (int) (MAGIC_MULTIPLIER * Math.max(desiredHeight, desiredWidth));
        final int resolvedWidth = resolveSize(desiredSize, widthMeasureSpec);
        final int resolvedHeight = resolveSize(desiredSize, heightMeasureSpec);
        setMeasuredDimension(resolvedWidth, resolvedHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d(TAG, "onSizeChanged() called with: w = [" + w + "], h = [" + h + "], oldw = [" + oldw + "], oldh = [" + oldh + "]");
        final int size = Math.min(w, h);
        mArcRect = new RectF(getPaddingLeft() + STROKE_WIDTH / 2, STROKE_WIDTH / 2 + getPaddingTop(), size - STROKE_WIDTH / 2 - getPaddingRight(), size - STROKE_WIDTH / 2 - getPaddingBottom());
    }

    private void drawLine(Canvas canvas) {
        float angle = (float) (Math.PI * (mProgress + START_ANGLE) / 180 );
        float lengthOfArrow = (float) (mArcRect.width()/2*0.7);
        float x = (float) (mArcRect.width()/2+ lengthOfArrow * Math.cos(angle) + mTextBounds.width());
        float y = (float) (mArcRect.height()/2 + lengthOfArrow * Math.sin(angle));
        Pt[] myPath;
        if (mProgress>60 && mProgress<150){
            myPath = new Pt[]{new Pt(x, y), new Pt(mArcRect.width()/2 + NET_RADIUS * 0.25f, mArcRect.height()/2 ),
                    new Pt(mArcRect.width()/2 - NET_RADIUS * 0.25f, mArcRect.height()/2), new Pt(x, y)};
        }else {
            myPath = new Pt[]{new Pt(x, y), new Pt(mArcRect.width()/2, (mArcRect.height()/2 + NET_RADIUS * 0.25f)),
                    new Pt(mArcRect.width()/2, mArcRect.height()/2 - NET_RADIUS * 0.25f), new Pt(x, y)};
        }
        Path path = new Path();
        path.moveTo(myPath[0].x, myPath[0].y);
        for (int i = 1; i < myPath.length; i++){
            path.lineTo(myPath[i].x, myPath[i].y);
        }
        canvas.drawPath(path, ARROW);
    }

    private void drawText(Canvas canvas) {
        float xForMin = (float) (mArcRect.width()/2 - mArcRect.width()/2 * Math.sin(20));
        float y = (float) (mArcRect.height()/2 + mArcRect.height()/2 * Math.cos(20)) + FONT_MIN_SIZE;
        canvas.drawText("0 км/ч", xForMin, y, TEXT_MIN_VELOCITY_PAINT);

        float xForMax = (float) (mArcRect.width()/2 + mArcRect.width()/2 * Math.sin(20) - 2*FONT_MIN_SIZE);
        canvas.drawText("220 км/ч", xForMax, y, TEXT_MAX_VELOCITY_PAINT);

        final String progressString = formatString(mProgress);
        float xForV = mArcRect.width() / 3f - mTextBounds.width() / 2f - mTextBounds.left;
        float yForV = mArcRect.height() / 2f + mTextBounds.height() / 2f - mTextBounds.bottom +
                mArcRect.top +(float) Math.PI*NET_RADIUS;
        canvas.drawText(progressString, xForV, yForV, TEXT_VELOCITY);
    }



    private String formatString(int progress) {
        return String.format("%d км/ч", progress);
    }

    private void init(Context context, AttributeSet attrs) {
        configureArc();
        configureNet();
        configureText();
        configureArrow();
    }


    private void configureArrow() {
        ARROW.setColor(Color.GRAY);
        ARROW.setStyle(Paint.Style.FILL);
    }


    private void configureText() {
        TEXT_MIN_VELOCITY_PAINT.setTextSize(FONT_MIN_SIZE);
        TEXT_MAX_VELOCITY_PAINT.setTextSize(FONT_MIN_SIZE);
        TEXT_VELOCITY.setTextSize(FONT_MAX_SIZE);
    }


    private void configureArc() {
        ARC_PAINT.setColor(Color.GREEN);
        ARC_PAINT.setStyle(Paint.Style.STROKE);
        ARC_PAINT.setStrokeWidth(STROKE_WIDTH);
//        RADIUS = mArcRect.width()/2;
        shaderGradient = new LinearGradient(0, RADIUS, 2f *RADIUS, RADIUS,
                new int[] { Color.GREEN, Color.YELLOW, Color.YELLOW, Color.RED }, null, Shader.TileMode.CLAMP);
        ARC_PAINT.setShader(shaderGradient);
    }


    private void configureNet() {
        CENTRAL_NET.setColor(Color.GRAY);
        CENTRAL_NET.setStyle(Paint.Style.FILL);
    }

}

class Pt{
    float x, y;
    Pt(float x, float y){
        this.x = x;
        this.y = y;
    }
}
