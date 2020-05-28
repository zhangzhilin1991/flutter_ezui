package com.nyiit.smartschool.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Date;

public class VideoPlaybackController extends View {

    private Date startDate;
    private Date stopDate;

    private float startX;
    private float startY;

    public VideoPlaybackController(Context context) {
        this(context, null);
    }

    public VideoPlaybackController(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoPlaybackController(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public VideoPlaybackController(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
        }

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.save();

        paint.setColor(Color.parseColor("#AAAAAA"));
        canvas.translate(0, height / 2);
        for (int i = 0; i < 25; i++) {
            canvas.drawText(i < 10 ?(0 + "i" + "：00"):("i" + ":00"),  i * width / 25, height / 2, paint);
        }
        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
