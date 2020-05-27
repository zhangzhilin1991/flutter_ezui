package com.nyiit.smartschool;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.nyiit.smartschool.adapter.VideoPlayerPageAdapter;
import com.nyiit.smartschool.bean.VideoPlayerBean;
import com.nyiit.smartschool.constants.IntentConstants;
import com.nyiit.smartschool.util.Fileutils;
import com.nyiit.smartschool.widget.VideoPlayer;
import com.videogo.errorlayer.ErrorInfo;
import com.videogo.exception.BaseException;
import com.videogo.openapi.EZConstants;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.EZOpenSDKListener;
import com.videogo.openapi.EZPlayer;
import com.videogo.openapi.bean.EZCameraInfo;
import com.videogo.openapi.bean.EZDeviceInfo;
import com.videogo.util.LogUtil;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.nyiit.smartschool.util.Fileutils.getRecordFilePath;


public class VideoPlayerActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = VideoPlayerActivity.class.getName();

    private Map<Integer, VideoPlayerBean> ezPlayerMaps;

    private ViewPager viewPager;
    private TabLayout tabLayout;
    VideoPlayerPageAdapter videoPlayerPageAdapter;
    List<View> viewList = new ArrayList<>();

    private int currentPlayer = 0;
    protected boolean isRecording = false;

    private VideoPlayer videoPlayer1;
    private VideoPlayer videoPlayer2;
    private VideoPlayer videoPlayer3;
    private VideoPlayer videoPlayer4;
    private ImageButton takePhoto;
    private ImageButton recordVideo;
    private ImageButton setResolution;
    private ImageButton deleteVideo;
    private ImageButton switchMute;

    private Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_4chanel_videoplayer);

        handler = new VideoPlayerHandler(this);
        ezPlayerMaps = ((App)getApplication()).getEzPlayerMaps();

        initView();

        handleIntent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //startplayVieo();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //stopPlayVideo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //for (VideoPlayerBean videoPlayerBean : ezPlayerMaps.values()) {
            //System.out.println("Value = " + value);
        //    videoPlayerBean.getEzPlayer().release(); //release player.
        //}
        stopPlayVideo();
    }

    private void initView() {
        //sf1 = findViewById(R.id.sf_player_surface1);
        //sf2 = findViewById(R.id.sf_player_surface2);
        //sf3 = findViewById(R.id.sf_player_surface3);
        //sf4 = findViewById(R.id.sf_player_surface4);
        //videoPlayer1 = new VideoPlayer(this);
        //videoPlayer2 = new VideoPlayer(this);
        //videoPlayer3 = new VideoPlayer(this);
        //videoPlayer4 = new VideoPlayer(this);
        takePhoto = findViewById(R.id.iv_take_photo);
        recordVideo = findViewById(R.id.iv_record_video);
        setResolution = findViewById(R.id.iv_set_resolution);
        deleteVideo = findViewById(R.id.delete_video);
        switchMute = findViewById(R.id.iv_switch_mute);

        takePhoto.setOnClickListener(this);
        recordVideo.setOnClickListener(this);
        setResolution.setOnClickListener(this);
        deleteVideo.setOnClickListener(this);
        switchMute.setOnClickListener(this);

        viewPager  = findViewById(R.id.vp_surfaceview);
        tabLayout = findViewById(R.id.tab_surfaceview);
        //RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)viewPager.getLayoutParams();
        //layoutParams.height = (int)(layoutParams.width * 4 / 3.0); //4/3
        //viewPager.setLayoutParams(layoutParams);

        //viewPager.setOnScrollChangeListener(new On);

        viewPager.setOffscreenPageLimit(3);
        for (int i = 0; i < 4 ; i++) {
            //TextView textView = new TextView(this);
            //textView.setText("这是第" + i + "个界面");
            VideoPlayer videoPlayer = new VideoPlayer(this);
            /*WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            int width = windowManager.getDefaultDisplay().getWidth();
            int height = windowManager.getDefaultDisplay().getHeight();

            ViewGroup.LayoutParams layoutParams = videoPlayer.getLayoutParams();
            layoutParams.width = width; // 4 / 3
            layoutParams.height = (int)(width * 3.0/ 4);
             */

            videoPlayer.setTag(i);
            videoPlayer.setId(R.id.fl_video_player);
            //videoPlayer.setOnClickListener(this);
            viewList.add(new VideoPlayer(this));
            //tabLayout.addTab(tabLayout.newTab().setText("视频" + i + 1));
            //tab.setCustomView();
            //tabLayout.getTabAt(i).setText("视频" + i + 1);
        }

        videoPlayerPageAdapter = new VideoPlayerPageAdapter(viewList);
        videoPlayerPageAdapter.setOnViewPageVClickListener(new VideoPlayerPageAdapter.OnViewPageVClickListener() {
            @Override
            public void onViewPageVClick(int pageIndex) {
                //Select new device.
                Intent intent = new Intent();
                intent.putExtra(IntentConstants.EXTRA_PLAYER_INDEX_KEY, pageIndex);
                setResult(IntentConstants.INTENT_RESPONSE_CODE_SELECT_NEW_DEVICE, intent);
                VideoPlayerActivity.this.finish();
            }
        });
        viewPager.setAdapter(videoPlayerPageAdapter);

        tabLayout.setupWithViewPager(viewPager);

        //for (int i = 0; i < 4; i++) { //设置player,必须先加再设置
            //tabLayout.addTab(tabLayout.newTab());
            //tabLayout.getTabAt(i).setText("视频" + i + 1);
        //}
    }

    private void handleIntent() {
        Intent intent = getIntent();
        int pageIndex = intent.getIntExtra(IntentConstants.EXTRA_PLAYER_INDEX_KEY, 0);
        EZCameraInfo ezCameraInfo = intent.getParcelableExtra(IntentConstants.EXTRA_CAMERA_INFO);
        EZDeviceInfo ezDeviceInfo = intent.getParcelableExtra(IntentConstants.EXTRA_DEVICE_INFO);
        //Intent;
        Log.d(TAG, "handleIntent() pageIndex: " + pageIndex);
        String deviceSerial = ezCameraInfo.getDeviceSerial();
        int cameraNo = ezCameraInfo.getCameraNo();
        //if (!ezPlayerMaps.containsKey(pageIndex)){
            EZPlayer ezPlayer = EZOpenSDK.getInstance().createPlayer(deviceSerial, cameraNo);
            ezPlayer.setHandler(handler);
            ezPlayerMaps.put(pageIndex, new VideoPlayerBean(ezCameraInfo, ezDeviceInfo, ezPlayer, pageIndex));
        //};
        viewPager.setCurrentItem(pageIndex);
        startplayVieo();
    }

    private void startplayVieo() {
        for (int i = 0; i < 4; i++) {
            if (ezPlayerMaps.containsKey(i)){
                EZPlayer ezPlayer = ezPlayerMaps.get(i).getEzPlayer();
                ezPlayer.setPlayVerifyCode("PDTYNU");
                VideoPlayer videoPlayer = (VideoPlayer)viewList.get(i);
                videoPlayer.showSurface();
                videoPlayer.setVideoPlayer(ezPlayer);
                //ezPlayer.setSurfaceHold(videoPlayer.getSurfaceHolder());
                ezPlayer.startRealPlay();
            }
        }
    }

    private void stopPlayVideo() {
        for (int i = 0; i < 4; i++) {
            if (ezPlayerMaps.containsKey(i)){
                EZPlayer ezPlayer = ezPlayerMaps.get(i).getEzPlayer();
                ezPlayer.stopRealPlay();
                //ezPlayer.setSurfaceHold(null);
            }
        }
    }

    @Override
    public void onClick(View v) {
        //Log.d(TAG, "onClick: " + v.getId());
        Log.d(TAG, "onClick() viewPager index: " + viewPager.getCurrentItem());
        switch (v.getId()){
            //EZPlayer ezPlayer;
            case R.id.iv_take_photo:
                int index = viewPager.getCurrentItem();
                if (ezPlayerMaps.containsKey(index)) {
                    EZCameraInfo ezCameraInfo = ezPlayerMaps.get(index).getEzCameraInfo();
                    String fileName = ezCameraInfo.getCameraName() + "_" + System.currentTimeMillis();
                    //File file = new File(DEFAULT_PICTURE_PATH);
                    //if(!file.exists()){
                    //    file.mkdirs();
                    //}
                    String filePath = Fileutils.getPictureFilePath(this, fileName);
                    Log.d(TAG, "onClick() take photo: filePath = " + filePath);
                    ezPlayerMaps.get(index).getEzPlayer().capturePicture(filePath);
                }
                break;
            case R.id.iv_record_video:
                int index1 = viewPager.getCurrentItem();
                if (ezPlayerMaps.containsKey(index1)) {
                    EZCameraInfo ezCameraInfo1 = ezPlayerMaps.get(index1).getEzCameraInfo();
                    if (isRecording) {
                        Log.d(TAG, "onClick() stop record video");
                        ezPlayerMaps.get(index1).getEzPlayer().stopLocalRecord();
                    } else {
                        String fileName1 = ezCameraInfo1.getCameraName() + "_" + System.currentTimeMillis() + ".mp4";
                        //File file2 = new File(DEFAULT_PICTURE_PATH);
                        //if(!file2.exists()){
                        //    file2.mkdirs();
                        //}
                        String filePath1 = getRecordFilePath(this, fileName1);
                        Log.d(TAG, "onClick() start record video: filePath = " + filePath1);
                        ezPlayerMaps.get(index1).getEzPlayer().setStreamDownloadCallback(new EZOpenSDKListener.EZStreamDownloadCallback() {
                            @Override
                            public void onSuccess(String filepath) {
                                Log.i(TAG, "EZStreamDownloadCallback onSuccess " + filepath);
                                //dialog("Record result", "saved to " + mCurrentRecordPath);
                            }

                            @Override
                            public void onError(EZOpenSDKListener.EZStreamDownloadError code) {
                                Log.e(TAG, "EZStreamDownloadCallback onError " + code.name());
                            }
                        });
                        if (ezPlayerMaps.get(index1).getEzPlayer().startLocalRecordWithFile(filePath1)){
                            Log.d(TAG, "recording");
                        } else {
                            Log.d(TAG, "recording failed");
                        }
                    }
                    isRecording = !isRecording;
                }
            case R.id.iv_switch_mute:
                    int index2 = viewPager.getCurrentItem();
                if (ezPlayerMaps.containsKey(index2)) {
                    EZPlayer ezPlayer = ezPlayerMaps.get(index2).getEzPlayer();
                    if (ezPlayer.isSpeakerphoneOn()) {
                        ezPlayer.closeSound();
                    } else {
                        ezPlayer.openSound();
                    }
                }
                break;
            case R.id.delete_video:
                int index3 = viewPager.getCurrentItem();
                if (ezPlayerMaps.containsKey(index3)) {
                    ezPlayerMaps.get(index3).getEzPlayer().release();
                    ezPlayerMaps.remove(index3); //删除当前
                    VideoPlayer videoPlayer = (VideoPlayer) viewList.get(index3);
                    videoPlayer.hideSurface();
                }
                break;
            case R.id.iv_set_resolution:
                int index4 = viewPager.getCurrentItem();
                if (ezPlayerMaps.containsKey(index4)) {
                    EZCameraInfo ezCameraInfo2 = ezPlayerMaps.get(index4).getEzCameraInfo();
                    String deviceSerial = ezCameraInfo2.getDeviceSerial();
                    int cameraNo = ezCameraInfo2.getCameraNo();
                    //try {
                        new Thread(new Runnable(){
                            @Override
                            public void run() {
                                //super.run();
                                try {
                                    EZOpenSDK.getInstance().setVideoLevel(deviceSerial, cameraNo, 1); //balance
                                    ezPlayerMaps.get(index4).getEzPlayer().stopRealPlay();
                                    ezPlayerMaps.get(index4).getEzPlayer().stopRealPlay();
                                } catch (BaseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    //} catch (BaseException e) {
                    //    e.printStackTrace();
                    //}
                }
                break;
        }
    }

    static class VideoPlayerHandler extends Handler {
        private WeakReference<VideoPlayerActivity> videoPlayerActivityWeakRef;

        public VideoPlayerHandler(VideoPlayerActivity videoPlayerActivity) {
            videoPlayerActivityWeakRef = new WeakReference<>(videoPlayerActivity);
        }


        @Override
        public void handleMessage(@NonNull Message msg) {
            VideoPlayerActivity videoPlayerActivity = videoPlayerActivityWeakRef.get();
            if (videoPlayerActivity == null) {
                return;
            }
            //handlemessage
            switch (msg.what) {
                case EZConstants.EZRealPlayConstants.MSG_REALPLAY_PLAY_SUCCESS:
                    //播放成功
                    Log.d(TAG, "handleMessage() MSG_REALPLAY_PLAY_SUCCESS");
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
