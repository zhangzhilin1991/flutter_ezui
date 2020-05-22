package com.example.androidinteract;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.google.gson.Gson;
import com.videogo.ui.cameralist.EZCameraListActivity;

import ezviz.ezopensdk.demo.SdkInitParams;
import ezviz.ezopensdk.demo.SpTool;
import ezviz.ezopensdk.demo.ValueKeys;
import io.flutter.BuildConfig;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;

import static com.videogo.constant.Constant.OAUTH_SUCCESS_ACTION;

import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.EzvizAPI;

public class FlutterAndroidIntegrationPlugin implements FlutterPlugin, MethodChannel.MethodCallHandler, ActivityAware {
    private static final String TAG = FlutterAndroidIntegrationPlugin.class.getName();

    static String CHANNEL = "plugins.com.nyiit.camera";

    String APP_KEY = "cbc4453d01b2406da120d5ed1453b737";

    private Activity activity;
    private Application application;

    private BroadcastReceiver mLoginResultReceiver = null;
    private SdkInitParams mSdkInitParams = null;

    MethodChannel methodChannel;

    public static void registerWith(PluginRegistry.Registrar registerWith) { // 保留旧版本兼容
        FlutterAndroidIntegrationPlugin instance =  new FlutterAndroidIntegrationPlugin ();
        instance.application = (Application) registerWith.context().getApplicationContext();
        instance.methodChannel = new MethodChannel(registerWith.messenger(), CHANNEL);
        instance.methodChannel.setMethodCallHandler(instance);
        instance.mSdkInitParams = instance.getLastSdkInitParams();
    }

    @Override
    public void onAttachedToEngine(FlutterPluginBinding flutterPluginBinding) {
        application = (Application) flutterPluginBinding.getApplicationContext();
        methodChannel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), CHANNEL);
        methodChannel.setMethodCallHandler(this);
        mSdkInitParams = getLastSdkInitParams();
    }

    @Override
    public void onDetachedFromEngine(FlutterPluginBinding flutterPluginBinding) {

    }

    @Override
    public void onAttachedToActivity(ActivityPluginBinding activityPluginBinding) {
        activity = activityPluginBinding.getActivity();


    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {

    }

    @Override
    public void onReattachedToActivityForConfigChanges(ActivityPluginBinding activityPluginBinding) {

    }

    @Override
    public void onDetachedFromActivity() {
        activity = null;
    }

    @Override
    public void onMethodCall(MethodCall methodCall, MethodChannel.Result result) {
        if (methodCall.method.equals("openCameraAct")) {
            if (activity == null){
                return;
            }
            EZOpenSDK.showSDKLog(BuildConfig.DEBUG == true);
            EZOpenSDK.initLib(application, APP_KEY);
            if (EzvizAPI.getInstance().isLogin()) {
                jumpToCameraListActivity();
                result.success("success");
            } else {
                registerLoginResultReceiver();
                EZOpenSDK.getInstance().openLoginPage();
            }

        } else {
            result.notImplemented();
        }
    }

    /**
     * 获取上次sdk初始化的参数
     */
    private SdkInitParams getLastSdkInitParams(){
        String lastSdkInitParamsStr = SpTool.obtainValue(ValueKeys.SDK_INIT_PARAMS);
        if (lastSdkInitParamsStr == null){
            return null;
        }else{
            return new Gson().fromJson(lastSdkInitParamsStr, SdkInitParams.class);
        }
    }

    private void registerLoginResultReceiver(){
        if (mLoginResultReceiver == null){
            mLoginResultReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.i(TAG, "login success by h5 page");
                    unregisterLoginResultReceiver();
                    //saveLastSdkInitParams(mSdkInitParams);
                    jumpToCameraListActivity();
                }
            };
            IntentFilter filter = new IntentFilter(OAUTH_SUCCESS_ACTION);
            activity.registerReceiver(mLoginResultReceiver, filter);
            Log.i(TAG, "registered login result receiver");
        }
    }

    private void unregisterLoginResultReceiver(){
        if (mLoginResultReceiver != null){
            activity.unregisterReceiver(mLoginResultReceiver);
            mLoginResultReceiver = null;
            Log.i(TAG, "unregistered login result receiver");
        }
    }

    /**
     * 保存上次sdk初始化的参数
     */
    private void saveLastSdkInitParams(SdkInitParams sdkInitParams) {
        // 不保存AccessToken
        sdkInitParams.accessToken = null;
        SpTool.storeValue(ValueKeys.SDK_INIT_PARAMS, sdkInitParams.toString());
    }

    private void jumpToCameraListActivity() {
        Intent intent = new Intent(activity, EZCameraListActivity.class);
        activity.startActivity(intent);
    }

}
