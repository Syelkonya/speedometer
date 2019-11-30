package com.example.speedometer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;


public class SpeedometerView extends View {

    private static final Paint ARC_PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint TEXT_MIN_VELOCITY_PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final float STROKE_WIDTH = 50f;
    private static final float RADIUS = 300f;
    private static final RectF ARC_RECT = new RectF(STROKE_WIDTH / 2, STROKE_WIDTH / 2, 2 * RADIUS, 2 * RADIUS);
    private static final float START_ANGLE = 160f;
    private static final Shader SHADER_GRADIENT = new LinearGradient(0, RADIUS, 2f *RADIUS, RADIUS,
            new int[] { Color.GREEN, Color.YELLOW, Color.YELLOW, Color.RED }, null, Shader.TileMode.CLAMP);

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
        float END_ANGLE = 360f - ((START_ANGLE - 90f) * 2);
        canvas.drawArc(ARC_RECT, START_ANGLE, END_ANGLE, false, ARC_PAINT);
        drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        float x = (float) ( RADIUS - RADIUS * Math.cos(20));
        float y = (float) (2*RADIUS - RADIUS * Math.sin(20));
        canvas.drawText("Гуси", x, y, TEXT_MIN_VELOCITY_PAINT);
    }

    private void init(Context context, AttributeSet attrs) {
        configureArc();
    }

    private void configureArc() {
        ARC_PAINT.setColor(Color.GREEN);
        ARC_PAINT.setStyle(Paint.Style.STROKE);
        ARC_PAINT.setStrokeWidth(STROKE_WIDTH);
        ARC_PAINT.setShader(SHADER_GRADIENT);
    }

}
