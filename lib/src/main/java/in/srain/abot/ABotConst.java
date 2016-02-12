package in.srain.abot;

import android.os.Handler;
import android.os.Looper;

public class ABotConst {
    public static final boolean DEBUG = false;

    public final static String TAG_FOR_ACTION = "abot-action";
    public final static String TAG_FOR_NODE = "abot-node";
    public static Handler sHandler;

    static {
        sHandler = new Handler(Looper.getMainLooper());
    }
}
