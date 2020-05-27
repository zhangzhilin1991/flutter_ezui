package com.nyiit.smartschool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.nyiit.smartschool.adapter.CameraListPullToRefreshAdapter;
import com.nyiit.smartschool.constants.IntentConstants;
import com.nyiit.smartschool.util.EZUtils;
import com.videogo.camera.CameraInfo;
import com.videogo.exception.BaseException;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.EzvizAPI;
import com.videogo.openapi.bean.EZCameraInfo;
import com.videogo.openapi.bean.EZDeviceInfo;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.nyiit.smartschool.constants.IntentConstants.INTENT_REQUEST_CODE_SELECT_NEW_DEVICE;
import static com.nyiit.smartschool.constants.IntentConstants.INTENT_RESPONSE_CODE_SELECT_NEW_DEVICE;
import static com.videogo.constant.Constant.OAUTH_SUCCESS_ACTION;

public class CameraListActivity extends AppCompatActivity implements CameraListPullToRefreshAdapter.OnCameraClickListener {
    private static final String TAG = CameraListActivity.class.getName();

    //public static final int INTENT_REQUEST_CODE_SELECT_NEW_DEVICE = 0;
    //public static final int INTENT_RESPONSE_CODE_SELECT_NEW_DEVICE = 1;
    //public static final int INTENT_REQUEST_CODE_SELECT_NEW_DEVICE = 0;
    //public static final String PLAYER_INDEX_KEY = "PLAYER_INDEX_KEY";

    private RecyclerView cameraListView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View loginView;
    private CameraListPullToRefreshAdapter mAdapter;
    //private boolean mHeaderOrFooter = true;
    private Handler handler;

    private int playerIndex = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cameralist);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        handler = new CameraListHandler(this);

        initView();
        getCameraDeviceInfoList(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //if (mHeaderOrFooter) {
        //    getCameraDeviceInfoList(true);
            //mHeaderOrFooter = false;
        //}
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLoginState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((App)getApplication()).getEzPlayerMaps().clear();
    }

    void initView() {
        swipeRefreshLayout = findViewById(R.id.sweaprefreshlayout);
        cameraListView = findViewById(R.id.camera_list_view);
        loginView = findViewById(R.id.login_btn);

        mAdapter = new CameraListPullToRefreshAdapter(this);
        mAdapter.setOnCameraClickListener(this);
        cameraListView.setAdapter(mAdapter);
        cameraListView.setLayoutManager(new LinearLayoutManager(this));

        swipeRefreshLayout.setProgressViewOffset(false, 50, 200);
        swipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);;
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        swipeRefreshLayout.setRefreshing(EzvizAPI.getInstance().isLogin()); //登陆的情况下，显示加载框

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //下拉刷新触发，获取数据
                getCameraDeviceInfoList(true);
            }
        });
        //swipeRefreshLayout;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPlayClicked(EZDeviceInfo eZDeviceInfo, int cameraIndex) {
        EZCameraInfo cameraInfo = EZUtils.getCameraInfoFromDevice(eZDeviceInfo, cameraIndex);
        if (cameraInfo == null) {
            return;
        }
        Intent intent = new Intent(CameraListActivity.this, VideoPlayerActivity.class);
        intent.putExtra(IntentConstants.EXTRA_PLAYER_INDEX_KEY, playerIndex);
        intent.putExtra(IntentConstants.EXTRA_CAMERA_INFO, cameraInfo);
        intent.putExtra(IntentConstants.EXTRA_DEVICE_INFO, eZDeviceInfo);
        startActivityForResult(intent, INTENT_REQUEST_CODE_SELECT_NEW_DEVICE);
        //startActivity(intent);
    }

    @Override
    public void onRecordClicked(EZDeviceInfo eZDeviceInfo, int cameraIndex) {

    }

    @Override
    public void onInfoClicked(EZDeviceInfo eZDeviceInfo, int cameraIndex) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //
        if (resultCode == INTENT_RESPONSE_CODE_SELECT_NEW_DEVICE) {
            playerIndex = data.getIntExtra(IntentConstants.EXTRA_PLAYER_INDEX_KEY, 0);
        } else {
            playerIndex = 0;
        }
        Log.d(TAG, "onActivityResult() playerIndex: " + playerIndex);
        
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void checkLoginState() {
        if (EzvizAPI.getInstance().isLogin()) {
            //jumpToCameraListActivity();
            //finish();
            showCameraListView();
        }else{
            showLoginView();
        }
    }

    private void showLoginView() {
        loginView.setVisibility(View.VISIBLE);
        cameraListView.setVisibility(View.GONE);
    }

    private void showCameraListView() {
        cameraListView.setVisibility(View.VISIBLE);
        loginView.setVisibility(View.GONE);
    }

    public void onLoginClick(View view) {
        registerLoginResultReceiver();
        EZOpenSDK.getInstance().openLoginPage();
    }

    public void getCameraDeviceInfoList(boolean mHeaderOrFooter) {
        if (EzvizAPI.getInstance().isLogin()) {
            new GetCameraInfoListTask(mHeaderOrFooter).execute();
        }
    }

    private void cancelPullToRefreshAnim() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 500);
    }




    static class CameraListHandler extends Handler {
        private WeakReference<CameraListActivity> cameraListActivityRef;

        public CameraListHandler(CameraListActivity cameraListActivity) {
            cameraListActivityRef = new WeakReference<>(cameraListActivity);
        }


        @Override
        public void handleMessage(@NonNull Message msg) {
            CameraListActivity cameraListActivity = cameraListActivityRef.get();
            if (cameraListActivity == null) {
                return;
            }
            //handlemessage

            super.handleMessage(msg);
        }
    }

    class GetCameraInfoListTask extends AsyncTask<Void, Void, List<EZDeviceInfo>> {
        private boolean mHeaderOrFooter;
        private int mErrorCode = 0;

        public GetCameraInfoListTask(boolean mHeaderOrFooter) {
            this.mHeaderOrFooter = mHeaderOrFooter;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //if (mHeaderOrFooter) {

            //}

        }

        @Override
        protected List<EZDeviceInfo> doInBackground(Void... voids) {
            if (CameraListActivity.this.isFinishing()){
                return null;
            }

            List<EZDeviceInfo> cameraInfoList = new ArrayList<>();

            try {
            //if (mHeaderOrFooter) {
            //    cameraInfoList = EZOpenSDK.getInstance().getDeviceList(0, 20);
            //} else {
                cameraInfoList = EZOpenSDK.getInstance().getDeviceList(
                        mAdapter.getItemCount() / 20 +
                                ((mAdapter.getItemCount() % 20 <= 1)? 0 : 1), 20);//考虑footer的情况
                return cameraInfoList;
            //}
            } catch (BaseException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<EZDeviceInfo> ezDeviceInfos) {
            super.onPostExecute(ezDeviceInfos);
            cancelPullToRefreshAnim();
            if (ezDeviceInfos == null) {
                return;
            }

            if (mHeaderOrFooter) {
                mAdapter.clearItems();
            }
            if (ezDeviceInfos.size() < 20) {
                mAdapter.setHasNoMoreData(true);
            }
            for (int i = 0; i < ezDeviceInfos.size(); i++) {
                mAdapter.addItem(ezDeviceInfos.get(i));
            }

            mAdapter.notifyDataSetChanged();
        }
    }

    BroadcastReceiver mLoginResultReceiver;
    private void registerLoginResultReceiver(){
        if (mLoginResultReceiver == null){
            mLoginResultReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.i(TAG, "login success by h5 page");
                    unregisterLoginResultReceiver();
                    getCameraDeviceInfoList(true);
                }
            };
            IntentFilter filter = new IntentFilter(OAUTH_SUCCESS_ACTION);
            registerReceiver(mLoginResultReceiver, filter);
            Log.i(TAG, "registered login result receiver");
        }
    }

    private void unregisterLoginResultReceiver() {
        if (mLoginResultReceiver != null){
            unregisterReceiver(mLoginResultReceiver);
            mLoginResultReceiver = null;
            Log.i(TAG, "unregistered login result receiver");
        }
    }
}
