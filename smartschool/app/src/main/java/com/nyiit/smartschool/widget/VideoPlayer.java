package com.nyiit.smartschool.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nyiit.smartschool.R;
import com.nyiit.smartschool.ui.videoplay.VideoPlayActivity;
import com.videogo.errorlayer.ErrorInfo;
import com.videogo.openapi.EZConstants;
import com.videogo.openapi.EZPlayer;
import com.videogo.openapi.EzvizAPI;

import java.lang.ref.WeakReference;

public class VideoPlayer extends FrameLayout implements SurfaceHolder.Callback {
    private static final String TAG = VideoPlayer.class.getName();

    private EZPlayer ezPlayer;

    private double aspect = 4 / 3.0;

    private SurfaceView sf_video;
    private ImageButton ib_add;
    private ProgressBar pb_loading;
    private Handler handler;

    private boolean isLoading = true;

    public void hideSurface() {
        sf_video.setVisibility(GONE);
        ib_add.setVisibility(VISIBLE);
        pb_loading.setVisibility(GONE);
    }

    public void showSurface() {
        pb_loading.setVisibility(isLoading?VISIBLE: GONE);
        sf_video.setVisibility(VISIBLE);
        ib_add.setVisibility(GONE);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        ib_add.setOnClickListener(onClickListener);
    }

    public void setVideoPlayer(EZPlayer ezPlayer) {
        showSurface();
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
        pb_loading = view.findViewById(R.id.pb_loading);
        handler = new VideoPlayerHandler();
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
            ezPlayer.setHandler(handler);
            ezPlayer.setSurfaceHold(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (ezPlayer != null) {
            ezPlayer.setHandler(handler);
            ezPlayer.setSurfaceHold(holder);
        }
        ezPlayer.startRealPlay();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (ezPlayer != null) {
            ezPlayer.setHandler(null);
            ezPlayer.setSurfaceHold(null);
        }
    }

    class VideoPlayerHandler extends Handler {

        @Override
        public void handleMessage(@NonNull Message msg) {
            //handlemessage
            switch (msg.what) {
                case EZConstants.EZRealPlayConstants.MSG_REALPLAY_PLAY_SUCCESS:
                    //播放成功
                    Log.d(TAG, "handleMessage() MSG_REALPLAY_PLAY_SUCCESS");
                    isLoading = false;
                    showSurface();
                    break;
                case EZConstants.EZRealPlayConstants.MSG_REALPLAY_PLAY_FAIL:
                    Log.d(TAG, "handleMessage() MSG_REALPLAY_PLAY_FAIL");
                    //播放失败,得到失败信息
                    ErrorInfo errorinfo = (ErrorInfo) msg.obj;
                    //得到播放失败错误码
                    int code = errorinfo.errorCode;
                    //得到播放失败模块错误码
                    String codeStr = errorinfo.moduleCode;
                    //得到播放失败描述
                    String description = errorinfo.description;
                    //得到播放失败解决方方案
                    String solution = errorinfo.sulution;
                    Log.d(TAG, "handleMessage() MSG_REALPLAY_PLAY_FAIL: moduleCode: "
                            + codeStr + ", description: " + description + ", solution: " + solution);
                    break;
                case EZConstants.MSG_VIDEO_SIZE_CHANGED:
                    //Log.d(TAG, "handleMessage() MSG_VIDEO_SIZE_CHANGED");
                    //解析出视频画面分辨率回调
                    try {
                        String temp = (String) msg.obj;
                        String[] strings = temp.split(":");
                        int mVideoWidth = Integer.parseInt(strings[0]);
                        int mVideoHeight = Integer.parseInt(strings[1]);
                        //解析出视频分辨率
                        Log.d(TAG, "handleMessage() MSG_VIDEO_SIZE_CHANGED mVideoWidth: " + mVideoWidth + ", mVideoHeight: " + mVideoHeight);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }

            super.handleMessage(msg);
        }
    }
}
