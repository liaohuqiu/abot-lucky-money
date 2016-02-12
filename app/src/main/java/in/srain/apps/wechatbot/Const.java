package in.srain.apps.wechatbot;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by srain on 1/16/16.
 */
public class Const {

    public final static String TAG_FOR_ACTION = "node-action";
    public static final String MM_PACKAGE_NAME = "com.tencent.mm";

    public static Handler sHandler;

    static {
        sHandler = new Handler(Looper.getMainLooper());
    }
}
