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
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;


public class SpeedometerView extends View {

    private static final Paint ARC_PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint TEXT_MIN_VELOCITY_PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint TEXT_MAX_VELOCITY_PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint CENTRAL_NET = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint TEXT_VELOCITY = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint ARROW = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final float STROKE_WIDTH = 50f;
    private static final float RADIUS = 300f;
    private static final RectF ARC_RECT = new RectF(STROKE_WIDTH / 2, STROKE_WIDTH / 2, 2 * RADIUS, 2 * RADIUS);
    private static final float START_ANGLE = 160f;
    private static final Shader SHADER_GRADIENT = new LinearGradient(0, RADIUS, 2f *RADIUS, RADIUS,
            new int[] { Color.GREEN, Color.YELLOW, Color.YELLOW, Color.RED }, null, Shader.TileMode.CLAMP);
    private static final float FONT_MIN_SIZE = 36f;
    private static final float FONT_MAX_SIZE = 72f;
    private static final float NET_RADIUS = 40f;


    private static float END_ANGLE = 360f - ((START_ANGLE - 90f) * 2);
    private Rect mTextBounds = new Rect();
    private int mProgress;

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
        canvas.drawArc(ARC_RECT, START_ANGLE, END_ANGLE, false, ARC_PAINT);
        canvas.drawCircle(RADIUS, RADIUS, NET_RADIUS, CENTRAL_NET);
        drawText(canvas);
        drawLine(canvas);
    }

    private void drawLine(Canvas canvas) {
        float angle = (float) (Math.PI * (mProgress + START_ANGLE) / 180 );
        float lengthOfArrow = (float) (RADIUS*0.7);
        float x = (float) (RADIUS+ lengthOfArrow * Math.cos(angle) + mTextBounds.width());
        float y = (float) (RADIUS + lengthOfArrow * Math.sin(angle));
        Pt[] myPath;
        if (mProgress>60 && mProgress<150){
            myPath = new Pt[]{new Pt(x, y), new Pt(RADIUS + NET_RADIUS * 0.25f, (RADIUS )),
                    new Pt(RADIUS - NET_RADIUS * 0.25f, RADIUS), new Pt(x, y)};
        }else {
            myPath = new Pt[]{new Pt(x, y), new Pt(RADIUS, (RADIUS + NET_RADIUS * 0.25f)),
                    new Pt(RADIUS, RADIUS - NET_RADIUS * 0.25f), new Pt(x, y)};
        }
        Path path = new Path();
        path.moveTo(myPath[0].x, myPath[0].y);
        for (int i = 1; i < myPath.length; i++){
            path.lineTo(myPath[i].x, myPath[i].y);
        }
        canvas.drawPath(path, ARROW);
    }

    private void drawText(Canvas canvas) {
        float xForMin = (float) (RADIUS - RADIUS * Math.sin(20));
        float y = (float) (RADIUS + RADIUS * Math.cos(20)) + FONT_MIN_SIZE;
        canvas.drawText("0 км/ч", xForMin, y, TEXT_MIN_VELOCITY_PAINT);

        float xForMax = (float) (RADIUS + RADIUS * Math.sin(20));
        canvas.drawText("220 км/ч", xForMax, y, TEXT_MAX_VELOCITY_PAINT);

        final String progressString = formatString(mProgress);
        getTextBounds(progressString);
        float xForV = ARC_RECT.width() / 3f - mTextBounds.width() / 2f - mTextBounds.left;
        float yForV = ARC_RECT.height() / 2f + mTextBounds.height() / 2f - mTextBounds.bottom +
                ARC_RECT.top + 2*NET_RADIUS;
        canvas.drawText(progressString, xForV, yForV, TEXT_VELOCITY);
    }

    private void getTextBounds(String progressString) {
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
        ARC_PAINT.setShader(SHADER_GRADIENT);
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
