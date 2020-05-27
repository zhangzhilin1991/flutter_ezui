package com.nyiit.smartschool.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nyiit.smartschool.R;
import com.videogo.openapi.EZPlayer;
import com.videogo.openapi.EzvizAPI;

public class VideoPlayer extends FrameLayout implements SurfaceHolder.Callback {
    private EZPlayer ezPlayer;

    private double aspect = 4 / 3.0;

    private SurfaceView sf_video;
    private ImageButton ib_add;

    public void hideSurface() {
        sf_video.setVisibility(GONE);
        ib_add.setVisibility(VISIBLE);
    }

    public void showSurface() {
        sf_video.setVisibility(VISIBLE);
        ib_add.setVisibility(GONE);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        ib_add.setOnClickListener(onClickListener);
    }

    public void setVideoPlayer(EZPlayer ezPlayer) {
        this.ezPlayer = ezPlayer;
    }

    public VideoPlayer(@NonNull Context context) {
        this(context, null, 0, 0);
    }

    public VideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public VideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        View view = LayoutInflater.from(context).inflate(R.layout.activity_videoplayer_surface, this);

        sf_video = view.findViewById(R.id.sf_player_surface);
        sf_video.getHolder().addCallback(this);
        ib_add = view.findViewById(R.id.ibtn_add_player);
    }

    /*@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int newHeightMeasureSpec = MeasureSpec.makeMeasureSpec((int)(MeasureSpec.getSize(widthMeasureSpec) / aspect), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, newHeightMeasureSpec);
    }
     */

    public SurfaceHolder getSurfaceHolder() {
        return sf_video.getHolder();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (ezPlayer != null) {
            ezPlayer.setSurfaceHold(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (ezPlayer != null) {
            ezPlayer.setSurfaceHold(holder);
        }
        ezPlayer.startRealPlay();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (ezPlayer != null) {
            ezPlayer.setSurfaceHold(null);
        }
    }
}
