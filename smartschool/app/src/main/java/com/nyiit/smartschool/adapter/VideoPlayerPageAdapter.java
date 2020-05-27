package com.nyiit.smartschool.adapter;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.nyiit.smartschool.widget.VideoPlayer;

import java.util.ArrayList;
import java.util.List;

public class VideoPlayerPageAdapter extends PagerAdapter {
    private List<View> viewList = new ArrayList<>();

    public interface OnViewPageVClickListener{
        void onViewPageVClick(int pageIndex);
    }

    private OnViewPageVClickListener onViewPageVClickListener;

    public VideoPlayerPageAdapter(List<View> viewList) {
        this.viewList = viewList;
    }

    @Override
    public int getCount() {
        return viewList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) { //此处添加标题
        return "视频" + (position + 1);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public void setOnViewPageVClickListener(OnViewPageVClickListener OnViewPageVClickListener) {
        //ib_add.setOnClickListener(onClickListener);
        this.onViewPageVClickListener = OnViewPageVClickListener;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        VideoPlayer videoPlayer = (VideoPlayer)viewList.get(position);
        videoPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("VideoPlayerPageAdapter", "onClick: " + position);
                if (onViewPageVClickListener != null) {
                    onViewPageVClickListener.onViewPageVClick(position);
                }
            }
        });
        container.addView(videoPlayer);
        return videoPlayer;
    }
}
