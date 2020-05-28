package com.nyiit.smartschool.constants;

public class RemoteConstant {
    // ---------------回放列表查询状态-------------------------
    // 查询回放列表->查询成功
    public static final int QUERY_CLOUD_SUCCESSFUL_NOLOACL = 1;
    public static final int QUERY_CLOUD_SUCCESSFUL_HASLOCAL = 11;
    public static final int QUERY_CLOUD_SUCCESSFUL_LOCAL_EX = 12;
    // 查询回放列表->查询后无数据
    public static final int QUERY_NO_DATA = 2;
    // 查询回放列表 ->只有本地录像
    public static final int QUERY_ONLY_LOCAL = 3;
    // 查询回放列表 ->查询本地录像成功
    public static final int QUERY_LOCAL_SUCCESSFUL = 4;
    // 查询回放列表->异常
    public static final int QUERY_EXCEPTION = 10000;

    public static final int HAS_LOCAL = 0;
    public static final int NO_LOCAL = 1;
    public static final int EXCEPTION_LOCAL = 2;

    // --------------------回放列表类型-----------------
    public static final int TYPE_CLOUD = 0;
    public static final int TYPE_LOCAL = 1;
    public static final int TYPE_MORE = 2;
}
