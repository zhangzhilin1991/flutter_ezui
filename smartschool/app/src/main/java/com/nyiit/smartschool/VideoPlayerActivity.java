package com.nyiit.smartschool;

import android.content.Intent;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.nyiit.smartschool.adapter.CameraListPullToRefreshAdapter;
import com.nyiit.smartschool.adapter.VideoPlayer;
import com.nyiit.smartschool.adapter.VideoPlayerPageAdapter;
import com.videogo.openapi.EZPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VideoPlayerActivity extends AppCompatActivity implements View.OnClickListener{

    private Map<Integer, EZPlayer> ezPlayerMaps = new HashMap<>(); //四路player

    private ViewPager viewPager;
    private TabLayout tabLayout;
    VideoPlayerPageAdapter videoPlayerPageAdapter;
    List<View> viewList = new ArrayList<>();

    private SurfaceView sf1;
    private SurfaceView sf2;
    private SurfaceView sf3;
    private SurfaceView sf4;
    private ImageButton takePhoto;
    private ImageButton recordVideo;
    private ImageButton setResolution;
    private ImageButton deleteVideo;
    private ImageButton setVolume;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_4chanel_videoplayer);

        initView();
    }

    private void initView() {
        //sf1 = findViewById(R.id.sf_player_surface1);
        //sf2 = findViewById(R.id.sf_player_surface2);
        //sf3 = findViewById(R.id.sf_player_surface3);
        //sf4 = findViewById(R.id.sf_player_surface4);
        viewPager  = findViewById(R.id.vp_surfaceview);
        viewPager.setOffscreenPageLimit(3);
        tabLayout = findViewById(R.id.tab_surfaceview);

        takePhoto = findViewById(R.id.iv_take_photo);
        recordVideo = findViewById(R.id.iv_record_video);
        setResolution = findViewById(R.id.iv_set_resolution);
        deleteVideo = findViewById(R.id.delete_video);
        setVolume = findViewById(R.id.iv_switch_mute);

        for (int i = 0; i < 4 ; i++) {
            TextView textView = new TextView(this);
            textView.setText("这是第" + i + "个界面");
            viewList.add(textView);
        }

        videoPlayerPageAdapter = new VideoPlayerPageAdapter(viewList);
        viewPager.setAdapter(videoPlayerPageAdapter);

        tabLayout.setupWithViewPager(viewPager);
    }

    private void handleIntent() {
        Intent intent = getIntent();
        //Intent;
    }

    @Override
    public void onClick(View v) {

    }


}
