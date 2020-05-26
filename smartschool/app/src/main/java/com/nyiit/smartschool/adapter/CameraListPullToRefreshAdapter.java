package com.nyiit.smartschool.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nyiit.smartschool.R;
import com.videogo.openapi.bean.EZDeviceInfo;

import java.util.ArrayList;
import java.util.List;

public class CameraListPullToRefreshAdapter extends RecyclerView.Adapter {

    public interface OnCameraClickListener{
        void onPlayClicked(EZDeviceInfo eZDeviceInfo, int cameraIndex);
        void onRecordClicked(EZDeviceInfo eZDeviceInfo, int cameraIndex);
        void onInfoClicked(EZDeviceInfo eZDeviceInfo, int cameraIndex);
    }

    private OnCameraClickListener onCameraClickListener;

    private static final int TYPE_CONTENT = 0;
    private static final int TYPE_FOOTER = 1;

    private boolean hasNoMoreData = false;

    private Context context;
    private List<EZDeviceInfo> ezDeviceInfoInfoList = new ArrayList<>();

    public CameraListPullToRefreshAdapter(Context context) {
        this.context = context;
    }

    public void addItem(EZDeviceInfo ezDeviceInfo) {
        ezDeviceInfoInfoList.add(ezDeviceInfo);
    }

    public void clearItems() {
        hasNoMoreData = false;
        ezDeviceInfoInfoList.clear();
    }

    public void setOnCameraClickListener(OnCameraClickListener onCameraClickListener) {
        this.onCameraClickListener = onCameraClickListener;
    }

    public void setHasNoMoreData(boolean hasNoMoreData) {
        this.hasNoMoreData = hasNoMoreData;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_CONTENT) {
            View view = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.activity_cameralist_recycleview_content, parent,false);
            return new CameraDeviceHolder(view);
        } else {
            //if (hasNoMoreData) { //no more data
            //    View view = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.activity_cameralist_recycleview_footer_nomoredata, null,false);
            //    return new FooterViewLoadingHolder(view);
            //}
            //loading
            View view = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.activity_cameralist_recycleview_footer_loading, parent,false);
            return new FooterViewLoadingHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_FOOTER) {
            if (hasNoMoreData) {
                ((FooterViewLoadingHolder)holder).textView.setVisibility(View.VISIBLE);
                ((FooterViewLoadingHolder)holder).progressBar.setVisibility(View.GONE);
            } else {
                ((FooterViewLoadingHolder)holder).progressBar.setVisibility(View.VISIBLE);
                ((FooterViewLoadingHolder)holder).textView.setVisibility(View.GONE);
            }
        } else {
            ((CameraDeviceHolder)holder).cameraNameTv.setText(ezDeviceInfoInfoList.get(position).getDeviceName());
            ((CameraDeviceHolder)holder).play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onCameraClickListener != null) {
                        onCameraClickListener.onPlayClicked(ezDeviceInfoInfoList.get(position), position);
                    }
                }
            });
            ((CameraDeviceHolder)holder).record.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onCameraClickListener != null) {
                        onCameraClickListener.onRecordClicked(ezDeviceInfoInfoList.get(position), position);
                    }
                }
            });
            ((CameraDeviceHolder)holder).info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onCameraClickListener != null) {
                        onCameraClickListener.onInfoClicked(ezDeviceInfoInfoList.get(position), position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == ezDeviceInfoInfoList.size()) {
            //if (isNoMoreData) {
            //    return
            //}
            return TYPE_FOOTER;
        }
        return TYPE_CONTENT;
        //return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return ezDeviceInfoInfoList != null? ezDeviceInfoInfoList.size(): 0; //考虑footer
    }

    public class CameraDeviceHolder extends RecyclerView.ViewHolder {
        TextView cameraNameTv;
        View showArea;
        ImageView spin;
        View hideArea;
        View play;
        View record;
        View info;

        private boolean isShowHideArea = false;

        public CameraDeviceHolder(View itemView) {
            super(itemView);
            showArea = itemView.findViewById(R.id.ll_showArea);
            spin = itemView.findViewById(R.id.iv_camera_spin);
            hideArea = itemView.findViewById(R.id.ll_hideArea);
            cameraNameTv = itemView.findViewById(R.id.tv_camera_name);
            play = itemView.findViewById(R.id.ll_camera_play);
            record = itemView.findViewById(R.id.ll_camera_record);
            info = itemView.findViewById(R.id.ll_camera_info);

            //setListener
            showArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isShowHideArea) {
                        spin.setImageResource(R.mipmap.down);
                        hideArea.setVisibility(View.GONE);
                    } else {
                        spin.setImageResource(R.mipmap.up);
                        hideArea.setVisibility(View.VISIBLE);
                    }
                    isShowHideArea = !isShowHideArea;
                }
            });

        }
    }

    public class FooterViewLoadingHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        TextView textView;

        public FooterViewLoadingHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.loadmore_pb);
            textView = itemView.findViewById(R.id.loadmore_tv);
        }
    }




}
