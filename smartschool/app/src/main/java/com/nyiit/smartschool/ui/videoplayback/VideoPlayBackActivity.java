package com.nyiit.smartschool.ui.videoplayback;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.nyiit.smartschool.R;
import com.nyiit.smartschool.ui.videoplay.VideoPlayActivity;
import com.nyiit.smartschool.bean.CloudPartInfoFileEx;
import com.nyiit.smartschool.constants.IntentConstants;
import com.nyiit.smartschool.constants.RemoteConstant;
import com.nyiit.smartschool.util.Fileutils;
import com.videogo.errorlayer.ErrorInfo;
import com.videogo.exception.BaseException;
import com.videogo.openapi.EZConstants;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.EZPlayer;
import com.videogo.openapi.bean.EZCameraInfo;
import com.videogo.openapi.bean.EZCloudRecordFile;
import com.videogo.openapi.bean.EZDeviceInfo;
import com.videogo.openapi.bean.EZDeviceRecordFile;
import com.videogo.openapi.bean.resp.CloudPartInfoFile;
import com.videogo.util.CollectionUtil;
import com.videogo.util.DateTimeUtil;
import com.videogo.util.LocalInfo;
import com.videogo.util.LogUtil;
import com.videogo.util.Utils;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.ezviz.stream.EZError.EZ_OK;
import static com.nyiit.smartschool.constants.IntentConstants.QUERY_DATE_INTENT_KEY;
import static com.nyiit.smartschool.util.Fileutils.getPicturePath;

public class VideoPlayBackActivity extends AppCompatActivity implements SurfaceHolder.Callback, View.OnClickListener {
    private static final String TAG = VideoPlayBackActivity.class.getName();

    private SurfaceView surfaceView;
    private SeekBar seekBar;

    private ImageButton ib_capturePic;
    private ImageButton ib_download_record;
    private ImageButton ib_switch_mute;
    private ImageButton ib_play;
    private ImageButton ib_set;

    private EZPlayer ezPlayer;
    private EZCameraInfo ezCameraInfo;
    private EZDeviceInfo ezDeviceInfo;

    private Date selectDate;

    private Handler handler;

    //private boolean isSoundOpen = false;

    private LocalInfo localInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_playback);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        handler = new Handler();

        initView();
        handleIntent();
        startQueryCloudRecordFiles();
    }

    private void initView() {
        surfaceView = findViewById(R.id.sf_player_playback);
        seekBar = findViewById(R.id.sb_progress);

        ib_capturePic = findViewById(R.id.ib_cap_rec_pic);
        ib_play = findViewById(R.id.ib_play_record);
        ib_download_record = findViewById(R.id.ib_download_record_video);
        ib_switch_mute = findViewById(R.id.iv_switch_mute);
        ib_set = findViewById(R.id.ib_set);
    }

    private void handleIntent() {
        Intent intent = getIntent();
        selectDate = (Date)(intent.getExtras().getSerializable(QUERY_DATE_INTENT_KEY));
        ezCameraInfo = intent.getParcelableExtra(IntentConstants.EXTRA_CAMERA_INFO);
        ezDeviceInfo = intent.getParcelableExtra(IntentConstants.EXTRA_DEVICE_INFO);
        //Intent;
        //Log.d(TAG, "handleIntent() pageIndex: " + pageIndex);
        String deviceSerial = ezCameraInfo.getDeviceSerial();
        int cameraNo = ezCameraInfo.getCameraNo();
        //if (!ezPlayerMaps.containsKey(pageIndex)){
        EZPlayer ezPlayer = EZOpenSDK.getInstance().createPlayer(deviceSerial, cameraNo);
        ezPlayer.setHandler(handler);

        localInfo = LocalInfo.getInstance();
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
        //play???
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (ezPlayer != null) {
            ezPlayer.setSurfaceHold(null);
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.ib_cap_rec_pic:
                String fileName = ezCameraInfo.getCameraName() + "_" + System.currentTimeMillis();
                String filePath = Fileutils.getPictureFilePath(this, fileName);
                Log.d(TAG, "onClick() take photo: filePath = " + filePath);
                if (ezPlayer.capturePicture(filePath) == EZ_OK) {
                    Toast.makeText(VideoPlayBackActivity.this, "图片保存到" + filePath, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(VideoPlayBackActivity.this, "截图失败！" + filePath, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ib_play_record:
                //ezPlayer
                break;
            case R.id.ib_download_record_video:
                break;
            case R.id.ib_set_mute:
                break;
            case R.id.ib_set:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_video_playback, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        } else if (item.getItemId() == R.id.menu_select_date) {
            showDatePicker();
        } else if (item.getItemId() == R.id.menu_open_record_folder){
            String path = getPicturePath(this);
            //Fileutils.OpenAssignFolder(this, path);
            //ntent intent = getFileIntent(this, path, "image/*");
            Intent albumIntent = new Intent(Intent.ACTION_VIEW, null);
            albumIntent.setType("image/*");
            startActivity(albumIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDatePicker() {
        TimePickerView pvTime = new TimePickerBuilder(VideoPlayBackActivity.this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                Toast.makeText(VideoPlayBackActivity.this, DateTimeUtil.formatDateToString(date, "yyyy-MM-dd"), Toast.LENGTH_SHORT).show();
            }
        }).build();
        pvTime.show();
    }

    private void startQueryCloudRecordFiles() {
        new GetRecordFileFromCloudTask(ezCameraInfo.getDeviceSerial(), ezCameraInfo.getCameraNo(), selectDate).execute();
    }

    static class VideoPlaybackHandler extends Handler {
        private WeakReference<VideoPlayActivity> videoPlayerActivityWeakRef;

        public VideoPlaybackHandler(VideoPlayActivity videoPlayActivity) {
            videoPlayerActivityWeakRef = new WeakReference<>(videoPlayActivity);
        }


        @Override
        public void handleMessage(@NonNull Message msg) {
            VideoPlayActivity videoPlayActivity = videoPlayerActivityWeakRef.get();
            if (videoPlayActivity == null) {
                return;
            }


            super.handleMessage(msg);
        }
    }

    private void convertCloudPartInfoFile2EZCloudRecordFile(EZCloudRecordFile dst, CloudPartInfoFile src) {
        dst.setCoverPic(src.getPicUrl());
        dst.setDownloadPath(src.getDownloadPath());
        dst.setFileId(src.getFileId());
        dst.setEncryption(src.getKeyCheckSum());
        dst.setStartTime(Utils.convert14Calender(src.getStartTime()));
        dst.setStopTime(Utils.convert14Calender(src.getEndTime()));
        dst.setDeviceSerial(src.getDeviceSerial());
        dst.setCameraNo(src.getCameraNo());
        dst.setVideoType(src.getVideoType());
        dst.setiStorageVersion(src.getiStorageVersion());
    }

    private class GetRecordFileFromRemoteDeviceTask extends AsyncTask<String, Void, Integer> {
        private String deviceSerial;
        private int channelNo;
        private Date queryDate;
        private int cloudTotal;
        private List<CloudPartInfoFileEx> playBackListLocalItems = new ArrayList<CloudPartInfoFileEx>();
        private List<CloudPartInfoFile> convertCalendarFiles;
        List<EZCloudRecordFile> ezCloudRecordFileList;

        public GetRecordFileFromRemoteDeviceTask(String deviceSerial, int channelNo, Date queryDate) {
            this.deviceSerial = deviceSerial;
            this.channelNo = channelNo;
            this.queryDate = queryDate;
        }

        @Override
        protected Integer doInBackground(String... strings) {
            cloudTotal = Integer.parseInt(strings[0]);
            return getRecordFileFromRemoteDevice();
        }

        private int getRecordFileFromRemoteDevice() {
            Date beginDate = DateTimeUtil.beginDate(queryDate);
            Date endDate = DateTimeUtil.endDate(queryDate);
            Calendar startTime = Calendar.getInstance();
            Calendar endTime = Calendar.getInstance();
            startTime.setTime(queryDate);
            endTime.setTime(queryDate);

            startTime.set(Calendar.HOUR_OF_DAY, 0);
            startTime.set(Calendar.MINUTE, 0);
            startTime.set(Calendar.SECOND, 0);
            endTime.set(Calendar.HOUR_OF_DAY, 23);
            endTime.set(Calendar.MINUTE, 59);
            endTime.set(Calendar.SECOND, 59);

            List<EZDeviceRecordFile> tmpList = null;
            try {
                tmpList = EZOpenSDK.getInstance().searchRecordFileFromDevice(deviceSerial,channelNo,
                        startTime, endTime);
            } catch (BaseException e) {
                e.printStackTrace();

                ErrorInfo errorInfo = e.getObject();
                LogUtil.debugLog("search file list failed. error ", errorInfo.toString());
            }

            convertCalendarFiles = new ArrayList<>();
            if (tmpList != null && tmpList.size() > 0) {
                for (int i = 0; i < tmpList.size(); i++) {
                    EZDeviceRecordFile file = tmpList.get(i);
                    CloudPartInfoFile cpif = new CloudPartInfoFile();

                    convertEZDeviceRecordFile2CloudPartInfoFile(cpif, file, i);
                    convertCalendarFiles.add(cpif);
                }
            }

            if (CollectionUtil.isNotEmpty(convertCalendarFiles)) {
                Collections.sort(convertCalendarFiles);
            }
            int length = convertCalendarFiles.size();
            int i = 0;
            while (i < length) {
                CloudPartInfoFileEx cloudPartInfoFileEx = new CloudPartInfoFileEx();
                CloudPartInfoFile dataOne = getCloudPartInfoFile(convertCalendarFiles.get(i), beginDate, endDate);
                dataOne.setPosition(cloudTotal + i);
                Calendar beginCalender = Utils.convert14Calender(dataOne.getStartTime());

                String hour = getHour(beginCalender.get(Calendar.HOUR_OF_DAY));
                cloudPartInfoFileEx.setHeadHour(hour);
                cloudPartInfoFileEx.setDataOne(dataOne);
                i++;
                if (i > length - 1) {
                    playBackListLocalItems.add(cloudPartInfoFileEx);
                    continue;
                }
                CloudPartInfoFile dataTwo = getCloudPartInfoFile(convertCalendarFiles.get(i), beginDate, endDate);
                if (hour.equals(getHour(Utils.convert14Calender(dataTwo.getStartTime()).get(Calendar.HOUR_OF_DAY)))) {
                    dataTwo.setPosition(cloudTotal + i);
                    cloudPartInfoFileEx.setDataTwo(dataTwo);
                    i++;
                    if (i > length - 1) {
                        playBackListLocalItems.add(cloudPartInfoFileEx);
                        continue;
                    }
                    CloudPartInfoFile dataThree = getCloudPartInfoFile(convertCalendarFiles.get(i), beginDate, endDate);
                    if (hour.equals(getHour(Utils.convert14Calender(dataThree.getStartTime()).get(Calendar.HOUR_OF_DAY)))) {
                        dataThree.setPosition(cloudTotal + i);
                        cloudPartInfoFileEx.setDataThree(dataThree);
                        i++;
                    }
                }
                playBackListLocalItems.add(cloudPartInfoFileEx);
            }

            if (CollectionUtil.isNotEmpty(playBackListLocalItems)) {
                return RemoteConstant.QUERY_LOCAL_SUCCESSFUL;
            }
            return RemoteConstant.QUERY_NO_DATA;
        }

        private String calendar2String(Calendar cal) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String dateStr = sdf.format(cal.getTime());

            return dateStr;
        }

        private void convertEZDeviceRecordFile2CloudPartInfoFile(CloudPartInfoFile dst, EZDeviceRecordFile src, int pos) {
            dst.setStartTime(calendar2String(src.getStartTime()));
            dst.setEndTime(calendar2String(src.getStopTime()));
            dst.setPosition(pos);
        }

        private CloudPartInfoFile getCloudPartInfoFile(CloudPartInfoFile cloudPartInfoFile, Date beginDate, Date endDate) {
            Calendar beginCalender = Utils.convert14Calender(cloudPartInfoFile.getStartTime());
            if (beginCalender.getTimeInMillis() < beginDate.getTime()) {
                beginCalender = Calendar.getInstance();
                beginCalender.setTime(beginDate);
            }
            Calendar endCalender = Utils.convert14Calender(cloudPartInfoFile.getEndTime());
            if (endCalender.getTimeInMillis() > endDate.getTime()) {
                endCalender = Calendar.getInstance();
                endCalender.setTime(endDate);
            }
            cloudPartInfoFile.setStartTime(new SimpleDateFormat("yyyyMMddHHmmss").format(beginCalender.getTime()));
            cloudPartInfoFile.setEndTime(new SimpleDateFormat("yyyyMMddHHmmss").format(endCalender.getTime()));
            return cloudPartInfoFile;
        }

        private String getHour(int hourOfDay) {
            return hourOfDay + ":00";
        }
    }

    private class GetRecordFileFromCloudTask extends AsyncTask<String, Void, Integer> {
        private String deviceSerial;
        private int channelNo;
        private Date queryDate;
        private List<CloudPartInfoFileEx> cloudPartInfoFileExList = new ArrayList<CloudPartInfoFileEx>();
        private List<CloudPartInfoFile> cloudPartFiles;

        public GetRecordFileFromCloudTask(String deviceSerial, int channelNo, Date queryDate) {
            this.deviceSerial = deviceSerial;
            this.channelNo = channelNo;
            this.queryDate = queryDate;
        }

        @Override
        protected Integer doInBackground(String... strings) {
            int queryCloudFiles = getRecordFileFromCloud();
            if (queryCloudFiles == RemoteConstant.QUERY_NO_DATA) {
                return RemoteConstant.QUERY_NO_DATA;
            } else {
                return RemoteConstant.QUERY_CLOUD_SUCCESSFUL_NOLOACL;
            }
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if (integer == RemoteConstant.QUERY_NO_DATA) {
                Toast.makeText(VideoPlayBackActivity.this, "加载失败,未获取到数据", Toast.LENGTH_LONG).show();
            } else if (integer == RemoteConstant.QUERY_CLOUD_SUCCESSFUL_NOLOACL) {
                Toast.makeText(VideoPlayBackActivity.this, "加载成功", Toast.LENGTH_SHORT).show();
                EZCloudRecordFile ezCloudRecordFile = new EZCloudRecordFile();
                convertCloudPartInfoFile2EZCloudRecordFile(ezCloudRecordFile, cloudPartFiles.get(0));
                ezPlayer.setSurfaceHold(surfaceView.getHolder());
                ezPlayer.setHandler(handler);
                ezPlayer.startPlayback(ezCloudRecordFile);
            }

            super.onPostExecute(integer);
        }

        private int getRecordFileFromCloud() {
            Calendar startTime = Calendar.getInstance();
            Calendar endTime = Calendar.getInstance();
            startTime.setTime(queryDate);
            endTime.setTime(queryDate);

            startTime.set(Calendar.HOUR_OF_DAY, 0);
            startTime.set(Calendar.MINUTE, 0);
            startTime.set(Calendar.SECOND, 0);
            endTime.set(Calendar.HOUR_OF_DAY, 23);
            endTime.set(Calendar.MINUTE, 59);
            endTime.set(Calendar.SECOND, 59);

            List<EZCloudRecordFile> tmpList = null;
            try {
                tmpList = EZOpenSDK.getInstance().searchRecordFileFromCloud(deviceSerial,channelNo,
                        startTime, endTime);
            } catch (BaseException e) {
                e.printStackTrace();
            }

            cloudPartFiles = new ArrayList<>();
            if (tmpList != null && tmpList.size() > 0) {
                for (int i = 0; i < tmpList.size(); i++) {
                    EZCloudRecordFile file = tmpList.get(i);
                    CloudPartInfoFile cpif = new CloudPartInfoFile();

                    convertEZCloudRecordFile2CloudPartInfoFile(cpif, file, i);
                    cloudPartFiles.add(cpif);
                }
            }

            if (CollectionUtil.isNotEmpty(cloudPartFiles)) {
                Collections.sort(cloudPartFiles);
            }

            int length = cloudPartFiles.size();
            int i = 0;
            while (i < length) {
                CloudPartInfoFileEx cloudPartInfoFileEx = new CloudPartInfoFileEx();
                CloudPartInfoFile dataOne = cloudPartFiles.get(i);
                dataOne.setPosition(i);
                Calendar beginCalender = Utils.convert14Calender(dataOne.getStartTime());
                String hour = getHour(beginCalender.get(Calendar.HOUR_OF_DAY));
                cloudPartInfoFileEx.setHeadHour(hour);
                cloudPartInfoFileEx.setDataOne(dataOne);
                i++;
                if (i > length - 1) {
                    cloudPartInfoFileExList.add(cloudPartInfoFileEx);
                    continue;
                }
                CloudPartInfoFile dataTwo = cloudPartFiles.get(i);
                if (hour.equals(getHour(Utils.convert14Calender(dataTwo.getStartTime()).get(Calendar.HOUR_OF_DAY)))) {
                    dataTwo.setPosition(i);
                    cloudPartInfoFileEx.setDataTwo(dataTwo);
                    i++;
                    if (i > length - 1) {
                        cloudPartInfoFileExList.add(cloudPartInfoFileEx);
                        continue;
                    }
                    CloudPartInfoFile dataThree = cloudPartFiles.get(i);
                    if (hour.equals(getHour(Utils.convert14Calender(dataThree.getStartTime()).get(Calendar.HOUR_OF_DAY)))) {
                        dataThree.setPosition(i);
                        cloudPartInfoFileEx.setDataThree(dataThree);
                        i++;
                    }
                }
                cloudPartInfoFileExList.add(cloudPartInfoFileEx);
            }
            if (CollectionUtil.isNotEmpty(cloudPartInfoFileExList)) {
                return RemoteConstant.QUERY_CLOUD_SUCCESSFUL_NOLOACL;
            }
            return RemoteConstant.QUERY_NO_DATA;

        }


        private String getHour(int hourOfDay) {
            return hourOfDay + ":00";
        }

        private void convertEZCloudRecordFile2CloudPartInfoFile(CloudPartInfoFile dst, EZCloudRecordFile src, int pos) {
            String startT = new SimpleDateFormat("yyyyMMddHHmmss").format(src.getStartTime().getTime());
            String endT = new SimpleDateFormat("yyyyMMddHHmmss").format(src.getStopTime().getTime());
            dst.setCloud(true);
            dst.setDownloadPath(src.getDownloadPath());
            dst.setEndTime(endT);
            dst.setFileId(src.getFileId());
            dst.setKeyCheckSum(src.getEncryption());
            dst.setPicUrl(src.getCoverPic());
            dst.setPosition(pos);
            dst.setStartTime(startT);
            dst.setDeviceSerial(src.getDeviceSerial());
            dst.setCameraNo(src.getCameraNo());
            dst.setVideoType(src.getVideoType());
            dst.setiStorageVersion(src.getiStorageVersion());
        }
    }
}
