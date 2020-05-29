package com.nyiit.smartschool.ui.videoplay;

import android.Manifest;
import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.nyiit.smartschool.App;
import com.nyiit.smartschool.R;
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

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.view.Gravity.NO_GRAVITY;
import static com.ezviz.stream.EZError.EZ_OK;
import static com.nyiit.smartschool.util.Fileutils.getPicturePath;
import static com.nyiit.smartschool.util.Fileutils.getRecordFilePath;

public class VideoPlayActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = VideoPlayActivity.class.getName();

    private Map<Integer, VideoPlayerBean> ezPlayerMaps;

    private ViewPager viewPager;
    private TabLayout tabLayout;
    VideoPlayerPageAdapter videoPlayerPageAdapter;
    List<View> viewList = new ArrayList<>();

    private int currentPlayer = 0;
    protected boolean isRecording = false;
    private boolean isMute = false;

    private VideoPlayer videoPlayer1;
    private VideoPlayer videoPlayer2;
    private VideoPlayer videoPlayer3;
    private VideoPlayer videoPlayer4;
    private View controlPanel;
    private ImageButton takePhoto;
    private ImageButton recordVideo;
    private ImageButton setResolution;
    private ImageButton deleteVideo;
    private ImageButton switchMute;

    private Handler handler;

    private PopupWindow mQualityPopupWindow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_4chanel_videoplayer);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        handler = new VideoPlayerHandler(this);
        ezPlayerMaps = ((App)getApplication()).getEzPlayerMaps();

        initView();

        handleIntent();

        grantPermission();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_video_play, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        } else if (item.getItemId() == R.id.menu_open_pic_folder){
            String path = getPicturePath(this);
            //Fileutils.OpenAssignFolder(this, path);
            //ntent intent = getFileIntent(this, path, "image/*");
            Intent albumIntent = new Intent(Intent.ACTION_VIEW, null);
            albumIntent.setType("image/*,video/*");
            startActivity(albumIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void grantPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
                // 检查权限状态
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //  用户彻底拒绝授予权限，一般会提示用户进入设置权限界面
                } else {
                    //  用户未彻底拒绝授予权限
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PERMISSION_GRANTED) {
                    // 申请成功
                } else {
                    // 申请失败
                    this.finish();
                }
            }
        }
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult() requestCode: " + requestCode +
                ", resultCode: " + resultCode + ", data: " + data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_SELECT_FILE) {
            Uri uri = data.getData();
            if (uri != null) {
                Toast.makeText(this, "文件路径：" + uri.getPath().toString(), Toast.LENGTH_SHORT).show();
                //Intent intent = getImageFileIntent(uri.get);
                startActivity(getImageFileIntent(uri));
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
     */

    private void initView() {
        //sf1 = findViewById(R.id.sf_player_surface1);
        //sf2 = findViewById(R.id.sf_player_surface2);
        //sf3 = findViewById(R.id.sf_player_surface3);
        //sf4 = findViewById(R.id.sf_player_surface4);
        //videoPlayer1 = new VideoPlayer(this);
        //videoPlayer2 = new VideoPlayer(this);
        //videoPlayer3 = new VideoPlayer(this);
        //videoPlayer4 = new VideoPlayer(this);
        controlPanel = findViewById(R.id.videoplayer_control_panel);
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
                VideoPlayActivity.this.finish();
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
            //ezPlayer.setHandler(handler);
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
                    int ret = ezPlayerMaps.get(index).getEzPlayer().capturePicture(filePath);
                    if (ret == EZ_OK){
                        Toast.makeText(this, "图片保存到" + filePath, Toast.LENGTH_SHORT).show();
                        Fileutils.saveImage(this, new File(filePath));
                    } else {
                        Toast.makeText(this, "截图失败", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.iv_record_video:
                int index1 = viewPager.getCurrentItem();
                if (ezPlayerMaps.containsKey(index1)) {
                    EZCameraInfo ezCameraInfo1 = ezPlayerMaps.get(index1).getEzCameraInfo();
                    if (isRecording) {
                        Log.d(TAG, "onClick() stop record video");
                        Toast.makeText(VideoPlayActivity.this, "停止录制", Toast.LENGTH_SHORT).show();
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
                                runOnUiThread(new Runnable() {
                                                  @Override
                                                  public void run() {
                                                      Toast.makeText(VideoPlayActivity.this, "录制文件保存到" + filepath, Toast.LENGTH_SHORT).show();
                                                  }
                                              });
                                Fileutils.saveVideo(VideoPlayActivity.this, new File(filePath1));
                            }

                            @Override
                            public void onError(EZOpenSDKListener.EZStreamDownloadError code) {
                                //Log.e(TAG, "EZStreamDownloadCallback onError " + code.name());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(VideoPlayActivity.this, "录制失败！", Toast.LENGTH_SHORT).show();
                                    }
                            });
                            }
                        });
                        if (ezPlayerMaps.get(index1).getEzPlayer().startLocalRecordWithFile(filePath1)){
                            //Log.d(TAG, "recording");
                            Toast.makeText(this, "正在录制...,", Toast.LENGTH_SHORT).show();
                        } else {
                            //Log.d(TAG, "recording failed");
                            Toast.makeText(this, "开始录制失败！", Toast.LENGTH_SHORT).show();
                        }
                    }
                    isRecording = !isRecording;
                }
                break;
            case R.id.iv_switch_mute:
                    int index2 = viewPager.getCurrentItem();
                if (ezPlayerMaps.containsKey(index2)) {
                    EZPlayer ezPlayer = ezPlayerMaps.get(index2).getEzPlayer();
                    if (ezPlayerMaps.get(index2).isMute()) {
                        ezPlayer.openSound();
                        ezPlayerMaps.get(index2).setMute(false);
                        //switchMute
                        Toast.makeText(this, "取消静音！", Toast.LENGTH_SHORT).show();
                    } else {
                        ezPlayer.closeSound();
                        ezPlayerMaps.get(index2).setMute(true);
                        Toast.makeText(this, "已静音！", Toast.LENGTH_SHORT).show();
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
                    showResulationPopupWindow(index4);
                    //} catch (BaseException e) {
                    //    e.printStackTrace();
                    //}
                }
                break;
        }
    }

    private void showResulationPopupWindow(int index) {
        EZCameraInfo ezCameraInfo2 = ezPlayerMaps.get(index).getEzCameraInfo();
        String deviceSerial = ezCameraInfo2.getDeviceSerial();
        int cameraNo = ezCameraInfo2.getCameraNo();
        //try {

        //mQualityPopupWindow = new PopupWindow();
        LayoutInflater inflater = LayoutInflater.from(this);
        View contentView = inflater.inflate(R.layout.activity_videoplayer_video_resolution_popupwindow,
                (ViewGroup) getWindow().getDecorView(), false);
        RadioGroup videoResolution = contentView.findViewById(R.id.rg_video_resolution);

        switch (ezCameraInfo2.getVideoLevel()) {
            //case VIDEO_LEVEL_FLUNET:
                //videoResolution.check(R.id.btn_video_low_resolution);
            //    break;
            case VIDEO_LEVEL_BALANCED:
                videoResolution.check(R.id.btn_video_medium_resolution);
                break;
            case VIDEO_LEVEL_HD:
                videoResolution.check(R.id.btn_video_high_resolution);
                break;
            default: //fluent
                videoResolution.check(R.id.btn_video_low_resolution);
        }

        mQualityPopupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT, true);

        //mQualityPopupWindow.showAtLocation(setResolution);
        //mQualityPopupWindow.showAtLocation();
        int[] location = new int[2];
        controlPanel.getLocationOnScreen(location);
        //popupWindow.setAnimationStyle(R.style.AnimationPopup);
        mQualityPopupWindow.showAtLocation(controlPanel, NO_GRAVITY, 0,
                location[1] - controlPanel.getHeight()); //50dp

        //popupWindow.setFocusable(true);
        //popupWindow.setOutsideTouchable(true);

        videoResolution.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int videoLevel = 0;
                switch (checkedId) {
                    case R.id.btn_video_high_resolution:
                        videoLevel = 2;
                        break;
                    //case R.id.btn_video_low_resolution:
                    //    videoLevel = 0;
                    //    break;
                    case R.id.btn_video_medium_resolution:
                        videoLevel = 1;
                        break;
                    default:
                        videoLevel = 0;
                }
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        //super.run();
                        try {
                            EZOpenSDK.getInstance().setVideoLevel(deviceSerial, cameraNo, 1); //balance
                            ezPlayerMaps.get(index).getEzPlayer().stopRealPlay();
                            ezPlayerMaps.get(index).getEzPlayer().startRealPlay();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(VideoPlayActivity.this, "切换成功", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (BaseException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                hideResulationPopupWindow();
            }
        });
    }

    private void hideResulationPopupWindow(){
        if (mQualityPopupWindow != null) {
            mQualityPopupWindow.dismiss();
            mQualityPopupWindow = null;
        }
    }

    static class VideoPlayerHandler extends Handler {
        private WeakReference<VideoPlayActivity> videoPlayerActivityWeakRef;

        public VideoPlayerHandler(VideoPlayActivity videoPlayActivity) {
            videoPlayerActivityWeakRef = new WeakReference<>(videoPlayActivity);
        }


        @Override
        public void handleMessage(@NonNull Message msg) {
            VideoPlayActivity videoPlayActivity = videoPlayerActivityWeakRef.get();
            if (videoPlayActivity == null) {
                return;
            }
            //handlemessage


            super.handleMessage(msg);
        }
    }


}
