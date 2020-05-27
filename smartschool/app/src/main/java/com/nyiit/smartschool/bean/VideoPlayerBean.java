package com.nyiit.smartschool.bean;

import com.videogo.openapi.EZPlayer;
import com.videogo.openapi.bean.EZCameraInfo;
import com.videogo.openapi.bean.EZDeviceInfo;

public class VideoPlayerBean {
    EZCameraInfo ezCameraInfo;
    EZDeviceInfo ezDeviceInfo;
    EZPlayer ezPlayer;
    int pageIndex;

    public VideoPlayerBean(EZCameraInfo ezCameraInfo, EZDeviceInfo ezDeviceInfo, EZPlayer ezPlayer, int pageIndex) {
        this.ezCameraInfo = ezCameraInfo;
        this.ezDeviceInfo = ezDeviceInfo;
        this.ezPlayer = ezPlayer;
        this.pageIndex = pageIndex;
    }

    public EZCameraInfo getEzCameraInfo() {
        return ezCameraInfo;
    }

    public void setEzCameraInfo(EZCameraInfo ezCameraInfo) {
        this.ezCameraInfo = ezCameraInfo;
    }

    public EZDeviceInfo getEzDeviceInfo() {
        return ezDeviceInfo;
    }

    public void setEzDeviceInfo(EZDeviceInfo ezDeviceInfo) {
        this.ezDeviceInfo = ezDeviceInfo;
    }

    public EZPlayer getEzPlayer() {
        return ezPlayer;
    }

    public void setEzPlayer(EZPlayer ezPlayer) {
        this.ezPlayer = ezPlayer;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }
}
