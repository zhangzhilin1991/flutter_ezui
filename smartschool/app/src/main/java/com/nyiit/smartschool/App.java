package com.nyiit.smartschool;

import android.app.Application;
import android.content.Intent;

import com.nyiit.smartschool.bean.VideoPlayerBean;
import com.nyiit.smartschool.ui.cameralist.CameraListActivity;
import com.videogo.openapi.EZOpenSDK;

import java.util.HashMap;
import java.util.Map;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class App extends Application {

    private final String APP_KEY = "cbc4453d01b2406da120d5ed1453b737";

    private Map<Integer, VideoPlayerBean> ezPlayerMaps = new HashMap<>(); //四路player
    
    private MethodChannel channel;
    FlutterEngine flutterEngine;

    @Override
    public void onCreate() {
        super.onCreate();

        /** * sdk日志开关，正式发布需要去掉 */
        EZOpenSDK.showSDKLog(BuildConfig.DEBUG);
        /** * 设置是否支持P2P取流,详见api */
        EZOpenSDK.enableP2P(false);

        /** * APP_KEY请替换成自己申请的 */
        EZOpenSDK.initLib(this, APP_KEY);

        flutterEngine = new FlutterEngine(this);

        // Start executing Dart code to pre-warm the FlutterEngine.
        flutterEngine.getDartExecutor().executeDartEntrypoint(
                DartExecutor.DartEntrypoint.createDefault()
        );

        flutterEngine.getNavigationChannel().setInitialRoute("/");

        // Cache the FlutterEngine to be used by FlutterActivity.
        FlutterEngineCache
                .getInstance()
                .put("my_engine_id", flutterEngine);



        channel = new MethodChannel(flutterEngine.getDartExecutor(), "com.nyiit.smartschool/action");

        channel.setMethodCallHandler(new MethodChannel.MethodCallHandler() {
            @Override
            public void onMethodCall(MethodCall call, MethodChannel.Result result) {
                switch (call.method){
                    case "startCameraListActivity":
                        Intent intent = new Intent(getApplicationContext(), CameraListActivity.class);
                        startActivity(intent);
                        break;
                    default:
                }
            }
        });
    }
        //channel
    
    public Map<Integer, VideoPlayerBean> getEzPlayerMaps() {
        return ezPlayerMaps;
    }
    
    public void clearEzPlayerMaps() {
        //for (VideoPlayerBean videoPlayerBean : ezPlayerMaps.values()) {
        //    //System.out.println("Value = " + value);
        //    videoPlayerBean.getEzPlayer().release();
        //}
        ezPlayerMaps.clear();
    }
}
