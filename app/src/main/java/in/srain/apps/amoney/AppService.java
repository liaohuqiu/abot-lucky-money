package in.srain.apps.amoney;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import in.srain.abot.action.ActionParser;
import in.srain.abot.action.BaseAction;
import in.srain.abot.element.Element;
import in.srain.apps.amoney.wechat.WeChatElementFilter;
import in.srain.cube.cache.DiskFileUtils;

public class AppService extends AccessibilityService {

    private static final String KEY_FOR_CMD = "cmd";

    public static void start(Context context, String cmd, Bundle bundle) {
        Intent intent = new Intent(context, AppService.class);
        intent.putExtra(KEY_FOR_CMD, cmd);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        restart();
    }

    private void restart() {

        String content = DiskFileUtils.readAssert(this, "elements.txt");
        Element.parse(content);

        String actionsContent = DiskFileUtils.readAssert(this, "actions.txt");
        BaseAction mainAction = ActionParser.parse(actionsContent);
        Bot.getInstance().setMainAction(mainAction);

        WeChatElementFilter.clear();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Bot.getInstance().onAccessibilityEvent(event);
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Utils.printIntent("onStartCommand", intent);
        if (intent != null) {
            String cmd = intent.getStringExtra(KEY_FOR_CMD);
            if (!TextUtils.isEmpty(cmd)) {
                restart();
            }
        }
        return START_STICKY;
    }
}
